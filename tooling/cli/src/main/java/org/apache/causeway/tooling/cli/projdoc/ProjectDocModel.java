/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.causeway.tooling.cli.projdoc;

import static guru.nidi.codeassert.config.Language.JAVA;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.structurizr.model.Container;
import com.structurizr.view.AutomaticLayout.RankDirection;

import org.apache.commons.lang3.builder.EqualsExclude;
import org.asciidoctor.ast.Document;
import org.springframework.lang.Nullable;

import org.apache.causeway.commons.collections.Can;
import org.apache.causeway.commons.internal.base._Files;
import org.apache.causeway.commons.internal.base._Strings;
import org.apache.causeway.commons.internal.exceptions._Exceptions;
import org.apache.causeway.commons.internal.graph._Graph;
import org.apache.causeway.tooling.c4.C4;
import org.apache.causeway.tooling.cli.CliConfig;
import org.apache.causeway.tooling.cli.adocfix.OrphanedIncludeStatementFixer;
import org.apache.causeway.tooling.j2adoc.J2AdocContext;
import org.apache.causeway.tooling.j2adoc.format.UnitFormatter;
import org.apache.causeway.tooling.javamodel.AnalyzerConfigFactory;
import org.apache.causeway.tooling.javamodel.ast.CodeClasses;
import org.apache.causeway.tooling.model4adoc.AsciiDocFactory;
import org.apache.causeway.tooling.projectmodel.ArtifactCoordinates;
import org.apache.causeway.tooling.projectmodel.Dependency;
import org.apache.causeway.tooling.projectmodel.ProjectNode;

import static org.apache.causeway.tooling.model4adoc.AsciiDocFactory.block;
import static org.apache.causeway.tooling.model4adoc.AsciiDocFactory.cell;
import static org.apache.causeway.tooling.model4adoc.AsciiDocFactory.doc;
import static org.apache.causeway.tooling.model4adoc.AsciiDocFactory.headRow;
import static org.apache.causeway.tooling.model4adoc.AsciiDocFactory.row;
import static org.apache.causeway.tooling.model4adoc.AsciiDocFactory.table;

import guru.nidi.codeassert.config.Language;
import guru.nidi.codeassert.model.CodeClass;
import guru.nidi.codeassert.model.Model;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

/**
 * Acts both as a model and a writer (adoc).
 * @since Sep 22, 2020
 *
 */
public class ProjectDocModel {

    private final ProjectNode projTree;
    private SortedSet<ProjectNode> modules;

    public ProjectDocModel(final ProjectNode projTree) {
        this.projTree = projTree;
    }

    public enum Mode {
        ALL,
        OVERVIEW,
        INDEX;
        public boolean includeOverview() {   return this == OVERVIEW || this == ALL; }
        public boolean includeIndex() {   return this == INDEX || this == ALL; }
    }

    public void generateAsciiDoc(final @NonNull CliConfig cliConfig, final @NonNull Mode mode) {

        modules = new TreeSet<ProjectNode>();
        projTree.depthFirst(modules::add);


        val j2aContext = J2AdocContext.builder()
                .formatterFactory(new Function<>() {
                    @SneakyThrows
                    @Override
                    public UnitFormatter apply(final J2AdocContext j2AdocContext) {
                        final CliConfig.Commands.Index.Formatter formatter = cliConfig.getCommands().getIndex().getFormatter();
                        final Class<? extends UnitFormatter> clz = formatter.getUnitFormatterClass();
                        final Constructor<? extends UnitFormatter> constructor = clz.getConstructor(J2AdocContext.class);
                        return constructor.newInstance(j2AdocContext);
                    }
                })
                .licenseHeader(cliConfig.getGlobal().getLicenseHeader())
                .namespacePartsSkipCount(cliConfig.getCommands().getIndex().getNamespacePartsSkipCount())
                .skipTitleHeader(cliConfig.getCommands().getIndex().isSkipTitleHeader())
                .build();

        // partition modules into sections
        val sections = new ArrayList<Section>();
        cliConfig.getCommands().getOverview().getSections().forEach((section, groupIdArtifactIdPattern)->{
            createSections(modules, section, groupIdArtifactIdPattern, sections::add);
        });

        // ensure that each module is referenced only by a single section,
        // preferring to be owned by a non-group section (ie more specific)
        val modulesReferencedByNonGroupSections =
            sections.stream()
                .filter(Section::isNotGroupLevelOnly)
                .flatMap(Section::streamMatchingProjectNodes)
                .collect(Collectors.toList());

        sections.stream()
                .filter(Section::isGroupLevelOnly)
                .forEach(section -> section.removeProjectNodes(modulesReferencedByNonGroupSections));

        // any remaining modules go into an 'Other' section
        sections.forEach(section -> modules.removeAll(section.getMatchingProjectNodes()));
        if(!modules.isEmpty()) {
            final Section other = new Section("Other", null, false);
            modules.forEach(other::addProjectNode);
            sections.add(other);
            modules.clear();
        }

        // now generate the overview or index
        final SortedSet<File> asciiDocFiles = new TreeSet<>();

        val overviewDoc = doc();
        overviewDoc.setTitle("System Overview");

        _Strings.nonEmpty(cliConfig.getGlobal().getLicenseHeader())
                .ifPresent(notice->AsciiDocFactory.attrNotice(overviewDoc, notice));

        _Strings.nonEmpty(cliConfig.getCommands().getOverview().getDescription())
                .ifPresent(block(overviewDoc)::setSource);

        writeSections(sections, overviewDoc, j2aContext, mode, asciiDocFiles::add);

        ProjectDocWriter.write(cliConfig, overviewDoc, j2aContext, mode);

        // update include statements ...
        OrphanedIncludeStatementFixer.fixIncludeStatements(asciiDocFiles, cliConfig, j2aContext);

    }

    @RequiredArgsConstructor
    public static class Section {
        @Getter
        private final String sectionName;
        @Getter
        private final String groupIdArtifactIdPattern;
        @Getter
        private final boolean addAdocFiles;

        private final List<ProjectNode> matchingProjectNodes = new ArrayList<>();

        public List<ProjectNode> getMatchingProjectNodes() {
            return Collections.unmodifiableList(matchingProjectNodes);
        }

        public Stream<ProjectNode> streamMatchingProjectNodes() {
            return matchingProjectNodes.stream();
        }

        void addProjectNode(final ProjectNode projectNode) {
            matchingProjectNodes.add(projectNode);
        }

        boolean isGroupLevelOnly() {
            return ! isNotGroupLevelOnly();
        }
        boolean isNotGroupLevelOnly() {
            return groupIdArtifactIdPattern.contains(":");
        }

        void removeProjectNodes(final Collection<ProjectNode> projectNodes) {
            matchingProjectNodes.removeAll(projectNodes);
        }

        @Override
        public String toString() {
            return String.format("%s (%s): %d modules", sectionName, isGroupLevelOnly() ? "group" : "non-group", getMatchingProjectNodes().size());
        }
    }

    // -- HELPER

    @RequiredArgsConstructor(staticName = "of")
    @EqualsAndHashCode
    private static class ProjectAndContainerTuple {
        final ProjectNode projectNode;
        @EqualsExclude final Container container;
    }

    private static class GroupDiagram {

        private final C4 c4;
        private final List<ProjectNode> projectNodes = new ArrayList<>();
        private final String diagramKey;

        public GroupDiagram(final C4 c4) {
            this.c4 = c4;
            this.diagramKey = c4.getWorkspaceName().replace(':', '~');
        }

        public void collect(final ProjectNode module) {
            projectNodes.add(module);
        }

        //XXX lombok issues, not using val here
        public String toPlantUml(final String softwareSystemName) {

            val softwareSystem = c4.softwareSystem(softwareSystemName, null);

            final Can<ProjectAndContainerTuple> tuples = Can.<ProjectNode>ofCollection(projectNodes)
            .map(projectNode->{
                val name = projectNode.getName();
                val description = ""; //projectNode.getDescription() XXX needs sanitizing, potentially breaks plantuml/asciidoc syntax
                val technology = String.format("packaging: %s", projectNode.getArtifactCoordinates().getPackaging());
                val container = softwareSystem.addContainer(name, description, technology);
                return ProjectAndContainerTuple.of(projectNode, container);
            });


            final _Graph<ProjectAndContainerTuple> adjMatrix =
                    _Graph.of(tuples, (a, b)->a.projectNode.getChildren().contains(b.projectNode));

            tuples.forEach(tuple->{
                adjMatrix.streamNeighbors(tuple)
                .forEach(dependentTuple->{
                    tuple.container.uses(dependentTuple.container, "");
                });
            });

            val containerView = c4.getViewSet()
                    .createContainerView(softwareSystem, c4.getWorkspaceName(), "Artifact Hierarchy (Maven)");
            containerView.addAllContainers();

            containerView.enableAutomaticLayout(RankDirection.LeftRight);

            val plantUmlSource = c4.toPlantUML(containerView);
            return plantUmlSource;
        }

        public String toAsciiDoc(final String softwareSystemName) {
            return AsciiDocFactory.toString(doc->
                        AsciiDocFactory.DiagramFactory
                        .plantumlSvg(doc, toPlantUml(softwareSystemName), diagramKey, null));
        }
    }

    private void createSections(
            final @NonNull SortedSet<ProjectNode> projectNodes,
            final @NonNull String sectionName,
            final @Nullable String pattern,
            final @NonNull Consumer<Section> sectionConsumer) {

        val section = new Section(sectionName, pattern, true);

        projectNodes.stream()
                .filter(module->matchesGroupId(module, pattern))
                .forEach(section::addProjectNode);

        sectionConsumer.accept(section);
    }

    private void writeSections(
            final @NonNull List<Section> sections,
            final @NonNull Document overviewDoc,
            final @NonNull J2AdocContext j2aContext,
            final @NonNull Mode mode,
            final @NonNull Consumer<File> onAdocFile) {

        sections.forEach(section -> {
            writeSection(section, overviewDoc, j2aContext, mode, onAdocFile);
        });
    }

    private void writeSection(
            final @NonNull Section section,
            final @NonNull Document overviewDoc,
            final @NonNull J2AdocContext j2aContext,
            final @NonNull Mode mode,
            final @NonNull Consumer<File> onAdocFile) {

        val sectionName = section.getSectionName();
        val groupIdPattern = section.getGroupIdArtifactIdPattern();

        val titleBlock = block(overviewDoc);

        val headingLevel =
                (groupIdPattern == null || !groupIdPattern.contains(":"))
                        ? "=="
                        : "===";
        titleBlock.setSource(String.format("%s %s", headingLevel, sectionName));

        val sectionModules = section.getMatchingProjectNodes();
        if(sectionModules.isEmpty()) {
            return;
        }

        val descriptionBlock = block(overviewDoc);
        val groupDiagram = new GroupDiagram(C4.of(sectionName, null));

        val table = table(overviewDoc);
        table.setTitle(String.format("Projects/Modules (%s)", sectionName));
        table.setAttribute("cols", "3a,5a", true);
        table.setAttribute("header-option", "", true);

        val headRow = headRow(table);

        cell(table, headRow, "Coordinates");
        cell(table, headRow, "Description");

        val projRoot = _Files.canonicalPath(projTree.getProjectDirectory())
                .orElseThrow(()->_Exceptions.unrecoverable("cannot resolve project root"));

        sectionModules
                .forEach(module -> {
                    if(mode.includeIndex()) {
                        gatherAdocFiles(module.getProjectDirectory(), onAdocFile);
                    }

                    val projPath = _Files.canonicalPath(module.getProjectDirectory()).get();
                    val projRelativePath =
                            Optional.ofNullable(
                                    _Strings.emptyToNull(
                                            _Files.toRelativePath(projRoot, projPath)))
                                    .orElse("/");

                    groupDiagram.collect(module);

                    val row = row(table);
                    cell(table, row, coordinates(module, projRelativePath));
                    cell(table, row, details(module, j2aContext));
                });

        descriptionBlock.setSource(groupDiagram.toAsciiDoc(sectionName));
    }

    private boolean matchesGroupId(final ProjectNode module, final String groupIdPattern) {
        val moduleCoords = module.getArtifactCoordinates();

        if(_Strings.isNullOrEmpty(moduleCoords.getGroupId())) {
            return false; // never match on missing data
        }
        if(_Strings.isNullOrEmpty(groupIdPattern)) {
            return true; // no groupIdPattern, always matches
        }
        if(groupIdPattern.equals(moduleCoords.getGroupId())) {
            return true; // exact match
        }
        if(groupIdPattern.endsWith(".*")) {
            val groupIdPrefix = groupIdPattern.substring(0, groupIdPattern.length()-2);
            if(groupIdPattern.contains(":")) {
                final String[] split = groupIdPrefix.split(":");
                val groupId = split[0];
                val artifactIdPrefix = split[1];
                if(groupId.equals(moduleCoords.getGroupId())) {
                    if(moduleCoords.getArtifactId().startsWith(artifactIdPrefix)) {
                        return true; // match on artifactId
                    }
                }
            } else {
                if(groupIdPrefix.equals(moduleCoords.getGroupId())) {
                    return true; // exact prefix match
                }
                if(moduleCoords.getGroupId().startsWith(groupIdPrefix+".")) {
                    return true; // prefix match
                }
            }
        }
        return false;
    }

    //    [source,yaml]
    //    ----
    //    Group: org.apache.causeway.commons
    //    Artifact: causeway-commons
    //    Type: jar
    //    Directory: /commons
    //    ----
    private String coordinates(final ProjectNode module, final String projRelativePath) {
        val coors = new StringBuilder();
        appendKeyValue(coors, "Group", module.getArtifactCoordinates().getGroupId());
        appendKeyValue(coors, "Artifact", module.getArtifactCoordinates().getArtifactId());
        appendKeyValue(coors, "Type", module.getArtifactCoordinates().getPackaging());
        appendKeyValue(coors, "Directory", projRelativePath.replaceAll("\\\\", "/"));
        return String.format("%s\n%s",
                module.getName(),
                AsciiDocFactory.toString(doc->
                    AsciiDocFactory.SourceFactory.yaml(doc, coors.toString(), null)));
    }

    private void appendKeyValue(final StringBuilder sb, final String key, final String value) {
        sb.append(String.format("%s: %s\n", key, value));
    }

    private String details(final ProjectNode module, final J2AdocContext j2aContext) {
        val description = sanitizeDescription(module.getDescription());
        val dependencyList = module.getDependencies()
                .stream()
                .map(Dependency::getArtifactCoordinates)
                .map(ArtifactCoordinates::toString)
                .map(ProjectDocModel::toAdocCompactListItem)
                .collect(Collectors.joining())
                .trim();
        val componentList = gatherSpringComponents(module.getProjectDirectory())
                .stream()
                .map(s->s.replace("org.apache.causeway.", "o.a.i."))
                .map(ProjectDocModel::toAdocCompactListItem)
                .collect(Collectors.joining())
                .trim();

        val indexEntriesCompactList = gatherGlobalDocIndexXrefs(module.getProjectDirectory(), j2aContext)
                .stream()
                .collect(Collectors.joining(", "))
                .trim();

        val sb = new StringBuilder();

        if(!description.isEmpty()) {
            sb.append(description).append("\n\n");
        }

        if(!componentList.isEmpty()) {
            sb.append(toAdocSection("Components", componentList));
        }

        if(!dependencyList.isEmpty()) {
            sb.append(toAdocSection("Dependencies", dependencyList));
        }

        if(!indexEntriesCompactList.isEmpty()) {
            sb.append(toAdocSection("Document Index Entries", indexEntriesCompactList));
        }

        return sb.toString();
    }

    static String sanitizeDescription(final String str) {
        return Arrays.stream(str.split("\n"))
                .map(String::trim)
                .reduce("", (x, y) -> x + (x.isEmpty() ? "" : "\n") + y);
    }

    private static String toAdocSection(final String title, final String content) {

        //XXX collapsible will be supported with antora 3
        //        return AsciiDocFactory.toString(doc->{
        //            val collapsibleBlock = AsciiDocFactory.collapsibleBlock(doc, content);
        //            collapsibleBlock.setTitle(title);
        //        });

        // render as Sidebar block for now
        return String.format(".%s\n****\n%s\n****\n\n", title, content);
    }

    private static String toAdocListItem(final String element) {
        return String.format("* %s\n", element);
    }

    private static String toAdocCompactListItem(final String element) {
        return String.format("%s +\n", element);
    }

    private SortedSet<String> gatherGlobalDocIndexXrefs(final File projDir, final J2AdocContext j2aContext) {

        val analyzerConfig = AnalyzerConfigFactory.maven(projDir, Language.JAVA).main();

        final SortedSet<String> docIndexXrefs = analyzerConfig.getSources(JAVA).stream()
        .filter(file->!file.getName().equals("module-info.java"))
        .flatMap(j2aContext::add)
        .map(unit->unit.getAsciiDocXref(j2aContext))
        .collect(Collectors.toCollection(TreeSet::new));

        return docIndexXrefs;
    }

    private SortedSet<String> gatherSpringComponents(final File projDir) {

        val analyzerConfig = AnalyzerConfigFactory.maven(projDir, Language.JAVA).main();

        val model = Model.from(analyzerConfig.getClasses()).read();

        SortedSet<String> components = model.getClasses()
                .stream()
//                .filter(CodeClasses::hasSourceFile)
//                .filter(CodeClasses.packageNameStartsWith("org.apache.causeway.applib."))
//                .peek(CodeClasses::log) //debug
                .filter(CodeClasses::isSpringStereoType)
                .map(CodeClass::getName)
                .collect(Collectors.toCollection(TreeSet::new));

        return components;
    }

    private void gatherAdocFiles(final File projDir, final Consumer<File> onFile) {

        val analyzerConfig = AnalyzerConfigFactory.maven(projDir, Language.ADOC).main();

        analyzerConfig.getSources(Language.ADOC)
                .stream()
                .forEach(onFile::accept);
    }


}


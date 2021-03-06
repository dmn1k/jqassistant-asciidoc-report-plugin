package org.jqassistant.contrib.plugin.asciidocreport;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.buschmais.jqassistant.core.analysis.api.rule.RuleException;
import com.buschmais.jqassistant.core.report.api.ReportException;
import com.buschmais.jqassistant.core.rule.api.source.FileRuleSource;
import com.buschmais.jqassistant.core.rule.api.source.RuleSource;
import com.buschmais.jqassistant.core.shared.io.ClasspathResource;

import org.junit.Before;
import org.junit.Test;

public class SourceFileMatcherTest {

    private File ruleDirectory;

    @Before
    public void setUp() {
        File classesDirectory = ClasspathResource.getFile(AsciidocReportPluginTest.class, "/");
        ruleDirectory = new File(classesDirectory, "jqassistant");
    }

    /**
     * Verifies that a given rule directory is scanned for adoc files.
     */
    @Test
    public void scanRuleDirectory() throws ReportException {
        SourceFileMatcher sourceFileMatcher = new SourceFileMatcher(ruleDirectory, "*.adoc", null);

        Map<File, List<File>> filesByBaseDir = sourceFileMatcher.match(Collections.emptySet());

        assertThat(filesByBaseDir.size(), equalTo(1));
        List<File> rulesDirectoryFiles = filesByBaseDir.get(ruleDirectory);
        assertThat(rulesDirectoryFiles, notNullValue());
        assertThat(rulesDirectoryFiles.size(), equalTo(1));
        assertThat(rulesDirectoryFiles.get(0).getName(), equalTo("index.adoc"));
    }

    /**
     * Verifies that the provided set of {@link RuleSource}s is used to detect the
     * "index.adoc" file.
     */
    @Test
    public void detectIndexFileFromRuleSources() throws RuleException {
        SourceFileMatcher sourceFileMatcher = new SourceFileMatcher(ruleDirectory, null, null);
        HashSet<RuleSource> ruleSources = new HashSet<>();
        File index = new File(ruleDirectory, "index.adoc");
        File other = new File(ruleDirectory, "additional-rules/other.adoc");
        ruleSources.add(new FileRuleSource(index));
        ruleSources.add(new FileRuleSource(other));

        Map<File, List<File>> filesByBaseDir = sourceFileMatcher.match(ruleSources);

        assertThat(filesByBaseDir.size(), equalTo(1));
        List<File> rulesDirectoryFiles = filesByBaseDir.get(ruleDirectory);
        assertThat(rulesDirectoryFiles, notNullValue());
        assertThat(rulesDirectoryFiles.size(), equalTo(1));
        assertThat(rulesDirectoryFiles.get(0), is(index));
    }

}

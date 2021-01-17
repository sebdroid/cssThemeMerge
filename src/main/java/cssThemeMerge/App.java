package cssThemeMerge;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;

import com.helger.commons.io.file.SimpleFileIO;
import com.helger.css.ECSSVersion;
import com.helger.css.decl.CSSFontFaceRule;
import com.helger.css.decl.CSSImportRule;
import com.helger.css.decl.CSSKeyframesRule;
import com.helger.css.decl.CSSMediaQuery;
import com.helger.css.decl.CSSMediaRule;
import com.helger.css.decl.CSSNamespaceRule;
import com.helger.css.decl.CSSPageRule;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CSSSupportsRule;
import com.helger.css.decl.CSSUnknownRule;
import com.helger.css.decl.CSSViewportRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.reader.CSSReader;
import com.helger.css.writer.CSSWriter;
import com.helger.css.writer.CSSWriterSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "cssThemeMerge", mixinStandardHelpOptions = true, version = "cssThemeMerge 1.0", description = "Removes redundant css to allow theme merging")
public class App implements Callable<Integer> {

    Logger log = LoggerFactory.getLogger(App.class);

    @Option(names = { "-d",
            "--default" }, description = "The default theme", defaultValue = "default.css", required = true)
    private String defaultThemeFile;

    @Option(names = { "-a",
            "--alternative" }, description = "The alternative theme", defaultValue = "alternative.css", required = true)
    private String alternativeThemeFile;

    @Option(names = { "-o", "--output" }, description = "The output file", defaultValue = "out.css", required = true)
    private String outputThemeFile;

    @Option(names = { "-t", "--theme" }, description = "Alternative colour scheme", defaultValue = "dark", required = true)
    private String colorScheme;

    @Option(names = { "-m", "--merge" }, description = "Merge color schemes", defaultValue = "true", required = true)
    private Boolean merge;

    @Override
    @SuppressWarnings("undefined")
    public Integer call() throws Exception {
        log.info("Attempting CSS parse");
        File defaultTheme = new File(defaultThemeFile);
        File alternativeTheme = new File(alternativeThemeFile);
        File outputFile = new File(outputThemeFile);

        CascadingStyleSheet defaultCSS = null;
        CascadingStyleSheet alternativeCSS = null;

        if (defaultTheme.exists()) {
            defaultCSS = CSSReader.readFromFile(defaultTheme, StandardCharsets.UTF_8, ECSSVersion.CSS30);
        } else {
            log.error("File not found");
            return 2;
        }
        if (alternativeTheme.exists()) {
            alternativeCSS = CSSReader.readFromFile(alternativeTheme, StandardCharsets.UTF_8, ECSSVersion.CSS30);
        } else {
            log.error("File not found");
            return 2;
        }

        if (defaultCSS == null || alternativeCSS == null) {
            log.error("Unable to parse CSS");
            return 1;
        }

        CascadingStyleSheet outputCSS = new CascadingStyleSheet();

        log.info("Finding necessary alternative rules");

        List<CSSImportRule> baseImportRules = alternativeCSS.getAllImportRules();
        baseImportRules.removeAll(defaultCSS.getAllImportRules());
        for (CSSImportRule rule : baseImportRules) {
            outputCSS.addImportRule(rule);
        }

        List<CSSNamespaceRule> baseNamespaceRules = alternativeCSS.getAllNamespaceRules();
        baseNamespaceRules.removeAll(defaultCSS.getAllNamespaceRules());
        for (CSSNamespaceRule rule : baseNamespaceRules) {
            outputCSS.addNamespaceRule(rule);
        }

        List<CSSStyleRule> baseStyleRules = alternativeCSS.getAllStyleRules();
        baseStyleRules.removeAll(defaultCSS.getAllStyleRules());
        List<CSSStyleRule> defaultStyleRules = defaultCSS.getAllStyleRules();
        defaultStyleRules.removeAll(alternativeCSS.getAllStyleRules());
        
        ListIterator<CSSStyleRule> baseStyleIterator = baseStyleRules.listIterator();
        ListIterator<CSSStyleRule> defaultStyleIterator = defaultStyleRules.listIterator();
        while(baseStyleIterator.hasNext()) {
            CSSStyleRule bRule = baseStyleIterator.next();
            CSSStyleRule dRule = defaultStyleIterator.next();
            bRule.jailbreak().m_aDeclarations.removeAll(dRule.getAllDeclarations());
            outputCSS.addRule(bRule);
        }

        List<CSSPageRule> basePageRules = alternativeCSS.getAllPageRules();
        basePageRules.removeAll(defaultCSS.getAllPageRules());
        for (CSSPageRule rule : basePageRules){
            outputCSS.addRule(rule);
        }
        
        List<CSSMediaRule> baseMediaRules = alternativeCSS.getAllMediaRules();
        baseMediaRules.removeAll(defaultCSS.getAllMediaRules());
        List<CSSMediaRule> defaultMediaRules = defaultCSS.getAllMediaRules();
        defaultMediaRules.removeAll(alternativeCSS.getAllMediaRules());
        
        ListIterator<CSSMediaRule> baseMediaIterator = baseMediaRules.listIterator();
        ListIterator<CSSMediaRule> defaultMediaIterator = defaultMediaRules.listIterator();
        while(baseMediaIterator.hasNext()) {
            CSSMediaRule bRule = baseMediaIterator.next();
            CSSMediaRule dRule = defaultMediaIterator.next();
            bRule.jailbreak().m_aRules.removeAll(dRule.getAllRules());
            outputCSS.addRule(bRule);
        }
        
        List<CSSFontFaceRule> baseFontfaceRules = alternativeCSS.getAllFontFaceRules();
        baseFontfaceRules.removeAll(defaultCSS.getAllFontFaceRules());
        for (CSSFontFaceRule rule : baseFontfaceRules){
            outputCSS.addRule(rule);
        }

        List<CSSKeyframesRule> baseKeyframesRules = alternativeCSS.getAllKeyframesRules();
        baseKeyframesRules.removeAll(defaultCSS.getAllKeyframesRules());
        for (CSSKeyframesRule rule : baseKeyframesRules){
            outputCSS.addRule(rule);
        }
        
        List<CSSViewportRule> baseViewportRules = alternativeCSS.getAllViewportRules();
        baseViewportRules.removeAll(defaultCSS.getAllViewportRules());
        for (CSSViewportRule rule : baseViewportRules){
            outputCSS.addRule(rule);
        }
        
        List<CSSSupportsRule> baseSupportRules = alternativeCSS.getAllSupportsRules();
        baseSupportRules.removeAll(defaultCSS.getAllSupportsRules());
        for (CSSSupportsRule rule : baseSupportRules){
            outputCSS.addRule(rule);
        }

        List<CSSUnknownRule> baseUnknownRules = alternativeCSS.getAllUnknownRules();
        baseUnknownRules.removeAll(defaultCSS.getAllUnknownRules());
        for (CSSUnknownRule rule : baseUnknownRules){
            outputCSS.addRule(rule);
        }

        if (merge){
            log.info("Merging CSS");

            CSSMediaRule alternateMode = new CSSMediaRule();
            alternateMode.jailbreak().m_aRules = outputCSS.getAllRules();
            alternateMode.addMediaQuery(new CSSMediaQuery(String.format("screen and (prefers-color-scheme: %s)", colorScheme)));
            defaultCSS.addRule(alternateMode);
            outputCSS = defaultCSS;
        }

        log.info("Attempting to save resultant CSS");

        try {
            CSSWriter Writer = new CSSWriter(new CSSWriterSettings(ECSSVersion.CSS30, false));
            Writer.setContentCharset(StandardCharsets.UTF_8.name());
            Writer.setHeaderText("This stylesheet was generated by cssThemeMerge\nMade by https://sebjo.se");
            SimpleFileIO.writeFile(outputFile, Writer.getCSSAsString(outputCSS), StandardCharsets.UTF_8);
        } catch (final Exception ex) {
            log.error("Unable to save final stylesheet");
        }

        log.info("Finished");

        return 0;
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}

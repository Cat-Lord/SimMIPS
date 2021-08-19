package sk.catheaven.codeEditor;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.reactfx.Subscription;
import sk.catheaven.events.ApplicationEvent;
import sk.catheaven.model.instructions.ArgumentType;
import sk.catheaven.model.instructions.Instruction;
import sk.catheaven.service.Assembler;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CodeEditor {
    private static String KEYWORD_PATTERN_STRING;
    private static final String NUMBER_PATTERN_STRING = "\\d+";
    private static final String COMMENT_PATTERN_STRING = Assembler.COMMENT_CHAR + ".*\\R";
    private static final String REGISTER_PATTERN_STRING = ArgumentType.getRegisterRegex();
    private static final String LABEL_PATTERN_STRING = ArgumentType.getLabelRegex();
    private static Pattern PATTERN;

    private final CodeArea codeArea;
    private final ExecutorService executor;
    private final Subscription cleanupWhenDone;

    public CodeEditor(Instruction[] instructionSet) {
        EventBus.getDefault().register(this);

        KEYWORD_PATTERN_STRING = "\\b(";

        String mnemoKeywords = Arrays.stream(instructionSet)
                                     .map(Instruction::getMnemo)
                                     .collect(Collectors.joining("|"));

        String keys = String.join("|", mnemoKeywords);
        KEYWORD_PATTERN_STRING = "\\b(" + keys + ")\\b";

        PATTERN = Pattern.compile(
                  "(?<KEYWORD>"  + KEYWORD_PATTERN_STRING  + ")" +
                 "|(?<NUMBER>"   + NUMBER_PATTERN_STRING   + ")" +
                 "|(?<COMMENT>"  + COMMENT_PATTERN_STRING  + ")" +
                 "|(?<REGISTER>" + REGISTER_PATTERN_STRING + ")" +
                 "|(?<LABEL>"    + LABEL_PATTERN_STRING    + ")"
        );

        codeArea = createCodeArea();
        // Following part of code was taken from https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsyncDemo.java
        executor = Executors.newSingleThreadExecutor();
        cleanupWhenDone = codeArea.multiPlainChanges()
                                  .successionEnds(java.time.Duration.ofMillis(500))
                                  .supplyTask(this::computeHighlightingAsync)
                                  .awaitLatest(codeArea.multiPlainChanges())
                                  .filterMap(t -> {
                                      if(t.isSuccess())
                                          return Optional.of(t.get());
                                      else
                                          return Optional.empty();
                                  })
                                  .subscribe(this::applyHighlighting);
    }

    // Method origin here: https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsyncDemo.java
    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<>() {
            @Override
            protected StyleSpans<Collection<String>> call() {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    // Method origin here: https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsyncDemo.java
    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        codeArea.setStyleSpans(0, highlighting);
    }

    // Method origin here: https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsyncDemo.java
    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD")  != null ? "keyword" :
                    matcher.group("NUMBER")   != null ? "number" :
                    matcher.group("COMMENT")  != null ? "comment" :
                    matcher.group("REGISTER") != null ? "register" :
                    matcher.group("LABEL")    != null ? "label" :
                    null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private CodeArea createCodeArea() {
        CodeArea codeArea = new CodeArea();
        IntFunction<Node> numberFactory = LineNumberFactory.get(codeArea);
        IntFunction<Node> arrowFactory = new ArrowFactory(codeArea.currentParagraphProperty());
        IntFunction<Node> graphicFactory = line -> {
            HBox innerHbox = new HBox(
                    numberFactory.apply(line),
                    arrowFactory.apply(line)
            );
            innerHbox.setSpacing(5);
            innerHbox.setAlignment(Pos.CENTER_LEFT);
            return innerHbox;
        };

        codeArea.setParagraphGraphicFactory(graphicFactory);
        return codeArea;
    }

    @Subscribe
    public void stop(ApplicationEvent event) {
        cleanupWhenDone.unsubscribe();
        executor.shutdown();
    }

    public CodeArea getCodeArea() {
        return codeArea;
    }
}

package sample;


import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;
import org.reactfx.value.Val;
import org.reactfx.value.Var;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javafx.application.Application.setUserAgentStylesheet;
import static org.reactfx.EventStreams.nonNullValuesOf;

/**
 *
 * @author TARUN
 */
public class Interviewee implements Initializable {


    private Stage stage;
    BoundsPopup AutoComplete;
    double caretXOffset = 0;
    double caretYOffset = 0;
    String currentWord = "";

    @FXML
    private CodeArea editor;
    ObservableList<String> availableThemes = FXCollections.observableArrayList("Dark", "Light");
    private static final String[] KEYWORDSJAVA = new String[]{"abstract", "assert", "boolean", "break",
        "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else",
        "enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import",
        "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public",
        "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "void", "volatile", "while"
    };

    private static final String[] KEYWORDSCPP = {"auto", "const", "double", "float", "int", "short",
        "struct", "unsigned", "break", "continue", "else", "for", "long", "signed",
        "switch", "void", "case", "default", "enum", "goto", "register", "sizeof",
        "typedef", "volatile", "char", "do", "extern", "if", "return", "static",
        "union", "while", "asm", "dynamic_cast", "namespace", "reinterpret_cast", "try",
        "bool", "explicit", "new", "static_cast", "typeid", "catch", "false", "operator",
        "template", "typename", "class", "friend", "private", "this", "using", "const_cast",
        "inline", "public", "throw", "virtual", "delete", "mutable", "protected", "true", "wchar_t"};

    private static final String[] KEYWORDSC = {"auto", "break", "case", "char", "const", "continue", "default",
        "do", "double", "else", "enum", "extern", "float", "for", "goto", "if", "int", "long", "register",
        "return", "short", "signed", "sizeof", "static", "struct", "switch", "typedef", "union",
        "unsigned", "void", "volatile", "while"
    };

    private static final String[] KEYWORDSPYTHON = {"False", "class", "finally", "is", "return", "None", "continue",
        "for", "lambda", "try", "True", "def", "from", "nonlocal", "while", "and", "del", "global", "not", "with", "as",
        "elif", "if", "or", "yield", "assert", "else", "import", "pass", "break", "except", "in", "raise"
    };

    private static final String KEYWORD_PATTERN_JAVA = "\\b(" + String.join("|", KEYWORDSJAVA) + ")\\b";
    private static final String KEYWORD_PATTERN_CPP = "\\b(" + String.join("|", KEYWORDSCPP) + ")\\b";
    private static final String KEYWORD_PATTERN_C = "\\b(" + String.join("|", KEYWORDSC) + ")\\b";
    private static final String KEYWORD_PATTERN_PYTHON = "\\b(" + String.join("|", KEYWORDSPYTHON) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN_JAVA = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN_JAVA + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    private static final Pattern PATTERN_CPP = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN_CPP + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    private static final Pattern PATTERN_C = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN_C + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    private static final Pattern PATTERN_PYTHON = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN_PYTHON + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    public void setStageAndSetupListeners(Stage stage) {
        this.stage = stage;
    }

    private class BoundsPopup extends Popup {

        /**
         * Indicates whether popup should still be shown even when its item
         * (caret/selection) is outside viewport
         */
        private final Var<Boolean> showWhenItemOutsideViewport = Var.newSimpleVar(true);

        public final EventStream<Boolean> outsideViewportValues() {
            return showWhenItemOutsideViewport.values();
        }

        public final void invertViewportOption() {
            showWhenItemOutsideViewport.setValue(!showWhenItemOutsideViewport.getValue());
        }

        /**
         * Indicates whether popup has been hidden since its item
         * (caret/selection) is outside viewport and should be shown when that
         * item becomes visible again
         */
        private final Var<Boolean> hideTemporarily = Var.newSimpleVar(false);

        public final boolean isHiddenTemporarily() {
            return hideTemporarily.getValue();
        }

        public final void setHideTemporarily(boolean value) {
            hideTemporarily.setValue(value);
        }

        public final void invertVisibility() {
            if (isShowing()) {
                hide();
            } else {
                show(stage);
            }
        }

        private final VBox vbox;
        private final ListView<String> button;

        public final void setText(String text) {
            button.getItems().clear();
            button.getItems().add("First Item");
            button.getItems().add("Second Item");
            button.getItems().add("Third Item");
        }

        public final void setText(List<String> words) {
            button.getItems().clear();
            int ROW_HEIGHT = 24;
            for (String s : words) {
                button.getItems().add(s);
            }
            button.setPrefHeight(words.size() * ROW_HEIGHT + 2);
        }
        EventHandler<KeyEvent> Select_autoComplete = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    String selectedItem = button.getSelectionModel().getSelectedItem();
                    if (currentWord.length() <= selectedItem.length()) {
                        selectedItem = selectedItem.substring(currentWord.length());
                        editor.insertText(editor.getCaretPosition(), selectedItem);
                    }
                    AutoComplete.invertVisibility();
                    event.consume();
                }
            }
        };
        EventHandler<MouseEvent> Select_autoComplete_Click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectedItem = button.getSelectionModel().getSelectedItem();
                editor.insertText(editor.getCaretPosition(), selectedItem);
                AutoComplete.invertVisibility();
                event.consume();
            }
        };

        BoundsPopup(String buttonText) {
            super();
            button = new ListView<String>();
            button.getItems().add("");
            button.addEventFilter(KeyEvent.KEY_PRESSED, Select_autoComplete);
            button.addEventFilter(MouseEvent.MOUSE_CLICKED, Select_autoComplete_Click);
            vbox = new VBox(button);
            vbox.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, null, null)));
            vbox.setPadding(new Insets(5));
            getContent().add(vbox);
        }
    }

    class ArrowFactory implements IntFunction<Node> {

        private final ObservableValue<Integer> shownLine;

        ArrowFactory(ObservableValue<Integer> shownLine) {
            this.shownLine = shownLine;
        }

        @Override
        public Node apply(int lineNumber) {
            Polygon triangle = new Polygon(0.0, 0.0, 10.0, 5.0, 0.0, 10.0);
            triangle.setFill(Color.GREEN);
            ObservableValue<Boolean> visible = Val.map(
                    shownLine,
                    sl -> sl == lineNumber);

            triangle.visibleProperty().bind(Val.conditionOnShowing(visible, triangle));
            return triangle;
        }
    }
    int line_number = 0, flip = 0;


    static int language = 0;

    private static int editorStatus = 0;



    @FXML
    void onclickPYTHON(MouseEvent event) {

    }


    //Creating EventHandler Object
    EventHandler<KeyEvent> Tab_Filter = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.TAB) {
                String s = "        ";//Tab size=8
                editor.insertText(editor.getCaretPosition(), s);
                event.consume();
            }
        }
    };

    Pattern whiteSpace = Pattern.compile("^\\s+");
    //Creating EventHandler Object   
    EventHandler<KeyEvent> Indentation_filter = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER) {
                Matcher m = whiteSpace.matcher(editor.getParagraph(editor.getCurrentParagraph()).getSegments().get(0));
                if (m.find()) {
                    System.out.println("enter pressed");
                    Platform.runLater(() -> editor.insertText(editor.getCaretPosition(), System.lineSeparator() + m.group()));
                }
                event.consume();
            }
        }
    };
    EventHandler<KeyEvent> AutoComplete_filter = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.isControlDown() && event.getCode() == KeyCode.SPACE && event.getCharacter() != " ") {
                int nm = editor.caretColumnProperty().getValue();
                System.out.println("AutoComplete\n" + "line number= " + nm + " " + editor.getCaretColumn());
                Trie trie = new Trie(editor.getText());
                List<String> words = new ArrayList<String>();
                words = trie.getWordsForPrefix(currentWord);
                for (String word : words) {
                    System.out.println(word);
                }
                AutoComplete.setText(words);
                AutoComplete.show(stage);
                event.consume();
            }
        }
    };


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        IntFunction<Node> numberFactory = LineNumberFactory.get(editor);
        IntFunction<Node> arrowFactory = new ArrowFactory(editor.currentParagraphProperty());
        IntFunction<Node> graphicFactory = line -> {
            HBox hbox = new HBox(
                    numberFactory.apply(line),
                    arrowFactory.apply(line));
            hbox.setAlignment(Pos.CENTER_LEFT);
            return hbox;
        };
        editor.setParagraphGraphicFactory(graphicFactory);
        Subscription cleanupWhenNoLongerNeedIt = editor.multiPlainChanges().successionEnds(Duration.ofMillis(500)).subscribe(ignore -> editor.setStyleSpans(0, computeHighlighting(editor.getText())));
        editor.setStyle("-fx-font-size: 20px;");
        editor.setWrapText(true);
        editor.addEventFilter(KeyEvent.KEY_PRESSED, Tab_Filter);
        editor.addEventFilter(KeyEvent.KEY_PRESSED, Indentation_filter);
        editor.addEventFilter(KeyEvent.KEY_PRESSED, AutoComplete_filter);
        AutoComplete = new BoundsPopup("I am the caret popup button!");
        EventStream<Optional<Bounds>> caretBounds = nonNullValuesOf(editor.caretBoundsProperty());
        Subscription caretPopupSub = EventStreams.combine(caretBounds, AutoComplete.outsideViewportValues())
                .subscribe(tuple3 -> {
                    Optional<Bounds> opt = tuple3._1;
                    boolean showPopupWhenCaretOutside = tuple3._2;

                    if (opt.isPresent()) {
                        Bounds b = opt.get();
                        AutoComplete.setX(b.getMaxX() + caretXOffset);
                        AutoComplete.setY(b.getMaxY() + caretYOffset);

                        if (AutoComplete.isHiddenTemporarily()) {
                            AutoComplete.show(stage);
                            AutoComplete.setHideTemporarily(false);
                        }

                    } else {
                        if (!showPopupWhenCaretOutside) {
                            AutoComplete.hide();
                            AutoComplete.setHideTemporarily(true);
                        }
                    }
                });
        editor.caretPositionProperty().addListener((obs, oldPosition, newPosition) -> {
            String text = editor.getText().substring(0, newPosition.intValue());
            int index;
            for (index = text.length() - 1; index >= 0 && !Character.isWhitespace(text.charAt(index)); index--);
            String prefix = text.substring(index + 1, text.length());
            currentWord = prefix;
        });
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN_C.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass
                    = matcher.group("KEYWORD") != null ? "keyword"
                    : matcher.group("PAREN") != null ? "paren"
                    : matcher.group("BRACE") != null ? "brace"
                    : matcher.group("BRACKET") != null ? "bracket"
                    : matcher.group("SEMICOLON") != null ? "semicolon"
                    : matcher.group("STRING") != null ? "string"
                    : matcher.group("COMMENT") != null ? "comment"
                    : null;
            /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}

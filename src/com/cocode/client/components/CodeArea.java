package com.cocode.client.components;

import com.cocode.client.utils.ColorPack;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

public class CodeArea extends RTextScrollPane {
    private RSyntaxTextArea textArea;
    private Timer keyTimer = new Timer();

    public CodeArea() {
        this(new RSyntaxTextArea());
    }

    public CodeArea(RSyntaxTextArea textArea) {
        super(textArea);

        this.textArea = textArea;

        setBorder(null);

        Gutter gutter = RSyntaxUtilities.getGutter(textArea);

        gutter.setBackground(ColorPack.BG);
        gutter.setForeground(Color.WHITE);
        gutter.setBorderColor(new Color(103, 110, 149));

        textArea.addKeyListener(new TypingListener());
        textArea.setCodeFoldingEnabled(true);
        textArea.setAntiAliasingEnabled(true);
        textArea.setBracketMatchingEnabled(true);
        textArea.setBackground(new Color(41, 45, 62));
        textArea.setForeground(new Color(255, 255, 255));
        textArea.setCurrentLineHighlightColor(new Color(32, 35, 49));
        textArea.setSelectionColor(new Color(60, 67, 95));
        textArea.setSelectedTextColor(new Color(255, 255, 255));
        textArea.setMatchedBracketBGColor(new Color(60, 67, 95));
        textArea.setCaretColor(ColorPack.FUNCTION);
        textArea.setSecondaryLanguageBackground(1, new Color(41, 45, 62));
        textArea.setSecondaryLanguageBackground(2, new Color(41, 45, 62));
        textArea.setSecondaryLanguageBackground(3, new Color(41, 45, 62));

        SyntaxScheme scheme = textArea.getSyntaxScheme();
        scheme.getStyle(Token.COMMENT_DOCUMENTATION).foreground = ColorPack.COMMENT;
        scheme.getStyle(Token.COMMENT_EOL).foreground = ColorPack.COMMENT;
        scheme.getStyle(Token.COMMENT_KEYWORD).foreground = ColorPack.COMMENT;
        scheme.getStyle(Token.COMMENT_MARKUP).foreground = ColorPack.COMMENT;
        scheme.getStyle(Token.COMMENT_MULTILINE).foreground = ColorPack.COMMENT;
        scheme.getStyle(Token.DATA_TYPE).foreground = ColorPack.DATA_TYPE;
        scheme.getStyle(Token.ERROR_CHAR).foreground = ColorPack._UNF;
        scheme.getStyle(Token.ERROR_STRING_DOUBLE).foreground = ColorPack._UNF;
        scheme.getStyle(Token.FUNCTION).foreground = ColorPack.FUNCTION;
        scheme.getStyle(Token.IDENTIFIER).foreground = ColorPack.IDENTIFIER;
        scheme.getStyle(Token.LITERAL_BOOLEAN).foreground = ColorPack.NUM;
        scheme.getStyle(Token.LITERAL_CHAR).foreground = ColorPack.STRING;
        scheme.getStyle(Token.LITERAL_NUMBER_DECIMAL_INT).foreground = ColorPack.NUM;
        scheme.getStyle(Token.LITERAL_NUMBER_FLOAT).foreground = ColorPack.NUM;
        scheme.getStyle(Token.LITERAL_NUMBER_HEXADECIMAL).foreground = ColorPack.NUM;
        scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = ColorPack.STRING;
        scheme.getStyle(Token.NULL).foreground = ColorPack.STRING;
        scheme.getStyle(Token.RESERVED_WORD).foreground = ColorPack.DATA_TYPE;
        scheme.getStyle(Token.RESERVED_WORD_2).foreground = ColorPack.DATA_TYPE;
        scheme.getStyle(Token.OPERATOR).foreground = ColorPack.OPERATOR;
        scheme.getStyle(Token.SEPARATOR).foreground = ColorPack.OPERATOR;
        scheme.getStyle(Token.MARKUP_TAG_DELIMITER).foreground = ColorPack.OPERATOR;
        scheme.getStyle(Token.MARKUP_TAG_NAME).foreground = ColorPack.NUM;
        scheme.getStyle(Token.MARKUP_TAG_ATTRIBUTE).foreground = ColorPack.DATA_TYPE;
        scheme.getStyle(Token.MARKUP_TAG_ATTRIBUTE_VALUE).foreground = ColorPack.STRING;
        textArea.setSyntaxScheme(scheme);

        textArea.revalidate();
    }

    public void setSyntaxEditingStyle(String styleKey) {
        textArea.setSyntaxEditingStyle(styleKey);
    }

    public void setText(String text) {
        textArea.setText(text);
    }

    private class TypingListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
//            System.out.println("stop");
//            keyTimer.cancel();
//            System.out.println("schedule");
//            task();


        }
    }

    private void task() {
        TimerTask keyTimerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println(textArea.getText());
            }
        };

        keyTimer = new Timer();
        keyTimer.schedule(keyTimerTask, 300);
    }
}

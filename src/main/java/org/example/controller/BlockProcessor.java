package org.example.controller;

import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author stifell on 13.02.2025
 */
public class BlockProcessor {
    private final File file;
    private static final Pattern DUPLICATE_PATTERN = Pattern.compile(
            "\\$\\{DUPLICATE\\((\\d+)(?:,\\s*(\\w+))?\\)\\[(.*?)\\]\\}",
            Pattern.DOTALL
    );

    public BlockProcessor(File file) {
        this.file = file;
    }

    public void processBlockFile(String newFilePath) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(file))) {
            // Обработка параграфов документа – итерируемся по копии списка, поскольку могут быть ситуации,
            // когда необходимо добавить параграфы
            List<XWPFParagraph> paragraphs = new ArrayList<>(doc.getParagraphs());
            for (XWPFParagraph p : paragraphs) {
                processContainer(p);
            }
            // Обработка таблиц
            for (XWPFTable table : doc.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        // Итерируемся по копии списка параграфов ячейки
                        List<XWPFParagraph> cellParagraphs = new ArrayList<>(cell.getParagraphs());
                        for (XWPFParagraph p : cellParagraphs) {
                            processContainer(p);
                        }
                    }
                }
            }
            saveFile(newFilePath, doc);
        }
    }

    private void processContainer(XWPFParagraph paragraph) {
        String text = paragraph.getText();
        Matcher matcher = DUPLICATE_PATTERN.matcher(text);
        if (!matcher.find()) return;

        // Извлекаем параметры команды
        int copies = Integer.parseInt(matcher.group(1));
        String mode = matcher.group(2);
        if (mode == null) {
            mode = "newline"; // режим по умолчанию
        }
        String content = matcher.group(3);
        int contentStart = text.indexOf(content, matcher.start());
        int contentEnd = contentStart + content.length();
        // Находим оригинальные стили
        List<RunStyle> styles = extractStyles(paragraph, contentStart, contentEnd);
        // Удаляем всю оригинальную команду
        removeCommandRuns(paragraph, matcher.start(), matcher.end());
        // Выбор режима дублирования через switch - можно добавлять новые режимы
        switch (mode) {
            case "space":
                insertDuplicatedContentSpace(paragraph, copies, styles);
                break;
            case "newline":
            default:
                insertDuplicatedContent(paragraph, copies, styles);
                break;
        }
    }

    private void removeCommandRuns(XWPFParagraph p, int cmdStart, int cmdEnd) {
        List<XWPFRun> runsToRemove = new ArrayList<>();
        int pos = 0;
        for (XWPFRun run : p.getRuns()) {
            String runText = run.getText(0);
            if (runText == null) continue;
            int runStart = pos;
            int runEnd = pos + runText.length();
            if (runEnd > cmdStart && runStart < cmdEnd) {
                runsToRemove.add(run);
            }
            pos += runText.length();
        }
        // Удаляем все найденные Runs
        runsToRemove.forEach(r -> p.removeRun(p.getRuns().indexOf(r)));
    }

    // Режим newline: все копии выделяются новыми параграфами для разделения новой строки
    private void insertDuplicatedContent(XWPFParagraph p, int copies, List<RunStyle> styles) {
        IBody body = p.getBody();
        // Сохраняем исходное форматирование параграфа
        ParagraphAlignment alignment = p.getAlignment();
        int indentFirstLine = p.getIndentationFirstLine();
        int indentLeft = p.getIndentationLeft();
        int indentRight = p.getIndentationRight();
        int spacingBefore = p.getSpacingBefore();
        int spacingAfter = p.getSpacingAfter();
        double spacingBetween = p.getSpacingBetween();
        String style = p.getStyle();
        for (int i = 0; i < copies; i++) {
            XWPFParagraph currentParagraph;
            if (i == 0) {
                currentParagraph = p;
            } else {
                currentParagraph = body.insertNewParagraph(p.getCTP().newCursor());
            }
            // Восстанавливаем сохраненное форматирование
            currentParagraph.setAlignment(alignment);
            currentParagraph.setIndentationFirstLine(indentFirstLine);
            currentParagraph.setIndentationLeft(indentLeft);
            currentParagraph.setIndentationRight(indentRight);
            currentParagraph.setSpacingBefore(spacingBefore);
            currentParagraph.setSpacingAfter(spacingAfter);
            currentParagraph.setSpacingBetween(spacingBetween);
            currentParagraph.setStyle(style);

            // Вставляем текст с сохранением стилей для каждого Run
            for (RunStyle runStyle : styles) {
                XWPFRun newRun = currentParagraph.createRun();
                applyStyle(newRun, runStyle);
                newRun.setText(runStyle.text);
            }
        }
    }

    // Режим space: все копии вставляются в один параграф, разделённые пробелом
    private void insertDuplicatedContentSpace(XWPFParagraph p, int copies, List<RunStyle> styles) {
        for (int i = 0; i < copies; i++) {
            if (i > 0) {
                XWPFRun spaceRun = p.createRun();
                spaceRun.setText(" ");
            }
            for (RunStyle runStyle : styles) {
                XWPFRun newRun = p.createRun();
                applyStyle(newRun, runStyle);
                newRun.setText(runStyle.text);
            }
        }
    }

    private List<RunStyle> extractStyles(XWPFParagraph p, int start, int end) {
        List<RunStyle> styles = new ArrayList<>();
        XWPFDocument doc = p.getDocument();
        int pos = 0;
        for (XWPFRun run : p.getRuns()) {
            String runText = run.getText(0);
            if (runText == null) continue;
            int runStart = pos;
            int runEnd = pos + runText.length();
            pos = runEnd;
            if (runEnd < start) continue;
            if (runStart > end) break;
            // Вычисляем пересечение с целевой областью
            int intersectStart = Math.max(runStart, start);
            int intersectEnd = Math.min(runEnd, end);
            if (intersectStart >= intersectEnd) continue;
            String part = runText.substring(
                    intersectStart - runStart,
                    intersectEnd - runStart
            );
            // Иногда возникает ошибка: getFontSize() возвращает -1
            int fontSize = run.getFontSize();
            if (fontSize == -1) {
                // Пробуем взять размер по умолчанию из стилей документа
                if (doc.getStyles() != null && doc.getStyles().getDefaultRunStyle() != null) {
                    int defaultFontSize = doc.getStyles().getDefaultRunStyle().getFontSize();
                    fontSize = (defaultFontSize != -1) ? defaultFontSize : 12;
                } else {
                    fontSize = 12; // значение по умолчанию
                }
            }
            styles.add(new RunStyle(
                    part,
                    run.isBold(),
                    run.isItalic(),
                    run.getFontFamily(),
                    fontSize,
                    run.getColor(),
                    run.getUnderline()
            ));
        }
        return styles;
    }

    private void saveFile(String filePath, XWPFDocument doc) throws IOException {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            doc.write(out);
        }
    }

    private void applyStyle(XWPFRun run, RunStyle style) {
        run.setBold(style.bold);
        run.setItalic(style.italic);
        run.setFontFamily(style.fontFamily);
        run.setFontSize(style.fontSize);
        run.setColor(style.color);
        run.setUnderline(style.underline);
        // Можно добавить другие свойства стиля по необходимости
    }

    private class RunStyle {
        final String text;
        final boolean bold;
        final boolean italic;
        final String fontFamily;
        final int fontSize;
        final String color;
        final UnderlinePatterns underline;

        RunStyle(String text, boolean bold, boolean italic, String fontFamily, int fontSize, String color, UnderlinePatterns underline) {
            this.text = text;
            this.bold = bold;
            this.italic = italic;
            this.fontFamily = fontFamily;
            this.fontSize = fontSize;
            this.color = color;
            this.underline = underline;
        }
    }
}
package org.example;

import org.apache.poi.xwpf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.List;
import java.util.HashMap;

/**
 * Класс WordDOCX предоставляет функционал для изменения содержимого документа типа .docx.
 * Он позволяет заменить определенные текстовые метки в документе на указанные значения.
 * Для работы с документом используются библиотеки Apache POI.
 */
public class WordDOCX {
    private final TagMap tagMap;
    WordDOCX(TagMap tagMap){
        this.tagMap = tagMap;
    }

    /**
     * Метод changeFile() выполняет замену текста в документе и сохранение изменений.
     * @throws IOException если возникают проблемы при чтении или записи файла.
     */
    public void changeFile() throws IOException {
        // извлечение пути к файлу test.doc из ресурсов класспути и сохранение его в переменной filePath
        String fileUrl = getClass().getClassLoader().getResource("test.docx").getPath();
        // декодируем путь к файлу, чтобы обработать специальные символы, такие как пробелы или кириллические символы
        String filePath = URLDecoder.decode(fileUrl, "UTF-8");
        // Создаем новый путь к файлу
        String newFilePath = filePath.replace("test.doc", "new_test.doc");
        // inputStream - входной поток данных, FileInputStream - чтения байтов из файла
        try (InputStream inputStream = new FileInputStream(filePath)){
            // создание объект для работы с .docx
            XWPFDocument doc = new XWPFDocument(inputStream);
            // замена текста в docx и сохранение изменений
            for(HashMap.Entry<String, String> entry: tagMap.getTagMap().entrySet()) {
                // получение ключа
                String tag = entry.getKey();
                // получение значения
                String replaceWord = entry.getValue();
                doc = replaceText(doc, tag, replaceWord);
            }
            saveFile(newFilePath, doc);
            doc.close();
        }
    }

    /**
     * Метод replaceText() выполняет замену указанного тега на соответствующее слово в документе.
     * @param doc объект XWPFDocument, представляющий документ, в котором нужно выполнить замену.
     * @param tag тег, который требуется заменить.
     * @param replaceWord слово, на которое нужно заменить тег.
     * @return XWPFDocument с выполненной заменой.
     */
    private XWPFDocument replaceText(XWPFDocument doc, String tag, String replaceWord) {
        // обработка всех абзацев в документе
        iterationAllParagraphs(doc.getParagraphs(), tag, replaceWord);
        // при наличии таблиц
        for (XWPFTable table : doc.getTables()) {
            // обработка каждой строки в таблице
            for (XWPFTableRow row : table.getRows()) {
                // обработка каждой ячейки в строке
                for (XWPFTableCell cell : row.getTableCells()) {
                    // обработка всех абзацев в ячейке
                    iterationAllParagraphs(cell.getParagraphs(), tag, replaceWord);
                }
            }
        }
        return doc;
    }

    /**
     * Метод iterationAllParagraphs() выполняет итерацию по всем абзацам в списке абзацев документа
     * и передает каждый абзац в метод iterateThroughRuns() для замены текста.
     * @param paragraphs список абзацев, которые нужно обработать.
     * @param tag тег, который требуется заменить.
     * @param replaceWord слово, на которое нужно заменить тег.
     */
    private void iterationAllParagraphs(List<XWPFParagraph> paragraphs, String tag, String replaceWord){
        // итерация по всем абзацам в списке
        paragraphs.forEach(paragraph -> iterateThroughRuns(paragraph, tag, replaceWord));
    }

    /**
     * Метод iterateThroughRuns() выполняет итерацию по всем объектам XWPFRun в абзаце и проверяет наличие
     * указанного тега в тексте каждого объекта. При обнаружении тега выполняется его замена.
     * @param paragraph абзац, в котором нужно проверить и заменить теги.
     * @param tag тег, который требуется заменить.
     * @param replaceWord слово, на которое нужно заменить тег.
     */
    private void iterateThroughRuns(XWPFParagraph paragraph, String tag, String replaceWord){
        List<XWPFRun> runs = paragraph.getRuns();
        // проверка наличия объектов XWPFRun в абзаце
        if (runs == null){
            return;
        }
        // итерация по всем объектам в абзаце
        for (XWPFRun run : runs){
            String paragraphText = run.getText(0);
            // проверка наличия текста в объекте XWPFRun
            if (paragraphText != null){
                // проверка наличия тега в тексте
                if (paragraphText.contains(tag)) {
                    // Замена тега на соответствующее слово
                    replaceWord(run, paragraphText, tag, replaceWord);
                } else if (paragraphText.contains("${") && !tagMap.getTagMap().containsKey(paragraphText)) {
                    // замена переменной в первом случае разбиения
                    replaceVariableFirstCase(paragraph, run, tag, replaceWord);
                } else if (paragraphText.equals("$")) {
                    // замена переменной во втором случае разбиения
                    replaceVariableSecondCase(paragraph, run, tag, replaceWord);
                }
            }
        }
    }

    /**
     * Метод replaceVariableFirstCase() заменяет переменную в первом случае разбиения строки,
     * когда переменная начинается в одном объекте XWPFRun и заканчивается в другом.
     * Начало: ${ -> name -> }
     * Итог: tag -> "" -> ""
     * @param paragraph абзац, содержащий объекты XWPFRun, в которых находится переменная.
     * @param run объект XWPFRun, содержащий начало переменной.
     * @param tag тег, который требуется заменить.
     * @param replaceWord слово, на которое нужно заменить тег.
     */
    private void replaceVariableFirstCase(XWPFParagraph paragraph, XWPFRun run, String tag, String replaceWord){
        List<XWPFRun> runs = paragraph.getRuns();
        // получаем индекс текущего объекта
        int i = runs.indexOf(run);
        // получаем следующий объект после текущего
        XWPFRun tagWordRun = runs.get(i+1);
        // получаем текст следующего объекта
        String tagWordParagraphText = tagWordRun.getText(0);
        // проверка, что переменная содержится в следующем объекте XWPFRun
        if (tag.contains(tagWordParagraphText)){
            // проверка, что переменная не заканчивается в текущем объекте - временно
            if (!tagWordParagraphText.contains("}")){
                // замена тега на соответствующее слово в текущем объекте XWPFRun
                replaceWord(run, run.getText(0), "${", replaceWord);
                // замена закрывающей скобки в следующем объекте XWPFRun
                replaceTextAndRemoveBrace(runs.get(i+2), runs.get(i+2).getText(0), "}");
                // удаление переменной из следующего объекта XWPFRun
                replaceTextAndRemoveBrace(tagWordRun, tagWordParagraphText, tagWordParagraphText);
            } else {
                System.err.println("В файле .docx ошибка: нашлось разбиение по типу \"tag}\"");
            }
        }
    }

    /**
     * Метод replaceVariableSecondCase() заменяет переменную во втором случае разбиения строки,
     * когда переменная начинается в одном объекте XWPFRun, а заканчивается в другом, с промежуточной '{'.
     * Начало: $ -> { -> name -> }
     * Итог: tag -> "" -> "" -> ""
     * @param paragraph абзац, содержащий объекты XWPFRun, в которых находится переменная.
     * @param run текущий объект XWPFRun, содержащий начало переменной.
     * @param tag тег, который требуется заменить.
     * @param replaceWord слово, на которое нужно заменить тег.
     */
    private void replaceVariableSecondCase(XWPFParagraph paragraph, XWPFRun run, String tag, String replaceWord){
        List<XWPFRun> runs = paragraph.getRuns();
        // получаем индекс текущего объекта
        int i = runs.indexOf(run);
        // получаем следующий объект после текущего
        XWPFRun nextRun = runs.get(i+1);
        // получаем текст следующего объекта
        String nextParagraphText = nextRun.getText(0);
        // проверка, что следующий объект содержит символ '{'
        if (nextParagraphText.equals("{")){
            // получаем объект переменной
            XWPFRun tagWordRun = runs.get(i+2);
            // получаем текст
            String tagWordParagraphText = tagWordRun.getText(0);
            // проверка, что переменная содержится в объект переменной
            if (tag.contains(tagWordParagraphText)){
                // проверка, что переменная не заканчивается в текущем объекте - временно
                if (!tagWordParagraphText.contains("}")){
                    // замена тега на соответствующее слово в текущем объекте
                    replaceWord(run, run.getText(0), "$", replaceWord);
                    // удаление закрывающей скобки в следующем объекте
                    replaceTextAndRemoveBrace(runs.get(i+3), runs.get(i+3).getText(0), "}");
                    // удаление переменной из следующего объекта
                    replaceTextAndRemoveBrace(tagWordRun, tagWordParagraphText, tagWordParagraphText);
                    // удаление символа '{' из следующего объекта
                    replaceTextAndRemoveBrace(nextRun, nextParagraphText, "{");
                } else {
                    System.err.println("В файле .docx ошибка: нашлось разбиение по типу \"tag}\"");
                }
            }
        }
    }

    /**
     * Заменяет указанное слово в тексте объекта XWPFRun на другое слово.
     * @param run объект XWPFRun, содержащий текст, который нужно изменить.
     * @param text текст, который требуется изменить.
     * @param word слово, которое нужно заменить.
     * @param replaceWord слово, на которое нужно заменить указанное слово.
     */
    private void replaceWord(XWPFRun run, String text, String word, String replaceWord){
        // заменяет указанное слово в тексте на другое слово
        String updatedText = text.replace(word, replaceWord);
        // устанавливает обновленный текст в объекте
        run.setText(updatedText, 0);
    }

    /**
     * Заменяет указанный текст в объекте XWPFRun на пустую строку.
     * @param run объект XWPFRun, содержащий текст, который нужно изменить.
     * @param text текст, который требуется изменить.
     * @param toRemove текст, который нужно удалить из исходного текста.
     */
    private void replaceTextAndRemoveBrace(XWPFRun run, String text, String toRemove) {
        // заменяет указанный текст на пустую строку
        String updatedText = text.replace(toRemove, "");
        // устанавливает обновленный текст в объекте
        run.setText(updatedText, 0);
    }

    /**
     * Метод saveFile() сохраняет содержимое документа в файле.
     * @param filePath путь к файлу, в который нужно сохранить документ
     * @param doc объект XWPFDocument, содержащий измененное содержимое документа
     * @throws IOException если возникает ошибка ввода-вывода при записи файла
     */
    private void saveFile(String filePath, XWPFDocument doc) throws IOException {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            // записывание содержимого документа в файл, который был открыт для записи
            doc.write(out);
        }
    }
}
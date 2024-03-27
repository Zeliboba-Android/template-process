package org.example;

import org.apache.poi.xwpf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

public class WordDOCX {
    public void changeFile(TagMap tagMap) throws IOException {
        // извлечение пути к файлу test.doc из ресурсов класспути и сохранение его в переменной filePath
        String fileUrl = getClass().getClassLoader().getResource("test.docx").getPath();
        // декодируем путь к файлу, чтобы обработать специальные символы, такие как пробелы или кириллические символы
        String filePath = URLDecoder.decode(fileUrl, "UTF-8");
        // inputStream - входной поток данных, FileInputStream - чтения байтов из файла
        try (InputStream inputStream = new FileInputStream(filePath)){
            // создание объект для работы с .docx
            XWPFDocument doc = new XWPFDocument(inputStream);
            // замена текста в docx и сохранение изменений
            for(Map.Entry<String, String> entry: tagMap.getTagMap().entrySet()) {
                // получение ключа
                String tag = entry.getKey();
                // получение значения
                String replaceWord = entry.getValue();
                doc = replaceText(doc, tag, replaceWord);
            }
            saveFile(filePath, doc);
            doc.close();
        }
    }

    private XWPFDocument replaceText(XWPFDocument doc, String tag, String replaceWord) {
        iterationAllParagraphs(doc.getParagraphs(), tag, replaceWord);
        // при наличии таблиц - доработаем
        /*for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    iterationAllParagraphs(cell.getParagraphs(), tag, replaceWord);
                }
            }
        }*/
        return doc;
    }

    private void iterationAllParagraphs(List<XWPFParagraph> paragraphs, String tag, String replaceWord){
        paragraphs.forEach(paragraph -> replaceParagraph(paragraph, tag, replaceWord));
    }

    private void replaceParagraph(XWPFParagraph paragraph, String tag, String replaceWord){
        for (XWPFRun run: paragraph.getRuns()){
            String paragraphText = run.getText(0);
            if (paragraphText != null && paragraphText.contains(tag)) {
                String updatedParagraphText = paragraphText.replace(tag, replaceWord);
                // меняем тег, а не добавляем меняемое слово в конец (pos - 0)
                run.setText(updatedParagraphText, 0);
            }
        }
    }

    private void saveFile(String filePath, XWPFDocument doc) throws IOException {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            doc.write(out);
        }
    }
}

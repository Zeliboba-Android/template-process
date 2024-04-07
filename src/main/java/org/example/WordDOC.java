package org.example;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 * Класс WordDOC предоставляет функционал для изменения содержимого документа типа .doc.
 * Он позволяет заменить определенные текстовые метки в документе на указанные значения.
 * Для работы с документом используются библиотека Apache POI и объект POIFSFileSystem.
 */
public class WordDOC {
    private final TagMap tagMap;
    WordDOC(TagMap tagMap){
        this.tagMap = tagMap;
    }
    /**
     * Метод changeFile() извлекает документ test.doc из ресурсов класспути,
     * заменяет определенные текстовые метки в документе и сохраняет изменения.
     * @throws IOException если возникает ошибка ввода-вывода при чтении или записи файла
     */
    public void changeFile() throws IOException{
        // извлечение пути к файлу test.doc из ресурсов класспути и сохранение его в переменной filePath
        String fileUrl = getClass().getClassLoader().getResource("test.doc").getPath();
        // декодируем путь к файлу, чтобы обработать специальные символы, такие как пробелы или кириллические символы
        String filePath = URLDecoder.decode(fileUrl, "UTF-8");
        // создаем новый путь к файлу
        String newFilePath = filePath.replace("test.doc", "new_test.doc");
        // inputStream - входной поток данных, FileInputStream - чтения байтов из файла
        // POIFSFileSystem - объект для работы с документом Word
        try (InputStream inputStream = new FileInputStream(filePath);
             POIFSFileSystem fileSystem = new POIFSFileSystem(inputStream)){
            // создание объект для работы с .doc
            HWPFDocument doc = new HWPFDocument(fileSystem);
            // замена текста в doc и сохранение изменений
            doc = replaceText(doc);
            saveFile(newFilePath, doc);
            doc.close();
        }
    }

    /**
     * Метод replaceText() заменяет текстовые метки в документе на указанные значения.
     * @param doc объект HWPFDocument, представляющий документ типа .doc
     * @return объект HWPFDocument с замененным текстом
     */
    private HWPFDocument replaceText(HWPFDocument doc){
        // диапазон, охватывающий весь текст документа
        Range range = doc.getRange();
        for(HashMap.Entry<String, String> entry: tagMap.getTagMap().entrySet()) {
            // получение ключа
            String tag = entry.getKey();
            // получение значения
            String replaceWord = entry.getValue();
            // замена текста в диапазоне
            range.replaceText(tag, replaceWord);
        }
        return doc;
    }

    /**
     * Метод saveFile() сохраняет содержимое документа в файле.
     * @param filePath путь к файлу, в который нужно сохранить документ
     * @param doc объект HWPFDocument, содержащий измененное содержимое документа
     * @throws IOException если возникает ошибка ввода-вывода при записи файла
     */
    private void saveFile(String filePath, HWPFDocument doc) throws IOException {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            // записывание содержимого документа в файл, который был открыт для записи
            doc.write(out);
        }
    }
}
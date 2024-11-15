package org.example.controller;

import org.example.model.TagDatabase;
import org.example.model.TagMap;
import org.example.view.ViewModelTable;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GenerateFileUsingTable {
    private TagMap tagMap;
    // Загрузка данных из CSV файла
    private Map<String, String> dataMap = new HashMap<>();
    private TagDatabase tagDatabase;
    GenerateFileUsingTable(TagMap tagMap, String csvFilePath) {
        this.tagMap = tagMap;
        this.tagDatabase = new TagDatabase("jdbc:sqlite:tags.db");
        // Путь к CSV файлу с данными
        try (// Укажите правильную кодировку вашего файла CSV
             BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFilePath),"cp1251"));
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", 3);
                if (parts.length == 3) {
                    String tag = parts[0].trim(); // Убираем лишние пробелы
                    // Преобразование строки в байтовый массив с указанной кодировкой (например, windows-1251)
                    byte[] bytes = parts[2].trim().getBytes("cp1251");
                    // Преобразование байтового массива обратно в строку с другой кодировкой (например, windows-1251)
                    String newValue = new String(bytes, "cp1251");
                    if (!newValue.isEmpty()) {
                        dataMap.put(tag, newValue);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void fillTagsUsingTable() {
        for (HashMap.Entry<String, String> entry : dataMap.entrySet()) {
            String tag = entry.getKey();
            String placeholder = entry.getValue();  // Значение placeholder из CSV файла
            // Получаем новый tag из базы данных по значению tag
            String newTag = tagDatabase.getTagByPlaceholder(tag);

            // Если в базе данных найден новый тег, заменяем старый
            if (newTag != null) {
                tag = newTag; // Заменяем tag на значение из базы данных
            }

            // Добавляем обновленный tag в tagMap
            tagMap.addTag(tag, placeholder);
        }
    }

}
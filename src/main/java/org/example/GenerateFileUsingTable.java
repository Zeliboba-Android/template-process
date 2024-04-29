package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GenerateFileUsingTable {
    private TagMap tagMap;
    // Загрузка данных из CSV файла
    Map<String, String> dataMap = new HashMap<>();
    public GenerateFileUsingTable(TagMap tagMap) {

        this.tagMap = tagMap;
        // Путь к CSV файлу с данными
        String csvFilePath = "C:\\Users\\dimas\\IdeaProjects\\template-process\\src\\main\\resources\\testTable.csv";
        try (// Укажите правильную кодировку вашего файла CSV
             BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFilePath),"cp1251"));
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", 2);
                if (parts.length == 2) {
                    String tag = parts[0].trim(); // Убираем лишние пробелы
                    // Преобразование строки в байтовый массив с указанной кодировкой (например, windows-1251)
                    byte[] bytes = parts[1].trim().getBytes("cp1251");
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
        tagMap.getTagMap().put("${fio}", dataMap.get("${fio}"));
        tagMap.getTagMap().put("${date}", dataMap.get("${date}"));
        tagMap.getTagMap().put("${post}", dataMap.get("${post}"));
        tagMap.getTagMap().put("${company}", dataMap.get("${company}"));
        tagMap.getTagMap().put("${decode}", dataMap.get("${decode}"));
        tagMap.getTagMap().put("${chief}", dataMap.get("${chief}"));
    }
}

package org.example;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Денис on 21.05.2024
 */
public class DocumentGenerator {
    private Main main;
    private GenerateFileUsingTable generateFileUsingTable;
    private Authors additionalAuthors;
    private Authors multiAuthors;
    TagExtractor tagExtractor;
    TagMap tagMap;
    private TagMap copyTagMap = new TagMap();
    String outputFolderPath;
    File[] selectedFiles;
    public DocumentGenerator(Main main) {
        this.main = main;
        tagMap = new TagMap();
        tagExtractor = new TagExtractor(this.main);
    }

    public void fillAddAuthorsTags() {
        if (additionalAuthors == null || additionalAuthors.getTagMaps().isEmpty()) {
            return;
        }
        Map<String, String> tagMapCopy = new HashMap<>(copyTagMap.getTagMap());
        for (Map.Entry<String, String> entry : tagMapCopy.entrySet()) {
            String tag = entry.getKey();
            String newTag = tag;
            if (tag.contains("key_ria_author") && tag.matches(".*\\d+.*")) {
                int authorIndex = extractAuthorIndex(tag);
                if (authorIndex > 3) {
                    // Определяем новый индекс автора, начиная с 2
                    int newIndex;
                    if (authorIndex > 6) {
                        newIndex = 2 + (authorIndex - 7) % 3;
                    } else {
                        newIndex = 2 + (authorIndex - 4) % 4;
                    }
                    newTag = tag.replaceFirst("X\\d+", "X" + newIndex);
                }
                additionalAuthors.addTagToAuthor(authorIndex, newTag, entry.getValue());
                copyTagMap.removeTag(tag); // Удаляем тег из tagMap
            }
        }
    }

    public void fillMultiAuthorsTags() {
        if (multiAuthors == null || multiAuthors.getTagMaps().isEmpty()) {
            return;
        }
        Map<String, String> tagMapCopy = new HashMap<>(copyTagMap.getTagMap());
        for (Map.Entry<String, String> entry : tagMapCopy.entrySet()) {
            String tag = entry.getKey();
            if (tag.contains("key_ria_author") && tag.matches(".*\\d+.*")) {
                int authorIndex = extractAuthorIndex(tag);
                String newTag = tag.replaceFirst("X\\d+", "X" + 1);
                multiAuthors.addTagToAuthor(authorIndex, newTag, entry.getValue());
                copyTagMap.removeTag(tag); // Удаляем тег из tagMap
            }
        }
    }

    private int extractAuthorIndex(String tag) {
        // Извлечение индекса автора из тега, например, из "key_ria_authorX3" вернет 2 (индексация с 0)
        String indexStr = tag.replaceAll("\\D+", ""); // Удаляем все нецифровые символы
        return Integer.parseInt(indexStr) - 1;
    }

    // функция заполнения значений тегов
    void fillTags() {
        // Получаем список тегов
        Set<String> tags = tagExtractor.uniqueTags;
        // Получаем список всех текстовых полей из ViewModel
        List<JTextField> textFields = main.viewModelStartScreen.viewModelTextFields.findTextFields();
        // Проверяем, что количество текстовых полей соответствует количеству тегов
        if (textFields.size() != tags.size()) {
            System.out.println(textFields.size());
            System.out.println(tags.size());
            System.out.println("Количество текстовых полей не соответствует количеству тегов.");
            return; // Возможно, стоит бросить исключение здесь
        }
        // Очищаем TagMap перед заполнением новыми значениями
        tagMap = new TagMap();
        // Проходим по всем текстовым полям и соответствующим тегам
        Iterator<String> tagsIterator = tags.iterator();
        for (JTextField textField : textFields) {
            if (tagsIterator.hasNext()) {
                String tag = tagsIterator.next(); // Получаем текущий тег
                String value = textField.getText(); // Получаем значение из соответствующего текстового поля
                // Добавляем тег и его значение в TagMap
                tagMap.addTag(tag, value);
            }
        }
    }

    void chooseFillTag(){
        if (main.viewModelStartScreen.verification){
            fillTags();
        }else {
            generateFileUsingTable = new GenerateFileUsingTable(tagMap, outputFolderPath);
            generateFileUsingTable.fillTagsUsingTable();
        }
    }

    void generateDocument() {
        chooseFillTag();
        // Создаем изменяемый список для хранения файлов, которые нужно обработать
        List<File> filesToProcess = new ArrayList<>(List.of(selectedFiles));
        int countAuthors = main.viewModelStartScreen.selectedNumber;
        if (countAuthors > 1) {
            copyTagMap = new TagMap(new HashMap<>(tagMap.getTagMap()));
            additionalAuthors = new Authors(countAuthors);
            fillAddAuthorsTags();
            Iterator<File> iterator = filesToProcess.iterator();
            while (iterator.hasNext()){
                File file = iterator.next();
                String fileName = file.getName();
                if (fileName.contains("main")) {
                    // Объединяем теги первого автора и общие теги
                    TagMap combinedTagMap = new TagMap(new HashMap<>(copyTagMap.getTagMap()));
                    combinedTagMap.combineTags(additionalAuthors.getMainTagMap());
                    replaceText(file, combinedTagMap, fileName.replace("main_", "1_"));
                    iterator.remove();
                }
                else if (fileName.contains("additional")) {
                    // Обрабатываем дополнительные файлы для остальных авторов
                    for (int i = 1; i < additionalAuthors.getTagMaps().size(); i += 3) {
                        TagMap additionalTagMap = new TagMap(new HashMap<>(copyTagMap.getTagMap()));
                        StringBuilder authorNumbers = new StringBuilder();
                        for (int j = 0; j < 3; j++) {
                            if (i + j < additionalAuthors.getTagMaps().size()) {
                                additionalTagMap.combineTags(additionalAuthors.getTagMapByIndex(i + j));
                                authorNumbers.append(i + j + 1 + "_");
                            }
                        }
                        replaceText(file, additionalTagMap, fileName.replace("additional_", authorNumbers));
                    }
                    iterator.remove();
                } else if (fileName.contains("multi")) {
                    copyTagMap = new TagMap(new HashMap<>(tagMap.getTagMap()));
                    // Разбиваем теги по типу "key_ria_authorX..." для каждого автора
                    multiAuthors = new Authors(countAuthors);
                    fillMultiAuthorsTags();
                    // Обрабатываем файлы, которые должны генерироваться для каждых авторов
                    for (int i = 0; i < countAuthors; i++){
                        TagMap multiTagMap = new TagMap(new HashMap<>(copyTagMap.getTagMap()));
                        multiTagMap.combineTags(multiAuthors.getTagMapByIndex(i));
                        replaceText(file, multiTagMap, fileName.replace("multi_", (i + 1) + "_"));
                    }
                    iterator.remove();
                }
            }
        }
        for (File file: filesToProcess){
            replaceText(file, tagMap, file.getName());
        }
    }

    void replaceText(File file, TagMap tags, String authorPrefix){
        String fileName = file.getName();
        try {
            // Проверка наличия пустых значений в TagMap
            boolean hasEmptyValues = checkForEmptyValues();
            if (!hasEmptyValues) {
                String newFilePath = outputFolderPath + File.separator + authorPrefix;
                if (fileName.endsWith(".doc")) {
                    WordDOC wordDOC = new WordDOC(tags, file);
                    wordDOC.changeFile(newFilePath);
                    System.out.println("Файл " + fileName + " успешно изменен");
                } else if (fileName.endsWith(".docx")) {
                    WordDOCX wordDOCX = new WordDOCX(tags, file);
                    wordDOCX.changeFile(newFilePath);
                    System.out.println("Файл " + fileName + " успешно изменен");
                } else
                    System.out.println("Файл " + fileName + " не формата doc/docx");
            } else {
                System.out.println("Не удалось изменить файл" + fileName + ".Обнаружены пустые значения.");
            }
        } catch (IOException e) {
            // обработка исключения в случае возникновения ошибки при изменении файла
            System.err.println("Ошибка при изменении файла " + fileName);
            throw new RuntimeException(e);
        }
    }

    // функция проверяет есть ли в теге что-то или там пусто, или null и выводит соответсвующее сообщение в консоль,
    // а если значение есть, то возвращает false
    boolean checkForEmptyValues() {
        boolean hasEmptyValues = false;
        for (Map.Entry<String, String> entry : tagMap.getTagMap().entrySet()) {
            String value = entry.getValue();
            if (value == null ||value.isEmpty()) {
                // Обработка пустого значения
                System.out.println("Пустое значение для ключа: " + entry.getKey());
                hasEmptyValues = true;
            }
        }
        return hasEmptyValues;
    }

    // Метод для создания папки сохранения
    void createFolder() {
        // Получаем текущую дату и время
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        String currentDateTime = sdf.format(new Date());
        String targetFolder = getClass().getClassLoader().getResource("").getPath();
        targetFolder = URLDecoder.decode(targetFolder, StandardCharsets.UTF_8);
        outputFolderPath = targetFolder + currentDateTime;
        // Создаем папку
        File outputFolder = new File(outputFolderPath);
        if (!outputFolder.exists()) {
            if (outputFolder.mkdirs()) {
                System.out.println("Создана папка для сохранения файлов: " + outputFolder.getAbsolutePath());
            } else {
                System.err.println("Не удалось создать папку для сохранения файлов");
            }
        }
    }
}

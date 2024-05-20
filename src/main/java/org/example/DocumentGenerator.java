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
    TagExtractor tagExtractor;
    TagMap tagMap;
    String outputFolderPath;
    File[] selectedFiles;
    public DocumentGenerator(Main main) {
        this.main = main;
        tagMap = new TagMap();
        tagExtractor = new TagExtractor();
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
                tagMap.getTagMap().put(tag, value);
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
        for (File file : selectedFiles) {
            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".doc")) {
                replaceTextDoc(file);
            } else if (fileName.endsWith(".docx")) {
                replaceTextDocx(file);
            }
        }
    }

    void replaceTextDoc(File file){
        // создание экземпляра класса WordDOC
        WordDOC wordDOC = new WordDOC(tagMap, file);
        try {
            // Проверка наличия пустых значений в TagMap
            boolean hasEmptyValues = checkForEmptyValues();
            if (!hasEmptyValues) {
                // вызов метода изменения файла
                wordDOC.changeFile(outputFolderPath);
                System.out.println("Файл .doc успешно изменен");
            } else {
                System.out.println(tagMap.getTagMap());
                System.out.println("Не удалось изменить файл .doc Обнаружены пустые значения.");
            }
        } catch (IOException e) {
            // обработка исключения в случае возникновения ошибки при изменении файла
            System.err.println("Ошибка при изменении файла .doc:");
            throw new RuntimeException(e);
        }
    }

    void replaceTextDocx(File file){
        // создание экземпляра класса WordDOCX
        WordDOCX wordDOCX = new WordDOCX(tagMap, file);
        try {
            // Проверка наличия пустых значений в TagMap
            boolean hasEmptyValues = checkForEmptyValues();
            if (!hasEmptyValues) {
                // вызов метода изменения файла
                wordDOCX.changeFile(outputFolderPath);
                System.out.println("Файл .docx успешно изменен");
            } else {
                System.out.println("Не удалось изменить файл .docx Обнаружены пустые значения.");
            }
        } catch (IOException e) {
            // обработка исключения в случае возникновения ошибки при изменении файла
            System.err.println("Ошибка при изменении файла .docx:");
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

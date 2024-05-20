package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Main {
    public JFrame frame;
    private ViewModelStartScreen viewModelStartScreen;
    TagMap tagMap;
    File[] selectedFiles;
    private GenerateFileUsingTable generateFileUsingTable;
    private String outputFolderPath;
    Main() {
        viewModelStartScreen = new ViewModelStartScreen(this);
        tagMap = new TagMap();
        generateFileUsingTable = new GenerateFileUsingTable(tagMap);
        frame = new JFrame("Генерация документов"); // Создаем главное окно
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Устанавливаем операцию закрытия
        frame.getContentPane().add(viewModelStartScreen); // Добавляем ViewModel в контейнер главного окна
        frame.setSize(300,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true); // Делаем окно видимым
        createFolder();
    }

    // Функция создания папки для сохранения файлов с текущей датой и временем
    private void createFolder() {
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

    // функция заполнения значений тегов
    void fillTags() {
        TextFieldGenerator textFieldGenerator = viewModelStartScreen.viewModelTextFields.getTextFieldGenerator();
        // Получаем список тегов
        Set<String> tags = textFieldGenerator.getTags();

        // Получаем список всех текстовых полей из ViewModel
        List<JTextField> textFields = viewModelStartScreen.viewModelTextFields.findTextFields();

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
        if (!viewModelStartScreen.viewModelTextFields.choose){
            fillTags();
        }else {
            generateFileUsingTable.fillTagsUsingTable();
        }

    }

    void replaceTextDoc(){
        // вызов функции заполнения тегов
        chooseFillTag();
        // создание экземпляра класса WordDOC
        WordDOC wordDOC = new WordDOC(tagMap,this);
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

    void replaceTextDocx(){
        chooseFillTag();
        // создание экземпляра класса WordDOCX
        WordDOCX wordDOCX = new WordDOCX(tagMap,this);
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
    void disposeFrame(Frame frame){
        frame.dispose();
    }

    public static void main(String[] args) {
        new Main();
    }
}
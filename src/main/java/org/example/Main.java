package org.example;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;

public class Main {
    private ViewModel viewModel;
    private static TagMap tagMap;

    Main() {
        viewModel = new ViewModel(this);
        JFrame frame = new JFrame("Main Frame"); // Создаем главное окно
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Устанавливаем операцию закрытия
        frame.getContentPane().add(viewModel); // Добавляем ViewModel в контейнер главного окна
        frame.setSize(500,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true); // Делаем окно видимым

    }

    // функция заполнения значений тегов
    void fillTags(){
        tagMap = new TagMap();
        if (viewModel.getTextFieldFIO()!= null)
            tagMap.getTagMap().put("${fio}", viewModel.getTextFieldFIO());
        if (viewModel.getTextFieldDate()!=null)
            tagMap.getTagMap().put("${date}", viewModel.getTextFieldDate());
        if (viewModel.getTextFieldPost()!=null)
            tagMap.getTagMap().put("${post}", viewModel.getTextFieldPost());
        if (viewModel.getTextFieldCompany()!=null)
            tagMap.getTagMap().put("${company}", viewModel.getTextFieldCompany());
        if (viewModel.getTextFieldDecode()!=null)
            tagMap.getTagMap().put("${decode}", viewModel.getTextFieldDecode());
        if (viewModel.getTextFieldChief()!=null)
            tagMap.getTagMap().put("${chief}", viewModel.getTextFieldChief());
    }

    void replaceTextDoc(){
        // вызов функции заполнения тегов
        fillTags();
        // создание экземпляра класса WordDOC
        WordDOC wordDOC = new WordDOC(tagMap);
        try {
            // Проверка наличия пустых значений в TagMap
            boolean hasEmptyValues = checkForEmptyValues();
            if (!hasEmptyValues) {
                // вызов метода изменения файла
                wordDOC.changeFile();
                System.out.println("Файл .doc успешно изменен");
            } else {
                System.out.println("Не удалось изменить файл .doc. Обнаружены пустые значения.");
            }
        } catch (IOException e) {
            // обработка исключения в случае возникновения ошибки при изменении файла
            System.err.println("Ошибка при изменении файла .doc:");
            throw new RuntimeException(e);
        }
    }

    void replaceTextDocx(){
        // вызов функции заполнения тегов
        fillTags();
        // создание экземпляра класса WordDOC
        WordDOCX wordDOCX = new WordDOCX(tagMap);
        try {
            // вызов метода изменения файла
            wordDOCX.changeFile();
            System.out.println("Файл .docx успешно изменен");
        } catch (IOException e) {
            // обработка исключения в случае возникновения ошибки при изменении файла
            System.err.println("Ошибка при изменении файла .docx:");
            throw new RuntimeException(e);
        }
    }
    boolean checkForEmptyValues() {
        boolean hasEmptyValues = false;
        for (Map.Entry<String, String> entry : tagMap.getTagMap().entrySet()) {
            String value = entry.getValue();
            if (value == null || value.isEmpty()) {
                // Обработка пустого значения
                System.out.println("Пустое значение для ключа: " + entry.getKey());
                hasEmptyValues = true;
            }
        }
        return hasEmptyValues;
    }
    public static void main(String[] args) {
        new Main();
    }
}
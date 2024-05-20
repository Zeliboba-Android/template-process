package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewModelTextFields extends JPanel {
    private Main main;
    private DocumentGenerator documentGenerator;
    private JFrame frame;
    private JButton generateButton;
    public Button buttonBackSpace;
    public JButton chooseFileButton;
    public JLabel fileLabel;
    private ViewModelStartScreen viewModelStartScreen;
    public ViewModelTextFields(Main main,ViewModelStartScreen viewModelStartScreen, DocumentGenerator documentGenerator) {
        this.main = main;
        this.viewModelStartScreen = viewModelStartScreen;
        this.documentGenerator = documentGenerator;
        setLayout(null);
        setFocusable(true);
        initializeUI();
    }

    private void initializeUI() {
        buttonBackSpace = new Button("Назад");
        buttonBackSpace.setBounds(0,0,70,50);
        buttonBackSpace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame = new JFrame("Генерация документов"); // Создаем главное окно
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Устанавливаем операцию закрытия
                frame.getContentPane().add(viewModelStartScreen); // Добавляем ViewModel в контейнер главного окна
                frame.setSize(300,500);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true); // Делаем окно видимым
                main.disposeFrame(viewModelStartScreen.getTextFieldsFrameTextFields());
            }
        });
        add(buttonBackSpace);

        fileLabel = new JLabel("Файл(ы) не выбран(ы):");
        fileLabel.setBounds(150, 65, 400, 30);
        add(fileLabel);
        chooseFileButton = new JButton("Выбор файлов (doc/docx)");
        chooseFileButton.setBounds(150, 20, 200, 50);
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeTextFields();
                FileDialog fileDialog = new FileDialog((Frame) null, "Выберите файл", FileDialog.LOAD);
                fileDialog.setMultipleMode(true); // выбор нескольких файлов
                fileDialog.setVisible(true);
                documentGenerator.selectedFiles = fileDialog.getFiles();
                if (documentGenerator.selectedFiles != null && documentGenerator.selectedFiles.length > 0) {
                    fileLabel.setText("Выбранные файлы: ");
                    for (File file : documentGenerator.selectedFiles) {
                        fileLabel.setText(fileLabel.getText() + " " + file.getName() + ";");
                    }
                }
                assert documentGenerator.selectedFiles != null;
                documentGenerator.createFolder();
                if (viewModelStartScreen.verification){
                    int count = documentGenerator.tagExtractor.writeTagsToSet(documentGenerator.selectedFiles).size();
                    generateTextFields(count);
                    System.out.println(count);
                } else {
                    documentGenerator.tagExtractor.writeTagsToCSV(documentGenerator.selectedFiles,
                            documentGenerator.outputFolderPath);
                }
            }
        });
        add(chooseFileButton);
        //создание элемента меню для выбора расширения .doc
        //создание кнопки для генерации документов после заполнения всех полей
        generateButton = new JButton("Генерация документов");
        generateButton.setBounds(150,490,200,50);
        generateButton.setRolloverEnabled(false);
        generateButton.addActionListener(new ActionListener() {// вызов функции для генерации
            // документов при нажатии на кнопку
            @Override
            public void actionPerformed(ActionEvent e) {
                documentGenerator.generateDocument();
            }
        });
        add(generateButton);
    }

    // Метод для динамической генерации полей тегов
    public void generateTextFields(int numberOfFields) {
        JTextField[] textFields = new JTextField[numberOfFields];
        for (int i = 0; i < numberOfFields; i++) {
            textFields[i] = new JTextField();
            textFields[i].setBounds(150, 100 + i * 40, 200, 30);
            add(textFields[i]);
        }
        revalidate(); // Перерисовываем панель для отображения добавленных компонентов
        repaint();
    }

    // Метод для получения всех текстовых полей
    public java.util.List<JTextField> findTextFields() {
        List<JTextField> textFields = new ArrayList<>();

        // Получаем все компоненты на панели
        Component[] components = getComponents();

        // Итерируем по всем компонентам
        for (Component component : components) {
            // Проверяем, является ли компонент текстовым полем
            if (component instanceof JTextField) {
                // Приводим компонент к типу JTextField и добавляем его в список
                JTextField textField = (JTextField) component;
                textFields.add(textField);
            }
        }

        return textFields;
    }
    private void removeTextFields() {
        // Получаем все компоненты на панели
        Component[] components = getComponents();

        // Итерируем по всем компонентам
        for (Component component : components) {
            // Проверяем, является ли компонент текстовым полем
            if (component instanceof JTextField) {
                // Удаляем текстовое поле
                remove(component);
            }
        }
        // Перерисовываем панель после удаления текстовых полей
        revalidate();
        repaint();
    }
}

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
    private TextFieldGenerator textFieldGenerator;


    private JFrame frame;
    boolean choose;
    private JButton generateButton;
    public Button buttonBackSpace;

    public JButton chooseFileButton;
    public JLabel fileLabel;
    private ViewModelStartScreen viewModelStartScreen;
    public TextFieldGenerator getTextFieldGenerator() {
        return textFieldGenerator;
    }


    public ViewModelTextFields(Main main,ViewModelStartScreen viewModelStartScreen) {
        this.main = main;
        this.viewModelStartScreen = viewModelStartScreen;
        textFieldGenerator = new TextFieldGenerator(this);
        setLayout(null);
        setFocusable(true);
        initializeUI();
    }


    public void generateDocument(){
        if (viewModelStartScreen.radioButtonDOC.isSelected()){
            main.replaceTextDoc();
        }
        else if (viewModelStartScreen.radioButtonDOCX.isSelected()){
            main.replaceTextDocx();
        }
        else {
            JOptionPane.showMessageDialog(this, "Please select a document type.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
                main.selectedFiles = fileDialog.getFiles();
                if (main.selectedFiles != null && main.selectedFiles.length > 0) {
                    fileLabel.setText("Выбранные файлы: ");
                    for (File file : main.selectedFiles) {
                        fileLabel.setText(fileLabel.getText() + " " + file.getName() + ";");
                    }
                }
                assert main.selectedFiles != null;
                int count = textFieldGenerator.countTags(main.selectedFiles);
                textFieldGenerator.generateTextFields(count);
                System.out.println(count);

            }
        });
        add(chooseFileButton);
        //создание элемента меню для выбора расширения .doc
        //создание кнопки для генерации документов после заполнения всех полей
        generateButton = new JButton("Generate document");
        generateButton.setBounds(150,490,200,50);
        generateButton.setRolloverEnabled(false);
        generateButton.addActionListener(new ActionListener() {// вызов функции для генерации
            // документов при нажатии на кнопку
            @Override
            public void actionPerformed(ActionEvent e) {
                choose = false;
                generateDocument();
            }
        });
        add(generateButton);
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
    public JFrame getFrame() {
        return frame;
    }

}

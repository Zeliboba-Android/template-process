package org.example;

import javax.swing.*;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewModel extends JPanel {
    private Main main;
    private TextFieldGenerator textFieldGenerator;
    File[] selectedFiles;
    boolean choose;
    private JButton generateButton;
    private JButton generateButtonUsingTable;
    private JRadioButton radioButtonDOC;
    private JRadioButton radioButtonDOCX;
    private JLabel labelTypeOfDocument;
    private JButton chooseFileButton;
    private JLabel fileLabel;

    public TextFieldGenerator getTextFieldGenerator() {
        return textFieldGenerator;
    }

    public ViewModel(Main main){
        this.main = main;
        setLayout(null);
        setFocusable(true);
        initializeUI();
    }
    //функция которая для генерации документа с определенным расширением в зависимости от выбранного значения меню и
    // обработка события если ничего не выбрано


    // создание и добавление необходимых текстовых полей, кнопки или надписей
    public void initializeUI(){

//        chooseFileButton = new JButton("Выберите файл (doc/docx)");
//        chooseFileButton.setBounds(150, 20, 200, 50);
//        chooseFileButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JFileChooser fileChooser = new JFileChooser();
//                fileChooser.setMultiSelectionEnabled(true); // выбор нескольких файлов
//                int result = fileChooser.showOpenDialog(ViewModel.this);
//                if (result == JFileChooser.APPROVE_OPTION) {
//                    File[] selectedFiles = fileChooser.getSelectedFiles();
//                    fileLabel.setText("Выбранные файлы: ");
//                    // выбранные файлы
//                    for (File file : selectedFiles) {
//                        System.out.println("Выбранные файлы: " + file.getName());
//                        // выводим имя каждого выбранного файла
//                        fileLabel.setText(fileLabel.getText() + " " + file.getName() + ";");
//                    }
//                }
//            }
//        });
//        add(chooseFileButton);

        generateButtonUsingTable = new JButton("Generate using CSV");
        generateButtonUsingTable.setBounds(150,550,200,50);
        generateButtonUsingTable.setBackground(Color.lightGray);
        generateButtonUsingTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                choose = true;
//                generateDocument();
            }
        });
        add(generateButtonUsingTable);
    }
    // функция добавления подсказки в текстовое поле
    private void addPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new FocusListener() {// если пользователь будет вводить что-то
            // в текстовое поле подсказка пропадет
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {// когда пользователь ничего не ввёл
                // в текстовое поле подсказка есть
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
    }

}
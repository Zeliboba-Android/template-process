package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ViewModel extends JPanel {
    private Main main;
    boolean choose;
    private JButton generateButton;
    private JButton generateButtonUsingTable;
    private JTextField textFieldFIO;
    private JTextField textFieldDate;
    private JTextField textFieldPost;
    private JTextField textFieldCompany;
    private JTextField textFieldDecode;
    private JTextField textFieldChief;
    private JRadioButton radioButtonDOC;
    private JRadioButton radioButtonDOCX;
    private JLabel labelTypeOfDocument;

    public ViewModel(Main main){
        this.main = main;
        setLayout(null);
        setFocusable(true);
        initializeUI();
    }
    //функция которая для генерации документа с определенным расширением в зависимости от выбранного значения меню и
    // обработка события если ничего не выбрано
    public void generateDocument(){
        if (radioButtonDOC.isSelected()){
            main.replaceTextDoc();
        }
        else if (radioButtonDOCX.isSelected()){
            main.replaceTextDocx();
        }
        else {
            JOptionPane.showMessageDialog(this, "Please select a document type.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // создание и добавление необходимых текстовых полей, кнопки или надписей
    public void initializeUI(){
        //создание текстового поля для ввода ФИО
        textFieldFIO = new JTextField();
        textFieldFIO.setBounds(150, 25, 200, 30);
        addPlaceholder(textFieldFIO,"Enter your FIO");
        add(textFieldFIO);
        //создание текстового поля для ввода даты рождения
        textFieldDate = new JTextField();
        textFieldDate.setBounds(150, 75, 200, 30);
        addPlaceholder(textFieldDate,"Enter date");
        add(textFieldDate);
        //создание текстового поля для ввода должности
        textFieldPost = new JTextField();
        textFieldPost.setBounds(150, 125, 200, 30);
        addPlaceholder(textFieldPost,"Enter your post");
        add(textFieldPost);
        //создание текстового поля для ввода названия компании
        textFieldCompany = new JTextField();
        textFieldCompany.setBounds(150, 175, 200, 30);
        addPlaceholder(textFieldCompany,"Enter your company");
        add(textFieldCompany);
        //создание текстового поля для ввода расшифровки подписи
        textFieldDecode = new JTextField();
        textFieldDecode.setBounds(150, 225, 200, 30);
        addPlaceholder(textFieldDecode,"Enter your decode");
        add(textFieldDecode);
        //создание текстового поля для ввода расшифровки подписи начальника
        textFieldChief = new JTextField();
        textFieldChief.setBounds(150, 275, 200, 30);
        addPlaceholder(textFieldChief,"Enter your chief");
        add(textFieldChief);
        //создание надписи для меню с выбором расширения файла
        labelTypeOfDocument = new JLabel();
        labelTypeOfDocument.setBounds(150,325,200,30);
        labelTypeOfDocument.setText("Select the document type");
        Font font = labelTypeOfDocument.getFont();
        labelTypeOfDocument.setFont(font.deriveFont(Font.BOLD,14));
        add(labelTypeOfDocument);
        //создание элемента меню для выбора расширения .doc
        radioButtonDOC = new JRadioButton(".doc");
        radioButtonDOC.setBounds(150, 375, 80, 30);
        add(radioButtonDOC);
        //создание элемента меню для выбора расширения .docx
        radioButtonDOCX = new JRadioButton(".docx");
        radioButtonDOCX.setBounds(230, 375, 100, 30);
        add(radioButtonDOCX);
        // Создаем группу для радиобаттонов
        ButtonGroup documentTypeGroup = new ButtonGroup();
        // Добавляем радиобаттон в группу
        documentTypeGroup.add(radioButtonDOC);
        documentTypeGroup.add(radioButtonDOCX);
        //создание кнопки для генерации документов после заполнения всех полей
        generateButton = new JButton("Generate document");
        generateButton.setBounds(150,425,200,50);
        generateButton.setRolloverEnabled(false);
        generateButton.setBackground(Color.lightGray);
        generateButton.addActionListener(new ActionListener() {// вызов функции для генерации
            // документов при нажатии на кнопку
            @Override
            public void actionPerformed(ActionEvent e) {
                choose = false;
                generateDocument();
            }
        });
        add(generateButton);
        generateButtonUsingTable = new JButton("Generate using CSV");
        generateButtonUsingTable.setBounds(150,500,200,50);
        generateButtonUsingTable.setBackground(Color.lightGray);
        generateButtonUsingTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                choose = true;
                generateDocument();
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

    //геттер поля фио для получения значение из текстового поля и проверки введено ли в него что-то, если нет,
    // то вывод соответствующего сообщения на экран
    public String getTextFieldFIO() {
        String fio = textFieldFIO.getText().trim();
        if (fio.isEmpty() || fio.equals("Enter your FIO")){
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your FIO.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
        return fio;
    }
    //геттер поля даты рождения для получения значение из текстового поля и проверки введено ли в него что-то, если нет,
    // то вывод соответствующего сообщения на экран
    public String getTextFieldDate() {
        String date = textFieldDate.getText().trim();
        if (date.isEmpty() || date.equals("Enter date")){
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter date.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
        return date;
    }
    //геттер поля должности для получения значение из текстового поля и проверки введено ли в него что-то, если нет,
    // то вывод соответствующего сообщения на экран
    public String getTextFieldPost() {
        String post = textFieldPost.getText().trim();
        if (post.isEmpty() || post.equals("Enter your post")){
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your post.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
        return post;
    }
    //геттер поля названия компании для получения значение из текстового поля и проверки введено ли в него что-то,
    // если нет,
    // то вывод соответствующего сообщения на экран
    public String getTextFieldCompany() {
        String company = textFieldCompany.getText().trim();
        if (company.isEmpty() || company.equals("Enter your company")){
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your company.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
        return company;
    }
    //геттер поля расшифровки подписи для получения значение из текстового поля и проверки введено ли в него что-то,
    // если нет,
    // то вывод соответствующего сообщения на экран
    public String getTextFieldDecode() {
        String decode = textFieldDecode.getText().trim();
        if (decode.isEmpty() || decode.equals("Enter your decode")){
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your decode.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
        return decode;
    }
    //геттер поля расшифровки подписи руководителя для получения значение из текстового поля и проверки введено
    // ли в него что-то, если нет,
    // то вывод соответствующего сообщения на экран
    public String getTextFieldChief() {
        String chief = textFieldChief.getText().trim();
        if (chief.isEmpty() || chief.equals("Enter your chief")){
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter Enter your chief.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
        return chief;
    }

}
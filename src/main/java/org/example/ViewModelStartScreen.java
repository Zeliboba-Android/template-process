package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ViewModelStartScreen extends JPanel {
    private Main main;
    public ViewModelTextFields viewModelTextFields;
    private ViewModelTable viewModelTable;
    private JPanel panel;
    private JLabel labelChoosingGenerateMethod;
    private JButton buttonGenerateWithTextFields;
    private JButton buttonGenerateWithTable;
    public JRadioButton radioButtonDOC;
    public JRadioButton radioButtonDOCX;
    public ButtonGroup documentTypeGroup = new ButtonGroup();
    private JLabel labelTypeOfDocument;
    private JComboBox<Integer> authorComboBox;
    private Font font;
    private int selectedNumber;

    public JFrame getTextFieldsFrameTextFields() {
        return textFieldsFrameTextFields;
    }

    private JFrame textFieldsFrameTextFields;

    public JFrame getTextFieldsFrameTable() {
        return textFieldsFrameTable;
    }

    private JFrame textFieldsFrameTable;
    public ViewModelStartScreen(Main main) {
        this.main = main;
        viewModelTextFields = new ViewModelTextFields(main,this);
        viewModelTable = new ViewModelTable(main,this);
        initializeStartScreen();

    }


    public void initializeStartScreen(){
        setLayout(null);
        setFocusable(true);
        labelChoosingGenerateMethod = new JLabel();
        labelChoosingGenerateMethod.setText("Как сгенерировать документ?");
        font = labelChoosingGenerateMethod.getFont();
        labelChoosingGenerateMethod.setFont(font.deriveFont(Font.BOLD,14));
        labelChoosingGenerateMethod.setBounds(50,50,250,50);
        add(labelChoosingGenerateMethod);
        buttonGenerateWithTextFields = new JButton("Использовать поля ввода");
        buttonGenerateWithTextFields.setBounds(50,100,200,50);
        buttonGenerateWithTextFields.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Создаем новое окно JFrame для отображения ViewModelGenerateWithTextFields
                textFieldsFrameTextFields = new JFrame("Генерация с текстовыми полями");
                textFieldsFrameTextFields.getContentPane().add(viewModelTextFields); // Добавляем ViewModelGenerateWithTextFields в контейнер окна
                textFieldsFrameTextFields.setSize(500, 700);
                textFieldsFrameTextFields.setLocationRelativeTo(null);
                textFieldsFrameTextFields.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                textFieldsFrameTextFields.setVisible(true);
                main.disposeFrame(main.frame);

            }
        });
        add(buttonGenerateWithTextFields);
        buttonGenerateWithTable = new JButton("Использовать таблицу");
        buttonGenerateWithTable.setBounds(50,175,200,50);
        buttonGenerateWithTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFieldsFrameTable = new JFrame("Генерация с помощью таблицы");
                textFieldsFrameTable.getContentPane().add(viewModelTable); // Добавляем ViewModelGenerateWithTextFields в контейнер окна
                textFieldsFrameTable.setSize(500, 700);
                textFieldsFrameTable.setLocationRelativeTo(null);
                textFieldsFrameTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                textFieldsFrameTable.setVisible(true);
                main.disposeFrame(main.frame);

            }
        });
        add(buttonGenerateWithTable);
        labelTypeOfDocument = new JLabel();
        labelTypeOfDocument.setBounds(50,240,250,30);
        labelTypeOfDocument.setText("Выберите тип документа");
        font = labelTypeOfDocument.getFont();
        labelTypeOfDocument.setFont(font.deriveFont(Font.BOLD,14));
        add(labelTypeOfDocument);
        radioButtonDOC = new JRadioButton(".doc");
        radioButtonDOC.setBounds(70, 280, 80, 30);
        add(radioButtonDOC);
        //создание элемента меню для выбора расширения .docx
        radioButtonDOCX = new JRadioButton(".docx");
        radioButtonDOCX.setBounds(150, 280, 100, 30);
        add(radioButtonDOCX);
        // Создаем группу для радиобаттонов
        documentTypeGroup.add(radioButtonDOC);
        documentTypeGroup.add(radioButtonDOCX);
        // Создаем JComboBox для выбора цифр от 1 до 15
        Integer[] numbers = new Integer[15];
        for (int i = 0; i < 15; i++) {
            numbers[i] = i + 1;
        }
        authorComboBox = new JComboBox<>(numbers);
        authorComboBox.setBounds(50, 320, 200, 30);
        add(authorComboBox);

        // Обработка выбора в JComboBox (опционально)
        authorComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedNumber = (int) authorComboBox.getSelectedItem();
            }
        });

    }
}

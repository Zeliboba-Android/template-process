package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ViewModelStartScreen extends JPanel {
    private Main main;
    private DocumentGenerator documentGenerator;
    public ViewModelTextFields viewModelTextFields;
    private ViewModelTable viewModelTable;
    private JLabel labelChoosingGenerateMethod;
    private JLabel labelChooseCountOfAuthor;
    private JButton buttonGenerateWithTextFields;
    private JButton buttonGenerateWithTable;
    private JComboBox<Integer> authorComboBox;
    private Font font;
    boolean verification;
    int selectedNumber = 1;
    private JFrame textFieldsFrameTextFields;
    private JFrame textFieldsFrameTable;
    String[] select;
    public ViewModelStartScreen(Main main, DocumentGenerator documentGenerator) {
        this.main = main;
        this.documentGenerator = documentGenerator;
        viewModelTextFields = new ViewModelTextFields(main,this, this.documentGenerator,viewModelTable);
        viewModelTable = new ViewModelTable(main,this, this.documentGenerator);
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
                textFieldsFrameTextFields = new JFrame("Генерация с текстовыми полями");
                textFieldsFrameTextFields.getContentPane().add(viewModelTextFields);
                textFieldsFrameTextFields.setSize(830, 700);
                textFieldsFrameTextFields.setLocationRelativeTo(null);
                textFieldsFrameTextFields.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                textFieldsFrameTextFields.setVisible(true);
                verification = true;
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
                verification = false;
                main.disposeFrame(main.frame);
            }
        });
        add(buttonGenerateWithTable);
        // Создаем JComboBox для выбора цифр от 1 до 15
        Integer[] numbers = new Integer[15];
        for (int i = 0; i < 15; i++) {
            numbers[i] = i + 1;
        }
        labelChooseCountOfAuthor = new JLabel("Выберите количество авторов");
        labelChooseCountOfAuthor.setBounds(50,250,250,30);
        font = labelChooseCountOfAuthor.getFont();
        labelChooseCountOfAuthor.setFont(font.deriveFont(Font.BOLD,14));
        add(labelChooseCountOfAuthor);
        authorComboBox = new JComboBox<>(numbers);
        authorComboBox.setBounds(50, 280, 200, 30);
        add(authorComboBox);

        // Обработка выбора в JComboBox (опционально)
        authorComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedNumber = (int) authorComboBox.getSelectedItem();
            }
        });
    }

    public JFrame getTextFieldsFrameTable() {
        return textFieldsFrameTable;
    }

    public JFrame getTextFieldsFrameTextFields() {
        return textFieldsFrameTextFields;
    }

}

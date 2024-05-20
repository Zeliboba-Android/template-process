package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewModelTable extends JPanel {
    private JButton generateButtonUsingTable;
    private ViewModelStartScreen viewModelStartScreen;
    public Button buttonBackSpace;
    private JFrame frame;
    private Main main;

    public ViewModelTable(Main main, ViewModelStartScreen viewModelStartScreen) {
        this.viewModelStartScreen = viewModelStartScreen;  // Сохраняем переданный экземпляр
        this.main = main;
        setLayout(null);
        setFocusable(true);
        initializeTable();
    }
    public void initializeTable(){
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
                main.disposeFrame(viewModelStartScreen.getTextFieldsFrameTable());
            }
        });
        add(buttonBackSpace);
        ViewModelTextFields viewModelTextFields = new ViewModelTextFields(main,viewModelStartScreen);
        add(viewModelTextFields.chooseFileButton);
        add(viewModelTextFields.fileLabel);

        generateButtonUsingTable = new JButton("Generate using CSV");
        generateButtonUsingTable.setBounds(150,150,200,50);
        generateButtonUsingTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewModelStartScreen.viewModelTextFields.choose = true;
                viewModelStartScreen.viewModelTextFields.generateDocument();
            }
        });
        add(generateButtonUsingTable);

    }
}

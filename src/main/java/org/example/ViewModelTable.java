package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewModelTable extends JPanel {
    private Main main;
    private ViewModelStartScreen viewModelStartScreen;
    private DocumentGenerator documentGenerator;
    private JButton generateButtonUsingTable;
    public JButton buttonBackSpace;

    public ViewModelTable(Main main, ViewModelStartScreen viewModelStartScreen, DocumentGenerator documentGenerator) {
        this.viewModelStartScreen = viewModelStartScreen;  // Сохраняем переданный экземпляр
        this.documentGenerator = documentGenerator;
        this.main = main;
        setLayout(null);
        setFocusable(true);
        initializeTable();
    }
    public void initializeTable(){
        ViewModelTextFields viewModelTextFields = new ViewModelTextFields(main, viewModelStartScreen, documentGenerator);
        buttonBackSpace = new JButton();
        buttonBackSpace.setText("⬅");
        Font font;
        font = buttonBackSpace.getFont();
        buttonBackSpace.setFont(font.deriveFont(Font.PLAIN,32));
        buttonBackSpace.setBounds(0, 0, 70, 50);
        buttonBackSpace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.generateFrame();
                main.disposeFrame(viewModelStartScreen.getTextFieldsFrameTable());
                viewModelTextFields.getFileLabel().setText("Файл(ы) не выбран(ы):");

            }
        });
        add(buttonBackSpace);
        add(viewModelTextFields.chooseFileButton);
        add(viewModelTextFields.getFileLabel());
        generateButtonUsingTable = new JButton("Генерация с помощью таблицы");
        generateButtonUsingTable.setBounds(100,150,200,50);
        generateButtonUsingTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                documentGenerator.generateDocument();
            }
        });
        add(generateButtonUsingTable);

    }
}

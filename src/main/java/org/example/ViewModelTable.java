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
    private JComboBox<String> selectFilesForTableComboBox;
    private ViewModelTextFields viewModelTextFields;  // Add a field for ViewModelTextFields

    private JLabel fileLabel;

    public ViewModelTable(Main main, ViewModelStartScreen viewModelStartScreen, DocumentGenerator documentGenerator) {
        this.viewModelStartScreen = viewModelStartScreen;  // сохраняем переданный экземпляр
        this.documentGenerator = documentGenerator;
        this.main = main;
        this.viewModelTextFields = new ViewModelTextFields(main, viewModelStartScreen, documentGenerator,this);  // Initialize ViewModelTextFields

        setLayout(null);
        setFocusable(true);
        initializeTable();
    }

    public void initializeTable(){
        buttonBackSpace = new JButton();
        buttonBackSpace.setText("⬅");
        Font font = buttonBackSpace.getFont();
        buttonBackSpace.setFont(font.deriveFont(Font.PLAIN,32));
        buttonBackSpace.setBounds(0, 0, 70, 50);
        buttonBackSpace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.generateFrame();
                main.disposeFrame(viewModelStartScreen.getTextFieldsFrameTable());
                fileLabel.setText("Файл(ы) не выбран(ы):");
                clearComboBox();
            }
        });
        add(buttonBackSpace);

        // Add components from ViewModelTextFields
        viewModelTextFields.chooseFileButton.setBounds(100, 20, 200, 50);
        add(viewModelTextFields.chooseFileButton);
        fileLabel = new JLabel("Файл(ы) не выбран(ы):");
        fileLabel.setBounds(100, 65, 400, 30);
        add(fileLabel);

        generateButtonUsingTable = new JButton("Генерация с помощью таблицы");
        generateButtonUsingTable.setBounds(100, 150, 200, 50);
        generateButtonUsingTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                documentGenerator.generateDocument();
            }
        });
        add(generateButtonUsingTable);
    }
    public void updateComboBox(String[] select) {
        if (selectFilesForTableComboBox != null) {
            remove(selectFilesForTableComboBox);
        }
        selectFilesForTableComboBox = new JComboBox<>(select);
        selectFilesForTableComboBox.setBounds(100, 100, 300, 30);
        add(selectFilesForTableComboBox);
        revalidate();
        repaint();
    }
    public void clearComboBox() {
        if (selectFilesForTableComboBox != null) {
            remove(selectFilesForTableComboBox);
            selectFilesForTableComboBox = null;
        }
        revalidate();
        repaint();
    }
    public JLabel getFileLabel() {
        return fileLabel;
    }

}

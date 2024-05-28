package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ViewModelTextFields extends JPanel {
    private Main main;
    private DocumentGenerator documentGenerator;
    private JButton generateButton;
    public JButton buttonBackSpace;
    public JButton chooseFileButton;

    private JLabel fileLabel;
    private ViewModelStartScreen viewModelStartScreen;
    private JPanel textFieldPanel;
    private JScrollPane scrollPane;
    private JComboBox<String> selectFilesComboBox;
    Set<String> selectFile = new HashSet<>();

    public ViewModelTextFields(Main main, ViewModelStartScreen viewModelStartScreen, DocumentGenerator documentGenerator) {
        this.main = main;
        this.viewModelStartScreen = viewModelStartScreen;
        this.documentGenerator = documentGenerator;
        setLayout(null);
        setFocusable(true);
        initializeUI();
    }

    private void initializeUI() {
        buttonBackSpace = new JButton();
        buttonBackSpace.setText("⬅");
        Font font = buttonBackSpace.getFont();
        buttonBackSpace.setFont(font.deriveFont(Font.PLAIN, 32));
        buttonBackSpace.setBounds(0, 0, 70, 50);
        buttonBackSpace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.generateFrame();
                main.disposeFrame(viewModelStartScreen.getTextFieldsFrameTextFields());
                fileLabel.setText("Файл(ы) не выбран(ы):");
                removeTextFields();
                clearComboBox();
            }
        });
        add(buttonBackSpace);

        fileLabel = new JLabel("Файл(ы) не выбран(ы):");
        fileLabel.setBounds(100, 65, 400, 30);
        add(fileLabel);

        chooseFileButton = new JButton("Выбор файлов (doc/docx)");
        chooseFileButton.setBounds(100, 20, 200, 50);
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeTextFields();
                FileDialog fileDialog = new FileDialog((Frame) null, "Выберите файл", FileDialog.LOAD);
                fileDialog.setMultipleMode(true);
                fileDialog.setVisible(true);
                documentGenerator.selectedFiles = fileDialog.getFiles();
                if (documentGenerator.selectedFiles != null && documentGenerator.selectedFiles.length > 0) {
                    fileLabel.setText("Выбранные файлы: ");
                    String[] select = new String[documentGenerator.selectedFiles.length];
                    for (int i = 0; i < documentGenerator.selectedFiles.length; i++) {
                        select[i] = documentGenerator.selectedFiles[i].getName();
                    }
                    updateComboBox(select);
                } else {
                    clearComboBox();
                }
                documentGenerator.createFolder();
                if (viewModelStartScreen.verification) {
                    int count = documentGenerator.tagExtractor.writeTagsToSet(documentGenerator.selectedFiles).size();
                    generateTextFields(count);
                    System.out.println(count);
                } else {
                    documentGenerator.tagExtractor.writeTagsToCSV(documentGenerator.selectedFiles, documentGenerator.outputFolderPath);
                }
            }
        });
        add(chooseFileButton);

        generateButton = new JButton("Генерация документов");
        generateButton.setBounds(100, 490, 200, 50);
        generateButton.setRolloverEnabled(false);
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                documentGenerator.generateDocument();
            }
        });
        add(generateButton);

        textFieldPanel = new JPanel();
        textFieldPanel.setLayout(null);

        scrollPane = new JScrollPane(textFieldPanel);
        scrollPane.setBounds(100, 125, 300, 360);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane);
    }

    private void updateComboBox(String[] select) {
        if (selectFilesComboBox != null) {
            remove(selectFilesComboBox);
        }
        selectFilesComboBox = new JComboBox<>(select);
        selectFilesComboBox.setBounds(100, 100, 300, 20);
        add(selectFilesComboBox);
        revalidate();
        repaint();
    }

    private void clearComboBox() {
        if (selectFilesComboBox != null) {
            remove(selectFilesComboBox);
            selectFilesComboBox = null;
        }
        revalidate();
        repaint();
    }

    // метод для добавления подсказок в текстовые поля
    private void addPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
    }

    // Способ динамической генерации текстовых полей тегов с заполнителями
    public void generateTextFields(int numberOfFields) {
        textFieldPanel.removeAll();
        textFieldPanel.setPreferredSize(new Dimension(200, numberOfFields * 40));

        JTextField[] textFields = new JTextField[numberOfFields];
        List<String> uniqueTags = new ArrayList<>(documentGenerator.tagExtractor.uniqueTags);

        for (int i = 0; i < numberOfFields; i++) {
            textFields[i] = new JTextField();
            textFields[i].setBounds(0, i * 40, 270, 30);
            if (i < uniqueTags.size()) {
                String tag = uniqueTags.get(i);
                if (tag.startsWith("${key_ria_")) {
                    tag = tag.substring(10); // Удалить префикс "${key_ria_"
                }
                String suffix = "}";
                if (tag.endsWith(suffix)) {
                    tag = tag.substring(0, tag.length() - suffix.length()); // Удалить суффикс
                }
                addPlaceholder(textFields[i], tag); // Установите текст-заполнитель из обработанного тега
            }
            textFieldPanel.add(textFields[i]);
        }
        textFieldPanel.revalidate();
        textFieldPanel.repaint();
        scrollPane.revalidate();
        scrollPane.repaint();
    }

    public List<JTextField> findTextFields() {
        List<JTextField> textFields = new ArrayList<>();
        Component[] components = textFieldPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JTextField) {
                textFields.add((JTextField) component);
            }
        }
        return textFields;
    }

    private void removeTextFields() {
        textFieldPanel.removeAll();
        textFieldPanel.revalidate();
        textFieldPanel.repaint();
        scrollPane.revalidate();
        scrollPane.repaint();
    }

    public JLabel getFileLabel() {
        return fileLabel;
    }
}

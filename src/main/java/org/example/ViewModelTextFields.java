package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.*;
import java.util.List;

public class ViewModelTextFields extends JPanel {
    private Main main;
    private DocumentGenerator documentGenerator;
    private JButton generateButton;
    public JButton buttonBackSpace;
    public JButton chooseFileButton;

    private JLabel chooseFileLabel;
    private ViewModelStartScreen viewModelStartScreen;
    private JPanel textFieldPanel;
    private JScrollPane scrollPane;
    private JScrollPane scrollPaneButton;
    private JComboBox<String> selectFilesComboBox;
    private ViewModelTable viewModelTable;
    private JPanel buttonPanel;
    private HashMap<String, List<String>> fileTagMap;

    private Map<String, String> tagValuesMap; // Map to store tag values

    public ViewModelTextFields(Main main, ViewModelStartScreen viewModelStartScreen, DocumentGenerator documentGenerator, ViewModelTable viewModelTable) {
        this.main = main;
        this.viewModelStartScreen = viewModelStartScreen;
        this.documentGenerator = documentGenerator;
        this.viewModelTable = viewModelTable;
        setLayout(null);
        setFocusable(true);
        tagValuesMap = new HashMap<>(); // Initialize the map
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
                removeTextFields();
                buttonPanel.removeAll();
                chooseFileLabel.setText("");
            }
        });
        add(buttonBackSpace);

        chooseFileLabel = new JLabel("Файлы не выбраны");
        chooseFileLabel.setBounds(300,85,400,30);
        add(chooseFileLabel);


        chooseFileButton = new JButton("Выбор файлов (doc/docx)");
        chooseFileButton.setBounds(300, 40, 200, 50);
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeTextFields();
                FileDialog fileDialog = new FileDialog((Frame) null, "Выберите файл", FileDialog.LOAD);
                fileDialog.setMultipleMode(true);
                fileDialog.setVisible(true);
                documentGenerator.selectedFiles = fileDialog.getFiles();
                if (documentGenerator.selectedFiles != null && documentGenerator.selectedFiles.length > 0) {
                    chooseFileLabel.setText("Выбранные файлы: ");
                    viewModelStartScreen.select = new String[documentGenerator.selectedFiles.length];
                    for (int i = 0; i < documentGenerator.selectedFiles.length; i++) {
                        viewModelStartScreen.select[i] = documentGenerator.selectedFiles[i].getName();
                    }

                    // Update combo box in ViewModelTable
                    if (viewModelTable != null) {
                        viewModelTable.clearComboBox();
                        viewModelTable.updateComboBox(viewModelStartScreen.select);
                        viewModelTable.getFileLabel().setText("Выбранные файлы: ");
                    }
                }
                documentGenerator.createFolder();
                if (viewModelStartScreen.verification) {
                    fileTagMap = documentGenerator.tagExtractor.writeTagsToMap(documentGenerator.selectedFiles);
                    generateFileButtons(fileTagMap);
                    generateTextFields(getAllTags(fileTagMap));
                } else {
                    documentGenerator.tagExtractor.writeTagsToCSV(documentGenerator.selectedFiles, documentGenerator.outputFolderPath);
                }
            }

        });

        add(chooseFileButton);

        generateButton = new JButton("Генерация документов");
        generateButton.setBounds(300, 500, 200, 50);
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
        scrollPane.setBounds(70, 125, 300, 360);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane);

        buttonPanel = new JPanel();
        add(buttonPanel);

        scrollPaneButton = new JScrollPane(buttonPanel);
        scrollPaneButton.setBounds(450,125,300,360);
        scrollPaneButton.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPaneButton);
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
                } else {
                    // Save the value to the map when the text field loses focus
                    tagValuesMap.put(placeholder, textField.getText());
                }
            }
        });
    }

    // Способ динамической генерации текстовых полей тегов с заполнителями
    public void generateTextFields(List<String> tags) {
        textFieldPanel.removeAll();
        textFieldPanel.setPreferredSize(new Dimension(200, tags.size() * 40));

        JTextField[] textFields = new JTextField[tags.size()];

        for (int i = 0; i < tags.size(); i++) {
            textFields[i] = new JTextField();
            textFields[i].setBounds(0, i * 40, 270, 30);
            String tag = tags.get(i);
            if (tag.startsWith("${key_ria_")) {
                tag = tag.substring(10); // Удалить префикс "${key_ria_"
            }
            String suffix = "}";
            if (tag.endsWith(suffix)) {
                tag = tag.substring(0, tag.length() - suffix.length()); // Удалить суффикс
            }
            addPlaceholder(textFields[i], tag); // Установите текст-заполнитель из обработанного тега

            // Set the text field value if it exists in the map
            if (tagValuesMap.containsKey(tag)) {
                textFields[i].setText(tagValuesMap.get(tag));
                textFields[i].setForeground(Color.BLACK);
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



    public void generateFileButtons(HashMap<String, List<String>> fileTagMap) {
        buttonPanel.removeAll();
        chooseFileLabel.setText("Выбранные файлы: Показать все теги");

        // Add "Show All Tags" button
        JButton showAllTagsButton = new JButton("Показать все теги");
        showAllTagsButton.setPreferredSize(new Dimension(250, 30));
        showAllTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateTextFields(getAllTags(fileTagMap));
                chooseFileLabel.setText("Выбранный файл: " + showAllTagsButton.getText());
            }
        });
        if (documentGenerator.selectedFiles.length != 0)
            buttonPanel.add(showAllTagsButton);

        // Add file-specific buttons
        int yOffset = 40; // Initial Y position for file buttons
        for (String fileName : fileTagMap.keySet()) {
            JButton fileButton = new JButton(fileName);
            fileButton.setPreferredSize(new Dimension(250, 35)); // Set fixed size
            fileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chooseFileLabel.setText("Выбранный файл: " + fileName); // Set the file name in the fileLabel
                    List<String> tags = fileTagMap.get(fileName);
                    generateTextFields(tags);
                }
            });
            buttonPanel.add(fileButton);
            yOffset += 40; // Increment Y position for the next button
        }

        // Calculate the total height needed for all buttons
        int totalHeight = yOffset;

        // Set the preferred size of buttonPanel to accommodate all buttons
        buttonPanel.setPreferredSize(new Dimension(200, totalHeight));

        // Revalidate and repaint buttonPanel
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }




    private List<String> getAllTags(HashMap<String, List<String>> fileTagMap) {
        Set<String> allTags = new HashSet<>();
        for (List<String> tags : fileTagMap.values()) {
            allTags.addAll(tags);
        }
        return new ArrayList<>(allTags);
    }
}

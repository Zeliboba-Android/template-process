package org.example.view;

import org.example.controller.DocumentGenerator;
import org.example.main.Main;
import org.example.model.TagDatabase;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

public class ViewModelTextFields extends JPanel {
    private Main main;
    private DocumentGenerator documentGenerator;
    private JButton generateButton;
    public JButton buttonBackSpace;
    public JButton chooseFileButton;
    private JButton showAllTagsButton;
    private JButton clearButton;
    private JLabel chooseFileLabel;
    private ViewModelStartScreen viewModelStartScreen;
    private JPanel textFieldPanel;
    private JScrollPane scrollPane;
    private JScrollPane scrollPaneButton;
    private ViewModelTable viewModelTable;
    private JPanel buttonPanel;
    private HashMap<String, List<String>> fileTagMap;
    private Map<String, String> tagValuesMap; // Map to store tag values
    private TagDatabase tagDatabase; // Database instance
    private static final Dimension COMPONENT_SIZE = new Dimension((int)(250 * 1.4), (int)(40 * 1.4));

    ViewModelTextFields(Main main, ViewModelStartScreen viewModelStartScreen, DocumentGenerator documentGenerator, ViewModelTable viewModelTable) {
        this.main = main;
        this.viewModelStartScreen = viewModelStartScreen;
        this.documentGenerator = documentGenerator;
        this.viewModelTable = viewModelTable;
        this.tagDatabase = new TagDatabase();
        ViewStyles.stylePanel(this);
        setLayout(null);
        setFocusable(true);
        tagValuesMap = new HashMap<>();
        initializeUI();
        // Добавляем слушатель для изменения размеров панели
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustScrollPaneSizes();
            }
        });
    }
    private void adjustScrollPaneSizes() {
        // Получаем родительский контейнер (окно)
        Window window = SwingUtilities.getWindowAncestor(this);

        if (window != null) {
            int windowWidth = window.getWidth();
            int windowHeight = window.getHeight();

            int marginTop = windowHeight / 6;   // Верхний отступ от окна
            int marginBottom = windowHeight / 6;  // Нижний отступ от окна
            int marginLeft = windowWidth / 18;  // Левый отступ от окна

            // Установим размеры для scrollPane с учетом отступов
            int scrollPaneWidth = (windowWidth / 2) - marginLeft;
            int scrollPaneHeight = windowHeight - marginTop - marginBottom;
            scrollPane.setBounds(marginLeft, marginTop, scrollPaneWidth, scrollPaneHeight);

            // Установим размеры для scrollPaneButton с учетом отступов
            int scrollPaneButtonX = ((windowWidth / 2 + 50) + marginLeft / 2);
            scrollPaneButton.setBounds(scrollPaneButtonX, marginTop, scrollPaneWidth - scrollPaneWidth / 7, scrollPaneHeight);

            // Изменение размеров кнопок и текстовых полей относительно размеров панели
            int buttonWidth = (int) ((scrollPaneButton.getWidth() / 3) * 1.6);
            int buttonHeight = scrollPaneButton.getHeight() / 10;

            int textFieldWidth = scrollPane.getWidth() - 50;
            int textFieldHeight = 30;

            // Изменяем размеры и положение кнопок
            int buttonY = scrollPaneHeight / 15;
            int buttonSpacing = 20; // Фиксированное расстояние между кнопками

            chooseFileButton.setBounds(scrollPaneButtonX - scrollPaneButtonX / 4, buttonY, buttonWidth, buttonHeight);
            clearButton.setBounds(scrollPaneButtonX - scrollPaneButtonX / 4 + buttonWidth + buttonSpacing, buttonY, buttonWidth, buttonHeight);

            chooseFileLabel.setBounds(scrollPaneButtonX - scrollPaneButtonX / 4, scrollPaneHeight / 6, buttonWidth, buttonHeight);
            generateButton.setBounds(scrollPaneButtonX - scrollPaneButtonX / 4, scrollPaneHeight + scrollPaneHeight / 3 - 20, buttonWidth, buttonHeight);

            // Динамическое изменение размеров текстовых полей
            int topPadding = 10;
            int padding = 10;
            textFieldPanel.setPreferredSize(new Dimension(textFieldWidth, findTextFields().size() * textFieldHeight + topPadding));

            for (JTextField textField : findTextFields()) {
                textField.setBounds(padding, topPadding, textFieldWidth, textFieldHeight);
                topPadding += textFieldHeight + padding;
            }

            revalidate();
            repaint();
        }
    }

    private boolean areAllTextFieldsFilled() {
        for (JTextField textField : findTextFields()) {
            String placeholder = (String) textField.getClientProperty("placeholder"); // Получаем плейсхолдер
            String text = textField.getText().trim();

            // Проверяем, заполнено ли текстовое поле
            if (text.isEmpty() || text.equals(placeholder)) {
                return false; // Если текстовое поле пустое или равно плейсхолдеру, возвращаем false
            }
        }
        return true; // Все текстовые поля заполнены корректно
    }

    private void updateGenerateButtonState() {
        generateButton.setEnabled(areAllTextFieldsFilled()); // Активируем или деактивируем кнопку
        generateButton.setFocusable(areAllTextFieldsFilled());
    }

    private void initializeUI() {
        buttonBackSpace = new JButton();
        buttonBackSpace.setText("⬅");
        Font font = buttonBackSpace.getFont();
        ViewStyles.styleButton(buttonBackSpace);
        buttonBackSpace.setFont(font.deriveFont(Font.PLAIN, 32));
        buttonBackSpace.setBounds(5, 5, 100, 70);  // Устанавливаем фиксированные размеры
        buttonBackSpace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.switchToPanel(viewModelStartScreen);
            }
        });
        add(buttonBackSpace);

        chooseFileLabel = new JLabel("Файлы не выбраны");
        chooseFileLabel.setBounds(300, 40, 400, 30); // Устанавливаем фиксированные размеры
        ViewStyles.styleLabel(chooseFileLabel);
        add(chooseFileLabel);

        chooseFileButton = new JButton("Выбор файлов (doc/docx)");
        chooseFileButton.setBounds(300, 80, 200, 50); // Устанавливаем фиксированные размеры
        ViewStyles.styleButton(chooseFileButton);
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeTextFields();
                tagValuesMap.clear();
                FileDialog fileDialog = new FileDialog((Frame) null, "Выберите файл", FileDialog.LOAD);
                fileDialog.setFile("*.doc;*.docx");
                fileDialog.setMultipleMode(true);
                fileDialog.setVisible(true);
                documentGenerator.selectedFiles = fileDialog.getFiles();

                if (documentGenerator.selectedFiles != null && documentGenerator.selectedFiles.length > 0) {
                    chooseFileLabel.setText("Выбранные файлы: ");
                    viewModelStartScreen.select = new String[documentGenerator.selectedFiles.length];
                    for (int i = 0; i < documentGenerator.selectedFiles.length; i++) {
                        viewModelStartScreen.select[i] = documentGenerator.selectedFiles[i].getName();
                    }

                    // Обновить поле со списком в ViewModelTable
                    if (viewModelTable != null) {
                        viewModelTable.clearComboBox();
                        viewModelTable.updateComboBox(viewModelStartScreen.select);
                        viewModelTable.getFileLabel().setText("Выбранные файлы: ");
                        viewModelTable.generateButtonUsingTable.setEnabled(true);
                        viewModelTable.createCSVButton.setEnabled(true);
                        viewModelTable.selectCSVButton.setEnabled(true);
                    }
                }
                documentGenerator.createFolder();
                if (viewModelStartScreen.verification) {
                    fileTagMap = documentGenerator.tagExtractor.writeTagsToMap(documentGenerator.selectedFiles);
                    generateFileButtons(fileTagMap);
                    generateTextFields(getAllTags(fileTagMap));
                }
            }
        });
        add(chooseFileButton);
        clearButton = new JButton("Очистить всё");
        ViewStyles.styleButton(clearButton);
        clearButton.setBounds(500, 80, 100, 50);
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeTextFields();
                removeFileButtons();
                chooseFileLabel.setText("Файлы не выбраны");
                generateButton.setEnabled(false);
                revalidate();
                repaint();
            }
        });
        add(clearButton);

        generateButton = new JButton("Генерация документов");
        ViewStyles.styleButton(generateButton);
        generateButton.setEnabled(false);
        generateButton.setFocusable(false); // Убираем фокус с кнопки
        generateButton.setBounds(300, 150, 200, 50); // Устанавливаем фиксированные размеры
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showAllTagsButton != null) {
                    showAllTagsButton.doClick();  // Имитируем нажатие на кнопку "Показать все теги"
                }

                // Генерируем документ
                documentGenerator.generateDocument();

                // Очищаем текстовые поля
                clearTextFields();

                tagValuesMap.clear();
            }
        });
        add(generateButton);

        textFieldPanel = new JPanel();
        textFieldPanel.setLayout(null);
        ViewStyles.stylePanel(textFieldPanel);

        scrollPane = new JScrollPane(textFieldPanel);
        scrollPane.setBounds(70, 125, 300, 360); // Устанавливаем фиксированные размеры
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        ViewStyles.styleScrollBar(scrollPane.getVerticalScrollBar());
        add(scrollPane);

        buttonPanel = new JPanel();
        ViewStyles.stylePanel(buttonPanel);
        add(buttonPanel);

        scrollPaneButton = new JScrollPane(buttonPanel);
        scrollPaneButton.setBounds(450, 125, 400, 360); // Устанавливаем фиксированные размеры
        scrollPaneButton.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneButton.getVerticalScrollBar().setUnitIncrement(16);
        ViewStyles.styleScrollBar(scrollPaneButton.getVerticalScrollBar());
        add(scrollPaneButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    // метод для добавления подсказок в текстовые поля
    private void addPlaceholder(JTextField textField, String tag) {
        String placeholder = tagDatabase.getPlaceholder(tag);
        if (placeholder == null) {
            placeholder = tag;
            tagDatabase.saveTag(tag);
        }

        // Добавляем тег в скобках к плейсхолдеру
        placeholder += " (" + tag + ")";

        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.putClientProperty("placeholder", placeholder); // Сохраняем плейсхолдер в свойство текстового поля

        String finalPlaceholder = placeholder;
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(finalPlaceholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                } else {
                    tagValuesMap.put(tag, textField.getText());
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                String inputText = textField.getText().trim();

                // Если поле пустое, сохраняем плейсхолдер как значение
                if (inputText.isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(finalPlaceholder);
                } else {
                    tagValuesMap.put(tag, inputText); // Сохраняем введенное значение
                }

                // Проверка для тегов с чекбоксами
                if (tag.equals("${key_ria_type_x_pr}") || tag.equals("${key_ria_type_x_bd59}") || tag.equals("${key_ria_type_x_bd34}")) {
                    if (!inputText.isEmpty() && !inputText.equals("0") && !inputText.equals("1")) {
                        textField.setBackground(Color.RED); // Изменяем фон на красный при ошибке
                        JOptionPane.showMessageDialog(null,
                                "Ошибка: Введите только 0 или 1 для тега " + tag,
                                "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                        textField.requestFocus(); // Фокус на поле для исправления
                    } else {
                        textField.setBackground(Color.WHITE); // Восстанавливаем стандартный фон
                    }
                }
            }
        });
    }

    // Способ динамической генерации текстовых полей тегов с заполнителями
    private void generateTextFields(List<String> tags) {
        textFieldPanel.removeAll();
        System.out.println(tagValuesMap);
        int padding = 10; // Отступы по бокам
        int topPadding = 10; // Отступ сверху для первого элемента
        textFieldPanel.setPreferredSize(new Dimension(textFieldPanel.getWidth(), tags.size() * 40 + topPadding));
        JTextField[] textFields = new JTextField[tags.size()];

        for (int i = 0; i < tags.size(); i++) {
            int currentIndex = i;
            textFields[i] = new JTextField();
            textFields[i].setBounds(padding, topPadding + i * 40, textFieldPanel.getWidth() - 2 * padding, 30); // Учет отступов
            String tag = tags.get(i);
            addPlaceholder(textFields[i], tag); // Устанавливаем текст-заполнитель из обработанного тега
            ViewStyles.styleTextField(textFields[i]);

            // Устанавливаем текст, если он уже есть в tagValuesMap
            if (tagValuesMap.containsKey(tag)) {
                textFields[i].setText(tagValuesMap.get(tag));
                textFields[i].setForeground(Color.BLACK);
            }

            textFields[i].addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_UP) { // Стрелка вверх
                        if (currentIndex > 0) {
                            JTextField previousField = textFields[currentIndex - 1];
                            previousField.requestFocus(); // Переход к предыдущему полю
                            fieldVisibility(previousField);
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) { // Стрелка вниз
                        if (currentIndex < textFields.length - 1) {
                            JTextField nextField = textFields[currentIndex + 1];
                            nextField.requestFocus(); // Переход к следующему полю
                            fieldVisibility(nextField);
                        }
                    }
                }
            });


            // Добавляем DocumentListener для обновления состояния кнопки генерации
            textFields[i].getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateGenerateButtonState(); // Проверка при вставке текста
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateGenerateButtonState(); // Проверка при удалении текста
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateGenerateButtonState(); // Проверка при изменении текста
                }
            });

            textFieldPanel.add(textFields[i]);
        }
        updateGenerateButtonState();
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

    private void removeFileButtons(){
        buttonPanel.removeAll();
        buttonPanel.revalidate();
        buttonPanel.repaint();
        scrollPaneButton.revalidate();
        scrollPaneButton.repaint();
    }

    private void generateFileButtons(HashMap<String, List<String>> fileTagMap) {
        buttonPanel.removeAll();
        chooseFileLabel.setText("Выбранные файлы: Показать все теги");

        // Устанавливаем BoxLayout для buttonPanel (вертикальное расположение)
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(Box.createVerticalStrut(10));  // 10 пикселей отступа

        // Кнопка "Показать все теги"
        showAllTagsButton = new JButton("Показать все теги");
        ViewStyles.styleButton(showAllTagsButton);
        showAllTagsButton.setPreferredSize(new Dimension(400, 50));
        showAllTagsButton.setMaximumSize(new Dimension(400, 50));
        showAllTagsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        showAllTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateTextFields(getAllTags(fileTagMap));
                chooseFileLabel.setText("Выбранный файл: " + showAllTagsButton.getText());
            }
        });

        // Добавляем кнопку "Показать все теги"
        if (documentGenerator.selectedFiles.length != 0)
            buttonPanel.add(showAllTagsButton);
        buttonPanel.add(Box.createVerticalStrut(10));  // 10 пикселей отступа
        // Кнопки для конкретных файлов
        for (String fileName : fileTagMap.keySet()) {
            JButton fileButton = new JButton(fileName);
            fileButton.setPreferredSize(new Dimension(400, 50));
            fileButton.setMaximumSize(new Dimension(400, 50));
            fileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            ViewStyles.styleButton(fileButton);
            fileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chooseFileLabel.setText("Выбранный файл: " + fileName); // Установка имени файла
                    List<String> tags = fileTagMap.get(fileName);
                    generateTextFields(tags); // Генерация текстовых полей для выбранного файла
                }
            });

            // Добавляем кнопку для каждого файла
            buttonPanel.add(fileButton);
            // Добавляем отступ после каждой кнопки
            buttonPanel.add(Box.createVerticalStrut(10));  // 10 пикселей отступа
        }

        // Обновляем buttonPanel
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void closeDatabase() {
        tagDatabase.close(); // Method to close the database connection
    }

    private void clearTextFields() {
        for (JTextField textField : findTextFields()) {
            textField.setText(""); // Очищаем текстовое поле
            textField.setForeground(Color.GRAY); // Возвращаем цвет плейсхолдера
            textField.setText((String) textField.getClientProperty("placeholder")); // Устанавливаем плейсхолдер
        }
    }

    private List<String> getAllTags(HashMap<String, List<String>> fileTagMap) {
        Set<String> allTags = new HashSet<>();
        for (List<String> tags : fileTagMap.values()) {
            allTags.addAll(tags);
        }
        return new ArrayList<>(allTags);
    }

    // Обеспечение видимости поля ввода
    private void fieldVisibility(JTextField field){
        SwingUtilities.invokeLater(() -> {
            field.scrollRectToVisible(field.getBounds()); // Прокрутка к полю
            scrollPane.getViewport().setViewPosition(new Point(0, field.getY())); // Принудительное обновление позиции
        });
    }
}
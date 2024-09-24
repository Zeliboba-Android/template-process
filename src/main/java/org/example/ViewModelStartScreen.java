package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;


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
    private JLabel universityLabel;
    public static BufferedImage logo;
    boolean verification;
    int selectedNumber = 1;
    private JFrame textFieldsFrameTextFields;
    private JFrame textFieldsFrameTable;
    String[] select;
    // Константы для одинакового размера компонентов
    private static final Dimension COMPONENT_SIZE = new Dimension(260, 40);
    private static final Dimension LABEL_SIZE = new Dimension(300, 50); // Размер надписи
    private static final Dimension IMAGE_SIZE = new Dimension(220, 165); // Размер изображения

    public ViewModelStartScreen(Main main, DocumentGenerator documentGenerator) {
        this.main = main;
        this.documentGenerator = documentGenerator;
        viewModelTextFields = new ViewModelTextFields(main,this, this.documentGenerator,viewModelTable);
        viewModelTable = new ViewModelTable(main,this, this.documentGenerator);
        try {
            logo = ImageIO.read(getClass().getClassLoader().getResource("image/Logo.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        initializeStartScreen();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Рисуем изображение (если оно загружено)
        if (logo != null) {
            g.drawImage(logo,
                    getWidth() / 2 -20 - IMAGE_SIZE.width / 2,
                    getHeight() / 8 - IMAGE_SIZE.height / 4,
                    IMAGE_SIZE.width,
                    IMAGE_SIZE.height,
                    null);
        }
    }


    public void initializeStartScreen() {
        // Применяем стиль к текущей панели
        ViewStyles.stylePanel(this);
        // Устанавливаем GridBagLayout как менеджер компоновки
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); // Отступы по умолчанию: сверху, слева, снизу, справа
        gbc.anchor = GridBagConstraints.CENTER; // Центрируем элементы
        gbc.fill = GridBagConstraints.NONE; // Компоненты не растягиваются
        // Создаем и стилизуем метку для выбора количества авторов
        labelChooseCountOfAuthor = new JLabel("Выберите количество авторов");
        ViewStyles.styleLabel(labelChooseCountOfAuthor);
        labelChooseCountOfAuthor.setPreferredSize(COMPONENT_SIZE); // Устанавливаем размер
        // Устанавливаем отступ слева для метки
        gbc.insets = new Insets(10, 10, 10, 0);
        gbc.gridy = 1;
        add(labelChooseCountOfAuthor, gbc);

        // Создаем и стилизуем JComboBox для выбора количества авторов
        Integer[] numbers = new Integer[10];
        for (int i = 0; i < 10; i++) {
            numbers[i] = i + 1;
        }
        authorComboBox = new JComboBox<>(numbers);
        ViewStyles.styleComboBox(authorComboBox);
        authorComboBox.setMaximumRowCount(10);
        authorComboBox.setPreferredSize(COMPONENT_SIZE); // Устанавливаем размер
        gbc.insets = new Insets(10, 0, 10, 0); // Сброс отступов для остальных элементов
        gbc.gridy = 2;
        add(authorComboBox, gbc);

        // Обработка выбора в JComboBox
        authorComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedNumber = (int) authorComboBox.getSelectedItem();
            }
        });
        universityLabel = new JLabel("RUDN University");
        ViewStyles.styleStartLabel(universityLabel);
        universityLabel.setPreferredSize(LABEL_SIZE);
        universityLabel.setHorizontalAlignment(SwingConstants.CENTER); // Центрируем текст
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0); // Отступ снизу для надписи
        add(universityLabel, gbc);
        // Создаем и стилизуем метку для выбора метода генерации
        labelChoosingGenerateMethod = new JLabel("Как сгенерировать документ?");
        ViewStyles.styleLabel(labelChoosingGenerateMethod);
        labelChoosingGenerateMethod.setPreferredSize(COMPONENT_SIZE); // Устанавливаем размер
        // Устанавливаем отступ слева для метки
        gbc.insets = new Insets(10, 10, 10, 0);
        gbc.gridy = 3;
        add(labelChoosingGenerateMethod, gbc);

        // Создаем и стилизуем кнопку для генерации с текстовыми полями
        buttonGenerateWithTextFields = new JButton("Использовать поля ввода");
        ViewStyles.styleButton(buttonGenerateWithTextFields);
        buttonGenerateWithTextFields.setPreferredSize(COMPONENT_SIZE); // Устанавливаем размер
        gbc.insets = new Insets(10, 0, 10, 0); // Сброс отступов для остальных элементов
        gbc.gridy = 4;
        add(buttonGenerateWithTextFields, gbc);
        buttonGenerateWithTextFields.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFieldsFrameTextFields = new JFrame("Генерация с текстовыми полями");
                textFieldsFrameTextFields.getContentPane().add(viewModelTextFields);
                textFieldsFrameTextFields.setSize(new Dimension(830, 750));
                textFieldsFrameTextFields.setMinimumSize(new Dimension(900, 800)); // Задаем минимальный размер
                textFieldsFrameTextFields.setLocationRelativeTo(null);
                textFieldsFrameTextFields.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                textFieldsFrameTextFields.setVisible(true);
                verification = true;
                main.disposeFrame(main.frame);
            }
        });


        // Создаем и стилизуем кнопку для генерации с помощью таблицы
        buttonGenerateWithTable = new JButton("Использовать таблицу");
        ViewStyles.styleButton(buttonGenerateWithTable);
        buttonGenerateWithTable.setPreferredSize(COMPONENT_SIZE); // Устанавливаем размер
        gbc.gridy = 5;
        add(buttonGenerateWithTable, gbc);
        buttonGenerateWithTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFieldsFrameTable = new JFrame("Генерация с помощью таблицы");
                textFieldsFrameTable.getContentPane().add(viewModelTable);
                textFieldsFrameTable.setSize(new Dimension(500, 700));
                textFieldsFrameTable.setMinimumSize(new Dimension(550, 750));
                textFieldsFrameTable.setLocationRelativeTo(null);
                textFieldsFrameTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                textFieldsFrameTable.setVisible(true);
                verification = false;
                main.disposeFrame(main.frame);
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

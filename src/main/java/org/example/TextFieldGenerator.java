package org.example;

import org.apache.commons.compress.archivers.dump.InvalidFormatException;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;

import javax.swing.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFieldGenerator {
    private ViewModelTextFields viewModelTextFields;

    private Set<String> Tags = new HashSet<>();
    public TextFieldGenerator(ViewModelTextFields viewModelTextFields) {
        this.viewModelTextFields = viewModelTextFields;
    }

    public int countTags(File[] files) {

        String filePath;
        String regex = "\\$\\{[^}]+\\}";
        Pattern pattern = Pattern.compile(regex);
        try {
            for (File file: files){
                filePath = file.getAbsolutePath();
                if (filePath.endsWith(".docx")) {
                    try (InputStream fis = new FileInputStream(filePath)) {
                        XWPFDocument document = new XWPFDocument(OPCPackage.open(fis));

                        // Перебираем все параграфы в документе
                        for (XWPFParagraph paragraph : document.getParagraphs()) {
                            String[] words = paragraph.getText().split("\\s+");
                            for (String word : words) {
                                if (file.isFile() && (file.getName().endsWith(".doc") || file.getName().endsWith(".docx"))) {
                                    // Читаем содержимое файла
                                    StringBuilder content = new StringBuilder();
                                    try {
                                        if (file.getName().endsWith(".doc")) {
                                            // Для файлов .doc
                                            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
                                            HWPFDocument doc = new HWPFDocument(fileInputStream);
                                            WordExtractor extractor = new WordExtractor(doc);
                                            String text = extractor.getText();
                                            Matcher matcher = pattern.matcher(text);
                                            while (matcher.find()) {
                                                String tag = matcher.group();
                                                if (!Tags.contains(tag)) {
                                                    Tags.add(tag);
                                                    System.out.println(tag);
                                                }
                                            }
                                            extractor.close();
                                        } else if (file.getName().endsWith(".docx")) {
                                            // Для файлов .docx
                                            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
                                            XWPFDocument doc = new XWPFDocument(fileInputStream);
                                            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
                                            String text = extractor.getText();
                                            Matcher matcher = pattern.matcher(text);
                                            while (matcher.find()) {
                                                String tag = matcher.group();
                                                if (!Tags.contains(tag)) {
                                                    Tags.add(tag);
                                                    System.out.println(tag);
                                                }
                                            }
                                            extractor.close();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        // Перебираем все таблицы в документе
                        for (XWPFTable table : document.getTables()) {
                            // Перебираем строки таблицы
                            for (XWPFTableRow row : table.getRows()) {
                                // Перебираем ячейки в строке таблицы
                                for (XWPFTableCell cell : row.getTableCells()) {
                                    // Получаем текст из ячейки и проверяем наличие тегов
                                    String cellText = cell.getText();
                                    if (cellText != null && cellText.matches("\\$\\{[^}]+\\}")) {
                                        System.out.println(cellText);
                                        Tags.add(cellText);
                                    }
                                }
                            }
                        }
                        document.close();
                    } catch (InvalidFormatException e) {
                        e.printStackTrace();
                    } catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Tags.size();
    }


    public JTextField[] generateTextFields(int numberOfFields) {
        JTextField[] textFields = new JTextField[numberOfFields];
        for (int i = 0; i < numberOfFields; i++) {
            textFields[i] = new JTextField();
            textFields[i].setBounds(150, 100 + i * 40, 200, 30);
            viewModelTextFields.add(textFields[i]);
        }
        viewModelTextFields.revalidate(); // Перерисовываем панель для отображения добавленных компонентов
        viewModelTextFields.repaint();
        return textFields;
    }
    public Set<String> getTags() {
        return Tags;
    }
}

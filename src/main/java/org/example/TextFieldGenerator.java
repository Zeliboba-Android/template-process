package org.example;

import org.apache.commons.compress.archivers.dump.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;

import javax.swing.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class TextFieldGenerator {
    private ViewModel viewModel;
    public TextFieldGenerator(ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public static int countTags(File[] files) {
        Set<String> Tags = new HashSet<>();
        String filePath;
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
                                if (word != null && (word.contains("${") || word.contains("(S{"))) {
                                    Tags.add(word);
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
                                    if (cellText != null && (cellText.contains("${") || cellText.contains("(S{"))) {
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
            viewModel.add(textFields[i]);
        }
        viewModel.revalidate(); // Перерисовываем панель для отображения добавленных компонентов
        viewModel.repaint();
        return textFields;
    }
}

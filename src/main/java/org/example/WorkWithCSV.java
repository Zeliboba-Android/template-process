package org.example;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class WorkWithCSV {
    public static void main(String[] args) {
        // Путь к папке с текстовыми документами
        String folderPath = WorkWithCSV.class.getClassLoader().getResource("").getPath();
        folderPath = URLDecoder.decode(folderPath, StandardCharsets.UTF_8);
        String csvFilePath = folderPath + "testTable.csv";
        // Путь к CSV файлу, в который будем записывать информацию
        String regex = "\\$\\{[^}]+\\}";
        Pattern pattern = Pattern.compile(regex);
        // Set для хранения уникальных тегов
        Set<String> uniqueTags = new HashSet<>();
        // Открываем поток для записи в CSV файл
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(csvFilePath,StandardCharsets.UTF_8)))) {
            // Получаем список файлов в указанной папке
            File folder = new File(folderPath);
            File[] files = folder.listFiles();
            if (files != null) {
                // Проходим по каждому файлу
                for (File file : files) {
                    // Проверяем, является ли файл текстовым документом
                    if (file.isFile() && (file.getName().endsWith(".doc") || file.getName().endsWith(".docx"))) {
                        // Читаем содержимое файла
                        StringBuilder content = new StringBuilder();
                        String extra = "";
                        try {
                            if (file.getName().endsWith(".doc")) {
                                // Для файлов .doc
                                FileInputStream fis = new FileInputStream(file.getAbsolutePath());
                                HWPFDocument document = new HWPFDocument(fis);
                                WordExtractor extractor = new WordExtractor(document);
                                String text = extractor.getText();
                                Matcher matcher = pattern.matcher(text);
                                while (matcher.find()) {
                                    String tag = matcher.group();
                                    if (!uniqueTags.contains(tag)) {
                                        writer.println(tag+";" + extra);
                                        uniqueTags.add(tag);
                                    }
                                }
                                extractor.close();
                            } else if (file.getName().endsWith(".docx")) {
                                // Для файлов .docx
                                FileInputStream fis = new FileInputStream(file.getAbsolutePath());
                                XWPFDocument document = new XWPFDocument(fis);
                                XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                                String text = extractor.getText();
                                Matcher matcher = pattern.matcher(text);
                                while (matcher.find()) {
                                    String tag = matcher.group();
                                    if (!uniqueTags.contains(tag)) {
                                        writer.println(tag+";"+ extra);
                                        uniqueTags.add(tag);
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
            System.out.println("Теги успешно записаны в файл " + csvFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

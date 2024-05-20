package org.example;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Денис on 20.05.2024
 */
public class TagExtractor {
    Set<String> uniqueTags = new HashSet<>();
    void writeTagsToCSV(File[] Files, String folderPath) {
        String csvFilePath = folderPath + File.separator + "tags.csv";
        String regex = "\\$\\{[^}]+\\}";
        Pattern pattern = Pattern.compile(regex);
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(csvFilePath, true), "cp1251")))) {
            for (File file : Files) {
                if (file.isFile() && (file.getName().endsWith(".doc") || file.getName().endsWith(".docx"))) {
                    try {
                        String text = readTextFromFile(file);
                        Matcher matcher = pattern.matcher(text);
                        while (matcher.find()) {
                            String tag = matcher.group();
                            if (!uniqueTags.contains(tag)) {
                                writer.println(tag + ";1");
                                uniqueTags.add(tag);
                            }
                        }
//                        writer.println();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> writeTagsToSet(File[] files) {
        uniqueTags = new HashSet<>();
        String regex = "\\$\\{[^}]+\\}";
        Pattern pattern = Pattern.compile(regex);
        for (File file : files) {
            if (file.isFile() && (file.getName().endsWith(".doc") || file.getName().endsWith(".docx"))) {
                try {
                    String text = readTextFromFile(file);
                    Matcher matcher = pattern.matcher(text);
                    while (matcher.find()) {
                        String tag = matcher.group();
                        if (!uniqueTags.contains(tag)) {
                            System.out.println(tag);
                            uniqueTags.add(tag);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return uniqueTags;
    }

    private String readTextFromFile(File file) throws IOException {
        StringBuilder text = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file.getAbsolutePath())) {
            if (file.getName().endsWith(".doc")) {
                HWPFDocument document = new HWPFDocument(fis);
                WordExtractor extractor = new WordExtractor(document);
                text.append(extractor.getText());
                extractor.close();
            } else if (file.getName().endsWith(".docx")) {
                XWPFDocument document = new XWPFDocument(fis);
                XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                text.append(extractor.getText());
                extractor.close();
            }
        }
        return text.toString();
    }
}

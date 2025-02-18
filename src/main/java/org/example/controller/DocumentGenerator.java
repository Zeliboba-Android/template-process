package org.example.controller;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import org.example.main.Main;
import org.example.model.*;
import org.example.view.ViewModelStartScreen;

import java.awt.*;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static org.example.view.ViewModelStartScreen.chosenDirectoryPath;
import static org.example.view.ViewModelStartScreen.isConvertToPdfSelected;

/**
 * @author Денис on 21.05.2024
 */
public class DocumentGenerator {
    private Main main;
    private Authors additionalAuthors;
    private Authors multiAuthors;
    public TagExtractor tagExtractor;
    private TagMap copyTagMap = new TagMap();
    private String outputFolderPath;

    public DocumentGenerator(Main main) {
        this.main = main;
        tagExtractor = new TagExtractor(this.main);
    }

    public String getOutputFolderPath() {
        return outputFolderPath;
    }

    public void setOutputFolderPath(String outputFolderPath) {
        this.outputFolderPath = outputFolderPath;
    }

    public void generateDocument(TagMap tagMap, File[] selectedFiles) {
        // Создаем изменяемый список для хранения файлов, которые нужно обработать
        List<File> filesToProcess = new ArrayList<>(List.of(selectedFiles));
        int countAuthors = ViewModelStartScreen.selectedNumber;
        if (countAuthors > 1) {
            // Проверяем, есть ли файлы с ключевыми словами
            boolean hasSpecialFiles = filesToProcess.stream().map(File::getName)
                    .anyMatch(name -> name.contains("main") || name.contains("additional") || name.contains("multi"));
            if (hasSpecialFiles)
                workWithSpecialFiles(filesToProcess, tagMap, countAuthors);
        }
        for (File file : filesToProcess) {
            replaceText(file, tagMap, file.getName());
        }

        // Конвертация в PDF, если включена опция convertToPdf
        if (isConvertToPdfSelected()) {
            convertAllWordDocumentsToPdf();
        }
    }

    private void workWithSpecialFiles(List<File> filesToProcess, TagMap tagMap, int countAuthors) {
        copyTagMap = new TagMap(new HashMap<>(tagMap.getTagMap()));
        additionalAuthors = new Authors(countAuthors);
        new SharedTagProcessor().fillAuthorsTags(copyTagMap, additionalAuthors);

        Iterator<File> iterator = filesToProcess.iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            String fileName = file.getName();
            if (fileName.contains("main")) {
                // Объединяем теги первого автора и общие теги
                processMainFile(file, fileName);
                iterator.remove();
            } else if (fileName.contains("additional")) {
                // Обрабатываем дополнительные файлы для остальных авторов
                processAdditionalFile(file, fileName);
                iterator.remove();
            } else if (fileName.contains("multi")) {
                processMultiFile(file, tagMap, countAuthors, fileName);
                iterator.remove();
            }
        }
    }

    private void processMainFile(File file, String fileName) {
        TagMap combinedTagMap = new TagMap(new HashMap<>(copyTagMap.getTagMap()));
        combinedTagMap.combineTags(additionalAuthors.getMainTagMap());
        replaceText(file, combinedTagMap, fileName.replace("main_", "1_"));
    }

    private void processAdditionalFile(File file, String fileName) {
        for (int i = 1; i < additionalAuthors.getTagMaps().size(); i += 3) {
            TagMap additionalTagMap = new TagMap(new HashMap<>(copyTagMap.getTagMap()));
            StringBuilder authorNumbers = new StringBuilder();
            for (int j = 0; j < 3; j++) {
                if (i + j < additionalAuthors.getTagMaps().size()) {
                    additionalTagMap.combineTags(additionalAuthors.getTagMapByIndex(i + j));
                    authorNumbers.append(i + j + 1 + "_");
                }
            }
            replaceText(file, additionalTagMap, fileName.replace("additional_", authorNumbers));
        }
    }

    private void processMultiFile(File file, TagMap tagMap, int countAuthors, String fileName) {
        copyTagMap = new TagMap(new HashMap<>(tagMap.getTagMap()));
        // Разбиваем теги по типу "key_ria_authorX..." для каждого автора
        multiAuthors = new Authors(countAuthors);
        new MultiTagProcessor().fillAuthorsTags(copyTagMap, multiAuthors);
        // Обрабатываем файлы, которые должны генерироваться для каждых авторов
        for (int i = 0; i < countAuthors; i++) {
            TagMap multiTagMap = new TagMap(new HashMap<>(copyTagMap.getTagMap()));
            multiTagMap.combineTags(multiAuthors.getTagMapByIndex(i));
            replaceText(file, multiTagMap, fileName.replace("multi_", (i + 1) + "_"));
        }
    }

    private void replaceText(File file, TagMap tags, String authorPrefix) {
        String fileName = file.getName();
        try {
            // Проверка наличия пустых значений в TagMap
            boolean hasEmptyValues = checkForEmptyValues(tags);
            if (!hasEmptyValues) {
                String newFolderPath = outputFolderPath + File.separator + "Word";
                createFolderIfNotExists(new File(newFolderPath));
                String newFilePath = newFolderPath + File.separator + authorPrefix;
                if (fileName.endsWith(".doc")) {
                    WordDOC wordDOC = new WordDOC(tags, file);
                    wordDOC.changeFile(newFilePath);
                } else if (fileName.endsWith(".docx")) {
                    WordDOCX wordDOCX = new WordDOCX(tags, file);
                    wordDOCX.changeFile(newFilePath);
                } else
                    System.out.println("Файл " + fileName + " не формата doc/docx");
            } else {
                System.out.println("Не удалось изменить файл" + fileName + ".Обнаружены пустые значения.");
            }
        } catch (IOException e) {
            // обработка исключения в случае возникновения ошибки при изменении файла
            System.err.println("Ошибка при изменении файла " + fileName);
            throw new RuntimeException(e);
        }
    }

    // Метод для чтения всех Word документов и их конвертации в PDF
    private void convertAllWordDocumentsToPdf() {
        File wordFolder = new File(outputFolderPath, "Word");
        File pdfFolder = new File(outputFolderPath, "PDF");

        createFolderIfNotExists(pdfFolder);
        // Читаем файлы из папки Word
        File[] wordFiles = wordFolder.listFiles((dir, name) -> name.endsWith(".docx"));

        if (wordFiles != null) {
            for (File wordFile : wordFiles) {
                String pdfFileName = wordFile.getName().replace(".docx", ".pdf");
                File pdfFile = new File(pdfFolder, pdfFileName);
                convertDocxToPdf(wordFile.getAbsolutePath(), pdfFile.getAbsolutePath());
            }
        }
    }

    // Метод конвертации DOCX в PDF
    public static void convertDocxToPdf(String docPath, String pdfPath) {
        IConverter converter = null;
        try {
            InputStream docxInputStream = new FileInputStream(docPath);
            OutputStream outputStream = new FileOutputStream(pdfPath);
            converter = LocalConverter.builder().build();
            converter.convert(docxInputStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (converter != null) {
                // Явно закрываем конвертер
                converter.shutDown();
            }
        }
    }

    // функция проверяет есть ли в теге что-то или там пусто, или null и выводит соответсвующее сообщение в консоль,
    // а если значение есть, то возвращает false
    private boolean checkForEmptyValues(TagMap tagMap) {
        boolean hasEmptyValues = false;
        for (Map.Entry<String, String> entry : tagMap.getTagMap().entrySet()) {
            String value = entry.getValue();
            if (value == null || value.isEmpty()) {
                // Обработка пустого значения
                System.out.println("Пустое значение для ключа: " + entry.getKey());
                hasEmptyValues = true;
            }
        }
        return hasEmptyValues;
    }

    // Метод для создания папки сохранения
    public void createFolder() {
        // Получаем текущую дату и время
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        String currentDateTime = sdf.format(new Date());
        String targetFolder = chosenDirectoryPath != null ? chosenDirectoryPath : getClass().getClassLoader().getResource("").getPath();
        targetFolder = URLDecoder.decode(targetFolder, StandardCharsets.UTF_8);
        outputFolderPath = targetFolder + File.separator + currentDateTime;
        // Создаем папку
        File outputFolder = new File(outputFolderPath);
        createFolderIfNotExists(outputFolder);
    }

    // Метод для открытия папки после генерации документов
    public void openFolder(String outputFolderPath) {
        try {
            Desktop.getDesktop().open(new File(outputFolderPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Создание папки при ее отсутствии
    private void createFolderIfNotExists(File folder) {
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }
}
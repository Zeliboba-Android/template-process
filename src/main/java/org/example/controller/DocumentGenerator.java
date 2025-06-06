package org.example.controller;

import org.example.model.*;
import org.example.view.ViewModelStartScreen;

import java.io.*;
import java.util.*;
import java.util.List;

import static org.example.view.ViewModelStartScreen.isConvertToPdfSelected;

/**
 * @author Денис on 21.05.2024
 */
public class DocumentGenerator {
    private FileManager fileManager;
    private TagMap copyTagMap;

    public DocumentGenerator(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void generateDocument(TagMap tagMap, File[] selectedFiles) {
        // Создаем изменяемый список для хранения файлов, которые нужно обработать
        List<File> filesToProcess = new ArrayList<>(List.of(selectedFiles));
        int countAuthors = ViewModelStartScreen.selectedNumber;
        if (countAuthors > 1) {
            fileManager.preprocessAdditionalFiles(filesToProcess, countAuthors);
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
            PdfConverter pdfConverter = new PdfConverter(fileManager.getOutputFolderPath());
            pdfConverter.convertAllWordDocumentsToPdf();
        }
    }

    private void workWithSpecialFiles(List<File> filesToProcess, TagMap tagMap, int countAuthors) {
        copyTagMap = tagMap.copyTagMap();
        Authors additionalAuthors = new Authors(countAuthors);
        SharedTagProcessor sharedTagProcessor = new SharedTagProcessor();
        sharedTagProcessor.fillAuthorsTags(copyTagMap, additionalAuthors);

        Iterator<File> iterator = filesToProcess.iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            String fileName = file.getName();
            if (fileName.contains("main")) {
                // Объединяем теги первого автора и общие теги
                processMainFile(file, fileName, additionalAuthors);
                iterator.remove();
            } else if (fileName.contains("multi")) {
                processMultiFile(file, tagMap, countAuthors, fileName);
                iterator.remove();
            }
        }
    }

    private void processMainFile(File file, String fileName, Authors additionalAuthors) {
        TagMap combinedTagMap = copyTagMap.copyTagMap();
        combinedTagMap.combineTags(additionalAuthors.getMainTagMap());
        replaceText(file, combinedTagMap, fileName.replace("main_", "1_"));
    }

    private void processMultiFile(File file, TagMap tagMap, int countAuthors, String fileName) {
        copyTagMap = tagMap.copyTagMap();
        // Разбиваем теги по типу "key_ria_authorX..." для каждого автора
        Authors multiAuthors = new Authors(countAuthors);
        MultiTagProcessor multiTagProcessor = new MultiTagProcessor();
        multiTagProcessor.fillAuthorsTags(copyTagMap, multiAuthors);
        // Обрабатываем файлы, которые должны генерироваться для каждых авторов
        for (int i = 0; i < countAuthors; i++) {
            TagMap multiTagMap = copyTagMap.copyTagMap();
            multiTagMap.combineTags(multiAuthors.getTagMapByIndex(i));
            replaceText(file, multiTagMap, fileName.replace("multi_", (i + 1) + "_"));
        }
    }

    private void replaceText(File file, TagMap tags, String authorPrefix) {
        String fileName = file.getName();
        // Проверка наличия пустых значений в TagMap
        boolean hasEmptyValues = checkForEmptyValues(tags);
        if (!hasEmptyValues) {
            String newFolderPath = fileManager.getOutputFolderPath() + File.separator + "Word";
            fileManager.createFolderIfNotExists(new File(newFolderPath));
            String newFilePath = newFolderPath + File.separator + authorPrefix;
            if (fileName.endsWith(".doc")) {
                WordDOC.createFile(tags, file, newFilePath);
            } else if (fileName.endsWith(".docx")) {
                WordDOCX.createFile(tags, file, newFilePath);
            } else
                System.out.println("Файл " + fileName + " не формата doc/docx");
        } else {
            System.out.println("Не удалось изменить файл" + fileName + ".Обнаружены пустые значения.");
        }
    }

    // функция проверяет есть ли в теге что-то или там пусто, или null и выводит соответсвующее сообщение в консоль,
    // а если значение есть, то возвращает false
    private boolean checkForEmptyValues(TagMap tagMap) {
        boolean hasEmptyValues = false;
        for (Map.Entry<String, String> entry : tagMap.entrySet()) {
            String value = entry.getValue();
            if (value == null || value.isEmpty()) {
                // Обработка пустого значения
                System.out.println("Пустое значение для ключа: " + entry.getKey());
                hasEmptyValues = true;
            }
        }
        return hasEmptyValues;
    }
}
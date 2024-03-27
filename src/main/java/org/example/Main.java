package org.example;

import java.io.IOException;

public class Main {
    private static TagMap tagMap;
    // функция заполнения значений тегов
    private static void fillTags(){
        tagMap = new TagMap();
        tagMap.getTagMap().put("${fio}", "Матюшкин Денис Владимирович");
        tagMap.getTagMap().put("${birth_date}", "12.11.2003");
        tagMap.getTagMap().put("${domicile}", "г. Москва, ул. Путина, д. 1");
    }
    private static void replaceTextDoc(){
        // создание экземпляра класса WordDOC
        WordDOC wordDOC = new WordDOC();
        try {
            // вызов функции заполнения тегов
            fillTags();
            // вызов метода изменения файла
            wordDOC.changeFile(tagMap);
            System.out.println("Файл .doc успешно изменен");
        } catch (IOException e) {
            // обработка исключения в случае возникновения ошибки при изменении файла
            System.err.println("Ошибка при изменении файла .doc:");
            throw new RuntimeException(e);
        }
    }

    private static void replaceTextDocx(){
        // создание экземпляра класса WordDOC
        WordDOCX wordDOCX = new WordDOCX();
        try {
            // вызов функции заполнения тегов
            fillTags();
            // вызов метода изменения файла
            wordDOCX.changeFile(tagMap);
            System.out.println("Файл .docx успешно изменен");
        } catch (IOException e) {
            // обработка исключения в случае возникновения ошибки при изменении файла
            System.err.println("Ошибка при изменении файла .docx:");
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        replaceTextDocx();
        replaceTextDoc();
    }
}
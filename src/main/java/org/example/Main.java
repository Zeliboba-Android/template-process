package org.example;

import java.io.IOException;

public class Main {
    private static TagMap tagMap;
    // функция заполнения значений тэгов
    public static void fillTags(){
        tagMap = new TagMap();
        tagMap.getTagMap().put("${fio}", "Матюшкин Денис Владимирович");
        tagMap.getTagMap().put("${birth_date}", "12.11.2003");
        tagMap.getTagMap().put("${domicile}", "г. Москва, ул. Путина, д. 1");
    }
    public static void main(String[] args) {
        // создание экземпляра класса WordDOC
        WordDOC wordDOC = new WordDOC();
        try {
            fillTags();
            // вызов метода изменения файла
            wordDOC.changeFile(tagMap);
            System.out.println("Файл успешно изменен");
        } catch (IOException e) {
            // обработка исключения в случае возникновения ошибки при изменении файла
            System.err.println("Ошибка при изменении файла:");
            throw new RuntimeException(e);
        }
    }

}
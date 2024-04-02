package org.example;

import java.util.HashMap;

/**
 * Класс TagMap представляет словарь тегов и соответствующих им значений.
 * Используется для хранения этих тегов.
 */
public class TagMap {
    private HashMap<String, String> tagMap;
    TagMap(){
        tagMap = new HashMap<>();
        // заполнение тегов и их значений по умолчанию
        tagMap.put("${fio}", "");
        tagMap.put("${date}", "");
        tagMap.put("${post}", "");
        tagMap.put("${company}", "");
        tagMap.put("${decode}", "");
        tagMap.put("${chief}", "");
    }

    // метод для возврата словаря тегов и их значений
    public HashMap<String, String> getTagMap(){
        return tagMap;
    }
}

package org.example;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс TagMap представляет словарь тегов и соответствующих им значений.
 * Используется для хранения этих тегов.
 */
public class TagMap {
    private HashMap<String, String> tagMap;
    TagMap(){
        tagMap = new HashMap<>();
        // заполнение тегов и их значений по умолчанию
//        tagMap.put("${fio}"," ");
//        tagMap.put("${date}"," ");
//        tagMap.put("${post}"," ");
//        tagMap.put("${company}"," ");
//        tagMap.put("${decode}"," ");
//        tagMap.put("${chief}"," ");
    }
    // метод для возврата словаря тегов и их значений
    public Map<String, String> getTagMap(){
        return tagMap;
    }
}

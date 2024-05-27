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
    }
    // метод для возврата словаря тегов и их значений
    public Map<String, String> getTagMap(){
        return tagMap;
    }

    public void removeTag(String tag) {
        tagMap.remove(tag);
    }

    // метод для добавления нового тега и его значения
    public void addTag(String tag, String value) {
        tagMap.put(tag, value);
    }
}

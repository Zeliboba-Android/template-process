package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Денис on 22.05.2024
 */
public class Authors {
    private List<TagMap> tagAuthors;
    private int countAuthors;

    public Authors(int countAuthors) {
        this.countAuthors = countAuthors;
        tagAuthors = new ArrayList<>();
        for (int i = 0; i < countAuthors; i++){
            tagAuthors.add(new TagMap());
        }
    }

    // Метод для получения тегов первого автора (основной TagMap)
    public TagMap getMainTagMap() {
        if (tagAuthors == null || tagAuthors.isEmpty()) {
            return new TagMap(new HashMap<>());
        }
        return tagAuthors.get(0);
    }

    // Метод для получения тегов автора по индексу
    public TagMap getTagMapByIndex(int index) {
        if (tagAuthors == null || tagAuthors.size() <= index) {
            return new TagMap(new HashMap<>());
        }
        return tagAuthors.get(index);
    }

    public List<TagMap> getTagMaps() {
        return tagAuthors;
    }

    public void addTagMap(TagMap tagMap) {
        tagAuthors.add(tagMap);
    }

    public void addTagToAuthor(int authorIndex, String tag, String value) {
        if (authorIndex >= 0 && authorIndex < tagAuthors.size()) {
            tagAuthors.get(authorIndex).addTag(tag, value);
        }
    }
}

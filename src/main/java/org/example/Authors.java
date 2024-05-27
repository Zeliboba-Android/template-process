package org.example;

import java.util.ArrayList;
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

package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // Создание экземпляра класса WordDOC
        WordDOC wordDOC = new WordDOC();
        try {
            // Вызов метода изменения файла
            wordDOC.changeFile();
            System.out.println("Файл успешно изменен");
        } catch (IOException e) {
            // Обработка исключения в случае возникновения ошибки при изменении файла
            System.err.println("Ошибка при изменении файла:");
            throw new RuntimeException(e);
        }
    }
}
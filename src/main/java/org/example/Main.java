package org.example;

import javax.swing.*;
import java.awt.*;

public class Main {
    public JFrame frame;
    ViewModelStartScreen viewModelStartScreen;
    private DocumentGenerator documentGenerator;
    Main() {
        documentGenerator = new DocumentGenerator(this);
        viewModelStartScreen = new ViewModelStartScreen(this, documentGenerator);
        generateFrame();
    }
    void generateFrame(){
        frame = new JFrame("Генерация документов"); // Создаем главное окно
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Устанавливаем операцию закрытия
        frame.getContentPane().add(viewModelStartScreen); // Добавляем ViewModel в контейнер главного окна
        frame.setSize(300,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true); // Делаем окно видимым
    }

    void disposeFrame(Frame frame){
        frame.dispose();
    }

    public static void main(String[] args) {
        new Main();
    }
}
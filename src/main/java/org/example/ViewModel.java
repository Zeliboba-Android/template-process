package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ViewModel extends JPanel {
    private Main main;
    private JButton generateButton;
    private JTextField textFieldFIO;
    private JTextField textFieldBirthDay;
    private JTextField textFieldesidence;
    private JRadioButton radioButtonDOC;
    private JRadioButton radioButtonDOCX;
    private JLabel labelTypeOfDocument;

    public ViewModel(Main main){
        this.main = main;
        setLayout(null);
        setFocusable(true);
        initializeUI();
    }
    public void generateDocument(){
        if (radioButtonDOC.isSelected()){
            main.replaceTextDoc();
        }
        else if (radioButtonDOCX.isSelected()){
            main.replaceTextDocx();
        }
        else {
            JOptionPane.showMessageDialog(this, "Please select a document type.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void initializeUI(){
        textFieldFIO = new JTextField();
        textFieldFIO.setBounds(150, 100, 200, 30);
        addPlaceholder(textFieldFIO,"Enter your FIO");

        add(textFieldFIO);

        textFieldBirthDay = new JTextField();
        textFieldBirthDay.setBounds(150, 150, 200, 30);
        addPlaceholder(textFieldBirthDay,"Enter your date of birth");
        add(textFieldBirthDay);

        textFieldesidence = new JTextField();
        textFieldesidence.setBounds(150, 200, 200, 30);
        addPlaceholder(textFieldesidence,"Enter your place of residence");
        add(textFieldesidence);

        labelTypeOfDocument = new JLabel();
        labelTypeOfDocument.setBounds(150,250,200,30);
        labelTypeOfDocument.setText("Select the document type");
        Font font = labelTypeOfDocument.getFont();
        labelTypeOfDocument.setFont(font.deriveFont(Font.BOLD,14));
        add(labelTypeOfDocument);

        radioButtonDOC = new JRadioButton(".doc");
        radioButtonDOC.setBounds(150, 300, 80, 30);
        add(radioButtonDOC);

        radioButtonDOCX = new JRadioButton(".docx");
        radioButtonDOCX.setBounds(230, 300, 100, 30);
        add(radioButtonDOCX);

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(radioButtonDOC);
        genderGroup.add(radioButtonDOCX);

        generateButton = new JButton("Generate document");
        generateButton.setBounds(150,350,200,50);
        generateButton.setRolloverEnabled(false);
        generateButton.setBackground(Color.lightGray);
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateDocument();
            }
        });
        add(generateButton);
    }
    private void addPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
    }


    public String getTextFieldFIO() {
        String fio = textFieldFIO.getText().trim();
        if (fio.isEmpty() || fio.equals("Enter your FIO")){
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your FIO.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
        return fio;
    }

    public String getTextFieldBirthday() {
        String birthday = textFieldBirthDay.getText().trim();
        if (birthday.isEmpty() || birthday.equals("Enter your date of birth")){
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your date of birth.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
        return birthday;
    }

    public String getTextFieldesidence() {
        String residence = textFieldesidence.getText().trim();
        if (residence.isEmpty() || residence.equals("Enter your place of residence")){
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your place of residence.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
        return residence;
    }
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.black);
    }

}

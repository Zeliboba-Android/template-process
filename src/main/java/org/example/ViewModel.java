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
    private JTextField textFieldDate;
    private JTextField textFieldPost;
    private JTextField textFieldCompany;
    private JTextField textFieldDecode;
    private JTextField textFieldChief;
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
        textFieldFIO.setBounds(150, 50, 200, 30);
        addPlaceholder(textFieldFIO,"Enter your FIO");
        add(textFieldFIO);

        textFieldDate = new JTextField();
        textFieldDate.setBounds(150, 100, 200, 30);
        addPlaceholder(textFieldDate,"Enter date");
        add(textFieldDate);

        textFieldPost = new JTextField();
        textFieldPost.setBounds(150, 150, 200, 30);
        addPlaceholder(textFieldPost,"Enter your post");
        add(textFieldPost);

        textFieldCompany = new JTextField();
        textFieldCompany.setBounds(150, 200, 200, 30);
        addPlaceholder(textFieldCompany,"Enter your company");
        add(textFieldCompany);

        textFieldDecode = new JTextField();
        textFieldDecode.setBounds(150, 250, 200, 30);
        addPlaceholder(textFieldDecode,"Enter your decode");
        add(textFieldDecode);

        textFieldChief = new JTextField();
        textFieldChief.setBounds(150, 300, 200, 30);
        addPlaceholder(textFieldChief,"Enter your chief");
        add(textFieldChief);

        labelTypeOfDocument = new JLabel();
        labelTypeOfDocument.setBounds(150,350,200,30);
        labelTypeOfDocument.setText("Select the document type");
        Font font = labelTypeOfDocument.getFont();
        labelTypeOfDocument.setFont(font.deriveFont(Font.BOLD,14));
        add(labelTypeOfDocument);

        radioButtonDOC = new JRadioButton(".doc");
        radioButtonDOC.setBounds(150, 400, 80, 30);
        add(radioButtonDOC);

        radioButtonDOCX = new JRadioButton(".docx");
        radioButtonDOCX.setBounds(230, 400, 100, 30);
        add(radioButtonDOCX);

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(radioButtonDOC);
        genderGroup.add(radioButtonDOCX);

        generateButton = new JButton("Generate document");
        generateButton.setBounds(150,450,200,50);
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

    public String getTextFieldDate() {
        String date = textFieldDate.getText().trim();
        if (date.isEmpty() || date.equals("Enter date")){
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter date.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
        return date;
    }

    public String getTextFieldPost() {
        String post = textFieldPost.getText().trim();
        if (post.isEmpty() || post.equals("Enter your post")){
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your post.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
        return post;
    }

    public String getTextFieldCompany() {
        String company = textFieldCompany.getText().trim();
        if (company.isEmpty() || company.equals("Enter your company")){
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your company.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
        return company;
    }

    public String getTextFieldDecode() {
        String decode = textFieldDecode.getText().trim();
        if (decode.isEmpty() || decode.equals("Enter your decode")){
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your decode.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
        return decode;
    }

    public String getTextFieldChief() {
        String chief = textFieldChief.getText().trim();
        if (chief.isEmpty() || chief.equals("Enter your chief")){
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter Enter your chief.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
        return chief;
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.black);
    }
}

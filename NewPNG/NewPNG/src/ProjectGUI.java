/**
 * NewPNG
 * Submitted by:
 * Tom Basha 	ID# 311425714
 * Roei Lahav	ID# 315808469
 */

import Main.CompressAndDecode;
import Main.Compression;
import Main.Decoding;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

class ProjectGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("NewPNG");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 300);

        /********** Menu Bar **********/
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBounds(0, 0, 700, 20);

        // Menu Bar > Instructions
        JMenu instructionsMenu = new JMenu("Instructions");
        JMenuItem instHead = new JMenuItem("How to use NewPNG?");
        JMenuItem instItem1 = new JMenuItem("1. Select a bmp or NewPNG image by clicking the \'Select file\' button");
        JMenuItem instItem2 = new JMenuItem("2. Select where to save the file by clicking the \'Change save directory\' button");
        JMenuItem instItem21 = new JMenuItem("     2.1 Default directory set to \'Project folder\\Images\\\' ");
        JMenuItem instItem3 = new JMenuItem("3. Select the operation you want to perform");
        JMenuItem instItem31 = new JMenuItem("     3.1. Compression");
        JMenuItem instItem32 = new JMenuItem("     3.2. Decoding");
        JMenuItem instItem33 = new JMenuItem("     3.3. Compression and decoding");
        JMenuItem instItem4 = new JMenuItem("4. Click 'Start' to perform the selected operation");
        JMenuItem instItem5 = new JMenuItem("5. When the operation is finished click 'Reset' to start over");

        instructionsMenu.add(instHead);
        instructionsMenu.addSeparator();
        instructionsMenu.add(instItem1);
        instructionsMenu.add(instItem2);
        instructionsMenu.add(instItem21);
        instructionsMenu.add(instItem3);
        instructionsMenu.add(instItem31);
        instructionsMenu.add(instItem32);
        instructionsMenu.add(instItem33);
        instructionsMenu.add(instItem4);
        instructionsMenu.add(instItem5);


        menuBar.add(instructionsMenu);

        // Menu Bar > Students
        JMenu studentsMenu = new JMenu("Students");
        menuBar.add(studentsMenu);
        JMenuItem sTom = new JMenuItem("Tom Basha");
        JMenuItem sTomID = new JMenuItem("ID: 311425714");
        JMenuItem sRoei = new JMenuItem("Roei Lahav");
        JMenuItem sRoeiID = new JMenuItem("ID: 315808469");
        studentsMenu.add(sTom);
        studentsMenu.add(sTomID);
        studentsMenu.addSeparator();
        studentsMenu.add(sRoei);
        studentsMenu.add(sRoeiID);

        /********** Header **********/
        JLabel header = new JLabel("NewPNG");
        header.setFont(new Font("Ariel", Font.BOLD, 30));
        header.setBounds(50, 60, 600, 30);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, Color.BLACK));

        /********** Selectors **********/
        JTextField filePath = new JTextField();
        JTextField saveToPath = new JTextField();
        JButton filePathBtn = new JButton("Select file");
        JButton savePathBtn = new JButton("Change save directory");

        filePath.setBounds(50, 100, 410, 25);
        filePathBtn.setBounds(470, 100, 180, 25);
        filePath.setEditable(false);
        filePath.setBackground(Color.WHITE);

        saveToPath.setBounds(50, 130, 410, 25);
        savePathBtn.setBounds(470, 130, 180, 25);
        saveToPath.setEditable(false);
        File directory = new File(System.getProperty("user.dir") + "\\Images\\");
        if (!directory.exists()) {
            directory.mkdir();
        }
        saveToPath.setText(System.getProperty("user.dir") + "\\Images\\");
        saveToPath.setBackground(Color.WHITE);

        JFileChooser fileSelector = new JFileChooser(new java.io.File("."));
        fileSelector.setDialogTitle("Select BMP or NewPNG file");
        JFileChooser saveToSelector = new JFileChooser(new java.io.File("."));
        saveToSelector.setApproveButtonText("Save here");
        saveToSelector.setDialogTitle("Select destination folder");
        saveToSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


        /********** Options **********/
        JRadioButtonMenuItem compressRadio = new JRadioButtonMenuItem();
        compressRadio.setText("Compress");
        compressRadio.setEnabled(false);
        compressRadio.setBounds(180, 165, 100, 20);
        compressRadio.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.BLACK));


        JRadioButtonMenuItem decodingRadio = new JRadioButtonMenuItem();
        decodingRadio.setText("Decode");
        decodingRadio.setEnabled(false);
        decodingRadio.setBounds(280, 165, 70, 20);
        decodingRadio.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.BLACK));

        JRadioButtonMenuItem bothRadio = new JRadioButtonMenuItem();
        bothRadio.setText("Compress & Decode");
        bothRadio.setEnabled(false);
        bothRadio.setBounds(365, 165, 200, 20);
        bothRadio.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.BLACK));

        ButtonGroup optionsGroup = new ButtonGroup();
        optionsGroup.add(compressRadio);
        optionsGroup.add(decodingRadio);
        optionsGroup.add(bothRadio);

        /********** Buttons **********/
        JButton start = new JButton("Start!");
        start.setBounds(300, 200, 100, 30);
        start.setEnabled(false);

        JButton reset = new JButton("Reset");
        reset.setBounds(300, 430, 100, 30);
        reset.setEnabled(false);

        /********** Feed **********/
        JTextArea feed = new JTextArea(5, 5);
        feed.setBackground(Color.WHITE);
        feed.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
        JScrollPane scrollPane = new JScrollPane(feed);
        scrollPane.setBounds(50, 250, 600, 170);
        scrollPane.setVisible(false);
        feed.setEditable(false);
        frame.add(scrollPane);
        PrintStream out = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                feed.append("" + (char) (b & 0xFF));
                feed.setCaretPosition(feed.getDocument().getLength());
            }
        });
        System.setOut(out);


        /********** functions **********/
        filePathBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "BMP & NewPNG files", "bmp", "NewPNG");
                fileSelector.setFileFilter(filter);
                int returnVal = fileSelector.showOpenDialog(fileSelector);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    if (fileSelector.getSelectedFile().getName().toLowerCase().endsWith("bmp")) {
                        optionsGroup.clearSelection();
                        start.setEnabled(true);
                        compressRadio.setEnabled(true);
                        bothRadio.setEnabled(true);
                        decodingRadio.setEnabled(false);
                        compressRadio.setSelected(true);
                    } else {
                        optionsGroup.clearSelection();
                        start.setEnabled(true);
                        compressRadio.setEnabled(false);
                        bothRadio.setEnabled(false);
                        decodingRadio.setEnabled(true);
                        decodingRadio.setSelected(true);
                    }
                    filePath.setText(fileSelector.getSelectedFile().getPath());
                }

            }
        });


        savePathBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = saveToSelector.showOpenDialog(saveToSelector);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    saveToPath.setText(saveToSelector.getSelectedFile().getPath() + "\\");
                }

            }
        });

        compressRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!savePathBtn.getText().isEmpty())
                    start.setEnabled(true);
            }
        });

        decodingRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!savePathBtn.getText().isEmpty())
                    start.setEnabled(true);
            }
        });

        bothRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!savePathBtn.getText().isEmpty())
                    start.setEnabled(true);
            }
        });

        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setSize(700, 300);
                scrollPane.setVisible(false);
                reset.setVisible(false);
                reset.setEnabled(false);
                filePathBtn.setEnabled(true);
                savePathBtn.setEnabled(true);
                feed.setText("");

                if (fileSelector.getSelectedFile().getName().toLowerCase().endsWith("bmp")) {
                    start.setEnabled(true);
                    compressRadio.setEnabled(true);
                    bothRadio.setEnabled(true);
                    decodingRadio.setEnabled(false);
                } else {
                    start.setEnabled(true);
                    compressRadio.setEnabled(false);
                    bothRadio.setEnabled(false);
                    decodingRadio.setEnabled(true);
                }
            }
        });

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setSize(700, 530);
                scrollPane.setVisible(true);
                reset.setVisible(true);
                start.setEnabled(false);
                filePathBtn.setEnabled(false);
                savePathBtn.setEnabled(false);
                bothRadio.setEnabled(false);
                compressRadio.setEnabled(false);
                decodingRadio.setEnabled(false);

                String selectedFilePath = fileSelector.getSelectedFile().getPath();
                File selectedFile = new File(selectedFilePath);
                String selectedPath = saveToPath.getText();

                if (compressRadio.isSelected()) {
                    Compression compress = new Compression(selectedFile, selectedPath, reset);
                    compress.execute();
                } else if (decodingRadio.isSelected()) {
                    Decoding decoding = new Decoding(selectedFile, selectedPath, reset);
                    decoding.execute();
                } else {
                    String fileName = selectedFile.getName().substring(0, selectedFile.getName().length() - 3) + "NewPNG";

                    CompressAndDecode compAndDec = new CompressAndDecode(selectedFile, selectedPath, fileName ,reset);
                    compAndDec.execute();
                }
            }
        });

        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(menuBar);
        frame.setLayout(null);
        frame.add(header);
        frame.add(filePath);
        frame.add(saveToPath);
        frame.add(filePathBtn);
        frame.add(savePathBtn);
        frame.add(compressRadio);
        frame.add(decodingRadio);
        frame.add(bothRadio);
        frame.add(start);
        frame.add(reset);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
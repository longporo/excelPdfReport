import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;

/**
 * The Frame class<br>
 *
 * @author Zihao Long
 * @version 1.0, 2021年09月26日 03:28
 * @since excelToPdf 0.0.1
 */
public class Frame {

    /**
     * GUI info
     */
    private JTextPane log_textarea;
    private JTextField excel_input;
    private JButton excel_btn;
    private JTextField pdf_input;
    private JButton pdf_btn;
    private JButton start_btn;
    private JPanel excel_div;
    private JPanel pdf_div;
    private JPanel start_div;
    private JPanel body;
    private JComboBox optionSelect;

    public static void main(String[] args) throws IOException {
        // init frame
        JFrame frame = new JFrame("Excel PDF Reports");
        frame.setSize(575, 400);
        frame.setContentPane(new Frame().body);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // window vertical center
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width / 2;
        int screenHeight = screenSize.height / 2;
        int height = frame.getHeight();
        int width = frame.getWidth();
        frame.setLocation(screenWidth - width / 2, screenHeight - height / 2);
    }

    public Frame() throws IOException {

        // log setting
        Logger root = Logger.getRootLogger();
        Appender appender = root.getAppender("WriterAppender");
        PipedReader reader = new PipedReader();
        Writer writer = new PipedWriter(reader);
        ((WriterAppender) appender).setWriter(writer);
        Thread t = new LogToGuiThread(reader);
        t.start();

        // excel file selecting button click event
        excel_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File[] fileArr = showExcelFileDialog();
                if (fileArr == null || fileArr.length == 0) {
                    return;
                }
                String filePath = "";
                for (int i = 0; i < fileArr.length; i++) {
                    File file = fileArr[i];
                    filePath += file.getAbsolutePath();
                    if (i != fileArr.length - 1) {
                        filePath += Constant.FILE_PATH_SPLIT_STR;
                    }
                }
                excel_input.setText(filePath);
            }
        });

        // directory selecting button click event
        pdf_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = showDirChoosingDialog();
                if (file == null) {
                    return;
                }
                pdf_input.setText(file.getAbsolutePath());
            }
        });

        // 'start' button click event
        start_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String excelFilePath = excel_input.getText();
                String pdfDirPath = pdf_input.getText();
                String selectedStr = (String) optionSelect.getSelectedItem();

                if ("Select an option".equalsIgnoreCase(selectedStr)) {
                    JOptionPane.showMessageDialog(null, "Please select an option.", "Warning", 2);
                    return;
                }

                if (" Select a folder or excel file...".equalsIgnoreCase(excelFilePath)) {
                    JOptionPane.showMessageDialog(null, "Please select a folder or excel file.", "Warning", 2);
                    return;
                }
                if (" Select a folder to save file...".equalsIgnoreCase(pdfDirPath)) {
                    JOptionPane.showMessageDialog(null, "Please select a folder to save file.", "Warning", 2);
                    return;
                }
                int confirmResult = JOptionPane.showConfirmDialog(null, "Start to generate?", "Prompt", 0);
                if (confirmResult == 1) {
                    return;
                }

                // Generate
                generatePdf(excelFilePath, pdfDirPath, selectedStr);
            }
        });
    }

    /**
     * The generation method<br>
     *
     * @param [excelFilePath, pdfDirPath, selectedStr]
     * @return void
     * @author Zihao Long
     */
    private static void generatePdf(String excelFilePath, String pdfDirPath, String selectedStr) {
        try {

            // get system info
            if ( Constant.FILE_PATH_NOTATION == null) {
                String osName = System.getProperty("os.name");
                if (osName.startsWith("Windows")) {
                    Constant.IS_WINDOWS = true;
                    Constant.FILE_PATH_NOTATION = "\\";
                } else {
                    // MacOs, linux
                    Constant.IS_MAC_OS = true;
                    Constant.FILE_PATH_NOTATION = "/";
                }
            }

            String fileName = "Main.pdf";
            if ("Generate PDF".equalsIgnoreCase(selectedStr)) {
                Constant.IS_GENERATE_PDF = true;
            } else if ("Combine files into one".equalsIgnoreCase(selectedStr)) {
                Constant.IS_GENERATE_PDF = false;
                fileName = "project-marking-combined.xlsx";
            }

            // set absolute path
            Constant.TARGET_FILE_PATH = pdfDirPath + Constant.FILE_PATH_NOTATION + fileName;

            Constant.logger.info("Reading excel file...");

            ExcelData.getExcelData(excelFilePath);

            Constant.logger.info("SUCCESS!!!");

            // Ask if open file
            int confirmResult = JOptionPane.showConfirmDialog(null, "File has been successfully generated! Open it?", "Prompt", 0);
            if (confirmResult == 1) {
                return;
            }
            openFile(Constant.TARGET_FILE_PATH);
        } catch (Exception e) {
//            e.printStackTrace();
            Constant.logger.error(e.getMessage());
        }
    }

    /**
     * Open file by command line<br>
     *
     * @param []
     * @return void
     * @author Zihao Long
     */
    private static void openFile(String filePath) throws IOException {
        if (Constant.IS_WINDOWS) {
            Runtime.getRuntime().exec("explorer.exe /select, " + filePath);
        } else if (Constant.IS_MAC_OS) {
            Runtime.getRuntime().exec("open " + filePath);
        }
    }

    /**
     * show excel file dialog<br>
     *
     * @param [type]
     * @return java.io.File
     * @author Zihao Long
     */
    public File[] showExcelFileDialog() {
        JFileChooser fileChooser = new JFileChooser();
        // set excel file filter
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                }
                return filterExcelFile(file);
            }

            @Override
            public String getDescription() {
                return "folder or excel file (*.csv, *.xls, *.xlsx)";
            }
        });

        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.showDialog(new JLabel(), "select");
        File[] selectedFiles = fileChooser.getSelectedFiles();
        return selectedFiles;
    }

    /**
     * Filter excel file<br>
     *
     * @param [file]
     * @return boolean
     * @author Zihao Long
     */
    public static boolean filterExcelFile(File file) {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith("csv")
                || fileName.endsWith("xls")
                || fileName.endsWith("xlsx")) {
            if (fileName.startsWith(".")) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * show directory choosing dialog<br>
     *
     * @param [type
     * @return java.io.File
     * @author Zihao Long
     */
    public File showDirChoosingDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.showDialog(new JLabel(), "select");
        File file = fileChooser.getSelectedFile();
        return file;
    }

    /**
     * The thread class, log to GUI<br>
     *
     * @param 
     * @return 
     * @author Zihao Long
     */
    class LogToGuiThread extends Thread {

        PipedReader reader;

        public LogToGuiThread(PipedReader reader) {
            this.reader = reader;
        }

        @Override
        public void run() {
            Scanner scanner = new Scanner(reader);
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNext()) {
                sb.append(scanner.nextLine());
                sb.append("\r\n");
                log_textarea.setText(sb.toString());
            }
        }
    }
}

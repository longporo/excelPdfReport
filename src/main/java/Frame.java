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
     * Define current system, the default is windows
     */
    private static boolean IS_WINDOWS = false;
    private static boolean IS_MAC_OS = false;

    /**
     * The file path notation
     *
     */
    public static String FILE_PATH_NOTATION = null;

    /**
     * The pdf absolute file path
     */
    public static String PDF_FILE_PATH;

    /**
     * The logger
     */
    public static Logger logger = Logger.getLogger(Frame.class);

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
                File file = showExcelFileDialog();
                if (file == null) {
                    return;
                }
                excel_input.setText(file.getAbsolutePath());
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

                if (" Select excel file...".equalsIgnoreCase(excelFilePath)) {
                    JOptionPane.showMessageDialog(null, "Please select excel file.", "Warning", 2);
                    return;
                }
                if (" Select a folder to save pdf...".equalsIgnoreCase(pdfDirPath)) {
                    JOptionPane.showMessageDialog(null, "Please select a folder to save pdf.", "Warning", 2);
                    return;
                }
                int confirmResult = JOptionPane.showConfirmDialog(null, "Start to generate?", "Prompt", 0);
                if (confirmResult == 1) {
                    return;
                }

                // Generate
                generatePdf(excelFilePath, pdfDirPath);
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
    private static void generatePdf(String excelFilePath, String pdfDirPath) {
        try {

            // get system info
            if ( FILE_PATH_NOTATION == null) {
                String osName = System.getProperty("os.name");
                if (osName.startsWith("Windows")) {
                    IS_WINDOWS = true;
                    FILE_PATH_NOTATION = "\\";
                } else {
                    // MacOs, linux
                    IS_MAC_OS = true;
                    FILE_PATH_NOTATION = "/";
                }
            }

            logger.info("Reading excel file...");

            ExcelData.getExcelData(excelFilePath,pdfDirPath + FILE_PATH_NOTATION);

            logger.info("SUCCESS!!!");

            // Ask if open file
            int confirmResult = JOptionPane.showConfirmDialog(null, "PDF has been generated! Open it?", "Prompt", 0);
            if (confirmResult == 1) {
                return;
            }
            openFile(PDF_FILE_PATH);
        } catch (Exception e) {
//            e.printStackTrace();
            logger.error(e.getMessage());
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
        if (IS_WINDOWS) {
            Runtime.getRuntime().exec("explorer.exe /select, " + filePath);
        } else if (IS_MAC_OS) {
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
    public File showExcelFileDialog() {
        JFileChooser fileChooser = new JFileChooser();
        // set excel file filter
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String fileName = file.getName().toLowerCase();
                if (fileName.endsWith("csv")
                        || fileName.endsWith("xls")
                        || fileName.endsWith("xlsx")) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "excel file (*.csv, *.xls, *.xlsx)";
            }
        });

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.showDialog(new JLabel(), "select");
        File file = fileChooser.getSelectedFile();
        return file;
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

package gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;
import service.MyService;
import util.Constant;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * The Frame class<br>
 *
 * @author Zihao Long
 * @version 1.0, 2021年09月26日 03:28
 * @since excelToPdf 0.0.1
 */
public class MyFrame {

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
    private JPanel year_div;
    private JTextField year_input;
    private JLabel year_label;

    public MyFrame() throws IOException {

        // log setting
        Logger root = Logger.getRootLogger();
        Appender appender = root.getAppender("WriterAppender");
        PipedReader reader = new PipedReader();
        Writer writer = new PipedWriter(reader);
        ((WriterAppender) appender).setWriter(writer);
        Thread t = new LogToGuiThread(reader);
        t.start();

        // Excel file selecting button click event
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
                try {
                    String academicYear = year_input.getText();
                    String excelFilePath = excel_input.getText();
                    String savingFolderPath = pdf_input.getText();
                    String selectedStr = (String) optionSelect.getSelectedItem();

                    if ("Select an option".equalsIgnoreCase(selectedStr)) {
                        JOptionPane.showMessageDialog(null, "Please select an option.", "Warning", 2);
                        return;
                    }

                    if (StringUtils.isEmpty(academicYear) && "Generate FYP Grading Report PDF".equalsIgnoreCase(selectedStr)) {
                        JOptionPane.showMessageDialog(null, "Please enter an academic year.", "Warning", 2);
                        return;
                    }

                    if (Constant.EXCEL_FILES_SELECTION.equalsIgnoreCase(excelFilePath)) {
                        JOptionPane.showMessageDialog(null, "Please select Excel files or a folder.", "Warning", 2);
                        return;
                    }

                    if (Constant.SAVE_FOLDER_SELECTION.equalsIgnoreCase(savingFolderPath)) {
                        JOptionPane.showMessageDialog(null, "Please select a folder to save output file.", "Warning", 2);
                        return;
                    }

                    int confirmResult = JOptionPane.showConfirmDialog(null, "Start to generate?", "Prompt", 0);
                    if (confirmResult == 1) {
                        return;
                    }

                    // generate file(PDF or Excel) by selection
                    MyService.genBySelection(academicYear, excelFilePath, savingFolderPath, selectedStr);

                    Constant.logger.info("SUCCESS!!!");

                    // ask if open file
                    confirmResult = JOptionPane.showConfirmDialog(null, "File has been generated! Open it?", "Prompt", 0);
                    if (confirmResult == 1) {
                        return;
                    }
                    MyService.openFile(Constant.TARGET_FILE_PATH);
                } catch (IOException e) {
                    e.printStackTrace();
                    Constant.logger.error(e.getMessage());
                }
            }
        });
    }

    /**
     * The main method<br>
     *
     * @param [args]
     * @return void
     * @author Zihao Long
     */
    public static void main(String[] args) throws IOException {
        // init frame
        JFrame frame = new JFrame("FYP Grading Report Generator");
        frame.setSize(575, 400);
        frame.setContentPane(new MyFrame().body);
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

    /**
     * Show Excel file dialog<br>
     *
     * @param [type]
     * @return java.io.File
     * @author Zihao Long
     */
    public File[] showExcelFileDialog() {
        JFileChooser fileChooser = new JFileChooser();
        // set Excel file filter
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                }
                return MyService.filterExcelFile(file);
            }

            @Override
            public String getDescription() {
                return "folder or Excel file (*.xls, *.xlsx)";
            }
        });

        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.showDialog(new JLabel(), "select");
        File[] selectedFiles = fileChooser.getSelectedFiles();
        return selectedFiles;
    }

    /**
     * Show directory choosing dialog<br>
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        body = new JPanel();
        body.setLayout(new GridLayoutManager(6, 1, new Insets(20, 20, 20, 20), -1, -1));
        excel_div = new JPanel();
        excel_div.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        body.add(excel_div, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 35), new Dimension(-1, 35), new Dimension(-1, 35), 0, false));
        excel_input = new JTextField();
        excel_input.setEditable(false);
        excel_input.setText(ResourceBundle.getBundle("frame").getString("excel.files.selection"));
        excel_div.add(excel_input, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 35), new Dimension(150, 35), new Dimension(-1, 35), 0, false));
        excel_btn = new JButton();
        excel_btn.setText("...");
        excel_div.add(excel_btn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 35), new Dimension(-1, 35), new Dimension(-1, 35), 0, false));
        pdf_div = new JPanel();
        pdf_div.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        body.add(pdf_div, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 35), new Dimension(-1, 35), new Dimension(-1, 35), 0, false));
        pdf_input = new JTextField();
        pdf_input.setEditable(false);
        pdf_input.setText(ResourceBundle.getBundle("frame").getString("save.folder.selection"));
        pdf_input.setToolTipText("");
        pdf_div.add(pdf_input, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 35), new Dimension(150, 35), new Dimension(-1, 35), 0, false));
        pdf_btn = new JButton();
        pdf_btn.setText("...");
        pdf_div.add(pdf_btn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 35), new Dimension(-1, 35), new Dimension(-1, 35), 0, false));
        start_div = new JPanel();
        start_div.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        body.add(start_div, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 35), new Dimension(-1, 35), new Dimension(-1, 35), 0, false));
        start_btn = new JButton();
        start_btn.setText("Start");
        start_div.add(start_btn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, 35), new Dimension(100, 35), new Dimension(100, 35), 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        body.add(scrollPane1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        log_textarea = new JTextPane();
        log_textarea.setEditable(false);
        log_textarea.setText("log output...");
        scrollPane1.setViewportView(log_textarea);
        optionSelect = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Select an option");
        defaultComboBoxModel1.addElement("Generate FYP Grading Report PDF");
        defaultComboBoxModel1.addElement("Combine Excel files into one");
        optionSelect.setModel(defaultComboBoxModel1);
        body.add(optionSelect, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        year_div = new JPanel();
        year_div.setLayout(new GridLayoutManager(1, 2, new Insets(0, 5, 0, 0), -1, -1));
        body.add(year_div, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        year_input = new JTextField();
        year_div.add(year_input, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 35), null, 0, false));
        year_label = new JLabel();
        this.$$$loadLabelText$$$(year_label, ResourceBundle.getBundle("frame").getString("year.input.label"));
        year_div.add(year_label, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return body;
    }

    /**
     * The thread class, log to GUI<br>
     *
     * @param
     * @author Zihao Long
     * @return
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

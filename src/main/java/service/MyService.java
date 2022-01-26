package service;

import pojo.Student;
import util.Constant;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The MyService<br>
 *
 * @author Zihao Long
 * @version 1.0, 2022-01-25 21:34
 * @since ExcelPDFReports 0.0.1
 */
public class MyService {

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
     * Generate file(pdf or excel) by selection<br>
     *
     * @param [excelFilePath, pdfDirPath, selectedStr]
     * @return void
     * @author Zihao Long
     */
    public static void generateBySelection(String excelFilePath, String pdfDirPath, String selectedStr) {
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

            String fileName = null;
            if ("Generate PDF".equalsIgnoreCase(selectedStr)) {
                Constant.IS_GENERATE_PDF = true;
                fileName = "Main.pdf";
            } else if ("Combine files into one".equalsIgnoreCase(selectedStr)) {
                Constant.IS_GENERATE_PDF = false;
                fileName = "project-marking-combined.xlsx";
            }

            // set absolute path
            Constant.TARGET_FILE_PATH = pdfDirPath + Constant.FILE_PATH_NOTATION + fileName;

            Constant.logger.info("Reading excel file...");

            List<Student> stuList = ExcelService.getExcelData(excelFilePath);
            if (Constant.IS_GENERATE_PDF) {
                PdfService.generatePdf(stuList);
            } else {
                ExcelService.combineToFile(stuList);
            }


            Constant.logger.info("SUCCESS!!!");

            // Ask if open file
            int confirmResult = JOptionPane.showConfirmDialog(null, "File has been successfully generated! Open it?", "Prompt", 0);
            if (confirmResult == 1) {
                return;
            }
            openFile(Constant.TARGET_FILE_PATH);
        } catch (Exception e) {
            e.printStackTrace();
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
     * Get info table columns by the number of graders<br><br>
     *
     * @param [student]
     * @return float[]
     * @author Zihao Long
     */
    public static float[] getGradeTableColumn(Student student) {
        Map<String, Student> graderMap = student.getGraderMap();
        int graderCounts = graderMap.keySet().size();
        float[] tableColumn;
        switch (graderCounts) {
            case 2:
                // two graders
                tableColumn = new float[]{3, 5, 3, 5};
                break;
            case 3:
                // three graders
                tableColumn = new float[]{3, 5, 3, 5, 3, 5};
                break;
            default:
                // one grader
                tableColumn = new float[]{3, 5};
        }
        return tableColumn;
    }

    /**
     * Get grader table columns by the number of graders<br><br>
     *
     * @param [student]
     * @return float[]
     * @author Zihao Long
     */
    public static float[] getGraderTableColumn(Student student) {
        Map<String, Student> graderMap = student.getGraderMap();
        int graderCounts = graderMap.keySet().size();
        float[] tableColumn;
        switch (graderCounts) {
            case 2:
                // two graders
                tableColumn = new float[]{5, 5};
                break;
            case 3:
                // three graders
                tableColumn = new float[]{8, 8, 8};
                break;
            default:
                // one grader
                tableColumn = new float[]{10};
        }
        return tableColumn;
    }
}

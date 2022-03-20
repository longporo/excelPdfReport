package service;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import pojo.Student;
import util.Constant;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The ExcelService<br>
 *
 * @param
 * @author Zihao Long
 * @return
 */
public class ExcelService {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0");

    private static final String[][] KEY_ARRAY = {{"grader", "Grader"}, {"projectNo", "Project No."}, {"role", "Role"}, {"otherGrader", "Other grader"}, {"studentName", "Student"}, {"studentNo", "SN."}, {"projectType", "Project type"}, {"credit", "Credit"}, {"grade", "Scr"}, {"abstractScr", "Abstract"}, {"abstractScrX", "/X"}, {"motivation", "Motivation"}, {"motivationX", "/X"}, {"background", "Background"}, {"backgroundX", "/X"}, {"problem", "Problem"}, {"problemX", "/X"}, {"solution", "Solution"}, {"solutionX", "/X"}, {"cte", "Conclusion or Testing and Evaluation"}, {"cteX", "/X"}, {"presentation", "Presentation"}, {"presentationX", "/X"}, {"comment", "Comment"}, {"title", "Title"}};

    /**
     * Get Excel data by path<br>
     *
     * @param [excelPath]
     * @return java.util.List<pojo.Student>
     * @author Zihao Long
     */
    public static List<Student> getExcelData(String excelPath) throws Exception {
        List<File> fileList = new ArrayList<>();
        String[] filePathArr = excelPath.split(Constant.FILE_PATH_SPLIT_STR);
        for (String filePath : filePathArr) {
            File inputFile = new File(filePath);
            File[] files = new File[]{inputFile};
            if (inputFile.isDirectory()) {
                files = inputFile.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return MyService.filterExcelFile(file);
                    }
                });
            }
            fileList.addAll(Arrays.asList(files));
        }


        List<Student> stuList = getImportData(fileList);
        if (stuList.size() == 0) {
            throw new RuntimeException("No data found in the Excel file.");
        }

        Constant.logger.info("Successfully parsed Excel data...");
        return stuList;
    }

    /**
     * Set data to file<br>
     *
     * @param [pdfPath, stuList]
     * @return void
     * @author Zihao Long
     */
    public static void combineToFile(List<Student> stuList) throws Exception {
        Constant.logger.info("Combining files...");
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Sheet1");
        XSSFRow row = sheet.createRow(0);
        XSSFRow secondRow = sheet.createRow(1);

        // full border style
        XSSFCellStyle fullBorderStyle = getFullBorderStyle(wb);

        // wrap text style to 'Conclusion or Testing and Evaluation' cell
        XSSFCellStyle wrapTextStyle = wb.createCellStyle();
        wrapTextStyle.setWrapText(true);

        // set title
        for (int i = 0; i < KEY_ARRAY.length; i++) {
            String title = KEY_ARRAY[i][1];
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(title);
            cell.setCellStyle(fullBorderStyle);
            if (title.equals("Conclusion or Testing and Evaluation")) {
                cell.setCellStyle(wrapTextStyle);
            }
            XSSFCell secondRowCell = secondRow.createCell(i);
            secondRowCell.setCellStyle(fullBorderStyle);
        }

        // merge 'Conclusion or Testing and Evaluation' cell
        CellRangeAddress region = new CellRangeAddress(0, 1, 19, 19);
        sheet.addMergedRegion(region);

        // set content
        int n = 1;
        for (Student student : stuList) {
            Field[] fields = student.getClass().getDeclaredFields();
            row = sheet.createRow(n + 1);
            for (int j = 0; j < KEY_ARRAY.length; j++) {
                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].getName().equals(KEY_ARRAY[j][0])) {
                        fields[i].setAccessible(true);
                        Object fieldObj = fields[i].get(student);
                        XSSFCell cell = row.createCell(j);
                        cell.setCellStyle(fullBorderStyle);
                        if (fieldObj == null || fieldObj.toString().isEmpty()) {
                            cell.setCellValue("");
                            break;
                        }
                        if ((fieldObj instanceof Number) || fields[i].getName().equals("projectType")) {
                            cell.setCellType(CellType.NUMERIC);
                            cell.setCellValue(Double.parseDouble(fieldObj.toString()));
                            break;
                        }
                        cell.setCellValue(fieldObj.toString());
                        break;
                    }
                }
            }
            n++;
        }
        // set column width
        for (int i = 0; i < KEY_ARRAY.length; i++) {
            sheet.setColumnWidth(i, 10 * 256);
        }
        wb.write(new FileOutputStream(Constant.TARGET_FILE_PATH));
    }

    /**
     * Get full border style<br>
     *
     * @param [wb]
     * @return org.apache.poi.xssf.usermodel.XSSFCellStyle
     * @author Zihao Long
     */
    private static XSSFCellStyle getFullBorderStyle(XSSFWorkbook wb) {
        XSSFCellStyle fullBorderStyle = wb.createCellStyle();
        fullBorderStyle.setBorderBottom(BorderStyle.THIN);
        fullBorderStyle.setBorderLeft(BorderStyle.THIN);
        fullBorderStyle.setBorderTop(BorderStyle.THIN);
        fullBorderStyle.setBorderRight(BorderStyle.THIN);
        return fullBorderStyle;
    }

    /**
     * Get import data by files<br>
     *
     * @param [files]
     * @return java.util.List<Student>
     * @author Zihao Long
     */
    private static List<Student> getImportData(List<File> fileList) throws Exception {
        List<Student> stuList = new ArrayList<>();
        for (File file : fileList) {
            XSSFWorkbook workbook = null;
            try {
                FileInputStream inputStream = new FileInputStream(file);
                workbook = new XSSFWorkbook(inputStream);
                XSSFSheet sheet = workbook.getSheet("Sheet1");
                int rows = sheet.getLastRowNum();
                if (rows < 2) {
                    continue;
                }

                int cols = sheet.getRow(1).getLastCellNum();
                for (int i = 2; i <= rows; i++) {
                    XSSFRow row = sheet.getRow(i);
                    // skip the empty row
                    if (row == null || row.getLastCellNum() != cols) {
                        continue;
                    }

                    Student student = new Student();
                    for (int j = 0; j < KEY_ARRAY.length; j++) {
                        String key = KEY_ARRAY[j][0];
                        String cellValue;
                        cellValue = handleCellType(row.getCell(j), key);
                        setValToStu(key, cellValue, student);
                    }
                    stuList.add(student);
                }
            } finally {
                if (workbook != null) {
                    workbook.close();
                }
            }
        }
        return stuList;
    }

    /**
     * Set val by key<br>
     *
     * @param [key, val, student]
     * @return void
     * @author Zihao Long
     */
    private static void setValToStu(String key, Object val, Student student) throws Exception {
        if (val == null) {
            return;
        }
        Field field = student.getClass().getDeclaredField(key);
        field.setAccessible(true);
        String fieldType = field.getGenericType().toString();
        if (fieldType.equals(BigDecimal.class.toString())) {
            String valStr = val.toString();
            if (StringUtils.isEmpty(valStr)) {
                return;
            }
            val = new BigDecimal(valStr);
        }
        field.set(student, val);
    }

    /**
     * Handle the cell type to string<br>
     *
     * @param [cell]
     * @param key
     * @return java.lang.String
     * @author Zihao Long
     */
    private static String handleCellType(XSSFCell cell, String key) {
        String str;
        switch (cell.getCellType()) {
            case STRING:
                str = cell.getStringCellValue().trim();
                break;
            case NUMERIC:
                Double holder = cell.getNumericCellValue();
                if (key.equals("projectType") || key.equals("studentNo")) {
                    str = holder.intValue() + "";
                } else {
                    str = holder.toString();
                }
                if (str.contains("E")) {
                    str = DECIMAL_FORMAT.format(holder);
                }
                break;
            case BOOLEAN:
                Boolean bool = cell.getBooleanCellValue();
                str = bool.toString();
                break;
            case FORMULA:
                Double formulaHolder = cell.getNumericCellValue();
                str = formulaHolder.toString();
                break;
            default:
                str = "";
        }
        return str;
    }
}

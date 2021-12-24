import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

public class ExcelData {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0");

    private static final String[][] KEY_ARRAY = {{"grader", "Grader"}, {"projectNo", "Project No."}, {"role", "Role"}, {"otherGrader", "Other grader"}, {"studentName", "Student"}, {"studentNo", "SN."}, {"projectType", "Project type"}, {"credit", "Credit"}, {"grade", "Scr"}, {"abstractScr", "Abstract"}, {"abstractScrX", "/X"}, {"motivation", "Motivation"}, {"motivationX", "/X"}, {"background", "Background"}, {"backgroundX", "/X"}, {"problem", "Problem"}, {"problemX", "/X"}, {"solution", "Solution"}, {"solutionX", "/X"}, {"cte", "Conclusion or Testing and Evaluation"}, {"cteX", "/X"}, {"presentation", "Presentation"}, {"presentationX", "/X"}, {"comment", "Comment"}, {"title", "Title"}};

    public static void getExcelData(String excelPath) throws Exception {
        List<File> fileList = new ArrayList<>();
        String[] filePathArr = excelPath.split(Constant.FILE_PATH_SPLIT_STR);
        for (String filePath : filePathArr) {
            File inputFile = new File(filePath);
            File[] files = new File[]{inputFile};
            if (inputFile.isDirectory()) {
                files = inputFile.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return Frame.filterExcelFile(file);
                    }
                });
            }
            fileList.addAll(Arrays.asList(files));
        }


        List<Student> stuList = getImportData(fileList);
        if (stuList.size() == 0) {
            throw new RuntimeException("No data found in the excel file.");
        }

        Constant.logger.info("Parsing excel data successfully...");

        if (Constant.IS_GENERATE_PDF) {
            generatePdf(stuList);
        } else {
            combineToFile(stuList);
        }
    }

    /**
     * Set data to file<br>
     *
     * @param [pdfPath, stuList]
     * @return void
     * @author Zihao Long
     */
    private static void combineToFile(List<Student> stuList) throws Exception {
        Constant.logger.info("Combining files...");
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Sheet1");
        HSSFRow row = sheet.createRow(0);

        for (int i = 0; i < KEY_ARRAY.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(KEY_ARRAY[i][1]);
        }

        // merge 'Conclusion or Testing and Evaluation' cell
        CellRangeAddress region = new CellRangeAddress(0, 1, 19, 19);
        sheet.addMergedRegion(region);

        int n = 1;
        for (Student student : stuList) {
            Field[] fields = student.getClass().getDeclaredFields();
            row = sheet.createRow(n + 1);
            for (int j = 0; j < KEY_ARRAY.length; j++) {
                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].getName().equals(KEY_ARRAY[j][0])) {
                        fields[i].setAccessible(true);
                        Object fieldObj = fields[i].get(student);
                        if (fieldObj == null || fieldObj.toString().isEmpty()) {
                            row.createCell(j).setCellValue("");
                            break;
                        }
                        if ((fieldObj instanceof Number)) {
                            HSSFCell cell = row.createCell(j);
                            cell.setCellType(CellType.NUMERIC);
                            cell.setCellValue(Double.parseDouble(fieldObj.toString()));
                            break;
                        }
                        row.createCell(j).setCellValue(fieldObj.toString());
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
        wb.write(new File(Constant.TARGET_FILE_PATH));
    }

    /**
     * Generate pdf<br>
     *
     * @param [stuList]
     * @return void
     * @author Zihao Long
     */
    private static void generatePdf(List<Student> stuList) throws Exception {
        Constant.logger.info("Generating PDF file...");
        Map<String, Map<String, Student>> stuMap = new HashMap<>(16);
        for (Student student : stuList) {
            String studentNo = student.getStudentNo();
            Map<String, Student> stuSubMap = stuMap.get(studentNo);
            if (stuSubMap == null) {
                stuSubMap = new HashMap<>(2);
                stuMap.put(studentNo, stuSubMap);
            }
            stuSubMap.put(student.getRole(), student);
        }

        // extract a student class from the stuMap to record the average grade and set to a student list
        List<Student> setStuList = new ArrayList<>();
        Set<String> keySet = stuMap.keySet();
        for (String key : keySet) {
            Map<String, Student> graderMap = stuMap.get(key);
            Set<String> graderSet = graderMap.keySet();
            BigDecimal totalGrade = BigDecimal.ZERO;
            int count = 0;
            Student tmpStu = null;
            for (String graderKey : graderSet) {
                tmpStu = graderMap.get(graderKey);
                totalGrade = totalGrade.add(tmpStu.getGrade());
                count++;
            }
            BigDecimal avgGrade = totalGrade.divide(new BigDecimal(count)).setScale(1, BigDecimal.ROUND_UP);
            tmpStu.setAvgGrade(avgGrade);
            tmpStu.setGraderMap(graderMap);
            setStuList.add(tmpStu);
        }
        // generate content to pdf
        PdfGen.generatePdf(setStuList);
    }

    /**
     * get import data by files<br>
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
                    if (row.getLastCellNum() != cols) {
                        continue;
                    }

                    Student student = new Student();
                    for (int j = 0; j < KEY_ARRAY.length; j++) {
                        String key = KEY_ARRAY[j][0];
                        String cellValue;
                        // the project type will be read as numeric type
                        if (key.equals("projectType")) {
                            Double holder = row.getCell(j).getNumericCellValue();
                            cellValue = holder.intValue() + "";
                        } else {
                            cellValue = handleCellType(row.getCell(j));
                        }
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
     * @return java.lang.String
     * @author Zihao Long
     */
    private static String handleCellType(XSSFCell cell) {
        String str;
        switch (cell.getCellType()) {
            case STRING:
                str = cell.getStringCellValue().trim();
                break;
            case NUMERIC:
                Double holder = cell.getNumericCellValue();
                str = holder.toString();
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

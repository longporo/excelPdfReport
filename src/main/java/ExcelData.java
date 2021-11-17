import org.apache.commons.lang3.StringUtils;
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

    private static final String[] KEY_ARRAY = new String[]{"grader", "projectNo", "role", "otherGrader", "studentName", "studentNo", "projectType", "credit", "grade", "abstractScr", "abstractScrX", "motivation", "motivationX", "background", "backgroundX", "problem", "problemX", "solution", "solutionX", "cte", "cteX", "presentation", "presentationX", "comment", "title"};

    public static void getExcelData(String excelPath, String pdfPath) throws Exception {

        Frame.PDF_FILE_PATH = pdfPath + "Main.pdf";

        File inputFile = new File(excelPath);
        File[] files = new File[]{inputFile};
        if (inputFile.isDirectory()) {
            files = inputFile.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return Frame.filterExcelFile(file);
                }
            });
        }

        List<Student> stuList = getImportData(files);
        if (stuList.size() == 0) {
            throw new RuntimeException("No data found in the excel file.");
        }

        Frame.logger.info("Parsing excel data successfully...");
        Frame.logger.info("Generating PDF file...");

        // generate content to pdf
        PdfGen.generatePdf(stuList);
    }

    /**
     * get import data by files<br>
     *
     * @param [files]
     * @return java.util.List<Student>
     * @author Zihao Long
     */
    private static List<Student> getImportData(File[] files) throws Exception {
        Map<String, Map<String, Student>> stuMap = new HashMap<>(16);
        for (File file : files) {
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
                        String key = KEY_ARRAY[j];
                        setValToStu(key, handleCellType(row.getCell(j)), student);
                    }

                    String studentNo = student.getStudentNo();
                    Map<String, Student> stuSubMap = stuMap.get(studentNo);
                    if (stuSubMap == null) {
                        stuSubMap = new HashMap<>(2);
                        stuMap.put(studentNo, stuSubMap);
                    }
                    stuSubMap.put(student.getRole(), student);
                }
            } finally {
                if (workbook != null) {
                    workbook.close();
                }
            }
        }

        // gen student list
        List<Student> stuList = new ArrayList<>();
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
            stuList.add(tmpStu);
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
                str = cell.getStringCellValue();
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

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ExcelData {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0");

    private static final String[] KEY_ARRAY = new String[]{"grader", "projectNo", "role", "otherGrader", "studentName", "studentNo", "projectType", "credit", "grade", "abstractScr", "abstractScrX", "motivation", "motivationX", "background", "backgroundX", "problem", "problemX", "solution", "solutionX", "cte", "cteX", "presentation", "presentationX", "comment", "title"};

    public static void getExcelData(String excelPath,String pdfPath) throws Exception {

        Frame.PDF_FILE_PATH = pdfPath + "Main.pdf";

        ArrayList<String> headerInfo = new ArrayList<>();

        FileInputStream inputStream = new FileInputStream(excelPath);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        XSSFSheet sheet = workbook.getSheet("Sheet1");

        int rows = sheet.getLastRowNum();
        int cols = sheet.getRow(1).getLastCellNum();
        //get header information for each student
        for (int i = 0; i < 1; ++i) {
            XSSFRow row = sheet.getRow(i);
            for (int j = 0; j < cols; ++j) {
                XSSFCell cell = row.getCell(j);
                String str = handleCellType(cell);
                headerInfo.add(str);
            }
        }

        Frame.logger.info("Parsing excel data successfully...");
        if (rows < 2) {
            throw new RuntimeException("The excel content is empty.");
        }
        Frame.logger.info("Generating PDF file...");

        // save student info to list
        List<Student> stuList = new ArrayList<>();
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
            stuList.add(student);
        }

        // generate content to pdf
        PdfGen.generatePdf(headerInfo, stuList);
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
        switch(cell.getCellType())
        {
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
            default: str = "";
        }
        return str;
    }
}

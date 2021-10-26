import com.itextpdf.kernel.pdf.PdfDocument;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ExcelData {

    public static void getExcelData(String excelPath,String pdfPath) throws IOException {
        ArrayList<String> headerInfo = new ArrayList<>();

        FileInputStream inputStream = new FileInputStream(excelPath);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        XSSFSheet sheet = workbook.getSheet("Sheet1");

        DecimalFormat df = new DecimalFormat("0");
        PdfDocument mainPdf = pdfGen.generatePdf(pdfPath);
        int rows = sheet.getLastRowNum();
        int cols = sheet.getRow(1).getLastCellNum();
        //get header information for each student
        for (int i = 0; i < 1; ++i) {
            XSSFRow row = sheet.getRow(i);
            for (int j = 0; j < cols; ++j) {
                XSSFCell cell = row.getCell(j);
                switch(cell.getCellType())
                {
                    case STRING:
                        headerInfo.add(cell.getStringCellValue()); break;

                    case NUMERIC:
                        Double holder =cell.getNumericCellValue();
                        if(holder.toString().contains("E")) headerInfo.add(df.format(cell.getNumericCellValue()));
                        else headerInfo.add(holder.toString());
                        break;

                    case BOOLEAN:
                        Boolean bool = cell.getBooleanCellValue();
                        headerInfo.add(bool.toString());
                        break;

                    case FORMULA:
                        Double formulaHolder =cell.getNumericCellValue();
                        headerInfo.add(formulaHolder.toString());
                        break;
                    default: headerInfo.add(" ");
                }
            }
        }
        //get student arraylist and create pdf
        ArrayList<String> studentInfo = new ArrayList<>();
        for (int i = 2; i < rows; ++i) {
            XSSFRow row = sheet.getRow(i);
            studentInfo.clear();
            for (int j = 0; j < cols; ++j) {
                XSSFCell cell = row.getCell(j);
                switch(cell.getCellType())
                {
                    case STRING:
                        studentInfo.add(cell.getStringCellValue()); break;

                    case NUMERIC:
                        Double holder =cell.getNumericCellValue();
                        if(holder.toString().contains("E")) studentInfo.add(df.format(cell.getNumericCellValue()));
                        else studentInfo.add(holder.toString());
                        break;

                    case BOOLEAN:
                        Boolean bool = cell.getBooleanCellValue();
                        studentInfo.add(bool.toString());
                        break;

                    case FORMULA:
                        Double formulaHolder =cell.getNumericCellValue();
                        studentInfo.add(formulaHolder.toString());
                        break;
                    default: studentInfo.add(" - ");
                }
            }
            pdfGen.studentPdf(headerInfo,studentInfo,pdfPath,mainPdf);

        }
        mainPdf.removePage(1);
        mainPdf.close();
    }
}

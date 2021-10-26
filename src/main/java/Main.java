import java.io.IOException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Main {
    public static void main(String[] args) throws IOException {
        Logger.getRootLogger().setLevel(Level.OFF);
        if(args.length == 2) {
            String excelPath = args[0];
            String pdfPath = args[1];
            //exact excel sheet path + pdf folder location pdfs named StudentNumber.pdf
            ExcelData.getExcelData(excelPath,pdfPath+"\\");
        }
        else {
            ExcelData.getExcelData("E:\\Coding\\secondpdftest.xlsx", "E:\\Coding\\ItextPdf\\");
            //
        }
    }
}

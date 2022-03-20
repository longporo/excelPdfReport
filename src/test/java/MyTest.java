import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import service.MyService;

/**
 * The MyTest class<br>
 *
 * @author Zihao Long
 * @version 1.0, 2022-03-14 17:46
 * @since ExcelPDFReports 0.0.1
 */
public class MyTest {

    /**
     * The test case table
     * +------+-------+---------------------------------+--------------+
     * |  ID  |  TCI  |              Inputs             | Exp. Results |
     * |      |       +--------------+------------------+--------------+
     * |      |       |   is folder  | is generated PDF |    output    |
     * +------+-------+--------------+------------------+--------------+
     * |  DT1 | Rule1 |       T      |         T        |      PDF     |
     * +------+-------+--------------+------------------+--------------+
     * |  DT2 | Rule2 |       T      |         F        |     Excel    |
     * +------+-------+--------------+------------------+--------------+
     * |  DT3 | Rule3 |       F      |         T        |      PDF     |
     * +------+-------+--------------+------------------+--------------+
     * |  DT4 | Rule4 |       F      |         F        |     Excel    |
     * +------+-------+--------------+------------------+--------------+
     * | DT5* |   -   |  empty file  |         F        |   Exception  |
     * +------+-------+--------------+------------------+--------------+
     * | DT6* |   -   | empty folder |         F        |   Exception  |
     * +------+-------+--------------+------------------+--------------+
     */


    /**
     * The regular data
     */
    private static Object[][] regularData = new Object[][]{
            // id, academicYear, excelFilePath, savingFolderPath, selectedStr
            {"DT1", "2022", "/Users/aihuishou/Desktop/test/input_excel", "/Users/aihuishou/Desktop/test/output_files/DT1", "Generate FYP Grading Report PDF"},
            {"DT2", "2022", "/Users/aihuishou/Desktop/test/input_excel", "/Users/aihuishou/Desktop/test/output_files/DT2", "Combine Excel files into one"},
            {"DT3", "2022", "/Users/aihuishou/Desktop/test/input_excel/pm_only.xlsx", "/Users/aihuishou/Desktop/test/output_files/DT3", "Generate FYP Grading Report PDF"},
            {"DT4", "2022", "/Users/aihuishou/Desktop/test/input_excel/pm_only.xlsx", "/Users/aihuishou/Desktop/test/output_files/DT4", "Combine Excel files into one"},
    };

    /**
     * The fault model data, the excelFilePath maps an empty file or folder
     *
     */
    private static Object[][] faultModelData = new Object[][]{
            // id, academicYear, excelFilePath, savingFolderPath, selectedStr
            {"DT5*", "2022", "/Users/aihuishou/Desktop/test/input_excel/empty_data.xlsx", "/Users/aihuishou/Desktop/test/output_files/DT5", "Generate FYP Grading Report PDF"},
            {"DT6*", "2022", "/Users/aihuishou/Desktop/test/empty_folder", "/Users/aihuishou/Desktop/test/output_files/DT6", "Generate FYP Grading Report PDF"},
    };

    @DataProvider(name = "regularData")
    public Object[][] getRegularData() {
        return regularData;
    }

    @DataProvider(name = "faultModelData")
    public Object[][] getFaultModelData() {
        return faultModelData;
    }

    /**
     * Regular test<br>
     *
     * @param [id, academicYear, excelFilePath, savingFolderPath, selectedStr]
     * @return void
     * @author Zihao Long
     */
    @Test(dataProvider = "regularData")
    public void regularTest(String id, String academicYear, String excelFilePath, String savingFolderPath, String selectedStr) {
        Logger.getRootLogger().setLevel(Level.OFF);
        MyService.genBySelection(academicYear, excelFilePath, savingFolderPath, selectedStr);
    }

    /**
     * Fault model test<br>
     * expected exception: java.lang.RuntimeException: No data found in the Excel file.<br>
     *
     * @param [id, academicYear, excelFilePath, savingFolderPath, selectedStr]
     * @return void
     * @author Zihao Long
     */
    @Test(dataProvider = "faultModelData")
    public void faultModelTest(String id, String academicYear, String excelFilePath, String savingFolderPath, String selectedStr) {
        Logger.getRootLogger().setLevel(Level.OFF);
        MyService.genBySelection(academicYear, excelFilePath, savingFolderPath, selectedStr);
    }
}

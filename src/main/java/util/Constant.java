package util;

import org.apache.log4j.Logger;

import java.util.ResourceBundle;

/**
 * The Constant<br>
 *
 * @param
 * @return
 * @author Zihao Long
 */
public class Constant {

    /**
     * The text fields on frame
     */
    public static String YEAR_INPUT_LABEL;
    public static String EXCEL_FILES_SELECTION;
    public static String SAVE_FOLDER_SELECTION;
    static {
        ResourceBundle resource = ResourceBundle.getBundle("frame");
        YEAR_INPUT_LABEL = resource.getString("year.input.label");
        EXCEL_FILES_SELECTION = resource.getString("excel.files.selection");
        SAVE_FOLDER_SELECTION = resource.getString("save.folder.selection");
    }

    /**
     * The academic year entered from user interface
     */
    public static String ACADEMIC_YEAR = null;

    /**
     * The grading report page title
     */
    public static String GRADING_REPORT_PAGE_TITLE = null;

    /**
     * Define the option selected
     */
    public static boolean IS_GENERATE_PDF = false;

    /**
     * Define current system, the default is windows
     */
    public static boolean IS_WINDOWS = false;
    public static boolean IS_MAC_OS = false;

    /**
     * The file path notation
     *
     */
    public static String FILE_PATH_NOTATION = null;

    /**
     * The target absolute file path
     */
    public static String TARGET_FILE_PATH = null;

    /**
     * The file path split string, used when select multiple files
     */
    public static String FILE_PATH_SPLIT_STR = "#.#";

    /**
     * The logger
     */
    public static Logger logger = Logger.getLogger(Constant.class);
}

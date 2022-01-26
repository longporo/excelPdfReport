package util;

import org.apache.log4j.Logger;

/**
 * The Constant<br>
 *
 * @param
 * @return
 * @author Zihao Long
 */
public class Constant {

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

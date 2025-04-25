package fun.masttf.bean;

import fun.masttf.utils.PropertiesUtils;

public class Constants {
    public static Boolean IGNORE_TABLE_PERFIX;
    public static String SUFFIX_BEAN_PARAM;
    public static String AUTHER_COMMENT;
    public static String PATH_JAVA = "java";
    public static String PATH_RESOURCES = "resources";
    public static String PATH_BASE;
    public static String PATH_PO;
    public static String PACKAGE_BASE;
    public static String PACKAGE_PO;
    static {
        AUTHER_COMMENT = PropertiesUtils.getProperty("auther.comment");
        IGNORE_TABLE_PERFIX = Boolean.valueOf(PropertiesUtils.getProperty("ignore.table.perfix"));
        SUFFIX_BEAN_PARAM = PropertiesUtils.getProperty("suffix.bean.param");
        PATH_BASE = PropertiesUtils.getProperty("path.base");
        PATH_BASE = PATH_BASE + PATH_JAVA + "/" + PropertiesUtils.getProperty("package.base");
        PATH_BASE = PATH_BASE.replace('.', '/');

        PATH_PO = PATH_BASE + "/" + PropertiesUtils.getProperty("package.po").replace('.', '/');

        PACKAGE_BASE = PropertiesUtils.getProperty("package.base");
        PACKAGE_PO = PACKAGE_BASE + '.' + PropertiesUtils.getProperty("package.po");

    }

    public static final String[] SQL_DATE_TIIME_TYPES = new String[] { "datetime", "timestamp" };
    public static final String[] SQL_DATE_TYPES = new String[] { "date" };
    public static final String[] SQL_DECIMAL_TYPE = new String[] { "decimal", "double", "float" };
    public static final String[] SQL_STRING_TYPE = new String[] { "char", "varchar", "text", "mediumtext", "longtext" };
    // Integer
    public static final String[] SQL_INTEGER_TYPE = new String[] { "int", "tinyint" };
    // Long
    public static final String[] SQL_LONG_TYPE = new String[] { "bigint" };

    public static void main(String[] args) {
        System.out.println(PACKAGE_PO);
    }
}

package fun.masttf.bean;

import fun.masttf.utils.PropertiesUtils;

public class Constants {
    public static Boolean IGNORE_TABLE_PERFIX;
    public static String SUFFIX_BEAN_QUERY;
    public static String SUFFIX_BEAN_QUERY_FUZZY;
    public static String SUFFIX_BEAN_QUERY_TIME_START;
    public static String SUFFIX_BEAN_QUERY_TIME_END;
    public static String SUFFIX_MAPPER;

    public static String AUTHER_COMMENT;
    public static String PATH_JAVA = "java";
    public static String PATH_RESOURCES = "resources";
    public static String PATH_BASE;
    public static String PATH_PO;
    public static String PATH_VO;
    public static String PATH_UTILS;
    public static String PATH_ENUMS;
    public static String PATH_QUERY;
    public static String PATH_MAPPER;
    public static String PATH_MAPPER_XML;
    public static String PATH_SERVICE;
    public static String PATH_SERVICE_IMPL;

    public static String PACKAGE_BASE;
    public static String PACKAGE_PO;
    public static String PACKAGE_VO;
    public static String PACKAGE_UTILS;
    public static String PACKAGE_ENUMS;
    public static String PACKAGE_QUERY;
    public static String PACKAGE_MAPPER;
    public static String PACKAGE_SERVICE;
    public static String PACKAGE_SERVICE_IMPL;

    // 需要忽略的属性
    public static String IGNORE_BEAN_TOJSON_FILED;
    public static String IGNORE_BEAN_TOJSON_EXPRESSION;
    public static String IGNORE_BEAN_TOJSON_CLASS;
    // 日期序列化，反序列化
    public static String BEAN_DATE_FORMAT_EXPRESSION;
    public static String BEAN_DATE_FORMAT_CLASS;
    public static String BEAN_DATE_UNFORMAT_EXPRESSION;
    public static String BEAN_DATE_UNFORMAT_CLASS;

    static {
        AUTHER_COMMENT = PropertiesUtils.getProperty("auther.comment");

        // 需要忽略的属性
        IGNORE_BEAN_TOJSON_FILED = PropertiesUtils.getProperty("ignore.bean.tojson.filed");
        IGNORE_BEAN_TOJSON_EXPRESSION = PropertiesUtils.getProperty("ignore.bean.tojson.expression");
        IGNORE_BEAN_TOJSON_CLASS = PropertiesUtils.getProperty("ignore.bean.tojson.class");

        // 日期序列化，反序列化
        BEAN_DATE_FORMAT_EXPRESSION = PropertiesUtils.getProperty("bean.date.format.expression");
        BEAN_DATE_FORMAT_CLASS = PropertiesUtils.getProperty("bean.date.format.class");
        BEAN_DATE_UNFORMAT_EXPRESSION = PropertiesUtils.getProperty("bean.date.unformat.expression");
        BEAN_DATE_UNFORMAT_CLASS = PropertiesUtils.getProperty("bean.date.unformat.class");

        IGNORE_TABLE_PERFIX = Boolean.valueOf(PropertiesUtils.getProperty("ignore.table.perfix"));
        SUFFIX_BEAN_QUERY = PropertiesUtils.getProperty("suffix.bean.query");
        SUFFIX_BEAN_QUERY_FUZZY = PropertiesUtils.getProperty("suffix.bean.query.fuzzy");
        SUFFIX_BEAN_QUERY_TIME_START = PropertiesUtils.getProperty("suffix.bean.query.time.start");
        SUFFIX_BEAN_QUERY_TIME_END = PropertiesUtils.getProperty("suffix.bean.query.time.end");
        SUFFIX_MAPPER = PropertiesUtils.getProperty("suffix.mapper");

        PACKAGE_BASE = PropertiesUtils.getProperty("package.base");
        PACKAGE_PO = PACKAGE_BASE + '.' + PropertiesUtils.getProperty("package.po");
        PACKAGE_VO = PACKAGE_BASE + '.' + PropertiesUtils.getProperty("package.vo");
        PACKAGE_UTILS = PACKAGE_BASE + '.' + PropertiesUtils.getProperty("package.utils");
        PACKAGE_ENUMS = PACKAGE_BASE + '.' + PropertiesUtils.getProperty("package.enums");
        PACKAGE_QUERY = PACKAGE_BASE + '.' + PropertiesUtils.getProperty("package.query");
        PACKAGE_MAPPER = PACKAGE_BASE + '.' + PropertiesUtils.getProperty("package.mapper");
        PACKAGE_SERVICE = PACKAGE_BASE + '.' + PropertiesUtils.getProperty("package.service");
        PACKAGE_SERVICE_IMPL = PACKAGE_BASE + '.' + PropertiesUtils.getProperty("package.service.impl");

        PATH_BASE = PropertiesUtils.getProperty("path.base");
        PATH_BASE = PATH_BASE + PATH_JAVA;
        PATH_PO = PATH_BASE + "/" + PACKAGE_PO.replace('.', '/');
        PATH_VO = PATH_BASE + "/" + PACKAGE_VO.replace('.', '/');
        PATH_UTILS = PATH_BASE + "/" + PACKAGE_UTILS.replace('.', '/');
        PATH_ENUMS = PATH_BASE + "/" + PACKAGE_ENUMS.replace('.', '/');
        PATH_QUERY = PATH_BASE + "/" + PACKAGE_QUERY.replace('.', '/');
        PATH_MAPPER = PATH_BASE + "/" + PACKAGE_MAPPER.replace('.', '/');

        PATH_MAPPER_XML = PropertiesUtils.getProperty("path.base") + PATH_RESOURCES + "/"
                + PACKAGE_MAPPER.replace('.', '/');
        PATH_SERVICE = PATH_BASE + "/" + PACKAGE_SERVICE.replace('.', '/');
        PATH_SERVICE_IMPL = PATH_BASE + "/" + PACKAGE_SERVICE_IMPL.replace('.', '/');
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
        System.out.println(PATH_MAPPER_XML);
    }
}

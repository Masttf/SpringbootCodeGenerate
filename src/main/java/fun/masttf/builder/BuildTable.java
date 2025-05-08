package fun.masttf.builder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fun.masttf.utils.*;

import fun.masttf.bean.Constants;
import fun.masttf.bean.FieldInfo;
import fun.masttf.bean.TableInfo;

public class BuildTable {
    private static Connection conn = null;
    private static final Logger logger = LoggerFactory.getLogger(BuildTable.class);
    private static String SQL_SHOW_TABLE_STATUS = "show table status";
    private static String SQL_SHOW_TABLE_FIELDS = "show full fields from %s";
    private static String SQL_SHOW_TABLE_INDEX = "show index from %s";
    static {
        try {
            Class.forName(PropertiesUtils.getProperty("db.driver.name"));
            conn = DriverManager.getConnection(
                    PropertiesUtils.getProperty("db.url"),
                    PropertiesUtils.getProperty("db.username"),
                    PropertiesUtils.getProperty("db.password"));
        } catch (Exception e) {
            logger.error("数据库连接失败", e);
        }
    }

    public static List<TableInfo> getTables() {
        List<TableInfo> tableInfoList = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String tableName = rs.getString("Name");
                String tableComment = rs.getString("Comment");
                TableInfo tableInfo = new TableInfo();
                String beanName = tableName;
                if (Constants.IGNORE_TABLE_PERFIX) {
                    beanName = tableName.substring(tableName.indexOf("_") + 1);
                }
                beanName = processField(beanName, true);
                tableInfo.setTableName(tableName);
                tableInfo.setComment(tableComment);
                tableInfo.setBeanName(beanName);
                tableInfo.setBeanQueryName(beanName + Constants.SUFFIX_BEAN_QUERY);
                readFieldInfo(tableInfo);
                getKeyIndexInfo(tableInfo);
                tableInfoList.add(tableInfo);
            }
        } catch (Exception e) {
            logger.error("获取表信息失败", e);
        }
        return tableInfoList;
    }

    private static void readFieldInfo(TableInfo tableInfo) {
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        List<FieldInfo> fieldExtendsList = new ArrayList<>();
        try (PreparedStatement ps = conn
                .prepareStatement(String.format(SQL_SHOW_TABLE_FIELDS, tableInfo.getTableName()));
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String field = rs.getString("Field");
                String type = rs.getString("Type");
                String extra = rs.getString("Extra");
                String comment = rs.getString("Comment");
                if (type.indexOf('(') > 0) {
                    type = type.substring(0, type.indexOf('('));
                }
                String propertyName = processField(field, false);
                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.setFieldName(field);
                fieldInfo.setSqlType(type);
                fieldInfo.setComment(comment);
                fieldInfo.setPropertyName(propertyName);
                fieldInfo.setAutoIncrement("auto_increment".equalsIgnoreCase(extra) ? true : false);
                fieldInfo.setJavaType(processJavaType(type));
                fieldInfoList.add(fieldInfo);
                if (ArrayUtils.contains(Constants.SQL_DATE_TIIME_TYPES, type)) {
                    tableInfo.setHaveDateTime(true);
                }
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, type)) {
                    tableInfo.setHaveDate(true);
                }
                if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPE, type)) {
                    tableInfo.setHaveBigDecimal(true);
                }
                if (fieldInfo.getJavaType().equals("String")) {
                    FieldInfo fieldFuzzy = new FieldInfo();
                    fieldFuzzy.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_FUZZY);
                    fieldFuzzy.setJavaType("String");
                    fieldFuzzy.setFieldName(field);
                    fieldFuzzy.setSqlType(type);
                    fieldExtendsList.add(fieldFuzzy);
                }

                if (fieldInfo.getJavaType().equals("Date")) {
                    FieldInfo fieldStart = new FieldInfo();
                    fieldStart.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_START);
                    fieldStart.setJavaType("String");
                    fieldStart.setFieldName(field);
                    fieldStart.setSqlType(type);
                    fieldExtendsList.add(fieldStart);

                    FieldInfo fieldEnd = new FieldInfo();
                    fieldEnd.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_END);
                    fieldEnd.setJavaType("String");
                    fieldEnd.setFieldName(field);
                    fieldEnd.setSqlType(type);
                    fieldExtendsList.add(fieldEnd);

                }
            }
            tableInfo.setFieldList(fieldInfoList);
            tableInfo.setFieldExtendsList(fieldExtendsList);
        } catch (Exception e) {
            logger.error("获取字段信息失败", e);
        }
        return;
    }

    private static void getKeyIndexInfo(TableInfo tableInfo) {
        try (PreparedStatement ps = conn
                .prepareStatement(String.format(SQL_SHOW_TABLE_INDEX, tableInfo.getTableName()));
                ResultSet rs = ps.executeQuery()) {
            Map<String, FieldInfo> fieldInfoList = new HashMap<>();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                fieldInfoList.put(fieldInfo.getFieldName(), fieldInfo);
            }
            while (rs.next()) {
                String keyName = rs.getString("Key_name");
                Integer nonUnique = rs.getInt("Non_unique");
                String columnName = rs.getString("Column_name");
                // 不是唯一索引跳过
                if (nonUnique == 1) {
                    continue;
                }
                List<FieldInfo> KeyFieldList = tableInfo.getKeyIndexMap().get(keyName);
                if (KeyFieldList == null) {
                    KeyFieldList = new ArrayList<>();
                    tableInfo.getKeyIndexMap().put(keyName, KeyFieldList);
                }
                KeyFieldList.add(fieldInfoList.get(columnName));
            }

        } catch (Exception e) {
            logger.error("获取索引信息失败", e);
        }
        return;
    }

    private static String processField(String field, Boolean uperCaseFirstLetter) {
        StringBuffer sb = new StringBuffer();
        String[] fields = field.split("_");
        sb.append(uperCaseFirstLetter ? StringUtils.upperCaseFirstLetter(fields[0]) : fields[0]);
        for (int i = 1; i < fields.length; i++) {
            sb.append(StringUtils.upperCaseFirstLetter(fields[i]));
        }
        return sb.toString();
    }

    private static String processJavaType(String type) {
        if (ArrayUtils.contains(Constants.SQL_INTEGER_TYPE, type)) {
            return "Integer";
        } else if (ArrayUtils.contains(Constants.SQL_LONG_TYPE, type)) {
            return "Long";
        } else if (ArrayUtils.contains(Constants.SQL_STRING_TYPE, type)) {
            return "String";
        } else if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, type)
                || ArrayUtils.contains(Constants.SQL_DATE_TIIME_TYPES, type)) {
            return "Date";
        } else if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPE, type)) {
            return "BigDecimal";
        } else {
            throw new RuntimeException("无法识别类型: " + type);
        }
    }
}

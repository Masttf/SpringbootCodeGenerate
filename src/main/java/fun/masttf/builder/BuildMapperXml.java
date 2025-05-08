package fun.masttf.builder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fun.masttf.bean.Constants;
import fun.masttf.bean.FieldInfo;
import fun.masttf.bean.TableInfo;
import fun.masttf.utils.StringUtils;

public class BuildMapperXml {
    private static final Logger logger = LoggerFactory.getLogger(BuildMapperXml.class);
    private static final String BaseResultMap = "BaseResultMap";
    private static final String BaseColumnList = "BaseColumnList";
    private static final String BaseQueryCondition = "BaseQueryCondition";
    private static final String BaseQueryExtendCondition = "BaseQueryExtendCondition";
    private static final String QueryCondition = "QueryCondition";

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_MAPPER_XML);
        if (!folder.exists())
            folder.mkdirs();

        String mapperName = tableInfo.getBeanName() + Constants.SUFFIX_MAPPER;
        String PoClass = Constants.PACKAGE_PO + "." + tableInfo.getBeanName();
        File poFile = new File(folder, mapperName + ".xml");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(out, "UTF-8");
            bw = new BufferedWriter(outw);
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            bw.newLine();
            bw.write(
                    "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
            bw.newLine();
            bw.write("<mapper namespace=\"" + Constants.PACKAGE_MAPPER + "." + mapperName + "\">");
            bw.newLine();

            bw.write("\t<!-- 实体类映射 -->");
            bw.newLine();
            bw.write("\t<resultMap id=\"" + BaseResultMap + "\" type=\"" + PoClass + "\">");
            bw.newLine();

            FieldInfo idField = null;
            List<FieldInfo> primaryFields = tableInfo.getKeyIndexMap().get("PRIMARY");
            if (primaryFields != null && primaryFields.size() == 1) {
                idField = primaryFields.get(0);
            }

            if (idField != null) {
                bw.write("\t\t<!--" + idField.getComment() + " -->");
                bw.newLine();
                bw.write("\t\t<id column=\"" + idField.getFieldName() + "\" property=\"" + idField.getPropertyName()
                        + "\" />");
                bw.newLine();
            }
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (idField != null && fieldInfo.getPropertyName().equals(idField.getPropertyName())) {
                    continue;
                }
                bw.write("\t\t<!--" + fieldInfo.getComment() + " -->");
                bw.newLine();
                bw.write("\t\t<result column=\"" + fieldInfo.getFieldName() + "\" property=\""
                        + fieldInfo.getPropertyName() + "\" />");
                bw.newLine();
            }
            bw.write("\t</resultMap>");
            bw.newLine();
            bw.newLine();

            bw.write("\t<!-- 基础查询列 -->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BaseColumnList + "\">");
            bw.newLine();
            StringBuilder colBuilder = new StringBuilder();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                colBuilder.append(fieldInfo.getFieldName()).append(",");
            }
            bw.write("\t\t" + colBuilder.substring(0, colBuilder.length() - 1));
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();
            bw.newLine();

            bw.write("\t<!-- 基础查询条件 -->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BaseQueryCondition + "\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                String tmpTest = "query." + fieldInfo.getPropertyName() + " != null";
                if (fieldInfo.getJavaType().equals("String")) {
                    tmpTest += " and query." + fieldInfo.getPropertyName() + " != ''";
                }
                bw.write("\t\t<if test=\"" + tmpTest + "\">");
                bw.newLine();
                bw.write("\t\t\tand " + fieldInfo.getFieldName() + " = #{query." + fieldInfo.getPropertyName() + "}");
                bw.newLine();
                bw.write("\t\t</if>");
                bw.newLine();
            }
            bw.write("\t</sql>");
            bw.newLine();
            bw.newLine();

            bw.write("\t<!-- 拓展的查询条件 -->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BaseQueryExtendCondition + "\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldExtendsList()) {
                String tmpTest = "query." + fieldInfo.getPropertyName() + " != null and query."
                        + fieldInfo.getPropertyName() + " != ''";

                String andWhere = "";
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPE, fieldInfo.getSqlType())) {
                    andWhere = "and " + fieldInfo.getFieldName() + " LIKE CONCAT('%', #{query."
                            + fieldInfo.getPropertyName() + "}, '%')";
                } else if (ArrayUtils.contains(Constants.SQL_DATE_TIIME_TYPES, fieldInfo.getSqlType())
                        || ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType())) {
                    if (fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_START)) {
                        andWhere = "<![CDATA[ and " + fieldInfo.getFieldName() + " >= str_to_date(#{query."
                                + fieldInfo.getPropertyName() + "},'%Y-%m-%d') ]]>";
                    } else if (fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_END)) {
                        andWhere = "<![CDATA[ and " + fieldInfo.getFieldName() + " < DATE_ADD(str_to_date(#{query."
                                + fieldInfo.getPropertyName() + "},'%Y-%m-%d'), INTERVAL 1 DAY) ]]>";
                    }
                }
                bw.write("\t\t<if test=\"" + tmpTest + "\">");
                bw.newLine();
                bw.write("\t\t\t" + andWhere);
                bw.newLine();
                bw.write("\t\t</if>");
                bw.newLine();
            }
            bw.write("\t</sql>");
            bw.newLine();
            bw.newLine();

            bw.write("\t<!-- 通用查询条件 -->");
            bw.newLine();
            bw.write("\t<sql id=\"" + QueryCondition + "\">");
            bw.newLine();
            bw.write("\t\t<where>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\"" + BaseQueryCondition + "\" />");
            bw.newLine();
            bw.write("\t\t\t<include refid=\"" + BaseQueryExtendCondition + "\" />");
            bw.newLine();
            bw.write("\t\t</where>");
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();
            bw.newLine();

            bw.write("\t<!-- 查询列表 -->");
            bw.newLine();
            bw.write("\t<select id=\"selectList\"  resultMap=\"" + BaseResultMap + "\">");
            bw.newLine();
            bw.write("\t\tSELECT <include refid=\"" + BaseColumnList + "\" />" + " FROM " + tableInfo.getTableName()
                    + " <include refid=\""
                    + QueryCondition + "\" />");
            bw.newLine();
            bw.write("\t\t<if test=\"query.orderBy != null and query.orderBy != ''\"> order by ${query.orderBy} </if>");
            bw.newLine();
            bw.write(
                    "\t\t<if test=\"query.simplePage != null\"> limit #{query.simplePage.start}, #{query.simplePage.end} </if>");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();
            bw.newLine();

            bw.write("\t<!-- 查询数量 -->");
            bw.newLine();
            bw.write("\t<select id=\"selectCount\"  resultType=\"java.lang.Integer\">");
            bw.newLine();
            bw.write("\t\t SELECT count(1) FROM " + tableInfo.getTableName() + " <include refid=\""
                    + QueryCondition + "\" />");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();
            bw.newLine();

            FieldInfo autoIncrease = null;
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (fieldInfo.isAutoIncrement()) {
                    autoIncrease = fieldInfo;
                    break;
                }
            }

            bw.write("\t<!-- 插入(匹配有效值) -->");
            bw.newLine();
            bw.write("\t<insert id=\"insert\">");
            bw.newLine();
            if (autoIncrease != null) {
                bw.write("\t\t<selectKey keyProperty=\"" + autoIncrease.getPropertyName() + "\" resultType=\""
                        + autoIncrease.getJavaType() + "\" order=\"AFTER\">");
                bw.newLine();
                bw.write("\t\t\tSELECT LAST_INSERT_ID()");
                bw.newLine();
                bw.write("\t\t</selectKey>");
                bw.newLine();
            }
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (fieldInfo.isAutoIncrement())
                    continue;
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (fieldInfo.isAutoIncrement())
                    continue;
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();
            bw.newLine();

            bw.write("\t<!-- 插入或者更新(匹配有效值) -->");
            bw.newLine();
            bw.write("\t<insert id=\"insertOrUpdate\">");
            bw.newLine();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"ON DUPLICATE KEY UPDATE\" suffixOverrides=\",\">");
            bw.newLine();

            Map<String, String> keyTempMap = new HashMap<>();
            for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
                List<FieldInfo> fieldInfoList = entry.getValue();
                for (FieldInfo fieldInfo : fieldInfoList) {
                    keyTempMap.put(fieldInfo.getFieldName(), fieldInfo.getFieldName());
                }
            }

            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (keyTempMap.containsKey(fieldInfo.getFieldName()))
                    continue;
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " = VALUES(" + fieldInfo.getFieldName() + "),");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();
            bw.newLine();

            StringBuffer fieldBuffer = new StringBuffer();
            StringBuffer propertyBuffer = new StringBuffer();
            StringBuffer fieldBuffer2 = new StringBuffer();
            StringBuffer fieldBufferAll = new StringBuffer();
            StringBuffer propertyBufferAll = new StringBuffer();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                fieldBufferAll.append(fieldInfo.getFieldName()).append(",");
                fieldBuffer2.append(fieldInfo.getFieldName() + " = VALUES(" + fieldInfo.getFieldName() + "),");
                propertyBufferAll.append("#{item.").append(fieldInfo.getPropertyName()).append("},");
                if (fieldInfo.isAutoIncrement())
                    continue;
                fieldBuffer.append(fieldInfo.getFieldName()).append(",");
                propertyBuffer.append("#{item.").append(fieldInfo.getPropertyName()).append("},");
            }

            bw.write("\t<!-- 添加(批量插入) -->");
            bw.newLine();
            bw.write("\t<insert id=\"insertBatch\">");
            bw.newLine();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName() + "("
                    + fieldBuffer.substring(0, fieldBuffer.length() - 1)
                    + ") VALUES");
            bw.newLine();
            bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\">");
            bw.newLine();
            bw.write("\t\t\t(" + propertyBuffer.substring(0, propertyBuffer.length() - 1) + ")");
            bw.newLine();
            bw.write("\t\t</foreach>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();
            bw.newLine();

            bw.write("\t<!-- 批量插入或更新(覆盖) -->");
            bw.newLine();
            bw.write("\t<insert id=\"insertOrUpdateBatch\">");
            bw.newLine();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName() + "("
                    + fieldBufferAll.substring(0, fieldBufferAll.length() - 1)
                    + ") VALUES");
            bw.newLine();
            bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\">");
            bw.newLine();
            bw.write("\t\t\t(" + propertyBufferAll.substring(0, propertyBufferAll.length() - 1) + ")");
            bw.newLine();
            bw.write("\t\t</foreach>");
            bw.newLine();
            bw.write("\t\tON DUPLICATE KEY UPDATE");
            bw.newLine();
            bw.write("\t\t" + fieldBuffer2.substring(0, fieldBuffer2.length() - 1));
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();
            bw.newLine();

            // 根据唯一索引更新
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                List<FieldInfo> fieldInfos = entry.getValue();
                StringBuilder methodName = new StringBuilder();
                StringBuilder paramName = new StringBuilder();
                Integer index = 0;
                for (FieldInfo fieldInfo : fieldInfos) {
                    if (index > 0) {
                        methodName.append("And");
                        paramName.append(" and ");
                    }
                    methodName.append(StringUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()));
                    paramName.append(fieldInfo.getFieldName() + " = #{" + fieldInfo.getPropertyName() + "}");
                    index++;
                }
                bw.write("\t<!-- 根据" + methodName + "查询 -->");
                bw.newLine();
                bw.write("\t<select id=\"selectBy" + methodName + "\" resultMap=\"" + BaseResultMap + "\">");
                bw.newLine();
                bw.write("\t\tSELECT <include refid=\"" + BaseColumnList + "\" />" + " FROM " + tableInfo.getTableName()
                        + " WHERE " + paramName);
                bw.newLine();
                bw.write("\t</select>");
                bw.newLine();
                bw.newLine();

                bw.write("\t<!-- 根据" + methodName + "更新 -->");
                bw.newLine();
                bw.write("\t<update id=\"updateBy" + methodName + "\">");
                bw.newLine();
                bw.write("\t\tUPDATE " + tableInfo.getTableName());
                bw.newLine();
                bw.write("\t\t<set>");
                bw.newLine();
                for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                    if (fieldInfo.isAutoIncrement())
                        continue;
                    bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                    bw.newLine();
                    bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " = #{bean." + fieldInfo.getPropertyName() + "},");
                    bw.newLine();
                    bw.write("\t\t\t</if>");
                    bw.newLine();
                }
                bw.write("\t\t</set>");
                bw.newLine();
                bw.write("\t\tWHERE " + paramName);
                bw.newLine();
                bw.write("\t</update>");
                bw.newLine();
                bw.newLine();

                bw.write("\t<!-- 根据" + methodName + "删除 -->");
                bw.newLine();
                bw.write("\t<delete id=\"deleteBy" + methodName + "\">");
                bw.newLine();
                bw.write("\t\tDELETE FROM " + tableInfo.getTableName() + " WHERE " + paramName);
                bw.newLine();
                bw.write("\t</delete>");
                bw.newLine();
                bw.newLine();
            }

            bw.write("</mapper>");
            bw.newLine();
            bw.flush();
        } catch (Exception e) {
            logger.error("创建MapperXml失败", e);
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (outw != null)
                    outw.close();
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

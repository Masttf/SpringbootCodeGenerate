package fun.masttf.builder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fun.masttf.utils.StringUtils;

import fun.masttf.bean.Constants;
import fun.masttf.bean.FieldInfo;
import fun.masttf.bean.TableInfo;

public class BuildMapperXml {
    private static final Logger logger = LoggerFactory.getLogger(BuildMapperXml.class);
    private static final String BaseResultMap = "BaseResultMap";
    private static final String BaseColumnList = "BaseColumnList";
    private static final String BaseQueryCondition = "BaseQueryCondition";

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

            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t<!--" + fieldInfo.getComment() + " -->");
                bw.newLine();
                String Key = "result";
                if (idField != null && fieldInfo.getPropertyName().equals(idField.getPropertyName())) {
                    Key = "id";
                }
                bw.write("\t\t<" + Key + " column=\"" + fieldInfo.getFieldName() + "\" property=\""
                        + fieldInfo.getPropertyName() + "\" />");
                bw.newLine();
            }
            bw.write("\t</resultMap>");
            bw.newLine();

            bw.write("\t<!-- 通用查询列 -->");
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

            bw.write("\t<!-- 基础查询条件 -->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BaseQueryCondition + "\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                String tmpTest = "query." + fieldInfo.getPropertyName() + " != null";
                if (fieldInfo.getJavaType().equals("String")) {
                    tmpTest += "AND query." + fieldInfo.getPropertyName() + " != ''";
                }
                bw.write("\t\t<if test=\"" + tmpTest + "\">");
                bw.newLine();
                bw.write("\t\t\tAND " + fieldInfo.getFieldName() + " = #{query." + fieldInfo.getPropertyName() + "}");
                bw.newLine();
                bw.write("\t\t</if>");
                bw.newLine();
            }
            bw.write("\t</sql>");
            bw.newLine();

            bw.newLine();
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

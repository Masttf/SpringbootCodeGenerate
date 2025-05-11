package fun.masttf.builder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fun.masttf.utils.StringUtils;

import fun.masttf.bean.Constants;
import fun.masttf.bean.FieldInfo;
import fun.masttf.bean.TableInfo;

public class BuildQuery {
    private static final Logger logger = LoggerFactory.getLogger(BuildQuery.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_QUERY);
        if (!folder.exists())
            folder.mkdirs();

        String className = tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY;
        File queryFile = new File(folder, className + ".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(queryFile);
            outw = new OutputStreamWriter(out, "UTF-8");
            bw = new BufferedWriter(outw);

            bw.write("package " + Constants.PACKAGE_QUERY + ";");
            bw.newLine();
            bw.newLine();

            if (tableInfo.isHaveDate() || tableInfo.isHaveDateTime()) {
                bw.write("import java.util.Date;");
                bw.newLine();
            }
            if (tableInfo.isHaveBigDecimal()) {
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }

            bw.newLine();
            BuildComment.createClassComment(bw, tableInfo.getComment() + "查询类");
            bw.write("public class " + className + " extends BaseQuery {");
            bw.newLine();
            bw.newLine();
            for (FieldInfo field : tableInfo.getFieldList()) {
                BuildComment.createFieldComment(bw, field.getComment());
                bw.write("\tprivate " + field.getJavaType() + " " + field.getPropertyName() + ";");
                bw.newLine();
                bw.newLine();

                if (field.getJavaType().equals("String")) {
                    bw.write("\tprivate String " + field.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_FUZZY + ";");
                    bw.newLine();
                    bw.newLine();

                }

                if (field.getJavaType().equals("Date")) {
                    bw.write("\tprivate String " + field.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_START
                            + ";");
                    bw.newLine();
                    bw.newLine();
                    bw.write("\tprivate String " + field.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_END
                            + ";");
                    bw.newLine();
                    bw.newLine();
                }
            }
            BuildGetterAndSetter(tableInfo.getFieldList(), bw);
            BuildGetterAndSetter(tableInfo.getFieldExtendsList(), bw);

            bw.newLine();
            bw.write("}");
            bw.flush();
        } catch (

        Exception e) {
            logger.error("创建Query失败", e);
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

    private static void BuildGetterAndSetter(List<FieldInfo> fieldList, BufferedWriter bw) throws IOException {
        for (FieldInfo field : fieldList) {
            String propertyName = field.getPropertyName();
            String tempFIeld;
            if (propertyName.length() > 1 && Character.isLowerCase(propertyName.charAt(0)) && Character.isUpperCase(propertyName.charAt(1))) {
                // logger.info(propertyName);
                tempFIeld = propertyName;
            } else {
                tempFIeld = StringUtils.upperCaseFirstLetter(propertyName);
            }
            bw.write("\tpublic " + field.getJavaType() + " get" + tempFIeld + "() {");
            bw.newLine();
            bw.write("\t\treturn this." + field.getPropertyName() + ";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            bw.write("\tpublic void set" + tempFIeld + "(" + field.getJavaType() + " "
                    + field.getPropertyName() + ") {");
            bw.newLine();
            bw.write("\t\tthis." + field.getPropertyName() + " = " + field.getPropertyName() + ";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
        }
    }
}

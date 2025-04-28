package fun.masttf.builder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

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
            bw.write("public class " + className + " {");
            bw.newLine();
            bw.newLine();
            ArrayList<FieldInfo> fieldExtends = new ArrayList<>();
            for (FieldInfo field : tableInfo.getFieldList()) {
                BuildComment.createFieldComment(bw, field.getComment());
                bw.write("\tprivate " + field.getJavaType() + " " + field.getPropertyName() + ";");
                bw.newLine();
                bw.newLine();

                if (field.getJavaType().equals("String")) {
                    FieldInfo fieldFuzzy = new FieldInfo();
                    fieldFuzzy.setPropertyName(field.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_FUZZY);
                    fieldFuzzy.setJavaType("String");
                    fieldExtends.add(fieldFuzzy);

                    bw.write("\tprivate String " + fieldFuzzy.getPropertyName() + ";");
                    bw.newLine();
                    bw.newLine();

                }

                if (field.getJavaType().equals("Date")) {
                    FieldInfo fieldStart = new FieldInfo();
                    fieldStart.setPropertyName(field.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_START);
                    fieldStart.setJavaType("String");
                    fieldExtends.add(fieldStart);

                    FieldInfo fieldEnd = new FieldInfo();
                    fieldEnd.setPropertyName(field.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_END);
                    fieldEnd.setJavaType("String");
                    fieldExtends.add(fieldEnd);

                    bw.write("\tprivate String " + fieldStart.getPropertyName()
                            + ";");
                    bw.newLine();
                    bw.newLine();
                    bw.write("\tprivate String " + fieldEnd.getPropertyName()
                            + ";");
                    bw.newLine();
                    bw.newLine();
                }
            }
            tableInfo.getFieldList().addAll(fieldExtends);
            for (FieldInfo field : tableInfo.getFieldList()) {
                String tempFIeld = StringUtils.upperCaseFirstLetter(field.getPropertyName());
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

            bw.newLine();
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
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
}

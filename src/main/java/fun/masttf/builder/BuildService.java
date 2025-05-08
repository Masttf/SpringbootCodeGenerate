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

import fun.masttf.bean.Constants;
import fun.masttf.bean.FieldInfo;
import fun.masttf.bean.TableInfo;
import fun.masttf.utils.StringUtils;

public class BuildService {
    private static final Logger logger = LoggerFactory.getLogger(BuildService.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_SERVICE);
        if (!folder.exists())
            folder.mkdirs();

        String className = tableInfo.getBeanName() + "Service";
        File poFile = new File(folder, className + ".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(out, "UTF-8");
            bw = new BufferedWriter(outw);

            bw.write("package " + Constants.PACKAGE_SERVICE + ";");
            bw.newLine();
            bw.newLine();

            bw.write("import java.util.List;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + ".PaginationResultVo;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanQueryName() + ";");
            bw.newLine();
            bw.newLine();

            BuildComment.createClassComment(bw, tableInfo.getComment() + "Serviece");
            bw.write("public interface " + className + " {");
            bw.newLine();
            bw.newLine();
            BuildComment.createMethodComment(bw, "根据条件查询列表");
            bw.write("\tList<" + tableInfo.getBeanName() + "> findListByQuery(" + tableInfo.getBeanQueryName()
                    + " query);");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "根据条件查询数量");
            bw.write("\tInteger findCountByQuery(" + tableInfo.getBeanQueryName() + " query);");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "分页查询");
            bw.write("\tPaginationResultVo<" + tableInfo.getBeanName() + ">" + " findListByPage("
                    + tableInfo.getBeanQueryName() + " query);");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "新增");
            bw.write("\tInteger add(" + tableInfo.getBeanName() + " bean);");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "批量新增");
            bw.write("\tInteger addBatch(List<" + tableInfo.getBeanName() + "> listBean);");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "批量新增/修改");
            bw.write("\tInteger addOrUpdateBatch(List<" + tableInfo.getBeanName() + "> listBean);");
            bw.newLine();
            bw.newLine();

            for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
                List<FieldInfo> fieldInfos = entry.getValue();
                StringBuilder methodName = new StringBuilder();
                StringBuilder methodParams = new StringBuilder();
                Integer index = 0;
                for (FieldInfo fieldInfo : fieldInfos) {
                    if (index > 0) {
                        methodName.append("And");
                        methodParams.append(", ");
                    }
                    methodName.append(StringUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()));
                    methodParams.append(fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
                    index++;
                }
                BuildComment.createMethodComment(bw, "根据" + methodName + "查询");
                bw.write("\t" + tableInfo.getBeanName() + " getBy" + methodName + "(" + methodParams + ");");
                bw.newLine();
                bw.newLine();

                BuildComment.createMethodComment(bw, "根据" + methodName + "更新");
                bw.write("\tInteger updateBy" + methodName + "(" + tableInfo.getBeanName() + " bean, " + methodParams
                        + ");");
                bw.newLine();
                bw.newLine();

                BuildComment.createMethodComment(bw, "根据" + methodName + "删除");
                bw.write("\tInteger deleteBy" + methodName + "(" + methodParams + ");");
                bw.newLine();
                bw.newLine();
            }

            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("创建Service失败", e);
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

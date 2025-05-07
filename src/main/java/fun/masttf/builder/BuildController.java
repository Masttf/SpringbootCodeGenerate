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

public class BuildController {
    private static final Logger logger = LoggerFactory.getLogger(BuildController.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_CONTROLLER);
        if (!folder.exists())
            folder.mkdirs();

        String className = tableInfo.getBeanName() + "Controller";
        String serviceName = tableInfo.getBeanName() + "Service";
        String serviceBeanName = StringUtils.lowerCaseFirstLetter(serviceName);
        File poFile = new File(folder, className + ".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(out, "UTF-8");
            bw = new BufferedWriter(outw);

            bw.write("package " + Constants.PACKAGE_CONTROLLER + ";");
            bw.newLine();
            bw.newLine();

            bw.write("import java.util.List;");
            bw.newLine();
            bw.write("import org.springframework.beans.factory.annotation.Autowired;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RequestMapping;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RestController;");
            bw.newLine();
            bw.write("import org.springframework.beans.factory.annotation.Qualifier;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RequestBody;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + ".ResponseVo;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() +
                    ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." +
                    tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_SERVICE + "." + serviceName + ";");
            bw.newLine();
            bw.newLine();

            BuildComment.createClassComment(bw, tableInfo.getComment() + "Controller");
            bw.write("@RestController(\"/api/" + StringUtils.lowerCaseFirstLetter(serviceBeanName) + "\")");
            bw.newLine();
            bw.write("public class " + className + " extends ABaseController {");
            bw.newLine();
            bw.newLine();

            bw.write("\t@Autowired");
            bw.newLine();
            bw.write("\t@Qualifier(\"" + serviceBeanName + "\")");
            bw.newLine();
            bw.write("\tprivate " + serviceName + " " + serviceBeanName + ";");
            bw.newLine();
            bw.newLine();
            BuildComment.createMethodComment(bw, "根据条件分页查询");
            bw.write("\t@RequestMapping(\"loadDataList\")");
            bw.newLine();
            bw.write("\tpublic ResponseVo<Object> loadDataList(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVo(" + serviceBeanName + ".findListByPage(query));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "新增");
            bw.write("\t@RequestMapping(\"add\")");
            bw.newLine();
            bw.write("\tpublic ResponseVo<Object> add(" + tableInfo.getBeanName() + " bean) {");
            bw.newLine();
            bw.write("\t\t" + serviceBeanName + ".add(bean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVo(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "批量新增");
            bw.write("\t@RequestMapping(\"addBatch\")");
            bw.newLine();
            bw.write("\tpublic ResponseVo<Object> addBatch(@RequestBody List<" + tableInfo.getBeanName()
                    + "> listBean) {");
            bw.newLine();
            bw.write("\t\t" + serviceBeanName + ".addBatch(listBean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVo(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "批量新增/修改");
            bw.write("\t@RequestMapping(\"addOrUpdateBatch\")");
            bw.newLine();
            bw.write(
                    "\tpublic ResponseVo<Object> addOrUpdateBatch(@RequestBody List<" + tableInfo.getBeanName()
                            + "> listBean) {");
            bw.newLine();
            bw.write("\t\t" + serviceBeanName + ".addOrUpdateBatch(listBean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVo(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
                List<FieldInfo> fieldInfos = entry.getValue();
                StringBuilder methodName = new StringBuilder();
                StringBuilder methodParams = new StringBuilder();
                StringBuilder params = new StringBuilder();
                Integer index = 0;
                for (FieldInfo fieldInfo : fieldInfos) {
                    if (index > 0) {
                        methodName.append("And");
                        methodParams.append(", ");
                        params.append(", ");
                    }
                    methodName.append(StringUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()));
                    methodParams.append(fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
                    params.append(fieldInfo.getPropertyName());
                    index++;
                }
                BuildComment.createMethodComment(bw, "根据" + methodName + "查询");
                bw.write("\t@RequestMapping(\"getBy" + methodName + "\")");
                bw.newLine();
                bw.write("\tpublic ResponseVo<Object> getBy" + methodName + "(" + methodParams + ") {");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVo(" + serviceBeanName + ".getBy" + methodName + "(" + params
                        + "));");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                BuildComment.createMethodComment(bw, "根据" + methodName + "更新");
                bw.write("\t@RequestMapping(\"updateBy" + methodName + "\")");
                bw.newLine();
                bw.write(
                        "\tpublic ResponseVo<Object> updateBy" + methodName + "(" + tableInfo.getBeanName() + " bean, "
                                + methodParams
                                + ") {");
                bw.newLine();
                bw.write("\t\t" + serviceBeanName + ".updateBy" + methodName + "(bean, " + params + ");");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVo(null);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                BuildComment.createMethodComment(bw, "根据" + methodName + "删除");
                bw.write("\t@RequestMapping(\"deleteBy" + methodName + "\")");
                bw.newLine();
                bw.write("\tpublic ResponseVo<Object> deleteBy" + methodName + "(" + methodParams + ") {");
                bw.newLine();
                bw.write("\t\t" + serviceBeanName + ".deleteBy" + methodName + "(" + params + ");");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVo(null);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
            }

            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("创建ServiceImpl失败", e);
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

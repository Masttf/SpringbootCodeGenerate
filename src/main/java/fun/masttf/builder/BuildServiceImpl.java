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

public class BuildServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(BuildServiceImpl.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_SERVICE_IMPL);
        if (!folder.exists())
            folder.mkdirs();

        String interfaceName = tableInfo.getBeanName() + "Service";
        String className = tableInfo.getBeanName() + "ServiceImpl";
        String mapperName = tableInfo.getBeanName() + Constants.SUFFIX_MAPPER;
        String mapperBeanName = StringUtils.lowerCaseFirstLetter(mapperName);
        File poFile = new File(folder, className + ".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(out, "UTF-8");
            bw = new BufferedWriter(outw);

            bw.write("package " + Constants.PACKAGE_SERVICE_IMPL + ";");
            bw.newLine();
            bw.newLine();

            bw.write("import java.util.List;");
            bw.newLine();
            bw.write("import org.springframework.stereotype.Service;");
            bw.newLine();
            bw.write("import org.springframework.beans.factory.annotation.Autowired;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + ".PaginationResultVo;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_SERVICE + "." + interfaceName + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_MAPPER + "." + mapperName + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + ".SimplePage;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_ENUMS + ".PageSize;");
            bw.newLine();
            bw.newLine();

            BuildComment.createClassComment(bw, tableInfo.getComment() + "Serviece");
            bw.write("@Service(\"" + StringUtils.lowerCaseFirstLetter(interfaceName) + "\")");
            bw.newLine();
            bw.write("public class " + className + " implements " + interfaceName + " {");
            bw.newLine();
            bw.newLine();

            bw.write("\t@Autowired");
            bw.newLine();
            bw.write("\tprivate " + mapperName + "<" + tableInfo.getBeanName()
                    + ", " + tableInfo.getBeanParamName() + "> " + mapperBeanName + ";");
            bw.newLine();
            bw.newLine();
            BuildComment.createMethodComment(bw, "根据条件查询列表");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic List<" + tableInfo.getBeanName() + "> findListByParam(" + tableInfo.getBeanParamName()
                    + " query) {");
            bw.newLine();
            bw.write("\t\treturn " + mapperBeanName + ".selectList(query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "根据条件查询数量");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic Integer findCountByParam(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\treturn " + mapperBeanName + ".selectCount(query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "分页查询");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic PaginationResultVo<" + tableInfo.getBeanName() + ">" + " findListByPage("
                    + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\tInteger count = " + mapperBeanName + ".selectCount(query);");
            bw.newLine();
            bw.write(
                    "\t\tInteger pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();");
            bw.newLine();
            bw.write("\t\tSimplePage page = new SimplePage(query.getPageNo(), count, pageSize);");
            bw.newLine();
            bw.write("\t\tquery.setSimplePage(page);");
            bw.newLine();
            bw.write("\t\tList<" + tableInfo.getBeanName() + "> list = " + mapperBeanName + ".selectList(query);");
            bw.newLine();
            bw.write("\t\tPaginationResultVo<" + tableInfo.getBeanName() + "> result = new PaginationResultVo<"
                    + tableInfo.getBeanName()
                    + ">(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);");
            bw.newLine();
            bw.write("\t\treturn result;");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "新增");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic Integer add(" + tableInfo.getBeanName() + " bean) {");
            bw.newLine();
            bw.write("\t\treturn " + mapperBeanName + ".insert(bean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "批量新增");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic Integer addBatch(List<" + tableInfo.getBeanName() + "> listBean) {");
            bw.newLine();
            bw.write("\t\tif(listBean == null || listBean.isEmpty()) {");
            bw.newLine();
            bw.write("\t\t\treturn 0;");
            bw.newLine();
            bw.write("\t\t}");
            bw.newLine();
            bw.write("\t\treturn " + mapperBeanName + ".insertBatch(listBean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "批量新增/修改");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic Integer addOrUpdateBatch(List<" + tableInfo.getBeanName() + "> listBean) {");
            bw.newLine();
            bw.write("\t\tif(listBean == null || listBean.isEmpty()) {");
            bw.newLine();
            bw.write("\t\t\treturn 0;");
            bw.newLine();
            bw.write("\t\t}");
            bw.newLine();
            bw.write("\t\treturn " + mapperBeanName + ".insertOrUpdateBatch(listBean);");
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
                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic " + tableInfo.getBeanName() + " getBy" + methodName + "(" + methodParams + ") {");
                bw.newLine();
                bw.write("\t\treturn " + mapperBeanName + ".selectBy" + methodName + "(" + params + ");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                BuildComment.createMethodComment(bw, "根据" + methodName + "更新");
                bw.write("\t@Override");
                bw.newLine();
                bw.write(
                        "\tpublic Integer updateBy" + methodName + "(" + tableInfo.getBeanName() + " bean, "
                                + methodParams
                                + ") {");
                bw.newLine();
                bw.write("\t\treturn " + mapperBeanName + ".updateBy" + methodName + "(bean, " + params + ");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                BuildComment.createMethodComment(bw, "根据" + methodName + "删除");
                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic Integer deleteBy" + methodName + "(" + methodParams + ") {");
                bw.newLine();
                bw.write("\t\treturn " + mapperBeanName + ".deleteBy" + methodName + "(" + params + ");");
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

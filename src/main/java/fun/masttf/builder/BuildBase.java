package fun.masttf.builder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fun.masttf.bean.Constants;

public class BuildBase {
    private static final Logger logger = LoggerFactory.getLogger(BuildBase.class);

    public static void execute() {
        ArrayList<String> headerInfoList = new ArrayList<>();
        // 枚举
        headerInfoList.add("package " + Constants.PACKAGE_ENUMS + ";");
        build(headerInfoList, "DateTimePatternEnum", Constants.PATH_ENUMS);
        build(headerInfoList, "PageSize", Constants.PATH_ENUMS);
        build(headerInfoList, "ResponseCodeEnum", Constants.PATH_ENUMS);

        // 时间工具类
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_UTILS + ";");
        build(headerInfoList, "DateUtils", Constants.PATH_UTILS);

        // 基础Mapper类
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_MAPPER + ";");
        build(headerInfoList, "BaseMapper", Constants.PATH_MAPPER);

        // 基础Query类
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_QUERY + ";");
        build(headerInfoList, "BaseQuery", Constants.PATH_QUERY);
        headerInfoList.add("import " + Constants.PACKAGE_ENUMS + ".PageSize;");
        build(headerInfoList, "SimplePage", Constants.PATH_QUERY);

        // VO
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_VO + ";");
        build(headerInfoList, "PaginationResultVo", Constants.PATH_VO);
        build(headerInfoList, "ResponseVo", Constants.PATH_VO);

        // Exception类
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_EXCEPTION + ";");
        headerInfoList.add("import " + Constants.PACKAGE_ENUMS + ".ResponseCodeEnum;");
        build(headerInfoList, "BusinessException", Constants.PATH_EXCEPTION);

        // 基础Controller类
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_CONTROLLER + ";");
        headerInfoList.add("import " + Constants.PACKAGE_VO + ".ResponseVo;");
        headerInfoList.add("import " + Constants.PACKAGE_ENUMS + ".ResponseCodeEnum;");
        build(headerInfoList, "ABaseController", Constants.PATH_CONTROLLER);
        headerInfoList.add("import " + Constants.PACKAGE_EXCEPTION + ".BusinessException;");
        build(headerInfoList, "AGlobalExceptionHandlerController", Constants.PATH_CONTROLLER);

    }

    private static void build(List<String> headerInfoList, String fileName, String outPutPath) {
        File file = new File(outPutPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        File javaFile = new File(outPutPath, fileName + ".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;

        InputStream in = null;
        InputStreamReader inr = null;
        BufferedReader bf = null;
        try {
            out = new FileOutputStream(javaFile);
            outw = new OutputStreamWriter(out, "UTF-8");
            bw = new BufferedWriter(outw);

            String path = BuildBase.class.getClassLoader().getResource("templates/" + fileName + ".txt").getPath();
            in = new FileInputStream(path);
            inr = new InputStreamReader(in, "UTF-8");
            bf = new BufferedReader(inr);

            for (String headerInfo : headerInfoList) {
                bw.write(headerInfo);
                bw.newLine();
                if (headerInfo.contains("package")) {
                    bw.newLine();
                }
            }

            String line = null;
            while ((line = bf.readLine()) != null) {
                bw.write(line);
                bw.newLine();
            }
            bw.flush();
        } catch (Exception e) {
            logger.error("生成基础类失败 :{}, 失败", fileName, e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (outw != null) {
                    outw.close();
                }
                if (bw != null) {
                    bw.close();
                }
                if (in != null) {
                    in.close();
                }
                if (inr != null) {
                    inr.close();
                }
                if (bf != null) {
                    bf.close();
                }
            } catch (Exception e) {
                logger.error("关闭流失败", e);
            }
        }
    }
}

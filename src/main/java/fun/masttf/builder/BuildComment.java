package fun.masttf.builder;

import java.io.BufferedWriter;
import java.util.Date;

import fun.masttf.bean.Constants;
import fun.masttf.utils.DateUtils;

public class BuildComment {
    public static void createClassComment(BufferedWriter bw, String classComment) throws Exception {
        bw.write("/**");
        bw.newLine();
        bw.write(" * @Description:" + classComment);
        bw.newLine();
        bw.write(" * ");
        bw.newLine();

        bw.write(" * @auther:" + Constants.AUTHER_COMMENT);
        bw.newLine();
        bw.write(" * @date:" + DateUtils.format(new Date(), DateUtils.YYYY_MM_DD));
        bw.newLine();
        bw.write(" */");
        bw.newLine();
    }

    public static void createFieldComment(BufferedWriter bw, String fieldComment) throws Exception {
        bw.write("\t/**");
        bw.newLine();
        bw.write("\t * " + (fieldComment == null ? "" : fieldComment));
        bw.newLine();
        bw.write("\t */");
        bw.newLine();
    }

    public static void createMethodComment() {

    }
}

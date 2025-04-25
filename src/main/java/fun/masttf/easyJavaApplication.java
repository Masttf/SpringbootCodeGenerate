
package fun.masttf;

import java.util.List;

import fun.masttf.bean.TableInfo;
import fun.masttf.builder.BuildPo;
import fun.masttf.builder.BuildTable;

public class easyJavaApplication {
    public static void main(String[] args) {
        List<TableInfo> tableInfoList = BuildTable.getTables();
        for (TableInfo tableInfo : tableInfoList) {
            BuildPo.execute(tableInfo);
        }
    }
}
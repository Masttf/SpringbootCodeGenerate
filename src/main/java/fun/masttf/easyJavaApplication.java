
package fun.masttf;

import java.util.List;

import fun.masttf.bean.TableInfo;
import fun.masttf.builder.BuildBase;
import fun.masttf.builder.BuildMapper;
import fun.masttf.builder.BuildMapperXml;
import fun.masttf.builder.BuildPo;
import fun.masttf.builder.BuildQuery;
import fun.masttf.builder.BuildService;
import fun.masttf.builder.BuildTable;

public class easyJavaApplication {
    public static void main(String[] args) {
        List<TableInfo> tableInfoList = BuildTable.getTables();
        BuildBase.execute();
        for (TableInfo tableInfo : tableInfoList) {
            BuildPo.execute(tableInfo);
            BuildQuery.execute(tableInfo);
            BuildMapper.execute(tableInfo);
            BuildMapperXml.execute(tableInfo);
            BuildService.execute(tableInfo);
        }
    }
}
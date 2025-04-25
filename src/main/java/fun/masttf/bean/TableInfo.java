package fun.masttf.bean;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableInfo {

    /*
     * * 表名
     */
    private String tableName;

    /*
     * * bean名称
     */
    private String beanName;

    /*
     * * 参数名称
     */
    private String beanParamName;
    /*
     * * 表注释
     */
    private String comment;

    /*
     * * 字段信息
     */
    private List<FieldInfo> fieldList;

    /*
     * * 唯一索引集合
     */
    private Map<String, List<FieldInfo>> keyIndexMap = new LinkedHashMap<>();

    /*
     * 是否有date类型
     */
    private boolean haveDate = false;

    /*
     * 是否有时间类型
     */
    private boolean haveDateTime = false;

    /*
     * 是否有 bigdecimal类型
     */
    private boolean haveBigDecimal = false;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanParamName() {
        return beanParamName;
    }

    public void setBeanParamName(String beanParamName) {
        this.beanParamName = beanParamName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<FieldInfo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<FieldInfo> fieldList) {
        this.fieldList = fieldList;
    }

    public Map<String, List<FieldInfo>> getKeyIndexMap() {
        return keyIndexMap;
    }

    public void setKeyIndexMap(Map<String, List<FieldInfo>> keyIndexMap) {
        this.keyIndexMap = keyIndexMap;
    }

    public boolean isHaveDate() {
        return haveDate;
    }

    public void setHaveDate(boolean haveDate) {
        this.haveDate = haveDate;
    }

    public boolean isHaveDateTime() {
        return haveDateTime;
    }

    public void setHaveDateTime(boolean haveDateTime) {
        this.haveDateTime = haveDateTime;
    }

    public boolean isHaveBigDecimal() {
        return haveBigDecimal;
    }

    public void setHaveBigDecimal(boolean haveBigDecimal) {
        this.haveBigDecimal = haveBigDecimal;
    }

}

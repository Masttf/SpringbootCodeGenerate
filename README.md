# SpringbootCodeGenerate

## 描述

SpringbootCodeGenerate 是一个 Java 项目，用于根据数据库表结构自动生成常见的 Springboot 代码，例如 PO、Mapper、Service、Controller 和查询类。它可以帮助开发人员快速搭建项目基础代码，减少重复劳动。

## 功能

- 根据数据库表信息生成以下类型的 Java 类：
    - PO (实体)
    - Query 对象 (用于查询参数封装)
    - Mapper 接口 (MyBatis Mapper)
    - Mapper XML 文件
    - Service 接口
    - Service 实现类
    - Controller 类
- 生成基础的工具类和常量。

## 如何运行

1.  配置 `src/main/resources/application.properties` 文件中的数据库连接信息。
2.  运行 `fun.masttf.easyJavaApplication` 类中的 `main` 方法。
3.  生成的代码将位于 `src/main/resources/application.properties`中的文件输出路径

## 项目结构

```
.
├── pom.xml                   # Maven 项目配置文件
├── README.md                 # 项目说明文件
└── src
    ├── main
    │   ├── java
    │   │   └── fun
    │   │       └── masttf
    │   │           ├── easyJavaApplication.java  # 程序入口
    │   │           ├── bean                      # 实体类 (TableInfo, FieldInfo, Constants)
    │   │           ├── builder                   # 代码生成器核心逻辑
    │   │           └── utils                     # 工具类 (日期, JSON, 属性文件处理)
    │   └── resources
    │       ├── application.properties    # 应用配置文件 (数据库连接等)
    │       └── templates                 # 代码模板文件
    └── test
        └── java
```

## 注意事项

- 请确保在运行前已正确配置数据库连接。
- 代码生成路径和包名可以在`application.properties`配置文件中进行调整。

## 具体说明

### Po

数据库表字段，下划线变为驼峰命名，实现`toString`

### Vo

Controller层返回的数据包装

```java
public class ResponseVo<T> {
    private String status;
    private Integer code;
    private String msg;
    private T data;
}
```

### query

比`Po`多了分页查询，String类型模糊匹配，Data类型范围查询

### mapper

对每个实体类都实现

```java
//insert插入数据
Integer insert(@Param("bean") T t);

//insert update 插入或者更新数据
Integer insertOrUpdate(@Param("bean") T t);

//insertBatch 批量插入数据
Integer insertBatch(@Param("list") List<T> list);

//insertOrUpdateBatch 批量插入或者更新数据 
Integer insertOrUpdateBatch(@Param("list") List<T> list);

//selectList 根据参数查询数据列表

List<T> selectList(@Param("query") P p);

//selectCount 根据参数查询数据条数
Integer selectCount(@Param("query") P p);
```

再根据唯一索引生产，根据唯一索引的查询、修改、删除方法

### mapperXml

- 实现`resultMap`与实体类对应，主键只有一个时，使用`id`否则使用`result`

- 实现通用查询条件，通过`if`标签与`where`判断传入的查询类的属性是不是`null`，字符串类似多判断是不是""

- `insert`判断是否有自增长索引，通过`selectKey`标签来获取新生成的自增id

  > [!NOTE]
  >
  > 只能有一个自增长列

- `insertOrUpdate`使用`ON DUPLICATE KEY UPDATE`在insert冲突时update原来的数据，不修改唯一索引
- `insertBatch` 插入的字段没有自增字段
- `insertOrUpdateBatch`插入全部字段使用`ON DUPLICATE KEY UPDATE`会覆盖原来的全部字段
- 根据唯一索引的查询、修改、删除

### service、serviceImpl

处理一下分页查询，调用mapper

### Controller

实现全局的异常捕获类

查询使用`GetMapping`

新增修改使用`PostMapping`，注意一下根据唯一索引的修改方法，这里使用url参数定位id，然后json传实体

删除使用`DeleteMapping`

> [!NOTE]
>
> 除查询外，其他操作只返回是否操作成功


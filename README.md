# 说明文档

## 工作流

1. 构建数据库记录条目 10M
2. 熟悉 BikCache 组件
3. 基于 BikCache 组件实现用户登录系统
4. 部署主备模式数据库，主从模式 redis
5. 编写模拟 300K 用户随机访问的程序，间隔可控制
6. 启动登录服务，根据不同的访问间隔多次进行验证
   - 验证 1：在系统启动 30 分钟后，关闭主数据库，检查系统服务情况
   - 验证 2：同时关闭主数据库及其可用区上的服务，检查系统服务情况
7. 将验证结果写成报告

## 构建数据库条目

首先创建 my_user_table, 包含字段 8 个字段，id、email、password、valid_data、status、create_time、change_time、text

![](figs/myUserTableField.png)

通过在 mysql 编写函数实现随机构建条目内容

```sql
CREATE DEFINER=`root`@`localhost` FUNCTION `generate_user_data`(n INT) RETURNS int
    DETERMINISTIC
BEGIN
  DECLARE i INT DEFAULT 1;
  
  WHILE i <= n DO
    INSERT INTO my_user_table (email, password, valid_date, status, create_time, change_time, text)
    VALUES (
      CONCAT(
        LEFT(MD5(RAND()), 10),
        '@example.com'
      ),
      MD5(RAND()),
      DATE_ADD(NOW(), INTERVAL FLOOR(RAND() * 3) YEAR),
      FLOOR(RAND() * 3),
      DATE_ADD(NOW(), INTERVAL - FLOOR(RAND() * 365) DAY),
      DATE_ADD(NOW(), INTERVAL - FLOOR(RAND() * 365) DAY),
      REPEAT(MD5(RAND()), 32)
    );
  
    SET i = i + 1;
  END WHILE;
  
  RETURN i;
END
```

产生的条目内容如下所示

![](figs/myUserTableContent.png)

## BikCache 组件

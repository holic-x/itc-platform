# itc-platform




## 🍚项目说明

​	智能百宝箱（后端）：提供API接口调用、智能BI、聚合搜索等工具应用

​	项目前端：[itc-platform](https://github.com/holic-x/itc-platform)

​	项目后端：[itc-platform-frontend](https://github.com/holic-x/itc-platform-frontend)



## ✨技术选型

> 前端：

- React 18
- Ant Design Pro 5.x 脚手架
- Ant Design & Procomponents 组件库
- Umi 4 前端框架
- OpenAPI 前端代码生成
- 图表开发：可视化开发库（echarts-for-react）



> 后端：

- ⚡基础框架：Java [Spring Boot](https://spring.io/projects/spring-boot) 框架（基于`springboot-react-init`通用模板构建）
- 👀鉴权框架：
  - 后端：[Shiro](https://shiro.apache.org/)安全认证框架
  - 前端：基于antd pro的权限认证机制

- 🚀ORM框架：
  - [MyBatis-Plus](http://www.mybatis.org/mybatis-3/zh/index.html) 及 MyBatis X 代码自动生成，分页插件
  - [SpringData JPA](https://spring.p2hp.com/projects/spring-data-jpa.html)：操作访问ES

- ⚡分布式框架：
  - [Dubbo](https://cn.dubbo.apache.org/zh-cn/) 分布式（RPC、Nacos）
  - [Spring Cloud Gateway](https://cloud.spring.io/spring-cloud-gateway/reference/html/) 微服务网关

- 🌈AI工具：
  - [CoZe](https://www.coze.cn/home)（AI Bot）
  - [YuAI](https://www.yucongming.com/)（免费AI接口）

- ✨SDK 开发：
  - [Spring Boot Starter](https://spring.io/projects/spring-boot)（后端SDK构建）
  - [Vite](https://vitejs.cn/vite3-cn/)（前端SDK构建）

- 🍉接口规范：基于OpenAPI构建接口规范
  - 后端：基于[Swagger](https://swagger.io/) + [Knife4j](https://doc.xiaominfo.com/docs/quick-start) 生成接口文档
  - 前端：基于OpenAPI插件生成接口服务（service）

- 🦪辅助工具：借助各种工具库辅助开发
  - [Hutool](https://www.hutool.cn/)：一个功能丰富且易用的Java开发工具库
  - Apache Common Utils：常用工具类
  - Gson：用于Java对象和JSON数据之间的转化工具类
  - ExcelUtils：Excel操作工具类

- 📊数据库：
  - Redis：缓存、排行、服务限流
  - MySQL 数据库：数据存储


- 🎰API 签名认证（Http 调用）
- 🍚消息队列：[RabbitMQ](https://www.rabbitmq.com/)
- 🔔应用部署：
  - [Nginx](https://www.nginx-cn.net/)：正向/反向代理、负载均衡
  - [Docker](https://www.docker.com/)：应用容器引擎



## 🚀项目介绍

### 1.系统模块

> 🔔后台管理：

- 基础信息板块（基于`springboot-react-init`通用模板构建，提供基本信息管理模板）
  - 用户管理
  - 数据管理
  - 模板管理
  - 文章管理
- 内容管理板板块
  - 公告管理
- 聚合搜索板块
  - 抓取文章信息管理
- API接口开放板块
  - 接口信息管理
  - 接口数据统计
- 智能BI板块
  - 图表信息管理
  - 图表数据分析



> 🚀前台：

- 聚合搜索
- API广场
- 智能报表





### 2.项目安装&配置

#### 基础配置

1）数据库初始化，执行 `sql/create_table.sql` 中的数据库语句，自动创建库表



2）创建`application-remote.yml`配置文件（建议创建一个本地开发的配置文件）

​	项目本地默认启用`application-remote.yml`配置，初始化参考配置如下，修改为相应环境配置

```yml
# 公共配置文件
spring:
  # 数据库配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/itc_platform
    username: 用户名
    password: 密码

  # Redis 配置
  redis:
    database: 1
    host: localhost
    port: 6379
    timeout: 5000
    password: 123456 # 没有密码可不填

  # 文件上传
  servlet:
    multipart:
      # 大小限制
      max-file-size: 10MB

  # Elasticsearch 配置(elasticsearch启用需确保es服务正常启动)
  elasticsearch:
    uris: http://localhost:9200
    username: root
    password: 123456

# 鱼聪明 AI 配置（https://yucongming.com/）
yuapi:
  client:
    access-key: 
    secret-key: 
```



3）导入项目、配置Maven环境依赖，启动项目，访问 `http://localhost:8101/api/doc.html` 即可打开接口文档访问

​	项目启动失败则依次查看日志，确认环境依赖配置是否完成





#### ES搜索引擎启用

​	ES相关配置在激活的配置文件中配置，关注ES涉及到的模块（Search），以windows版本配置为例

​	本地启动ES：执行elasticsearch.bat文件，启动成功访问http://localhost:9200，返回json信息确认是否启动成功

​	本地启动Kibana：执行kibana.bat文件，启动成功访问http://localhost:5601/app/dev_tools，Kibana依赖于ES环境

​	ES检索：修改FetchPostEsDTO的配置（放开@Document限制，会开启ES，聚合搜索走ES渠道）

​	ES数据同步：后台管理员在【抓取文章信息板块】提供了全量同步入口（通过按钮触发走全量同步文章信息）



#### Redis配置

​	Redis配置：注意password配置（一般默认无密码则不需要设定，如果是远程Redis数据库加了安全验证则需要确认密码并设定密码信息），配置参考`RedissonConfig.java`





### 🧱项目总结

> ✨扩展说明

​	项目模块部分接口目前还在扩展开发中，后续还会引入更多的模块和功能进行扩展和完善

​	此处时间线仅针对技术点切入、项目框架构建改造相关，具体功能迭代开发日志：详细内容请查看【[🔎项目开发日记](https://github.com/holic-x/itc-platform/blob/main/docs/%E9%A1%B9%E7%9B%AE%E5%BC%80%E5%8F%91%E6%97%A5%E8%AE%B0.md)】







> 📊感谢贡献

​	目前项目还存在很多不足，会不断地更新、完善，希望感兴趣的小伙伴一起学习，一起进步！！

​	对于项目中现存的Bug和改进建议可以在[Issues专区](https://github.com/holic-x/itc-platform/issues)中提出，闲余之际会积极反馈、积极调整，有能力&感兴趣的小伙伴也可以一起加入，多多[Pull requests](https://github.com/isxiaochen/Chen-Search-frontend/pulls)

​	

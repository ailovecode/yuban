# 宇伴 - 伙伴匹配系统

## 需求分析

1. 用户添加标签，（标签分类，怎么分类）学习方向 / 工作方向
   1. 暂不支持用户自定义标签，要留一个接口，先让管理员去添加标签
   2. 用户暂只能通过选择标签来填充；（参考 CSDN 添加兴趣标签）
   3. 标签的初始化就只添加一个性别标签，其他的通过自添加来实现；

2. 主动搜索，搜索其他同学
   1. 通过标签查找同学
   2. 利用 Redis 缓存 + 本地缓存

3. 组队
   1. 创建房间，
   2. 加入队伍
   3. 根据标签查询队伍
   4. 邀请加入队伍
4. 允许用户修改标签
5. 推荐
   1. 相似度计算算法 + 本地分布式计算

## 开发时间

计划1 ~ 2周之内完成

## 技术栈

### 前端

1. Vue3 开发框架（提高开发框架）
2. Vue UI 组件库（基于 Vue 的移动端组件库）
3. Vite（前端打包工具）
4. Nginx 单机部署

### 后端

1. Java编程语言 + springBoot 框架
2. springMVC + Mybatis + Mybatis - Plus
3. MySQL 数据库
4. Redis 数据库
5. Swagger + Knife4j 接口文档

## 项目开发

1. 前端项目初始化
2. 前端主页
   1. 组件选型
3. 数据库表设计
   1. 标签库
   2. 用户表
4. 开发后端 + 根据标签搜索用户
   1. 根据部分标签搜索
5. 开发前端 + 根据标签搜索用户

## TODO 优化点

### 后端优化

- [ ] 用户自定义标签，但是应该有一个时间限制，防止恶意攻击
- [ ] 通过 AI 将用户输入的标签进行分类，
- [ ] 添加 **个人介绍** 列表项
- [ ] 队伍属性添加标签，支持根据标签查找

### 前端优化

#### 个人中心页

- [ ] 头像修改
- [ ] 注册时间
- [ ] 页面布局

## 数据库表设计

### 标签表（分类表）

使用标签更加灵活

性别：男、女

方向：Java、C++

目标：考研、春招、秋招、社招、考公、竞赛

段位：初级、中级、高级、王者

省份：大一、大二、大三、大四、学生、待业、已就业、研一、研二、研三

状态：乐观、有点丧、一般、单身、已婚、有对象

【用户自定义标签】



### 【标签表】

字段：

id **[tagId]** int 主键

标签名 **[tagName]** varchar  (必须唯一， 构建唯一索引)

上传标签的用户 **[userId]** id int （通过userId查询标签，加上普通索引）

父标签 id **[parentId]** int (分类)

是否为父标签 **[isParent]** tinyint （0 - 不是， 1 - 是）

创建时间 **[createTime]**, datatime

更新时间 **[updateTime]**, datatime

是否删除 **[isDelete]**, tinyint(0, 1)

### 问题

> 如何查询所有标签，并且把标签分组

1. 按父标签id 分组，然后填充即可

> 根据父标签查询子标签

​	根据id查询，能实现

### 【用户表更新】

**按需开发！！！**

> 如何为用户补充标签？

1. 通过添加 tags 列表存储用户标签，（使用JSON格式字符串存储）

   1. 存储方便，查询方便，不用新建数据表，减少表格关联
   2. 标签可作为用户的固有属性（除了该系统，其他系统用到这个用户信息，是同样的标签）
   3. 缺点就是查询的效率不高，只能通过模糊查询，**通过缓存优化**

2. 建立一个关联表，存储用户和标签的 id

   1. 查询灵活，可以正查、反查
   2. 需要多维护一个关联表

   企业开发应该减少表之间的关联查询，很影响性能扩展，影响查询效率

## 后端开发

### 根据标签搜索用户

1. 允许用户传入多个标签，多个标签都存在才能搜出来。like ‘%java%’and like ‘%C++%’
2. 允许用户传入多个标签，存在一个标签就能搜出来。like ‘%java%’or like ‘%C++%’

2 种实现方式：

​	sql查询： 实现简单，通过拼接实现sql查询（私有化接口）

​	内存查询： 灵活，可以通过并发进一步优化（目前使用这个）

**不同情况的查询方式选择：**

- 如果参数可以分析，根据用户的参数来选择查询方式，比如标签数
- 如果参数不可分析，并且数据库连接足够、内存空间足够、可以并发同时查询，谁先查到返回谁
- 还可以SQL查询和内存计算结合起来，比如用SQL过滤掉部分tag

### 解析 JSON 字符串

序列化：将Java 对象转为JSON

反序列化：将JSON对象转为 Java

Java json  序列化库有多种：

1. gson （google 出品，值的推荐）
2. fastjson （阿里出品，不推荐）
3. jackson
4. kryo （性能极高的序列化率）

### Java8

parallelStream（平行流）

```java
Optional.ofNullable(tagNameSet).orElse(new HashSet<>());
```

### 后端整合 Swagger + Knife4j 接口文档

什么是接口文档？ 写接口的文档包含哪些信息：

-  请求参数
-  相应参数
   - 错误码
-  接口地址
-  接口名称
-  请求类型
-  请求格式
-  备注

前端后端人员都要用

作用：

- 有个书面内容，便于大家参考和查阅，便于 沉淀和维护，拒绝口口相传
- 便于前端和后端开发对接，前后端联调截止，后端 => 接口文档 <= 前端
- 好的接口文档支持在线调试、在线测试，可以作为工具提高我们的开发测试效率

怎么做？

- 手写 （比如文档，markdown）
- 自动化生成接口文档：自动根据项目代码生成完整的文档或在线调试的网页，Swagger、postman、apifox、apipost、eolink

通过 Knife4j 集成swagger 增强式UI：

1. Spring Boot 2 使用openai 2 的，导入依赖

2. 配置yml 配置文件

   ```yaml
   knife4j:
     enable: true  # 必填，是否启用Knife4j增强功能（默认false）
     #  production: true # 仅在处于生产环境时开启
     openapi:
       title: 宇伴匹配系统  # 必填，文档标题，显示在页面顶部
       description: "宇伴项目在线API文档"  # 可选，文档描述，支持Markdown语法
       email: xiaoyu208h@qq.com  # 可选，联系邮箱
       concat: xiaoyu  # 可选，联系人名称
       version: v4.0  # 必填，API版本号
       license: Apache 2.0  # 可选，开源协议名称
       license-url: https://example.com/license  # 可选，协议链接（需取消注释）
       terms-of-service-url: https://example.com/terms  # 可选，服务条款链接
    group:
     test1:
       group-name: 分组名称  # 必填，显示在文档左侧的分组名称
       api-rule: package  # 必填，分组规则（可选：package/path）
       api-rule-resources: 
         - com.zhy.yuban.controller  # 必填，扫描的控制器包路径
   ```

3. 然后启动访问 /doc.html 即可，不需要写一个配置文件

### 爬取用户信息

​	查找一些完成自我介绍的同学的信息

想要做一个优化，我这里将星球编号优化掉，因为没有什么卵用，我也没有星球；

那么这时候，星球中的自我介绍，我就可以使用了，还有一点就是怎么获取信息？

#### 如何抓取网页信息

1. 分析原网站是怎么获取这些数据的？具有哪些接口？

```bash
curl ^"https://api.zsxq.com/v2/hashtags/48844541281228/topics?count=20^&end_time=2024-07-15T11^%^3A26^%^3A12.836^%^2B0800^" ^
  -X ^"OPTIONS^" ^
  -H ^"accept: */*^" ^
  -H ^"accept-language: zh-CN,zh;q=0.9,en;q=0.8^" ^
  -H ^"access-control-request-headers: x-aduid,x-request-id,x-signature,x-timestamp,x-version^" ^
  -H ^"access-control-request-method: GET^" ^
  -H ^"origin: https://wx.zsxq.com^" ^
  -H ^"priority: u=1, i^" ^
  -H ^"referer: https://wx.zsxq.com/^" ^
  -H ^"sec-fetch-dest: empty^" ^
  -H ^"sec-fetch-mode: cors^" ^
  -H ^"sec-fetch-site: same-site^" ^
  -H ^"user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36^"
```

2. 通过代码调用接口
3. 清晰数据，并且写入数据库
4. 这个是没找好接口，咱们就直接通过 AI 创造数据吧！

判断存在哪些信息需要填充，然后通过AI填充数据

### Session 共享实现

1. 为了能够实现单机登录多机使用，将session共享到一个内存中供所有的分布式系统连接使用

2. 使用 redis 实现Session共享，原理就是将session数据存储到redis中，然后每次系统需要请求登录信息时访问 redis 即可

3. 实现步骤：

   1. 下载 redis

   2. 配置redis依赖

      ```xml
      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-data-redis</artifactId>
          <version>2.6.13</version>
      </dependency>
      ```

   3. 安装 redis 管理工具 QuickRedis 

   4. Spring session与redis整合 ，引入依赖

      ```xml
      <dependency>
          <groupId>org.springframework.session</groupId>
          <artifactId>spring-session-data-redis</artifactId>
          <version>2.6.4</version>
      </dependency>
      ```

   5. 配置redis信息

      ```yaml
      spring:
        redis:
          host: localhost
          port: 6379
          database: 0	# 用于指定redis 数据库 共 0-15 个
      ```

   6. 配置 session 存储位置

      ```yaml
      spring:
        session:
          timeout: 86400
          store-type: redis # 数据存储位置以及读取位置
      ```

### 个人信息页实现

1. 用户修改页前端页面、后端接口进行联调
2. 开发主页（简单的推荐，推荐同自己标签相同的同学）
3. 优化主页性能（缓存 + 定时任务 + 分布式锁）

#### 实现信息修改接口

1. 获取的用户修改信息和前端登录状态 √
2. 校验是否为空 √
3. 如果只有 id 信息，放回请求参数错误 √
4. 在 service 层校验用户信息是否为管理员或者是本人 √
5. 通过校验类 封装成一个服务，供所有接口使用 √

### 实现推荐信息接口

#### 基础

1. 全量查询，给前端返回 List 集合（少量数据） √
2. 进行批量插入数据信息 √
3. 实现多线程插入 √
4. 设置多线程数量分配 √
5. 实现分页查询，返回一页数据 √

#### 优化

1. 根据用户ID的不同来查找不同的数据，同时使用redis缓存机制增强查询速度，并设置缓存的存活时间 √
   1. 问题一：不同用户查询的数据依旧是一样的；
   2. 问题二：第一次访问的时候查询依旧很慢；



#### 缓存预热

优点：

1. 解决用户第一次访问慢的问题

缺点：

1. 增加开发成本
2. 预热的时机和时间不对，导致缓存的数据不对
3. 需要占用额外的空间

如何做？

1. 定时任务
   1. Spring Scheduler (springBoot 默认整合)
      1. 使用 @Scheduled(cron = "0 59 23 * * ?")和@EnableScheduling 配合上使用
      2. 同时设定执行时间
   2. quartz （独立的框架）
   3. xxl-job (小项目值的学习)
2. 模拟触发

#### 控制定时任务的执行

当服务部署到多台服务器上的时候，每台服务器上都会有定时任务，但是需要控制只有一台服务器去执行这个任务。

为什么？

1. 浪费资源，可以想象 10000 台服务器同时 ”打鸣“ 的情景；
2. 产生脏数据，比如重复插入

怎么做？

1. 分离定时任务和主程序，只有1个服务器运行定时任务。成本太大，部署成本，开发成本

2. 写死配置，在定时任务中设定校验配置，只允许 符合配置要求的 IP 地址才可以执行业务逻辑，其他直接返回，成本较低；但是问题就是这样写的配置就是写死了，不能动态处理

3. 根据上一个方法的启发，能不能有一个动态配置？答：有。动态配置能够方便、轻松地更新代码（而无需重启）；但同样还是只有符合要求的 IP 才可以进行业务处理。

   - 通过数据库
   - Redis
   - 配置中心（Nacos、Apollo、Spring Cloud Config）

   这样做的问题就是，IP 限制太局限了，而且有时候 IP 不可控就会出现麻烦，那还会有其他的解决方法。

### 组队功能

#### 需求分析

1. 每个人都可以发起组队，成为队长；（创建）
2. 组内成员邀请人员进入队伍 或者 只能队长邀请；（增加）
3. 队长有权控制队员的进出；（改）
4. 队长解散队伍；（删）
5. 任何队员自由退出队伍，如果是队长，则需要判断队伍是否还有人，若有，则将队长头衔交给第二个进入的人；（改）
   1. 队长头衔可以转让；
6. 队伍入队是否加密 预留，根据队长的需求去定；
7. 队员入队需要队长审批；（TODO）
8. 用户通过搜索队伍名称查找（p0)
9. 修改队伍信息；

#### 队伍实体类定义

- 队伍名称（标题）
- 队伍 id
- 队长 id
- 描述
- 队伍总坑位
- 剩余坑位
- 密码
- 队员 id 列表
  - 可通过将所有队员 id 一并存入到一个属性值中，使用`,`隔开
    - 这种方式查询比较方便，但删除需要麻烦一些
  - 或者通过联表存储，对应一个 team-user 表来存储**（选择这一种）**
    - 涉及到多对多的联表查询，可能性能方面会有差错
- 创建时间
- 修改时间
- 是否删除

**队伍表：**

|    字段名    |  字段类型   |           字段描述            |
| :----------: | :---------: | :---------------------------: |
|    teamId    |   bigint    |            队伍id             |
|   teamName   | varchar(50) |           队伍名称            |
|  captainId   |   bigint    |            队长id             |
| description  |    text     |           队伍描述            |
|  maxNumber   |     int     |         队伍最大人数          |
| remainNumber |     int     |         队伍剩余坑位          |
| teamPassword | varchar(20) |           入队密码            |
|  createTime  |  datetime   |           创建时间            |
|  updateTime  |  datetime   |           修改时间            |
|   isDelete   |   tinyint   | 是否删除（0 - 删除  1- 不删） |

**建表 SQL：**

```sql
create table team
(
    teamId           bigint auto_increment comment '主键 队伍id'
        primary key,
    teamName     varchar(256)                       not null comment '队伍名称',
    captainId    bigint								not null comment '队长id',
    description    varchar(1024)                      null comment '队伍描述',
    maxNumber       int default 1                   null comment '队伍最大人数',
    remainNumber       int                   null comment '队伍剩余坑位',
    teamPassword     varchar(256)                       null comment '入队密码',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间（数据插入时间）',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除（逻辑删除 0 1）',
)
    comment '队伍表';
```



**队伍 - 用户表：**

|   字段名   | 字段类型 |           字段描述            |
| :--------: | :------: | :---------------------------: |
|     id     |          |             主键              |
|   teamId   |  bigint  |            队伍id             |
|   userId   |  bigint  |            队员id             |
|  joinTime  | datetime |           加入时间            |
| createTime | datetime |           创建时间            |
| updateTime | datetime |           修改时间            |
|  isDelete  | tinyint  | 是否删除（0 - 删除  1- 不删） |

**建表SQL：**

```sql
create table user_team
(
    id           bigint auto_increment comment '主键'
        primary key,
    teamId     bigint                   not null comment '队伍id',
    userId    bigint								not null comment '队员id',
    joinTime   datetime default CURRENT_TIMESTAMP not null comment '加入时间（数据插入时间）',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间（数据插入时间）',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除（逻辑删除 0 1）',
)
    comment '用户-队伍表';
```

#### 增删改查



#### 业务逻辑



## 前端开发

### 前端路由引入

1. 下载 vue router

2. 创建路由，这里不能通过实例化创建，在 Vue3 + vue-router@4 必须通过 createRouter 创建实例

   ```typescript
   const router = VueRouter.createRouter({
       history: VueRouter.createWebHashHistory(),
       routes
   })
   ```

3. app.use(router)

4. 挂载 app

   ```typescript
   app.mount('#app');
   ```

5. 规定路由渲染地点 `<router-view />`

6. 通过 `<router-link>` 添加一个 to 标签会默认渲染成一个 `<a>`标签

7. 将标签栏改为路由模式，有了 `<router-view />` 直接添加 to 属性即可

### 搜索页面开发

1. 构想页面基本布局，可参考同类产品
2. 搜索框
   1. 带一个取消的按钮，可以清楚搜索框中的东西
3. 已选标签
   1. 通过一个可删标签来显示在页面中
   2. 这个标签选择是从下面的标签列表中选中获取的，而不是输入框中所查找的
4. 标签列表（采用树形标签分类，并且是多选）
   1. 当搜索框中没有输入时， 显示全部
   2. 根据搜索框的输入显示相关的数据
      1. 构建原始数据，保证每次取消搜索能够还原列表
      2. 新建显示数据列表，根据搜索来定

### 用户信息页面开发

1. 初步通过表单向来将数据展示出来
2. 通过点击箭头来跳转单条信息修改页面
3. 通过 URL 传递参数
   1. 在编辑页接受信息的时候，通过 useRoute 来接受路由，可通过 query 方法获取后面的信息

### 搜索结果用户列表展示

1. 基于搜索页面开发，设定一个搜索按钮跳转到结果展示页
2. 通过列表的形式展示用户卡片
3. 用户卡片展示 头像、姓名、个人介绍、标签、联系按钮

### 个人信息页开发

#### 用户登录功能

1. 实现一个登录页面 √
2. 直接请求后端登录接口即可 √

#### 个人信息页展现登录用户信息

1. 先获取当前用户信息 √（抽象出来一个方法，当系统使用量比较多的时候，可以将用户信息缓存一下）
2. 对接展示用户信息 √
3. 优化点：
   1. 页面大小的调整，滚轮的调整 √

#### 个人信息页的信息修改功能

1. 获取当前信息  √
2. 向后端传送 POST 请求 √

### 主页推荐信息开发

#### 基础

1. 通过后端 全量查询 返回List 集合进行展示 √
2. 优化 List 集合的展示（主页 和 搜索页），将展示组件抽象出来一个组件 √
3. 通过分页展示数据信息 √

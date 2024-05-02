/*
Navicat MySQL Data Transfer

Source Server         : txCloud-itc_platform
Source Server Version : 50744
Source Host           : 43.138.185.20:3306
Source Database       : itc_platform

Target Server Type    : MYSQL
Target Server Version : 50744
File Encoding         : 65001

Date: 2024-05-02 08:24:12
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for chart
-- ----------------------------
DROP TABLE IF EXISTS `chart`;
CREATE TABLE `chart` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `goal` text COLLATE utf8mb4_unicode_ci COMMENT '分析目标',
  `name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图表名称',
  `chartData` text COLLATE utf8mb4_unicode_ci COMMENT '图表数据',
  `chartType` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图表类型',
  `genChart` text COLLATE utf8mb4_unicode_ci COMMENT '生成的图表数据',
  `genResult` text COLLATE utf8mb4_unicode_ci COMMENT '生成的分析结论',
  `status` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'wait' COMMENT 'wait,running,succeed,failed',
  `execMessage` text COLLATE utf8mb4_unicode_ci COMMENT '执行信息',
  `userId` bigint(20) DEFAULT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图表信息表';

-- ----------------------------
-- Table structure for chart_1001
-- ----------------------------
DROP TABLE IF EXISTS `chart_1001`;
CREATE TABLE `chart_1001` (
  `日期` varchar(30) DEFAULT NULL,
  `用户数` decimal(10,0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for data_info
-- ----------------------------
DROP TABLE IF EXISTS `data_info`;
CREATE TABLE `data_info` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `dataName` varchar(512) CHARACTER SET utf8 DEFAULT NULL COMMENT '数据名称',
  `dataType` varchar(512) CHARACTER SET utf8 DEFAULT NULL COMMENT '数据类型',
  `dataContent` text CHARACTER SET utf8 COMMENT '数据内容',
  `creater` bigint(20) DEFAULT NULL COMMENT '创建者',
  `updater` bigint(20) DEFAULT NULL COMMENT '修改者',
  `status` tinyint(4) DEFAULT NULL COMMENT '数据状态(0-暂存；1-发布）',
  `isDelete` tinyint(4) DEFAULT '0' COMMENT '删除状态（0-未删除；1-已删除）',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for fetch_post
-- ----------------------------
DROP TABLE IF EXISTS `fetch_post`;
CREATE TABLE `fetch_post` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `title` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '内容',
  `category` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文章分类',
  `tags` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签列表（json 数组）',
  `userId` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建用户 id',
  `userInfo` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户(JSON）',
  `userName` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者名称',
  `viewNum` int(11) DEFAULT NULL COMMENT '阅读数',
  `thumbNum` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `favourNum` int(11) NOT NULL DEFAULT '0' COMMENT '收藏数',
  `commentNum` int(11) DEFAULT NULL COMMENT '评论数',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `status` tinyint(4) DEFAULT NULL COMMENT '文章状态（是否同步）',
  PRIMARY KEY (`id`),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子';

-- ----------------------------
-- Table structure for interface_info
-- ----------------------------
DROP TABLE IF EXISTS `interface_info`;
CREATE TABLE `interface_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(256) NOT NULL COMMENT '名称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '接口头像（图标）',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `url` varchar(512) NOT NULL COMMENT '接口地址',
  `requestParams` text NOT NULL COMMENT '请求参数',
  `requestHeader` text COMMENT '请求头',
  `responseHeader` text COMMENT '响应头',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '接口状态（0-关闭，1-开启）',
  `method` varchar(256) NOT NULL COMMENT '请求类型',
  `userId` bigint(20) NOT NULL COMMENT '创建人',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删, 1-已删)',
  `requestSample` varchar(255) DEFAULT NULL,
  `responseParams` text NOT NULL COMMENT '响应参数',
  `sourceType` varchar(255) DEFAULT NULL COMMENT '接口来源类型：1.外部接口 2.自研接口',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='接口信息';

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `title` varchar(255) NOT NULL COMMENT '公告标题',
  `content` varchar(2048) NOT NULL COMMENT '公告内容',
  `startTime` datetime DEFAULT NULL COMMENT '开始时间',
  `endTime` datetime DEFAULT NULL COMMENT '结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0: 关闭，1: 启用',
  `domain` varchar(255) DEFAULT NULL COMMENT '域名',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `creater` bigint(20) DEFAULT NULL COMMENT '创建者',
  `updater` bigint(20) DEFAULT NULL COMMENT '修改者',
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `title` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '内容',
  `tags` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签列表（json 数组）',
  `thumbNum` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `favourNum` int(11) NOT NULL DEFAULT '0' COMMENT '收藏数',
  `userId` bigint(20) NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `status` tinyint(4) DEFAULT NULL COMMENT '文章状态（0-暂存；1-发布）',
  PRIMARY KEY (`id`),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子';

-- ----------------------------
-- Table structure for post_favour
-- ----------------------------
DROP TABLE IF EXISTS `post_favour`;
CREATE TABLE `post_favour` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `postId` bigint(20) NOT NULL COMMENT '帖子 id',
  `userId` bigint(20) NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_postId` (`postId`),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='帖子收藏';

-- ----------------------------
-- Table structure for post_thumb
-- ----------------------------
DROP TABLE IF EXISTS `post_thumb`;
CREATE TABLE `post_thumb` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `postId` bigint(20) NOT NULL COMMENT '帖子 id',
  `userId` bigint(20) NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_postId` (`postId`),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='帖子点赞';

-- ----------------------------
-- Table structure for template
-- ----------------------------
DROP TABLE IF EXISTS `template`;
CREATE TABLE `template` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `templateName` varchar(512) DEFAULT NULL COMMENT '模板名称',
  `templateContent` text COMMENT '模板内容',
  `creater` bigint(20) DEFAULT NULL COMMENT '创建者',
  `updater` bigint(20) DEFAULT NULL COMMENT '修改者',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '修改时间',
  `isDelete` tinyint(4) DEFAULT '0' COMMENT '是否删除（0-未删除；1-已删除）',
  `status` tinyint(4) DEFAULT NULL COMMENT '模板状态（0-禁用；1-激活）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userAccount` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
  `userPassword` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `unionId` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信开放平台id',
  `mpOpenId` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号openId',
  `userName` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户昵称',
  `userAvatar` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户头像',
  `userProfile` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户简介',
  `userRole` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin/ban',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `userStatus` tinyint(4) DEFAULT NULL COMMENT '用户状态（0-禁用；1-激活）',
  `userDescr` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个人介绍',
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址信息',
  PRIMARY KEY (`id`),
  KEY `idx_unionId` (`unionId`)
) ENGINE=InnoDB AUTO_INCREMENT=1785615230961836035 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户';

-- ----------------------------
-- Table structure for user_extend
-- ----------------------------
DROP TABLE IF EXISTS `user_extend`;
CREATE TABLE `user_extend` (
  `id` bigint(11) NOT NULL COMMENT '主键id',
  `userId` bigint(11) DEFAULT NULL COMMENT '关联用户id',
  `accessKey` varchar(50) DEFAULT NULL COMMENT '开发者accessKey',
  `secretKey` varchar(50) DEFAULT NULL COMMENT '开发者secretKey',
  `isDevelop` tinyint(4) DEFAULT NULL COMMENT '是否开启开发者模式（申请ak/sk）',
  `score` int(10) DEFAULT NULL COMMENT '用户网站积分',
  `grade` varchar(40) DEFAULT NULL COMMENT '用户VIP等级（USER、VIP、SVIP等）',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '修改时间',
  `isDelete` varchar(255) DEFAULT '0' COMMENT '是否删除（0-否；1-是）',
  `registerChannel` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_interface_info
-- ----------------------------
DROP TABLE IF EXISTS `user_interface_info`;
CREATE TABLE `user_interface_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userId` bigint(20) NOT NULL COMMENT '调用用户 id',
  `interfaceInfoId` bigint(20) NOT NULL COMMENT '接口 id',
  `totalNum` int(11) NOT NULL DEFAULT '0' COMMENT '总调用次数',
  `leftNum` int(11) NOT NULL DEFAULT '0' COMMENT '剩余调用次数',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '0-正常，1-禁用',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删, 1-已删)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COMMENT='用户调用接口关系';

-- ----------------------------
-- Table structure for user_interface_info_call
-- ----------------------------
DROP TABLE IF EXISTS `user_interface_info_call`;
CREATE TABLE `user_interface_info_call` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userId` bigint(20) NOT NULL COMMENT '调用用户 id',
  `interfaceInfoId` bigint(20) NOT NULL COMMENT '调用接口 id',
  `interfaceInfoName` varchar(40) DEFAULT NULL COMMENT '调用接口名称',
  `status` int(11) DEFAULT '0' COMMENT '接口调用状态（0-正常，1-禁用）',
  `errMessage` varchar(512) DEFAULT NULL COMMENT '接口响应异常信息',
  `callTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '接口调用时间',
  `duration` varchar(400) DEFAULT NULL COMMENT '接口调用耗时',
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删, 1-已删)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COMMENT='用户调用接口关系';

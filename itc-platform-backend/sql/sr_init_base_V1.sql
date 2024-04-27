/*
Navicat MySQL Data Transfer

Source Server         : 127.0.0.1
Source Server Version : 50738
Source Host           : 127.0.0.1:3306
Source Database       : dada_db_base

Target Server Type    : MYSQL
Target Server Version : 50738
File Encoding         : 65001

Date: 2024-04-21 22:12:07
*/

SET FOREIGN_KEY_CHECKS=0;

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
-- Records of post
-- ----------------------------
INSERT INTO `post` VALUES ('1781721334301069315', '11rwr', '11rwerw', '[\"java\",\"php\",\"ant design pro\"]', '5', '0', '1781721334301069314', '2024-04-21 17:36:14', '2024-04-21 19:05:36', '0', '0');
INSERT INTO `post` VALUES ('1781721585078505473', '测试文章', '哈哈哈', '[\"php\",\"python\"]', '10', '5', '1781721334301069314', '2024-04-21 00:28:23', '2024-04-21 19:05:37', '0', '0');
INSERT INTO `post` VALUES ('1781997532633649154', '归属感', '改动', '[\"python\",\"php\"]', '0', '0', '1781721334301069314', '2024-04-21 18:44:54', '2024-04-21 18:53:33', '0', '1');
INSERT INTO `post` VALUES ('1782039528744177666', '31', '31', '[\"ant design pro\"]', '0', '0', '1781721334301069314', '2024-04-21 21:31:47', '2024-04-21 21:31:47', '0', '0');
INSERT INTO `post` VALUES ('1782042530494410753', '3123', '31313', '[\"php\",\"python\"]', '0', '0', '1781721334301069314', '2024-04-21 21:43:42', '2024-04-21 21:43:42', '0', '0');

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
-- Records of post_favour
-- ----------------------------

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
-- Records of post_thumb
-- ----------------------------

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
-- Records of template
-- ----------------------------
INSERT INTO `template` VALUES ('1782032354097868801', '123', '31312', '1781721334301069314', '1781721334301069314', '2024-04-21 21:03:16', '2024-04-21 21:35:52', '1', '0');
INSERT INTO `template` VALUES ('1782033055599407105', '123111', '31312', '1781721334301069314', '1781721334301069314', '2024-04-21 21:06:04', '2024-04-21 21:16:59', '1', '0');
INSERT INTO `template` VALUES ('1782034720394072066', '31', '313', '1781721334301069314', '1781721334301069314', '2024-04-21 21:12:41', '2024-04-21 21:35:47', '1', '0');
INSERT INTO `template` VALUES ('1782037825206636545', '4343', '4345345', '1781721334301069314', '1781721334301069314', '2024-04-21 21:25:01', '2024-04-21 21:25:01', '1', '0');
INSERT INTO `template` VALUES ('1782038095370145794', '4343', '4345345', '1781721334301069314', '1781721334301069314', '2024-04-21 21:25:48', '2024-04-21 21:25:48', '1', '0');
INSERT INTO `template` VALUES ('1782038796716494849', '4343', '4345345', '1781721334301069314', '1781721334301069314', '2024-04-21 21:27:45', '2024-04-21 21:27:45', '1', '0');
INSERT INTO `template` VALUES ('1782040168446840834', '一日游', '荣耀日', '1781721334301069314', '1781721334301069314', '2024-04-21 21:34:20', '2024-04-21 21:55:38', '0', '1');
INSERT INTO `template` VALUES ('1782041062643073025', '一日游', '荣耀日', '1781721334301069314', '1781721334301069314', '2024-04-21 21:37:53', '2024-04-21 21:55:38', '0', '1');
INSERT INTO `template` VALUES ('1782041536469401601', '一日游', '荣耀日', '1781721334301069314', '1781721334301069314', '2024-04-21 21:39:46', '2024-04-21 21:39:46', '0', '0');
INSERT INTO `template` VALUES ('1782042367944159234', '313', '31', '1781721334301069314', '1781721334301069314', '2024-04-21 21:43:04', '2024-04-21 21:43:04', '0', '0');
INSERT INTO `template` VALUES ('1782043115947945986', '6465', '64646', '1781721334301069314', '1781721334301069314', '2024-04-21 21:46:02', '2024-04-21 21:46:02', '0', '0');
INSERT INTO `template` VALUES ('1782045304644825089', '5+5+', '+', '1781721334301069314', '1781721334301069314', '2024-04-21 21:54:44', '2024-04-21 21:54:44', '0', '0');

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
  PRIMARY KEY (`id`),
  KEY `idx_unionId` (`unionId`)
) ENGINE=InnoDB AUTO_INCREMENT=1781913596406566915 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户';

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1781721334301069314', 'noob', '9a8fe57f7440d711ca7c45d933df4730', null, null, 'noob', 'http://cos.holic-x.com/profile/avatar/avatar02.png', null, 'admin', '2024-04-21 00:27:23', '2024-04-21 16:37:59', '0', '1');
INSERT INTO `user` VALUES ('1781912085693763585', '小红', '9a8fe57f7440d711ca7c45d933df4730', null, null, '312', 'https://img2.baidu.com/it/u=3442676033,4275801877&fm=253&fmt=auto&app=138&f=JPEG?w=522&h=386', null, 'admin', '2024-04-21 13:05:22', '2024-04-21 16:14:39', '0', '1');
INSERT INTO `user` VALUES ('1781913596406566914', '小白', '9a8fe57f7440d711ca7c45d933df4730', null, null, '312', 'https://img2.baidu.com/it/u=3442676033,4275801877&fm=253&fmt=auto&app=138&f=JPEG?w=522&h=386', null, 'admin', '2024-04-21 13:11:22', '2024-04-21 16:14:38', '0', '1');



-- init 脚本
DROP DATABASE IF EXISTS db_project;
CREATE DATABASE db_project CHARACTER SET utf8 COLLATE utf8_general_ci;

--  项目表
DROP TABLE IF EXISTS `project`;
CREATE TABLE IF NOT EXISTS `project` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `project_name` varchar(25) COMMENT '项目名称',
  `project_type` tinyint(2) NOT NULL COMMENT '项目类型',
  PRIMARY KEY (`id`)
) COMMENT='项目名称'  ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_general_ci;












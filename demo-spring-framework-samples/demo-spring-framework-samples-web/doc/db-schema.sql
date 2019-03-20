CREATE DATABASE IF NOT EXISTS demo;
use demo;
DROP TABLE IF EXISTS user;
CREATE TABLE  IF NOT EXISTS user(
	id int(11) not null AUTO_INCREMENT,
    name varchar(128) not null default '' COMMENT '用户名',
    gender int(11) not null default 0 COMMENT '性别',         
    age int(4) not null default 0 COMMENT '年龄', 
    birthday date not null COMMENT '生日', 
    degree int(4) not null default 0 COMMENT '学历', 
    cellphone varchar(128) not null default '' COMMENT '电话', 
    email  varchar(128) not null default '' COMMENT '邮件', 
    hobby int(4) not null default 0 COMMENT '爱好', 
    intro varchar(256) not null default '' COMMENT '简介', 
    lastUpdTime timestamp not null COMMENT '更新时间', 
    primary key(id))ENGINE=InnoDB DEFAULT CHARSET=utf8;

    -- MyISAM不支持事务
    
insert into user(name,age,birthday) values('ruanwei',28,'1983-07-06');

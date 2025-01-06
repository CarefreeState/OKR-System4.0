
-- 创建数据库
drop database if exists `db_okr_system`;
create database db_okr_system character set utf8mb4 collate utf8mb4_bin;
use `db_okr_system`;

drop table if exists `digital_resource`;
create table `digital_resource`
(
    `id` bigint primary key auto_increment comment '资源 id',
    `code` char(32) unique not null comment '资源码',
    `type` varchar(32) not null default 'undefined' comment '资源类型',
    `original_name` varchar(100) not null comment '上传时的文件名',
    `file_name` varchar(256) not null comment '在对象存储服务中存储的对象名',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- index
    unique index `uni_code`(`code` asc) using btree
) comment '资源表';


-- 创建用户表
drop table if exists `user`;
create table `user` (
    `id` bigint primary key auto_increment comment 'ID',
    `openid` varchar(32) not null default '' comment 'OpenID',
    `unionid` varchar(32) not null default '' comment 'UnionID',
    `nickname` varchar(32) not null default '' comment '昵称',
    `photo` varchar(500) not null default '' comment '用户头像',
    `email` varchar(64) not null default '' comment '邮箱',
    `phone` char(11) not null default '' comment '手机号',
    `user_type` int not null default 1 comment '用户类型（默认为 1 普通用户）',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
    unique index `uni_id`(`id` asc) using btree,
    index `idx_openid`(`openid` asc) using btree,
    index `idx_unionid`(`unionid` asc) using btree
) auto_increment 10000 comment '用户表';


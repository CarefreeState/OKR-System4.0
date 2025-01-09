
-- 创建数据库
drop database if exists `db_okr_system`;
create database db_okr_system character set utf8mb4 collate utf8mb4_bin;
use `db_okr_system`;

SET @@FOREIGN_KEY_CHECKS = 0;

drop table if exists `digital_resource`;
create table `digital_resource` (
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
    index `idx_openid`(`openid` asc) using btree,
    index `idx_unionid`(`unionid` asc) using btree
) auto_increment 10000 comment '用户表';

-- 创建 OKR 内核表
drop table if exists `okr_core`;
create table `okr_core` (
    `id` bigint primary key auto_increment comment 'ID',
    `celebrate_day` tinyint null default null comment '庆祝日（星期几）',
    `second_quadrant_cycle` int null default null comment '第二象限周期',
    `third_quadrant_cycle` int null default null comment '第三象限周期',
    `is_over` bit not null default b'0' comment '是否结束',
    `summary` text null default null comment '总结',
    `degree` int null default null comment '完成度',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间'
) comment 'OKR 内核表';

-- 创建个人 OKR 表
drop table if exists `personal_okr`;
create table `personal_okr` (
    `id` bigint primary key auto_increment comment 'ID',
    `core_id` bigint unique not null comment 'OKR 内核 ID',
    `user_id` bigint not null comment '用户 ID',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
    index `idx_core_id`(`core_id` asc) using btree,
    index `idx_user_id`(`user_id` asc) using btree
) comment '个人 OKR 表';

-- 创建团队 OKR 表
drop table if exists `team_okr`;
create table `team_okr` (
    `id` bigint primary key auto_increment comment 'ID',
    `core_id` bigint unique not null comment 'OKR 内核 ID',
    `parent_team_id` bigint comment '从属 OKR ID',
    `manager_id` bigint not null comment '管理这个 OKR 的用户 ID',
    `team_name` varchar(32) not null comment '团队名',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
    index `idx_core_id`(`core_id` asc) using btree ,
    index `idx_manager_id`(`manager_id` asc) using btree
) comment '团队 OKR 表';

delimiter //

create trigger before_insert_team_okr
    before insert on team_okr
    for each row
begin
    declare next_id bigint;
    set next_id = (select auto_increment from information_schema.tables where table_schema=database() and table_name='team_okr');
    set new.team_name = concat('团队 #', next_id);
end//

delimiter ;


-- 创建团队个人 OKR 表
drop table if exists `team_personal_okr`;
create table `team_personal_okr` (
     `id` bigint primary key auto_increment comment 'ID',
     `core_id` bigint unique not null comment 'OKR 内核 ID',
     `team_id` bigint not null comment '团队 OKR ID',
     `user_id` bigint not null comment '用户 ID',
    -- common column
     `version` int not null default 0 comment '乐观锁',
     `is_deleted` bit not null default b'0' comment '伪删除标记',
     `create_time` datetime not null default current_timestamp comment '创建时间',
     `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
     index `idx_core_id`(`core_id` asc) using btree,
     index `idx_team_id`(`team_id` asc) using btree,
     index `idx_user_id`(`user_id` asc) using btree
) comment '创建团队个人 OKR 表';

-- 创建第一象限表
drop table if exists `first_quadrant`;
create table `first_quadrant` (
    `id` bigint primary key auto_increment comment 'ID',
    `core_id` bigint unique not null comment 'OKR 内核 ID',
    `objective` varchar(128) not null default '' comment '目标',
    `deadline` datetime null default null comment '截止时间',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
    index `idx_core_id`(`core_id` asc) using btree
) comment '第一象限表';

-- 创建关键结果表
drop table if exists `key_result`;
create table `key_result` (
    `id` bigint primary key auto_increment comment 'ID',
    `first_quadrant_id` bigint not null comment '第一象限 ID',
    `content` varchar(128) not null default '' comment '关键结果内容',
    `probability` tinyint not null default 0 comment '完成概率（百分比）',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
    index `idx_first_quadrant_id`(`first_quadrant_id` asc) using btree
) comment '关键结果表';

-- 创建第二象限表
drop table if exists `second_quadrant`;
create table `second_quadrant` (
    `id` bigint primary key auto_increment comment 'ID',
    `core_id` bigint unique not null comment 'OKR 内核 ID',
    `deadline` datetime null default null comment '截止时间',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
    index `idx_core_id`(`core_id` asc) using btree
) comment '第二象限表';

-- 创建 Priority1 表
drop table if exists `priority_number_one`;
create table `priority_number_one` (
    `id` bigint primary key auto_increment comment 'ID',
    `second_quadrant_id` bigint not null comment '第二象限 ID',
    `content` varchar(128) not null default '' comment '计划内容',
    `is_completed` bit not null default b'0' comment '是否完成',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
    index `idx_second_quadrant_id`(`second_quadrant_id` asc) using btree
) comment 'Priority1 表';

-- 创建 Priority2 表
drop table if exists `priority_number_two`;
create table `priority_number_two` (
    `id` bigint primary key auto_increment comment 'ID',
    `second_quadrant_id` bigint not null comment '第二象限 ID',
    `content` varchar(128) not null default '' comment '计划内容',
    `is_completed` bit not null default b'0' comment '是否完成',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
    index `idx_second_quadrant_id`(`second_quadrant_id` asc) using btree
) comment 'Priority2 表';

-- 创建第三象限表
drop table if exists `third_quadrant`;
create table `third_quadrant` (
    `id` bigint primary key auto_increment comment 'ID',
    `core_id` bigint unique not null comment 'OKR 内核 ID',
    `deadline` datetime null default null comment '截止时间',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
    index `idx_core_id`(`core_id` asc) using btree
) comment '第三象限表';

-- 创建行动表
drop table if exists `action`;
create table `action` (
    `id` bigint primary key auto_increment comment 'ID',
    `third_quadrant_id` bigint not null comment '第三象限 ID',
    `content` varchar(128) not null default '' comment '行动内容',
    `is_completed` bit not null default b'0' comment '是否完成',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
    index `idx_third_quadrant_id`(`third_quadrant_id` asc) using btree
) comment '行动表';

-- 创建第四象限表
drop table if exists `fourth_quadrant`;
create table `fourth_quadrant` (
    `id` bigint primary key auto_increment comment 'ID',
    `core_id` bigint unique not null comment 'OKR 内核 ID',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
    index `idx_core_id`(`core_id` asc) using btree
) comment '第四象限表';

-- 创建指标表
drop table if exists `status_flag`;
create table `status_flag` (
    `id` bigint primary key auto_increment comment 'ID',
    `fourth_quadrant_id` bigint not null comment '第四象限 ID',
    `label` varchar(128) not null default '' comment '指标内容',
    `color` char(7) not null default '#00ff00' comment '指标颜色',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
    index `idx_fourth_quadrant_id`(`fourth_quadrant_id` asc) using btree
) comment '指标表';

-- 创建勋章表
drop table if exists `medal`;
create table `medal` (
    `id` bigint primary key auto_increment comment 'ID',
    `name` varchar(16) not null comment '称号',
    `description` varchar(64) not null comment '描述',
    `url` varchar(128) not null comment '勋章',
    `grey_url` varchar(128) not null comment '灰色勋章',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
    unique index `uni_id`(`id` asc) using btree
) comment '勋章表';

delete from medal where 1 = '1';
insert into medal (`id`, `name`, `description`, `url`, `grey_url`) values
       (1, '初心启航', '第一次成功制定OKR', 'media/medal/medal1.png', 'media/medal/grey_medal1.png'),
       (2, '硕果累累', '目标持续坚持完成', 'media/medal/medal2.png', 'media/medal/grey_medal2.png'),
       (3, '出类拔萃', '目标提早完成或超额完成', 'media/medal/medal3.png', 'media/medal/grey_medal3.png'),
       (4, '胜券在握', '信心指数拉满', 'media/medal/medal4.png', 'media/medal/grey_medal4.png'),
       (5, '短期达标', '短期计划推进卓有成效', 'media/medal/medal5.png', 'media/medal/grey_medal5.png'),
       (6, '长久有成', '中长期计划推进卓有成效', 'media/medal/medal6.png', 'media/medal/grey_medal6.png'),
       (7, '渐入佳境', '本周状态指标良好', 'media/medal/medal7.png', 'media/medal/grey_medal7.png')
;

-- 创建用户勋章关联表
drop table if exists `user_medal`;
create table `user_medal` (
    `id` bigint primary key auto_increment comment 'ID',
    `user_id` bigint not null comment '用户 ID',
    `medal_id` bigint not null comment '勋章 ID',
    `credit` bigint not null default 0 comment '积分',
    `level` int not null default 0 comment '等级',
    `is_read` bit not null default b'0' comment '是否查看',
    `issue_time` datetime default null comment '颁布时间',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
    index `idx_um`(`user_id` asc, `medal_id` asc) using btree
) comment '用户勋章关联表';


-- 创建 OKR 内核记录器表
drop table if exists `core_recorder`;
create table `core_recorder` (
    `id` bigint primary key auto_increment comment 'ID',
    `core_id` bigint unique not null comment 'OKR 内核 ID',
    `record_map` json not null comment 'OKR 记录对应表',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
    index `idx_core_id`(`core_id` asc) using btree
) comment 'OKR 内核记录器表';


-- 创建 OKR 内核日记录表
drop table if exists `day_record`;
create table `day_record` (
    `id` bigint primary key auto_increment comment 'ID',
    `core_id` bigint not null comment 'OKR 内核 ID',
    `record_date` date not null comment '日期',
    `credit1` double not null default 0.0 comment '信息指数平均值',
    `credit2` int not null default 0 comment '第二象限任务完成数',
    `credit3` int not null default 0 comment '第三象限任务完成数',
    `credit4` int not null default 0 comment '状态指标评估值',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
    index `idx_core_id`(`core_id` asc) using btree
) comment 'OKR 内核日记录表';


SET @@FOREIGN_KEY_CHECKS = 1;
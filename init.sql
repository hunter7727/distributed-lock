CREATE DATABASE IF NOT EXISTS `distributed_lock`;

USE `distributed_lock`;


DROP TABLE IF EXISTS `goods`;

CREATE TABLE `goods` (
                         `id` int(11) NOT NULL AUTO_INCREMENT,
                         `name` varchar(64) NOT NULL,
                         `number` int(11) NOT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='商品表';


insert  into `goods`(`id`,`name`,`number`) values
    (1,'茅台',10);


DROP TABLE IF EXISTS `lock`;

CREATE TABLE `lock` (
                        `id` int(11) NOT NULL AUTO_INCREMENT,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='锁表';


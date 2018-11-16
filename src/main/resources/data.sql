CREATE TABLE `house_price_record` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `state` varchar(20) DEFAULT NULL,
  `describe` varchar(20) DEFAULT NULL,
  `price` varchar(255) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `areaName` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6475 DEFAULT CHARSET=utf8;
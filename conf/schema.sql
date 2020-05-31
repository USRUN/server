CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_add` datetime NOT NULL,
  `date_update` datetime NOT NULL,
  `birthday` datetime DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `device_token` varchar(255) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `gender` int(11) DEFAULT NULL,
  `hcmus` bit(1) NOT NULL,
  `height` double DEFAULT NULL,
  `img` varchar(255) DEFAULT NULL,
  `is_enabled` bit(1) NOT NULL,
  `last_login` datetime DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `name_slug` varchar(50) DEFAULT NULL,
  `open_id` varchar(255) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `type` int(11) NOT NULL,
  `weight` double DEFAULT NULL,
  `team_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `user_roles` (
  `user_id` bigint(20) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
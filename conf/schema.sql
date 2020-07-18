CREATE TABLE IF NOT EXISTS `user`
(
    `userId`         bigint(20) NOT NULL AUTO_INCREMENT,
    `userCode`       varchar(255)        DEFAULT NULL,
    `email`          varchar(255)        DEFAULT NULL,
    `password`       varchar(255)        DEFAULT NULL,
    `deviceToken`    varchar(255)        DEFAULT NULL,
    `hcmus`          bit(1)              DEFAULT NULL,
    `avatar`         varchar(255)        DEFAULT NULL,
    `displayName`    varchar(255)        DEFAULT NULL,
    `shortAddress`   varchar(255)        DEFAULT NULL,
    `gender`         tinyint(4)          DEFAULT NULL,
    `followingCount` int(11)             DEFAULT NULL,
    `followerCount`  int(11)             DEFAULT NULL,
    `isEnabled`      bit(1)              DEFAULT NULL,
    `deleted`        bit(1)              DEFAULT NULL,
    `birthday`       timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `height`         int(11)             DEFAULT NULL,
    `weight`         int(11)             DEFAULT NULL,
    `lastLogin`      timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `userType`       tinyint(4)          DEFAULT NULL,
    `createTime`     timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updateTime`     timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `province`       int(11)             DEFAULT '0',
    PRIMARY KEY (`userId`),
    UNIQUE KEY `email` (`email`),
    KEY `userId_user_index` (`userId`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `userRole`
(
    `userId` bigint(20) NOT NULL,
    `roleId` int(11)    NOT NULL,
    PRIMARY KEY (`userId`, `roleId`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `userActivity`
(
    `userActivityId` bigint(20) NOT NULL,
    `userId`         bigint(20)          DEFAULT NULL,
    `createTime`     datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updateTime`     timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `totalDistance`  bigint(20)          DEFAULT NULL,
    `totalTime`      bigint(20)          DEFAULT NULL,
    `totalStep`      double              DEFAULT NULL,
    `avgPace`        double              DEFAULT NULL,
    `avgHeart`       int(11)             DEFAULT NULL,
    `maxHeart`       int(11)             DEFAULT NULL,
    `calories`       int(11)             DEFAULT NULL,
    `elevGain`       double              DEFAULT NULL,
    `elevMax`        double              DEFAULT NULL,
    `photo`          varchar(2048)       DEFAULT NULL,
    `title`          varchar(255)        DEFAULT NULL,
    `description`    text,
    `totalLove`      int(11)             DEFAULT NULL,
    `totalComment`   int(11)             DEFAULT NULL,
    `totalShare`     int(11)             DEFAULT NULL,
    `processed`      bit(1)              DEFAULT NULL,
    `deleted`        tinyint(4)          DEFAULT NULL,
    `privacy`        tinyint(4)          DEFAULT NULL,
    PRIMARY KEY (`userActivityId`),
    KEY `userId_userActivity_index` (`userId`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `userRelation`
(
    `follower` bigint(20) NOT NULL,
    `followed` bigint(20) NOT NULL,
    PRIMARY KEY (`follower`, `followed`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `team`
(
    `teamId`      bigint(20)   NOT NULL AUTO_INCREMENT,
    `privacy`     tinyint(4)            DEFAULT '0',
    `totalMember` int(11)               DEFAULT NULL,
    `teamName`    varchar(255) NOT NULL,
    `thumbnail`   varchar(255)          DEFAULT NULL,
    `verified`    tinyint(4)            DEFAULT NULL,
    `deleted`     tinyint(4)            DEFAULT NULL,
    `createTime`  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `description` text,
    `banner`      varchar(255)          DEFAULT NULL,
    `province`    int(11)               DEFAULT '0',
    PRIMARY KEY (`teamId`),
    UNIQUE KEY `uniq_teamname` (`teamName`),
    KEY `teamId_team_index` (`teamId`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `teamMember`
(
    `teamId`         bigint(20) NOT NULL,
    `userId`         bigint(20) NOT NULL,
    `teamMemberType` tinyint(4)          DEFAULT NULL,
    `addTime`        timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`teamId`, `userId`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `teamPlan`
(
    `teamPlanId`    bigint(20) NOT NULL AUTO_INCREMENT,
    `teamId`        bigint(20) NOT NULL,
    `status`        tinyint(4)          DEFAULT NULL,
    `startTime`     timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `endTime`       timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `routeSnapshot` varchar(255)        DEFAULT NULL,
    `deleted`       tinyint(4)          DEFAULT NULL,
    PRIMARY KEY (`teamPlanId`, `teamId`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `teamPlanParticipant`
(
    `teamPlanId` bigint(20) NOT NULL,
    `userId`     bigint(20) NOT NULL,
    `distance`   int(11) DEFAULT NULL,
    PRIMARY KEY (`teamPlanId`, `userId`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `love`
(
    `userId`     int(20) NOT NULL,
    `activityId` int(20) NOT NULL,
    PRIMARY KEY (`activityId`, `userId`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `event`
(
    `eventId`          bigint(20) NOT NULL AUTO_INCREMENT,
    `status`           tinyint(4)          DEFAULT NULL,
    `createTime`       timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `eventName`        varchar(255)        DEFAULT NULL,
    `subtitle`         varchar(255)        DEFAULT NULL,
    `thumbnail`        varchar(255)        DEFAULT NULL,
    `totalParticipant` int(11)             DEFAULT NULL,
    `startTime`        timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `endTime`          timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted`          bit(1)              DEFAULT NULL,
    PRIMARY KEY (`eventId`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `eventParticipant`
(
    `eventId`  bigint(20) NOT NULL,
    `userId`   bigint(20) NOT NULL,
    `teamId`   bigint(20) NOT NULL,
    `distance` bigint(20) NOT NULL,
    PRIMARY KEY (`userId`, `eventId`, `teamId`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `organization`
(
    `id`   bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `sponsor`
(
    `eventId`        bigint(20) NOT NULL,
    `organazationId` bigint(20) NOT NULL,
    PRIMARY KEY (`eventId`, `organazationId`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;
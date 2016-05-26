DROP DATABASE IF EXISTS `reports`;
CREATE DATABASE `reports`
  DEFAULT CHARACTER SET utf8;
USE `reports`;

--
-- Table structure for table `event_type`
--
DROP TABLE IF EXISTS `event_type`;
CREATE TABLE `event_type` (
  `_id`  INT(11) NOT NULL,
  `name` VARCHAR(45) DEFAULT NULL,
  PRIMARY KEY (`_id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
);

--
-- Dumping data for table `event_type`
--
INSERT INTO `event_type` VALUES (301, 'branch_delete');
INSERT INTO `event_type` VALUES (300, 'commit');
INSERT INTO `event_type` VALUES (102, 'issue_add_comment');
INSERT INTO `event_type` VALUES (105, 'issue_add_label');
INSERT INTO `event_type` VALUES (101, 'issue_close');
INSERT INTO `event_type` VALUES (103, 'issue_edit_comment');
INSERT INTO `event_type` VALUES (100, 'issue_open');
INSERT INTO `event_type` VALUES (104, 'issue_remove_comment');
INSERT INTO `event_type` VALUES (106, 'issue_remove_label');
INSERT INTO `event_type` VALUES (202, 'pr_add_comment');
INSERT INTO `event_type` VALUES (205, 'pr_add_label');
INSERT INTO `event_type` VALUES (201, 'pr_close');
INSERT INTO `event_type` VALUES (203, 'pr_edit_comment');
INSERT INTO `event_type` VALUES (207, 'pr_merge');
INSERT INTO `event_type` VALUES (200, 'pr_open');
INSERT INTO `event_type` VALUES (204, 'pr_remove_comment');
INSERT INTO `event_type` VALUES (206, 'pr_remove_label');

--
-- Table structure for table `project`
--
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `_id`  INT(11)     NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`_id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
);

--
-- Table structure for table `repository`
--
DROP TABLE IF EXISTS `repository`;
CREATE TABLE `repository` (
  `_id`     INT(11)      NOT NULL,
  `name`    VARCHAR(255) NOT NULL,
  `private` TINYINT(1)   NOT NULL,
  PRIMARY KEY (`_id`)
);

--
-- Table structure for table `project_repository`
--
DROP TABLE IF EXISTS `project_repository`;
CREATE TABLE `project_repository` (
  `_id`           INT(11) NOT NULL AUTO_INCREMENT,
  `project_id`    INT(11) NOT NULL,
  `repository_id` INT(11) NOT NULL,
  PRIMARY KEY (`_id`),
  KEY `project_id_INDEX` (`project_id`),
  KEY `repository_id_INDEX` (`repository_id`),
  CONSTRAINT `project` FOREIGN KEY (`project_id`) REFERENCES `project` (`_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `repository` FOREIGN KEY (`repository_id`) REFERENCES `repository` (`_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

--
-- Table structure for table `user`
--
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `_id`      INT(11)     NOT NULL,
  `username` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`_id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
);

--
-- Table structure for table `event`
--
DROP TABLE IF EXISTS `event`;
CREATE TABLE `event` (
  `_id`            INT(11)   NOT NULL AUTO_INCREMENT,
  `repository_id`  INT(11)   NOT NULL,
  `author_user_id` INT(11)   NOT NULL,
  `owner_user_id`  INT(11)   NOT NULL,
  `event_type_id`  INT(11)   NOT NULL,
  `date`           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`_id`),
  KEY `author_INDEX` (`author_user_id`),
  KEY `owner_INDEX` (`owner_user_id`),
  KEY `event_type_INDEX` (`event_type_id`),
  KEY `date_INDEX` (`date`),
  KEY `repository_INDEX` (`repository_id`),
  CONSTRAINT `author` FOREIGN KEY (`author_user_id`) REFERENCES `user` (`_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `event_repository` FOREIGN KEY (`repository_id`) REFERENCES `repository` (`_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `event_type` FOREIGN KEY (`event_type_id`) REFERENCES `event_type` (`_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `owner` FOREIGN KEY (`owner_user_id`) REFERENCES `user` (`_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- Dump completed

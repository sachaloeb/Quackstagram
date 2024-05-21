
DROP TABLE IF EXISTS `Follows`;
DROP TABLE IF EXISTS `hashtags`;

DROP TABLE IF EXISTS `Likes`;
DROP TABLE IF EXISTS `Notifications`;
DROP TABLE IF EXISTS `Comments`;

DROP TABLE IF EXISTS `Images`;
DROP TABLE IF EXISTS `Users`;


CREATE TABLE `Users` (
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `bio` longtext,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


INSERT INTO `Users` VALUES ('1','6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b','1'),('dani','fe675fe7aaee830b6fed09b64e034f84dcbdaeb429d9cccd4ebb90e15af8dd71','im chinese'),('Mystar','e7cf3ef4f17c3999a94f2c6f612e8a888e5b1026878e4e19398b23bd38ec221a',''),('sacha','03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4','its me'),('test','9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08','test'),('test1','1b4f0e9851971998e732078544c96b36c3d01cedf7caa332359d6f1d83567014','imatest'),('test2','e7cf3ef4f17c3999a94f2c6f612e8a888e5b1026878e4e19398b23bd38ec221a',''),('Xylo','e7cf3ef4f17c3999a94f2c6f612e8a888e5b1026878e4e19398b23bd38ec221a',''),('Zara','e7cf3ef4f17c3999a94f2c6f612e8a888e5b1026878e4e19398b23bd38ec221a','');




CREATE TABLE `Images` (
  `imageID` varchar(300) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `bio` longtext,
  `timestamp` datetime DEFAULT NULL,
  `likes` int DEFAULT NULL,
  PRIMARY KEY (`imageID`),
  KEY `username` (`username`),
  CONSTRAINT `images_ibfk_1` FOREIGN KEY (`username`) REFERENCES `Users` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


INSERT INTO `Images` VALUES ('Mystar_1','Mystar','Cookies gone?','2023-12-17 19:26:50',1),('Mystar_2','Mystar','In my soup a fly is.','2023-12-17 19:27:24',1),('Xylo_2','Xylo','Jedi mind trick failed.','2023-12-17 19:23:14',1),('Zara_1','Zara','Lost my map I have. Oops.','2023-12-17 19:24:31',1),('Zara_2','Zara','Yoga with Yoda','2023-12-17 19:25:03',1);




CREATE TABLE `Follows` (
  `follower_username` varchar(255) NOT NULL,
  `followed_username` varchar(255) NOT NULL,
  PRIMARY KEY (`follower_username`,`followed_username`),
  KEY `followed_username` (`followed_username`),
  CONSTRAINT `follows_ibfk_1` FOREIGN KEY (`follower_username`) REFERENCES `Users` (`username`),
  CONSTRAINT `follows_ibfk_2` FOREIGN KEY (`followed_username`) REFERENCES `Users` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


INSERT INTO `Follows` VALUES ('sacha','1'),('test','Mystar'),('sacha','Xylo');








CREATE TABLE `Likes` (
  `imageID` varchar(300) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`imageID`,`username`),
  KEY `username` (`username`),
  CONSTRAINT `likes_ibfk_1` FOREIGN KEY (`username`) REFERENCES `Users` (`username`),
  CONSTRAINT `likes_ibfk_2` FOREIGN KEY (`imageID`) REFERENCES `Images` (`imageID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


INSERT INTO `Likes` VALUES ('Xylo_2','sacha'),('Zara_1','sacha'),('Mystar_1','test'),('Mystar_2','test'),('Zara_2','test');





CREATE TABLE `Notifications` (
  `id` int NOT NULL AUTO_INCREMENT,
  `notif_receiver` varchar(255) DEFAULT NULL,
  `concerned_user` varchar(255) DEFAULT NULL,
  `liked_picture` varchar(255) DEFAULT NULL,
  `notif_type` varchar(255) DEFAULT NULL,
  `notif_timestamp` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `notif_receiver` (`notif_receiver`),
  KEY `concerned_user` (`concerned_user`),
  KEY `notifications_ibfk_3` (`liked_picture`),
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`notif_receiver`) REFERENCES `Users` (`username`),
  CONSTRAINT `notifications_ibfk_2` FOREIGN KEY (`concerned_user`) REFERENCES `Users` (`username`),
  CONSTRAINT `notifications_ibfk_3` FOREIGN KEY (`liked_picture`) REFERENCES `Images` (`imageID`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


INSERT INTO `Notifications` VALUES (1,'Mystar','test','Mystar_1','like','2024-05-08 18:14:11'),(2,'Zara','test','Zara_2','like','2024-05-08 18:14:20'),(3,'Mystar','test','Mystar_2','like','2024-05-08 18:14:35'),(4,'Mystar','test',NULL,'follow','2024-05-08 18:14:49'),(5,'Zara','sacha','Zara_1','like','2024-05-15 15:50:52'),(6,'1','sacha',NULL,'follow','2024-05-15 15:51:49'),(7,'Xylo','sacha',NULL,'follow','2024-05-15 15:52:41'),(8,'Xylo','sacha','Xylo_2','like','2024-05-15 15:52:52');



CREATE TABLE `Comments` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `imageID` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `comment_text` longtext,
  `timestamp` datetime DEFAULT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `imageID` (`imageID`),
  KEY `username` (`username`),
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`imageID`) REFERENCES `Images` (`imageID`),
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`username`) REFERENCES `Users` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


INSERT INTO `Comments` VALUES (1,'Mystar_1','sacha','Great photo!','2024-05-18 10:00:00'),(2,'Mystar_2','test','I love this!','2024-05-18 11:00:00'),(3,'Xylo_2','dani','Amazing!','2024-05-18 12:00:00'),(4,'Zara_1','Mystar','So cool!','2024-05-18 13:00:00'),(5,'Zara_2','Xylo','Nice shot!','2024-05-18 14:00:00');




CREATE TABLE `hashtags` (
  `keyword` varchar(255) NOT NULL,
  `imageID` varchar(255) NOT NULL,
  PRIMARY KEY (`keyword`,`imageID`),
  KEY `imageID` (`imageID`),
  CONSTRAINT `hashtags_ibfk_1` FOREIGN KEY (`imageID`) REFERENCES `Images` (`imageID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


INSERT INTO `hashtags` VALUES ('#cool','Mystar_1'),('#funny','Mystar_2'),('#amazing','Xylo_2'),('#cool','Zara_1'),('#yoga','Zara_2');







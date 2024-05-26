

DELIMITER //
CREATE PROCEDURE DecrementLikes(IN imgid VARCHAR(300))
	UPDATE Images SET likes = likes - 1 WHERE imageID = imgid
DELIMITER ;

DELIMITER //
CREATE FUNCTION GetUserTotalLikes(user VARCHAR(255)) RETURNS int
    READS SQL DATA
    DETERMINISTIC
BEGIN
    DECLARE total_likes INT DEFAULT 0;
    SELECT COALESCE(SUM(likes), 0) INTO total_likes FROM Images WHERE username = user;
    RETURN total_likes;
END
DELIMITER ;

DELIMITER //
CREATE PROCEDURE IncrementLikes(IN imgid VARCHAR(300))
	UPDATE Images SET likes = likes + 1 WHERE imageID = imgid
DELIMITER ;

DELIMITER //
CREATE TRIGGER `AfterInsertFollow` AFTER INSERT ON `Follows` FOR EACH ROW INSERT INTO Notifications (notif_receiver, concerned_user, liked_picture, notif_type, notif_timestamp)
    VALUES (NEW.followed_username, NEW.follower_username, NULL, 'follow', NOW())
DELIMITER ;

DELIMITER //
CREATE TRIGGER `AfterInsertLike` 
AFTER INSERT ON `Likes` 
	FOR EACH ROW BEGIN
		CALL IncrementLikes(NEW.imageID);
		SET @total_likes = (SELECT GetUserTotalLikes(username) FROM Images WHERE imageID = NEW.imageID);
		INSERT INTO Notifications (notif_receiver, concerned_user, liked_picture, notif_type, notif_timestamp)
    	VALUES ((SELECT username FROM Images WHERE imageID = NEW.imageID), NEW.username, NEW.imageID, 'like', NOW());
END
DELIMITER ;

DELIMITER //
CREATE TRIGGER `AfterDeleteLike` AFTER DELETE ON `Likes` FOR EACH ROW BEGIN
    CALL DecrementLikes(OLD.imageID);
    SET @total_likes = (SELECT GetUserTotalLikes(username) FROM Images WHERE imageID = OLD.imageID);
   	DELETE FROM Notifications WHERE liked_picture = OLD.imageID AND concerned_user=OLD.username;
END
DELIMITER ;

DELIMITER //
CREATE TRIGGER `AfterDeleteFollow` AFTER DELETE ON `Follows` FOR EACH ROW
   	DELETE FROM Notifications WHERE notif_receiver = OLD.followed_username AND concerned_user=OLD.follower_username AND notif_type='follow';   	
DELIMITER ;
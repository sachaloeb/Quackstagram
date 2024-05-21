DROP TRIGGER IF EXISTS AfterInsertLike;
DROP TRIGGER IF EXISTS AfterInsertFollow;
DROP PROCEDURE IF EXISTS UpdateLikes;
DROP FUNCTION IF EXISTS GetUserPostCount;
DROP FUNCTION IF EXISTS GetUserTotalLikes;


DELIMITER //

CREATE PROCEDURE IncrementLikes(IN imageId VARCHAR(300))
BEGIN
    UPDATE Images SET likes = likes + 1 WHERE imageID = imageId;
END
DELIMITER ;

DELIMITER //
CREATE PROCEDURE DecrementLikes(IN imageId VARCHAR(300))
BEGIN
	UPDATE Images SET likes = likes - 1 WHERE imageID = imageId;
END
DELIMITER ;

DELIMITER //
CREATE FUNCTION GetUserTotalLikes(user VARCHAR(255)) RETURNS INT
DETERMINISTIC READS SQL DATA
BEGIN
    DECLARE total_likes INT;
    SELECT SUM(likes) INTO total_likes FROM Images WHERE username = user;
    RETURN IFNULL(total_likes, 0);
END
DELIMITER ;


DELIMITER //
CREATE TRIGGER AfterInsertLike
AFTER INSERT ON Likes
FOR EACH ROW
BEGIN
    CALL IncrementLikes(NEW.imageID);
    SET @total_likes = GetUserTotalLikes((SELECT username FROM Images WHERE imageID = NEW.imageID));
END
DELIMITER ;

DELIMITER //
CREATE TRIGGER AfterDeleteLike
AFTER DELETE ON Likes
FOR EACH ROW
BEGIN
    CALL DecrementLikes(NEW.imageID);
    SET @total_likes = GetUserTotalLikes((SELECT username FROM Images WHERE imageID = NEW.imageID));
END
DELIMITER ;


DELIMITER //
CREATE TRIGGER AfterInsertFollow
AFTER INSERT ON Follows
FOR EACH ROW
BEGIN
    INSERT INTO Notifications (notif_receiver, concerned_user, liked_picture, notif_type, notif_timestamp)
    VALUES (NEW.followed_username, NEW.follower_username, NULL, 'follow', NOW());
END
DELIMITER ;
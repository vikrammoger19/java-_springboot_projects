create database foodApp;
use foodapp;

-- 1.verifying mobile number

CREATE TABLE `mobilenumberverification` (
  `mobileNumber` varchar(10) NOT NULL,
  `code` varchar(45) DEFAULT NULL,
  `expiryTime` varchar(45) DEFAULT NULL,
  `verified` tinyint DEFAULT NULL,
  PRIMARY KEY (`mobileNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 2.User registration table


CREATE TABLE `user` (
  `userId` int NOT NULL AUTO_INCREMENT,
  `userName` varchar(45) NOT NULL,
  `mobileNumber` varchar(10) NOT NULL,
  `email` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `code` varchar(45) DEFAULT NULL,
  `expiryTime` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `mobileNumber_UNIQUE` (`mobileNumber`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  CONSTRAINT `mobileNumber` FOREIGN KEY (`mobileNumber`) REFERENCES `mobilenumberverification` (`mobileNumber`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 3. Restaurant Table

 
CREATE TABLE `restaurant` (
  `restaurantId` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `location` varchar(45) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  `freeDelivery` tinyint DEFAULT NULL,
  `ratings` int DEFAULT '0',
  `favourites` int DEFAULT '0',
  `overAllRatings` double DEFAULT '0',
  `openTime` time NOT NULL,
  `closeTime` time NOT NULL,
  `image` longblob,
  `imageUrl` varchar(500) DEFAULT NULL,
  `minCost` double NOT NULL DEFAULT '0',
  `creditCard` tinyint DEFAULT NULL,
  PRIMARY KEY (`restaurantId`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 4.Menu Table 

CREATE TABLE `menu` (
  `restaurantId` int DEFAULT NULL,
  `dishId` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `price` int NOT NULL,
  `image` longblob,
  `imageUrl` varchar(500) DEFAULT NULL,
  `dishType` varchar(45) NOT NULL,
  PRIMARY KEY (`dishId`),
  KEY `restaurantid` (`restaurantId`),
  CONSTRAINT `restaurantid` FOREIGN KEY (`restaurantId`) REFERENCES `restaurant` (`restaurantId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5. Rating Tbale

CREATE TABLE `ratings` (
  `userId` int NOT NULL,
  `restaurantId` int NOT NULL,
  `rating` double DEFAULT '0',
  PRIMARY KEY (`userId`,`restaurantId`),
  KEY `restaurantId` (`restaurantId`),
  CONSTRAINT `ratings_ibfk_1` FOREIGN KEY (`restaurantId`) REFERENCES `restaurant` (`restaurantId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ratings_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ratings_chk_1` CHECK ((`rating` <= 5))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 6. Favourites Tbale

 
CREATE TABLE `favourites` (
  `userId` int NOT NULL,
  `restaurantId` int NOT NULL,
  PRIMARY KEY (`userId`,`restaurantId`),
  KEY `restaurantId` (`restaurantId`),
  CONSTRAINT `favourites_ibfk_1` FOREIGN KEY (`restaurantId`) REFERENCES `restaurant` (`restaurantId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `favourites_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 7.Address Table


CREATE TABLE `address` (
  `userId` int NOT NULL,
  `address` varchar(45) NOT NULL,
  PRIMARY KEY (`userId`),
  CONSTRAINT `userId` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 8.Card Table


CREATE TABLE `card` (
  `userId` int DEFAULT NULL,
  `cardNo` bigint NOT NULL,
  PRIMARY KEY (`cardNo`),
  KEY `userId` (`userId`),
  CONSTRAINT `card_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 9.Cart Table

CREATE TABLE `cart` (
  `cartId` int NOT NULL AUTO_INCREMENT,
  `userId` int DEFAULT NULL,
  `restaurantId` int DEFAULT NULL,
  `totalprice` double DEFAULT '0',
  PRIMARY KEY (`cartId`),
  KEY `userId` (`userId`),
  KEY `restaurantId` (`restaurantId`),
  CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`restaurantId`) REFERENCES `restaurant` (`restaurantId`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 10.Item Table


CREATE TABLE `item` (
  `dishId` int NOT NULL,
  `cartId` int NOT NULL,
  `count` int DEFAULT '1',
  PRIMARY KEY (`dishId`,`cartId`),
  KEY `cartId` (`cartId`),
  CONSTRAINT `item_ibfk_1` FOREIGN KEY (`dishId`) REFERENCES `menu` (`dishId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `item_ibfk_2` FOREIGN KEY (`cartId`) REFERENCES `cart` (`cartId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 11. Orders Table


CREATE TABLE `orders` (
  `cartId` int NOT NULL,
  `cardNo` bigint NOT NULL,
  `address` varchar(45) NOT NULL,
  `status` enum('successful','pending','failed') DEFAULT NULL,
  KEY `cardNo_idx` (`cardNo`),
  KEY `cartId` (`cartId`),
  CONSTRAINT `cardNo` FOREIGN KEY (`cardNo`) REFERENCES `card` (`cardNo`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `cartId` FOREIGN KEY (`cartId`) REFERENCES `cart` (`cartId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


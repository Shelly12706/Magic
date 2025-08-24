CREATE DATABASE  IF NOT EXISTS `magic_academy2` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `magic_academy2`;
-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: magic_academy2
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `id` int NOT NULL AUTO_INCREMENT,
  `student_no` varchar(10) NOT NULL,
  `username` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `student_no` (`student_no`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (1,'S0001','abcabc','小明','0988112233','www.abc.com','台北市','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5','2025-08-18 08:24:51'),(2,'S0002','qazwsx','小花','091122334455','www.bbb.com','台中市','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5','2025-08-18 08:26:28'),(3,'S0003','defdef','mary','0977123456','www,eee,com','新竹市','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5','2025-08-19 01:28:21'),(4,'S0004','qweqwe','方大大','095511223344','www.sss.com','桃園市','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5','2025-08-19 01:49:21'),(5,'S0005','poppop','terry','0933112233','www.abc.com','桃園市','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5','2025-08-19 01:53:19'),(6,'S0006','poipoi','李大方','0966112233','www.vvv.com','苗栗市','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5','2025-08-21 02:15:27'),(7,'S0007','tttyyy','陳筱麗','0982777111','www.nnn.com','桃園市','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5','2025-08-21 02:37:02'),(8,'S0008','zxczxc','王小強','0988112233','www.mmm.com','新竹市','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5','2025-08-21 03:26:24'),(9,'S0009','jkljkl','李芳芳','09991123445','www,bbb.com','嘉義市','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5','2025-08-22 02:14:18'),(10,'S0010','kkklll','蔡美美','091234567','www.lll.com','基隆市','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5','2025-08-22 03:30:52'),(11,'S0011','macmac','李麥克','094411223344','www.ooo.com','台北市','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5','2025-08-22 05:47:21'),(12,'S0012','qqqwww','張大發','097654321','www.ppp.com','台中市','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5','2025-08-22 06:27:36'),(13,'S0013','ggghhh','楊曉華','09834123456','www.ttt.com','高雄市','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5','2025-08-22 06:39:56'),(14,'S0014','abcdef','強森','0955122344','www.lll.com','台南市','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5','2025-08-22 07:01:12'),(15,'S0015','rrrttt','楊輪輪','09821342674','www.nnn.com','雲林縣','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5','2025-08-24 04:23:28'),(16,'S0016','uuuiii','張小帥','09721233456','www.jkl.com','花蓮市','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5','2025-08-24 05:27:51'),(17,'S0017','bnmbnm','陳美美','0944123456','www.uuu.com','台東市','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5','2025-08-24 08:17:06'),(18,'S0018','qwerty','吳大同','0966554433','www.rrr.com','宜蘭市','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5','2025-08-24 10:49:39');
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-24 21:41:00

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
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `course_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `credits` int NOT NULL,
  `day_of_week` int NOT NULL,
  `time_slot` varchar(20) NOT NULL,
  `teacher_no` varchar(10) NOT NULL,
  `course_code` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`course_id`),
  KEY `teacher_no` (`teacher_no`),
  CONSTRAINT `course_ibfk_1` FOREIGN KEY (`teacher_no`) REFERENCES `teacher` (`teacher_no`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,'魔法初階',3,1,'08:00-10:00','T0001','C0001'),(2,'咒語學',2,1,'08:00-10:00','T0002','C0002'),(3,'魔法藥水',3,2,'08:00-10:00','T0001','C0003'),(4,'飛行術',2,2,'10:10-12:00','T0001','C0004'),(5,'變形術',3,3,'08:00-10:00','T0002','C0005'),(6,'黑魔法防禦',3,3,'10:10-12:00','T0001','C0006'),(7,'魔法生物學',2,4,'08:00-10:00','T0001','C0007'),(8,'占卜學',2,4,'10:10-12:00','T0002','C0008'),(9,'古代魔法史',3,5,'10:10-12:00','T0002','C0009'),(10,'魔杖製作',2,5,'08:00-10:00','T0001','C0010'),(11,'咒語進階',3,5,'08:00-10:00','T0002','C0011'),(12,'魔法實習',3,1,'10:10-12:00','T0002','C0012'),(13,'元素魔法',3,2,'08:00-10:00','T0002','C0013'),(14,'魔法史研究',2,4,'10:10-12:00','T0001','C0014'),(15,'實戰演練',3,3,'08:00-10:00','T0001','C0015'),(17,'魔法進階',5,1,'10:10-12:00','T0001','C0017');
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
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

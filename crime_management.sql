-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: crime_management
-- ------------------------------------------------------
-- Server version	8.0.44

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
-- Table structure for table `cases`
--

DROP TABLE IF EXISTS `cases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cases` (
  `CaseID` int NOT NULL AUTO_INCREMENT,
  `CaseDetails` varchar(255) NOT NULL,
  `Location` varchar(50) DEFAULT NULL,
  `ReportedOn` date DEFAULT NULL,
  `OfficerID_FK` int DEFAULT NULL,
  `CategoryID_FK` int DEFAULT NULL,
  `Status_FK` int DEFAULT NULL,
  PRIMARY KEY (`CaseID`),
  KEY `OfficerID_FK` (`OfficerID_FK`),
  KEY `CategoryID_FK` (`CategoryID_FK`),
  KEY `Status_FK` (`Status_FK`),
  CONSTRAINT `cases_ibfk_1` FOREIGN KEY (`OfficerID_FK`) REFERENCES `officer` (`OfficerID`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `cases_ibfk_2` FOREIGN KEY (`CategoryID_FK`) REFERENCES `crimecategory` (`TypeID`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `cases_ibfk_3` FOREIGN KEY (`Status_FK`) REFERENCES `casestatus` (`StatusCode`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cases`
--

LOCK TABLES `cases` WRITE;
/*!40000 ALTER TABLE `cases` DISABLE KEYS */;
INSERT INTO `cases` VALUES (1,'He entred a white man houses and stole PS5 and iPhone 17 Pro Max 1TB','Sandton City','2025-05-05',1,4,2),(2,'Hacked FNB and stole 5000 employee data information','Randburg','2025-05-03',3,5,1),(3,'Hijacked a VW GTI 2025 from Makubenjalo Pub','Diepklof','2025-05-01',4,6,2),(4,'He beat up around 20 people with bottles at KitCat Lounge Club','Chon Buri, Thailand','2025-04-28',5,3,3),(5,'Property dispute, the man cheated on his wife with mbali and she wants divorse and everything they own','Soweto','2025-04-30',6,7,1),(7,'Didn\'t pay the bill when he was with women','Live Club, Sandton','2025-11-07',2,1,2);
/*!40000 ALTER TABLE `cases` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `casestatus`
--

DROP TABLE IF EXISTS `casestatus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `casestatus` (
  `StatusCode` int NOT NULL AUTO_INCREMENT,
  `StatusDescription` varchar(20) NOT NULL,
  PRIMARY KEY (`StatusCode`),
  UNIQUE KEY `StatusDescription` (`StatusDescription`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `casestatus`
--

LOCK TABLES `casestatus` WRITE;
/*!40000 ALTER TABLE `casestatus` DISABLE KEYS */;
INSERT INTO `casestatus` VALUES (3,'Closed'),(1,'Open'),(2,'Under Investigation');
/*!40000 ALTER TABLE `casestatus` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `crimecategory`
--

DROP TABLE IF EXISTS `crimecategory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `crimecategory` (
  `TypeID` int NOT NULL AUTO_INCREMENT,
  `CategoryName` varchar(50) NOT NULL,
  PRIMARY KEY (`TypeID`),
  UNIQUE KEY `CategoryName` (`CategoryName`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `crimecategory`
--

LOCK TABLES `crimecategory` WRITE;
/*!40000 ALTER TABLE `crimecategory` DISABLE KEYS */;
INSERT INTO `crimecategory` VALUES (3,'Assault'),(4,'Burglary'),(6,'Car Theft'),(5,'Cyber Crime'),(7,'Domestic'),(2,'Fraud'),(1,'Theft');
/*!40000 ALTER TABLE `crimecategory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `officer`
--

DROP TABLE IF EXISTS `officer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `officer` (
  `OfficerID` int NOT NULL AUTO_INCREMENT,
  `FirstName` varchar(50) NOT NULL,
  `LastName` varchar(50) NOT NULL,
  `Ranks` varchar(50) NOT NULL,
  `Phone` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`OfficerID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `officer`
--

LOCK TABLES `officer` WRITE;
/*!40000 ALTER TABLE `officer` DISABLE KEYS */;
INSERT INTO `officer` VALUES (1,'James','Patel','Senior Detective','0613456786'),(2,'Michael','Ngomane','Major General','0614906456'),(3,'Lerato','Punde','Captain','0856478766'),(4,'Busiwe','Dlamini','Lieutenant General','0897865678'),(5,'Genghis','Khan','Ultimate Conqueror','0101457856'),(6,'Thabo','Nkosi','Colonel','0768574367');
/*!40000 ALTER TABLE `officer` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-07 23:26:14

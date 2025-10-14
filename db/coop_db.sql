-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: coop_db
-- ------------------------------------------------------
-- Server version	8.0.40

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
-- Table structure for table `actuator_status`
--

DROP TABLE IF EXISTS `actuator_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `actuator_status` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pump_on` tinyint(1) NOT NULL DEFAULT '0',
  `fan_on` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  KEY `idx_actuator_status_created` (`created_at`),
  CONSTRAINT `chk_fan_on` CHECK ((`fan_on` in (0,1))),
  CONSTRAINT `chk_pump_on` CHECK ((`pump_on` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `actuator_status`
--

LOCK TABLES `actuator_status` WRITE;
/*!40000 ALTER TABLE `actuator_status` DISABLE KEYS */;
INSERT INTO `actuator_status` VALUES (1,0,1,'2025-10-14 17:56:37.054596','2025-10-14 18:22:45.234283');
/*!40000 ALTER TABLE `actuator_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iot_data`
--

DROP TABLE IF EXISTS `iot_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iot_data` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `temperature_c` decimal(6,2) NOT NULL,
  `humidity_pct` decimal(5,2) NOT NULL,
  `water_level_pct` decimal(5,2) NOT NULL,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  KEY `idx_iot_data_created_at` (`created_at`),
  CONSTRAINT `iot_data_chk_1` CHECK (((`humidity_pct` >= 0) and (`humidity_pct` <= 100))),
  CONSTRAINT `iot_data_chk_2` CHECK (((`water_level_pct` >= 0) and (`water_level_pct` <= 100)))
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iot_data`
--

LOCK TABLES `iot_data` WRITE;
/*!40000 ALTER TABLE `iot_data` DISABLE KEYS */;
INSERT INTO `iot_data` VALUES (1,24.75,56.30,72.00,'2025-10-14 18:05:22.303075'),(2,24.75,56.30,72.00,'2025-10-14 18:05:58.226266'),(3,24.75,56.30,72.00,'2025-10-14 18:27:11.541623'),(4,24.75,56.30,72.00,'2025-10-14 18:27:12.891870'),(5,24.75,56.30,72.00,'2025-10-14 18:27:14.626845'),(6,24.75,56.30,72.00,'2025-10-14 18:27:16.106911'),(7,24.75,56.30,7.00,'2025-10-14 18:27:21.390528'),(8,24.75,56.30,17.00,'2025-10-14 18:27:25.546230'),(9,24.75,26.30,17.00,'2025-10-14 18:27:30.598601'),(10,28.75,46.30,17.00,'2025-10-14 18:27:48.111501'),(11,28.75,46.30,100.00,'2025-10-14 18:27:55.589474'),(12,28.75,46.30,100.00,'2025-10-14 18:27:56.681866'),(13,28.75,46.30,100.00,'2025-10-14 18:27:58.114609'),(14,28.75,46.30,100.00,'2025-10-14 18:27:59.835108'),(15,28.75,46.30,100.00,'2025-10-14 18:28:01.186044'),(16,28.75,46.30,100.00,'2025-10-14 18:28:02.415639');
/*!40000 ALTER TABLE `iot_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_data`
--

DROP TABLE IF EXISTS `stock_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock_data` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `coop_id` varchar(64) NOT NULL,
  `chicks_count` int unsigned NOT NULL,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  KEY `idx_stock_coop_id` (`coop_id`),
  KEY `idx_stock_created_at` (`created_at`),
  CONSTRAINT `chk_chicks_count_nonneg` CHECK ((`chicks_count` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_data`
--

LOCK TABLES `stock_data` WRITE;
/*!40000 ALTER TABLE `stock_data` DISABLE KEYS */;
INSERT INTO `stock_data` VALUES (1,'coop5',2345,'2025-10-14 17:40:39.188521');
/*!40000 ALTER TABLE `stock_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `full_name` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `username` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(160) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `locked` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_username` (`username`),
  UNIQUE KEY `uk_users_email` (`email`),
  KEY `idx_users_username` (`username`),
  KEY `idx_users_email` (`email`),
  KEY `idx_users_role` (`role`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Tatenda ','talentchigwagwa','talentchigwagwa@gmail.com','$2a$10$SNSgVL1HZYgMhevouYzIZeAGEeknbu7dqTh1WulZh4ToQSAuJ2i7G','USER',1,0,'2025-10-14 14:05:52','2025-10-14 15:57:27'),(2,'David Jhon','wmuzenda','wmuzenda@zb.co.zw','$2a$10$rPwicfsIzY1ETXm2Zc4KVOSNhjpyT5INvloc15cIA9UcnJtkDLs0C','ADMIN',1,0,'2025-10-14 15:53:55','2025-10-14 15:53:55');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-14 19:09:45

-- MySQL dump 10.13  Distrib 8.0.12, for Linux (x86_64)
--
-- Host: localhost    Database: sapa
-- ------------------------------------------------------
-- Server version       8.0.12

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8mb4 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `nodes`
--

DROP TABLE IF EXISTS `nodes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `nodes` (
  `identifier` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` datetime(6) DEFAULT NULL,
  `uuid` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `depth` int(11) NOT NULL,
  `set_end` int(11) NOT NULL,
  `set_start` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`identifier`),
  UNIQUE KEY `UK_k6pm6tbx4iskwfuvhox4onyng` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nodes`
--

LOCK TABLES `nodes` WRITE;
/*!40000 ALTER TABLE `nodes` DISABLE KEYS */;
/*!40000 ALTER TABLE `nodes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sensors`
--

DROP TABLE IF EXISTS `sensors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `sensors` (
  `identifier` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` datetime(6) DEFAULT NULL,
  `uuid` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `configuration` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `node_identifier` bigint(20) NOT NULL,
  `type` varchar(255) NOT NULL,
  `unit` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`identifier`),
  UNIQUE KEY `UK_jrf1juocobpfypb4lnx5lyxol` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sensors`
--

LOCK TABLES `sensors` WRITE;
/*!40000 ALTER TABLE `sensors` DISABLE KEYS */;
/*!40000 ALTER TABLE `sensors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `state_correlations`
--

DROP TABLE IF EXISTS `state_correlations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `state_correlations` (
  `identifier` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` datetime(6) DEFAULT NULL,
  `uuid` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `end_state_identifier` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `start_state_identifier` bigint(20) NOT NULL,
  PRIMARY KEY (`identifier`),
  UNIQUE KEY `UK_4nb5geuqy74wdljqrrgu5cvor` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `state_correlations`
--

LOCK TABLES `state_correlations` WRITE;
/*!40000 ALTER TABLE `state_correlations` DISABLE KEYS */;
/*!40000 ALTER TABLE `state_correlations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `states`
--

DROP TABLE IF EXISTS `states`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `states` (
  `identifier` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` datetime(6) DEFAULT NULL,
  `uuid` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `emitted_at` datetime(6) NOT NULL,
  `sensor_identifier` bigint(20) NOT NULL,
  PRIMARY KEY (`identifier`),
  UNIQUE KEY `UK_lwif1xx4yivomgkddvw8a5afo` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `states`
--

LOCK TABLES `states` WRITE;
/*!40000 ALTER TABLE `states` DISABLE KEYS */;
/*!40000 ALTER TABLE `states` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `states_boolean`
--

DROP TABLE IF EXISTS `states_boolean`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `states_boolean` (
  `value` bit(1) DEFAULT NULL,
  `state_identifier` bigint(20) NOT NULL,
  PRIMARY KEY (`state_identifier`),
  CONSTRAINT `FK7iybmaw2xjbokhhct7u3l39ve` FOREIGN KEY (`state_identifier`) REFERENCES `states` (`identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `states_boolean`
--

LOCK TABLES `states_boolean` WRITE;
/*!40000 ALTER TABLE `states_boolean` DISABLE KEYS */;
/*!40000 ALTER TABLE `states_boolean` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `states_byte`
--

DROP TABLE IF EXISTS `states_byte`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `states_byte` (
  `value` tinyint(4) DEFAULT NULL,
  `state_identifier` bigint(20) NOT NULL,
  PRIMARY KEY (`state_identifier`),
  CONSTRAINT `FKg8e8tfb5eh55mrt93w961h0rw` FOREIGN KEY (`state_identifier`) REFERENCES `states` (`identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `states_byte`
--

LOCK TABLES `states_byte` WRITE;
/*!40000 ALTER TABLE `states_byte` DISABLE KEYS */;
/*!40000 ALTER TABLE `states_byte` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `states_double`
--

DROP TABLE IF EXISTS `states_double`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `states_double` (
  `value` double DEFAULT NULL,
  `state_identifier` bigint(20) NOT NULL,
  PRIMARY KEY (`state_identifier`),
  CONSTRAINT `FKebv317lbfyt0tar4orgvkhif6` FOREIGN KEY (`state_identifier`) REFERENCES `states` (`identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `states_double`
--

LOCK TABLES `states_double` WRITE;
/*!40000 ALTER TABLE `states_double` DISABLE KEYS */;
/*!40000 ALTER TABLE `states_double` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `states_float`
--

DROP TABLE IF EXISTS `states_float`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `states_float` (
  `value` float DEFAULT NULL,
  `state_identifier` bigint(20) NOT NULL,
  PRIMARY KEY (`state_identifier`),
  CONSTRAINT `FK4p1xwccgl8oo5q119de0gfcl7` FOREIGN KEY (`state_identifier`) REFERENCES `states` (`identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `states_float`
--

LOCK TABLES `states_float` WRITE;
/*!40000 ALTER TABLE `states_float` DISABLE KEYS */;
/*!40000 ALTER TABLE `states_float` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `states_integer`
--

DROP TABLE IF EXISTS `states_integer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `states_integer` (
  `value` int(11) DEFAULT NULL,
  `state_identifier` bigint(20) NOT NULL,
  PRIMARY KEY (`state_identifier`),
  CONSTRAINT `FK8lktj75ees6snmneywluw82iw` FOREIGN KEY (`state_identifier`) REFERENCES `states` (`identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `states_integer`
--

LOCK TABLES `states_integer` WRITE;
/*!40000 ALTER TABLE `states_integer` DISABLE KEYS */;
/*!40000 ALTER TABLE `states_integer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `states_label`
--

DROP TABLE IF EXISTS `states_label`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `states_label` (
  `end` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `start` datetime(6) NOT NULL,
  `state_identifier` bigint(20) NOT NULL,
  PRIMARY KEY (`state_identifier`),
  CONSTRAINT `FKpf82nwcvh9iacuojnvarhrkh1` FOREIGN KEY (`state_identifier`) REFERENCES `states` (`identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `states_label`
--

LOCK TABLES `states_label` WRITE;
/*!40000 ALTER TABLE `states_label` DISABLE KEYS */;
/*!40000 ALTER TABLE `states_label` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `states_long`
--

DROP TABLE IF EXISTS `states_long`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `states_long` (
  `value` bigint(20) DEFAULT NULL,
  `state_identifier` bigint(20) NOT NULL,
  PRIMARY KEY (`state_identifier`),
  CONSTRAINT `FKejxk2a3c0ddx8m0wbwm7kp6xc` FOREIGN KEY (`state_identifier`) REFERENCES `states` (`identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `states_long`
--

LOCK TABLES `states_long` WRITE;
/*!40000 ALTER TABLE `states_long` DISABLE KEYS */;
/*!40000 ALTER TABLE `states_long` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `states_short`
--

DROP TABLE IF EXISTS `states_short`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `states_short` (
  `value` smallint(6) DEFAULT NULL,
  `state_identifier` bigint(20) NOT NULL,
  PRIMARY KEY (`state_identifier`),
  CONSTRAINT `FKjywdams4rlunxiey9y3hutc7y` FOREIGN KEY (`state_identifier`) REFERENCES `states` (`identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `states_short`
--

LOCK TABLES `states_short` WRITE;
/*!40000 ALTER TABLE `states_short` DISABLE KEYS */;
/*!40000 ALTER TABLE `states_short` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `states_string`
--

DROP TABLE IF EXISTS `states_string`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `states_string` (
  `value` varchar(255) DEFAULT NULL,
  `state_identifier` bigint(20) NOT NULL,
  PRIMARY KEY (`state_identifier`),
  CONSTRAINT `FKe3xmcwejei2qgc1nm9mx2l3na` FOREIGN KEY (`state_identifier`) REFERENCES `states` (`identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `states_string`
--

LOCK TABLES `states_string` WRITE;
/*!40000 ALTER TABLE `states_string` DISABLE KEYS */;
/*!40000 ALTER TABLE `states_string` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `states_values`
--

DROP TABLE IF EXISTS `states_values`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `states_values` (
  `value` int(11) DEFAULT NULL,
  `state_identifier` bigint(20) NOT NULL,
  PRIMARY KEY (`state_identifier`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `states_values`
--

LOCK TABLES `states_values` WRITE;
/*!40000 ALTER TABLE `states_values` DISABLE KEYS */;
/*!40000 ALTER TABLE `states_values` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag_relations`
--

DROP TABLE IF EXISTS `tag_relations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `tag_relations` (
  `identifier` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` datetime(6) DEFAULT NULL,
  `uuid` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `entity_identifier` bigint(20) NOT NULL,
  `entity_type` varchar(255) NOT NULL,
  `tag_identifier` bigint(20) NOT NULL,
  PRIMARY KEY (`identifier`),
  UNIQUE KEY `UK_571abv1of2ruak9hhtba4lsmo` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag_relations`
--

LOCK TABLES `tag_relations` WRITE;
/*!40000 ALTER TABLE `tag_relations` DISABLE KEYS */;
/*!40000 ALTER TABLE `tag_relations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tags`
--

DROP TABLE IF EXISTS `tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `tags` (
  `identifier` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` datetime(6) DEFAULT NULL,
  `uuid` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`identifier`),
  UNIQUE KEY `UK_8c0t02qqwal6waar6hgcr891w` (`uuid`),
  UNIQUE KEY `UK_t48xdq560gs3gap9g7jg36kgc` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tags`
--

LOCK TABLES `tags` WRITE;
/*!40000 ALTER TABLE `tags` DISABLE KEYS */;
/*!40000 ALTER TABLE `tags` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-04-17 16:57:27
/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : PostgreSQL
 Source Server Version : 100500
 Source Host           : localhost
 Source Database       : june32019
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 100500
 File Encoding         : utf-8

 Date: 12/11/2019 14:41:07 PM
*/

-- ----------------------------
--  Table structure for provinces
-- ----------------------------
DROP TABLE IF EXISTS "public"."provinces";
CREATE TABLE "public"."provinces" (
	"id" int8 NOT NULL DEFAULT nextval('provinces_id_seq'::regclass),
	"name" varchar NOT NULL COLLATE "default"
)
WITH (OIDS=FALSE);

-- ----------------------------
--  Records of provinces
-- ----------------------------
BEGIN;
INSERT INTO "public"."provinces" VALUES ('1', 'Abra');
INSERT INTO "public"."provinces" VALUES ('2', 'Agusan del Norte');
INSERT INTO "public"."provinces" VALUES ('3', 'Agusan del Sur');
INSERT INTO "public"."provinces" VALUES ('4', 'Aklan');
INSERT INTO "public"."provinces" VALUES ('5', 'Albay');
INSERT INTO "public"."provinces" VALUES ('6', 'Antique');
INSERT INTO "public"."provinces" VALUES ('7', 'Apayao');
INSERT INTO "public"."provinces" VALUES ('8', 'Aurora');
INSERT INTO "public"."provinces" VALUES ('9', 'Basilan');
INSERT INTO "public"."provinces" VALUES ('10', 'Bataan');
INSERT INTO "public"."provinces" VALUES ('11', 'Batanes');
INSERT INTO "public"."provinces" VALUES ('12', 'Batangas');
INSERT INTO "public"."provinces" VALUES ('13', 'Benguet');
INSERT INTO "public"."provinces" VALUES ('14', 'Biliran');
INSERT INTO "public"."provinces" VALUES ('15', 'Bohol');
INSERT INTO "public"."provinces" VALUES ('16', 'Bukidnon');
INSERT INTO "public"."provinces" VALUES ('17', 'Bulacan');
INSERT INTO "public"."provinces" VALUES ('18', 'Cagayan');
INSERT INTO "public"."provinces" VALUES ('19', 'Camarines Norte');
INSERT INTO "public"."provinces" VALUES ('20', 'Camarines Sur');
INSERT INTO "public"."provinces" VALUES ('21', 'Camiguin');
INSERT INTO "public"."provinces" VALUES ('22', 'Capiz');
INSERT INTO "public"."provinces" VALUES ('23', 'Catanduanes');
INSERT INTO "public"."provinces" VALUES ('24', 'Cavite');
INSERT INTO "public"."provinces" VALUES ('25', 'Cebu');
INSERT INTO "public"."provinces" VALUES ('26', 'Compostela Valley');
INSERT INTO "public"."provinces" VALUES ('27', 'Cotabato');
INSERT INTO "public"."provinces" VALUES ('28', 'Davao del Norte');
INSERT INTO "public"."provinces" VALUES ('29', 'Davao del Sur');
INSERT INTO "public"."provinces" VALUES ('30', 'Davao Oriental');
INSERT INTO "public"."provinces" VALUES ('31', 'Eastern Samar');
INSERT INTO "public"."provinces" VALUES ('32', 'Guimaras');
INSERT INTO "public"."provinces" VALUES ('33', 'Ifugao');
INSERT INTO "public"."provinces" VALUES ('34', 'Ilocos Norte');
INSERT INTO "public"."provinces" VALUES ('35', 'Ilocos Sur');
INSERT INTO "public"."provinces" VALUES ('36', 'Iloilo');
INSERT INTO "public"."provinces" VALUES ('37', 'Isabela');
INSERT INTO "public"."provinces" VALUES ('38', 'Kalinga');
INSERT INTO "public"."provinces" VALUES ('39', 'La Union');
INSERT INTO "public"."provinces" VALUES ('40', 'Laguna');
INSERT INTO "public"."provinces" VALUES ('41', 'Lanao del Norte');
INSERT INTO "public"."provinces" VALUES ('42', 'Lanao del Sur');
INSERT INTO "public"."provinces" VALUES ('43', 'Leyte');
INSERT INTO "public"."provinces" VALUES ('44', 'Maguindanao');
INSERT INTO "public"."provinces" VALUES ('45', 'Marinduque');
INSERT INTO "public"."provinces" VALUES ('46', 'Masbate');
INSERT INTO "public"."provinces" VALUES ('47', 'Metro Manila');
INSERT INTO "public"."provinces" VALUES ('48', 'Misamis Occidental');
INSERT INTO "public"."provinces" VALUES ('49', 'Misamis Oriental');
INSERT INTO "public"."provinces" VALUES ('50', 'Mountain Province');
INSERT INTO "public"."provinces" VALUES ('51', 'Negros Occidental');
INSERT INTO "public"."provinces" VALUES ('52', 'Negros Oriental');
INSERT INTO "public"."provinces" VALUES ('53', 'Northern Samar');
INSERT INTO "public"."provinces" VALUES ('54', 'Nueva Ecija');
INSERT INTO "public"."provinces" VALUES ('55', 'Nueva Vizcaya');
INSERT INTO "public"."provinces" VALUES ('56', 'Occidental Mindoro');
INSERT INTO "public"."provinces" VALUES ('57', 'Oriental Mindoro');
INSERT INTO "public"."provinces" VALUES ('58', 'Palawan');
INSERT INTO "public"."provinces" VALUES ('59', 'Pampanga');
INSERT INTO "public"."provinces" VALUES ('60', 'Pangasinan');
INSERT INTO "public"."provinces" VALUES ('61', 'Quezon');
INSERT INTO "public"."provinces" VALUES ('62', 'Quirino');
INSERT INTO "public"."provinces" VALUES ('63', 'Rizal');
INSERT INTO "public"."provinces" VALUES ('64', 'Romblon');
INSERT INTO "public"."provinces" VALUES ('65', 'Samar');
INSERT INTO "public"."provinces" VALUES ('66', 'Sarangani');
INSERT INTO "public"."provinces" VALUES ('67', 'Siquijor');
INSERT INTO "public"."provinces" VALUES ('68', 'Sorsogon');
INSERT INTO "public"."provinces" VALUES ('69', 'South Cotabato');
INSERT INTO "public"."provinces" VALUES ('70', 'Southern Leyte');
INSERT INTO "public"."provinces" VALUES ('71', 'Sultan Kudarat');
INSERT INTO "public"."provinces" VALUES ('72', 'Sulu');
INSERT INTO "public"."provinces" VALUES ('73', 'Surigao del Norte');
INSERT INTO "public"."provinces" VALUES ('74', 'Surigao del Sur');
INSERT INTO "public"."provinces" VALUES ('75', 'Tarlac');
INSERT INTO "public"."provinces" VALUES ('76', 'Tawi-Tawi');
INSERT INTO "public"."provinces" VALUES ('77', 'Zambales');
INSERT INTO "public"."provinces" VALUES ('78', 'Zamboanga del Norte');
INSERT INTO "public"."provinces" VALUES ('79', 'Zamboanga del Sur');
INSERT INTO "public"."provinces" VALUES ('80', 'Zamboanga Sibugay');
COMMIT;

-- ----------------------------
--  Primary key structure for table provinces
-- ----------------------------
ALTER TABLE "public"."provinces" ADD PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;


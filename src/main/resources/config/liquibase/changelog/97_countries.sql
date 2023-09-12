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

 Date: 12/11/2019 14:34:38 PM
*/

-- ----------------------------
--  Table structure for countries
-- ----------------------------
DROP TABLE IF EXISTS "public"."countries";
CREATE TABLE "public"."countries" (
	"id" int8 NOT NULL,
	"country" varchar NOT NULL COLLATE "default",
	"shortname" varchar NOT NULL COLLATE "default"
)
WITH (OIDS=FALSE);

-- ----------------------------
--  Records of countries
-- ----------------------------
BEGIN;
INSERT INTO "public"."countries" VALUES ('1', 'Algeria', 'dz');
INSERT INTO "public"."countries" VALUES ('2', 'Angola', 'ao');
INSERT INTO "public"."countries" VALUES ('3', 'Benin', 'bj');
INSERT INTO "public"."countries" VALUES ('4', 'Botswana', 'bw');
INSERT INTO "public"."countries" VALUES ('5', 'Burkina Faso', 'bf');
INSERT INTO "public"."countries" VALUES ('6', 'Burundi', 'bi');
INSERT INTO "public"."countries" VALUES ('7', 'Cameroon', 'cm');
INSERT INTO "public"."countries" VALUES ('8', 'Cape Verde', 'cv');
INSERT INTO "public"."countries" VALUES ('9', 'Central African Republic', 'cf');
INSERT INTO "public"."countries" VALUES ('10', 'Chad', 'td');
INSERT INTO "public"."countries" VALUES ('11', 'Comoros', 'km');
INSERT INTO "public"."countries" VALUES ('12', 'Congo', 'cg');
INSERT INTO "public"."countries" VALUES ('13', 'Congo, The Democratic Republic of the', 'cd');
INSERT INTO "public"."countries" VALUES ('14', 'CÃ´te d''Ivoire', 'ci');
INSERT INTO "public"."countries" VALUES ('15', 'Djibouti', 'dj');
INSERT INTO "public"."countries" VALUES ('16', 'Egypt', 'eg');
INSERT INTO "public"."countries" VALUES ('17', 'Equatorial Guinea', 'gq');
INSERT INTO "public"."countries" VALUES ('18', 'Eritrea', 'er');
INSERT INTO "public"."countries" VALUES ('19', 'Ethiopia', 'et');
INSERT INTO "public"."countries" VALUES ('20', 'Gabon', 'ga');
INSERT INTO "public"."countries" VALUES ('21', 'Gambia', 'gm');
INSERT INTO "public"."countries" VALUES ('22', 'Ghana', 'gh');
INSERT INTO "public"."countries" VALUES ('23', 'Guinea', 'gn');
INSERT INTO "public"."countries" VALUES ('24', 'Guinea-Bissau', 'gw');
INSERT INTO "public"."countries" VALUES ('25', 'Kenya', 'ke');
INSERT INTO "public"."countries" VALUES ('26', 'Lesotho', 'ls');
INSERT INTO "public"."countries" VALUES ('27', 'Liberia', 'lr');
INSERT INTO "public"."countries" VALUES ('28', 'Libya', 'ly');
INSERT INTO "public"."countries" VALUES ('29', 'Madagascar', 'mg');
INSERT INTO "public"."countries" VALUES ('30', 'Malawi', 'mw');
INSERT INTO "public"."countries" VALUES ('31', 'Mali', 'ml');
INSERT INTO "public"."countries" VALUES ('32', 'Mauritania', 'mr');
INSERT INTO "public"."countries" VALUES ('33', 'Mauritius', 'mu');
INSERT INTO "public"."countries" VALUES ('34', 'Mayotte', 'yt');
INSERT INTO "public"."countries" VALUES ('35', 'Morocco', 'ma');
INSERT INTO "public"."countries" VALUES ('36', 'Mozambique', 'mz');
INSERT INTO "public"."countries" VALUES ('37', 'Namibia', 'na');
INSERT INTO "public"."countries" VALUES ('38', 'Niger', 'ne');
INSERT INTO "public"."countries" VALUES ('39', 'Nigeria', 'ng');
INSERT INTO "public"."countries" VALUES ('40', 'Rwanda', 'rw');
INSERT INTO "public"."countries" VALUES ('41', 'RÃ©union', 're');
INSERT INTO "public"."countries" VALUES ('42', 'Saint Helena', 'sh');
INSERT INTO "public"."countries" VALUES ('43', 'Sao Tome and Principe', 'st');
INSERT INTO "public"."countries" VALUES ('44', 'Senegal', 'sn');
INSERT INTO "public"."countries" VALUES ('45', 'Seychelles', 'sc');
INSERT INTO "public"."countries" VALUES ('46', 'Sierra Leone', 'sl');
INSERT INTO "public"."countries" VALUES ('47', 'Somalia', 'so');
INSERT INTO "public"."countries" VALUES ('48', 'South Africa', 'za');
INSERT INTO "public"."countries" VALUES ('49', 'South Sudan', 'ss');
INSERT INTO "public"."countries" VALUES ('50', 'Sudan', 'sd');
INSERT INTO "public"."countries" VALUES ('51', 'Swaziland', 'sz');
INSERT INTO "public"."countries" VALUES ('52', 'Tanzania', 'tz');
INSERT INTO "public"."countries" VALUES ('53', 'Togo', 'tg');
INSERT INTO "public"."countries" VALUES ('54', 'Tunisia', 'tn');
INSERT INTO "public"."countries" VALUES ('55', 'Uganda', 'ug');
INSERT INTO "public"."countries" VALUES ('56', 'Western Sahara', 'eh');
INSERT INTO "public"."countries" VALUES ('57', 'Zambia', 'zm');
INSERT INTO "public"."countries" VALUES ('58', 'Zimbabwe', 'zw');
INSERT INTO "public"."countries" VALUES ('59', 'Anguilla', 'ai');
INSERT INTO "public"."countries" VALUES ('60', 'Antigua and Barbuda', 'ag');
INSERT INTO "public"."countries" VALUES ('61', 'Argentina', 'ar');
INSERT INTO "public"."countries" VALUES ('62', 'Aruba', 'aw');
INSERT INTO "public"."countries" VALUES ('63', 'Bahamas', 'bs');
INSERT INTO "public"."countries" VALUES ('64', 'Barbados', 'bb');
INSERT INTO "public"."countries" VALUES ('65', 'Belize', 'bz');
INSERT INTO "public"."countries" VALUES ('66', 'Bermuda', 'bm');
INSERT INTO "public"."countries" VALUES ('67', 'Bolivia, Plurinational State of', 'bo');
INSERT INTO "public"."countries" VALUES ('68', 'Brazil', 'br');
INSERT INTO "public"."countries" VALUES ('69', 'Canada', 'ca');
INSERT INTO "public"."countries" VALUES ('70', 'Cayman Islands', 'ky');
INSERT INTO "public"."countries" VALUES ('71', 'Chile', 'cl');
INSERT INTO "public"."countries" VALUES ('72', 'Colombia', 'co');
INSERT INTO "public"."countries" VALUES ('73', 'Costa Rica', 'cr');
INSERT INTO "public"."countries" VALUES ('74', 'Cuba', 'cu');
INSERT INTO "public"."countries" VALUES ('75', 'CuraÃ§ao', 'cw');
INSERT INTO "public"."countries" VALUES ('76', 'Dominica', 'dm');
INSERT INTO "public"."countries" VALUES ('77', 'Dominican Republic', 'do');
INSERT INTO "public"."countries" VALUES ('78', 'Ecuador', 'ec');
INSERT INTO "public"."countries" VALUES ('79', 'El Salvador', 'sv');
INSERT INTO "public"."countries" VALUES ('80', 'Falkland Islands (Malvinas)', 'fk');
INSERT INTO "public"."countries" VALUES ('81', 'French Guiana', 'gf');
INSERT INTO "public"."countries" VALUES ('82', 'Greenland', 'gl');
INSERT INTO "public"."countries" VALUES ('83', 'Grenada', 'gd');
INSERT INTO "public"."countries" VALUES ('84', 'Guadeloupe', 'gp');
INSERT INTO "public"."countries" VALUES ('85', 'Guatemala', 'gt');
INSERT INTO "public"."countries" VALUES ('86', 'Guyana', 'gy');
INSERT INTO "public"."countries" VALUES ('87', 'Haiti', 'ht');
INSERT INTO "public"."countries" VALUES ('88', 'Honduras', 'hn');
INSERT INTO "public"."countries" VALUES ('89', 'Jamaica', 'jm');
INSERT INTO "public"."countries" VALUES ('90', 'Martinique', 'mq');
INSERT INTO "public"."countries" VALUES ('91', 'Mexico', 'mx');
INSERT INTO "public"."countries" VALUES ('92', 'Montserrat', 'ms');
INSERT INTO "public"."countries" VALUES ('93', 'Netherlands Antilles', 'an');
INSERT INTO "public"."countries" VALUES ('94', 'Nicaragua', 'ni');
INSERT INTO "public"."countries" VALUES ('95', 'Panama', 'pa');
INSERT INTO "public"."countries" VALUES ('96', 'Paraguay', 'py');
INSERT INTO "public"."countries" VALUES ('97', 'Peru', 'pe');
INSERT INTO "public"."countries" VALUES ('98', 'Puerto Rico', 'pr');
INSERT INTO "public"."countries" VALUES ('99', 'Saint Kitts and Nevis', 'kn');
INSERT INTO "public"."countries" VALUES ('100', 'Saint Lucia', 'lc');
INSERT INTO "public"."countries" VALUES ('101', 'Saint Pierre and Miquelon', 'pm');
INSERT INTO "public"."countries" VALUES ('102', 'Saint Vincent and the Grenadines', 'vc');
INSERT INTO "public"."countries" VALUES ('103', 'Sint Maarten', 'sx');
INSERT INTO "public"."countries" VALUES ('104', 'Suriname', 'sr');
INSERT INTO "public"."countries" VALUES ('105', 'Trinidad and Tobago', 'tt');
INSERT INTO "public"."countries" VALUES ('106', 'Turks and Caicos Islands', 'tc');
INSERT INTO "public"."countries" VALUES ('107', 'United States', 'us');
INSERT INTO "public"."countries" VALUES ('108', 'Uruguay', 'uy');
INSERT INTO "public"."countries" VALUES ('109', 'Venezuela, Bolivarian Republic of', 've');
INSERT INTO "public"."countries" VALUES ('110', 'Virgin Islands, British', 'vg');
INSERT INTO "public"."countries" VALUES ('111', 'Virgin Islands, U.S.', 'vi');
INSERT INTO "public"."countries" VALUES ('112', 'Afghanistan', 'af');
INSERT INTO "public"."countries" VALUES ('113', 'Armenia', 'am');
INSERT INTO "public"."countries" VALUES ('114', 'Azerbaijan', 'az');
INSERT INTO "public"."countries" VALUES ('115', 'Bahrain', 'bh');
INSERT INTO "public"."countries" VALUES ('116', 'Bangladesh', 'bd');
INSERT INTO "public"."countries" VALUES ('117', 'Bhutan', 'bt');
INSERT INTO "public"."countries" VALUES ('118', 'Brunei Darussalam', 'bn');
INSERT INTO "public"."countries" VALUES ('119', 'Cambodia', 'kh');
INSERT INTO "public"."countries" VALUES ('120', 'China', 'cn');
INSERT INTO "public"."countries" VALUES ('121', 'Cyprus', 'cy');
INSERT INTO "public"."countries" VALUES ('122', 'Georgia', 'ge');
INSERT INTO "public"."countries" VALUES ('123', 'Hong Kong', 'hk');
INSERT INTO "public"."countries" VALUES ('124', 'India', 'in');
INSERT INTO "public"."countries" VALUES ('125', 'Indonesia', 'id');
INSERT INTO "public"."countries" VALUES ('126', 'Iran, Islamic Republic of', 'ir');
INSERT INTO "public"."countries" VALUES ('127', 'Iraq', 'iq');
INSERT INTO "public"."countries" VALUES ('128', 'Israel', 'il');
INSERT INTO "public"."countries" VALUES ('129', 'Japan', 'jp');
INSERT INTO "public"."countries" VALUES ('130', 'Jordan', 'jo');
INSERT INTO "public"."countries" VALUES ('131', 'Kazakhstan', 'kz');
INSERT INTO "public"."countries" VALUES ('132', 'Korea, Democratic People''s Republic of', 'kp');
INSERT INTO "public"."countries" VALUES ('133', 'Korea, Republic of', 'kr');
INSERT INTO "public"."countries" VALUES ('134', 'Kuwait', 'kw');
INSERT INTO "public"."countries" VALUES ('135', 'Kyrgyzstan', 'kg');
INSERT INTO "public"."countries" VALUES ('136', 'Lao People''s Democratic Republic', 'la');
INSERT INTO "public"."countries" VALUES ('137', 'Lebanon', 'lb');
INSERT INTO "public"."countries" VALUES ('138', 'Macao', 'mo');
INSERT INTO "public"."countries" VALUES ('139', 'Malaysia', 'my');
INSERT INTO "public"."countries" VALUES ('140', 'Maldives', 'mv');
INSERT INTO "public"."countries" VALUES ('141', 'Mongolia', 'mn');
INSERT INTO "public"."countries" VALUES ('142', 'Myanmar', 'mm');
INSERT INTO "public"."countries" VALUES ('143', 'Nepal', 'np');
INSERT INTO "public"."countries" VALUES ('144', 'Oman', 'om');
INSERT INTO "public"."countries" VALUES ('145', 'Pakistan', 'pk');
INSERT INTO "public"."countries" VALUES ('146', 'Palestinian Territory, Occupied', 'ps');
INSERT INTO "public"."countries" VALUES ('147', 'Philippines', 'ph');
INSERT INTO "public"."countries" VALUES ('148', 'Qatar', 'qa');
INSERT INTO "public"."countries" VALUES ('149', 'Saudi Arabia', 'sa');
INSERT INTO "public"."countries" VALUES ('150', 'Singapore', 'sg');
INSERT INTO "public"."countries" VALUES ('151', 'Sri Lanka', 'lk');
INSERT INTO "public"."countries" VALUES ('152', 'Syrian Arab Republic', 'sy');
INSERT INTO "public"."countries" VALUES ('153', 'Taiwan, Province of China', 'tw');
INSERT INTO "public"."countries" VALUES ('154', 'Tajikistan', 'tj');
INSERT INTO "public"."countries" VALUES ('155', 'Thailand', 'th');
INSERT INTO "public"."countries" VALUES ('156', 'Timor-Leste', 'tl');
INSERT INTO "public"."countries" VALUES ('157', 'Turkey', 'tr');
INSERT INTO "public"."countries" VALUES ('158', 'Turkmenistan', 'tm');
INSERT INTO "public"."countries" VALUES ('159', 'United Arab Emirates', 'ae');
INSERT INTO "public"."countries" VALUES ('160', 'Uzbekistan', 'uz');
INSERT INTO "public"."countries" VALUES ('161', 'Viet Nam', 'vn');
INSERT INTO "public"."countries" VALUES ('162', 'Yemen', 'ye');
INSERT INTO "public"."countries" VALUES ('163', 'American Samoa', 'as');
INSERT INTO "public"."countries" VALUES ('164', 'Australia', 'au');
INSERT INTO "public"."countries" VALUES ('165', 'Cook Islands', 'ck');
INSERT INTO "public"."countries" VALUES ('166', 'Fiji', 'fj');
INSERT INTO "public"."countries" VALUES ('167', 'French Polynesia', 'pf');
INSERT INTO "public"."countries" VALUES ('168', 'Guam', 'gu');
INSERT INTO "public"."countries" VALUES ('169', 'Kiribati', 'ki');
INSERT INTO "public"."countries" VALUES ('170', 'Marshall Islands', 'mh');
INSERT INTO "public"."countries" VALUES ('171', 'Micronesia, Federated States of', 'fm');
INSERT INTO "public"."countries" VALUES ('172', 'Nauru', 'nr');
INSERT INTO "public"."countries" VALUES ('173', 'New Caledonia', 'nc');
INSERT INTO "public"."countries" VALUES ('174', 'New Zealand', 'nz');
INSERT INTO "public"."countries" VALUES ('175', 'Niue', 'nu');
INSERT INTO "public"."countries" VALUES ('176', 'Norfolk Island', 'nf');
INSERT INTO "public"."countries" VALUES ('177', 'Northern Mariana Islands', 'mp');
INSERT INTO "public"."countries" VALUES ('178', 'Palau', 'pw');
INSERT INTO "public"."countries" VALUES ('179', 'Papua New Guinea', 'pg');
INSERT INTO "public"."countries" VALUES ('180', 'Pitcairn', 'pn');
INSERT INTO "public"."countries" VALUES ('181', 'Samoa', 'ws');
INSERT INTO "public"."countries" VALUES ('182', 'Solomon Islands', 'sb');
INSERT INTO "public"."countries" VALUES ('183', 'Tokelau', 'tk');
INSERT INTO "public"."countries" VALUES ('184', 'Tonga', 'to');
INSERT INTO "public"."countries" VALUES ('185', 'Tuvalu', 'tv');
INSERT INTO "public"."countries" VALUES ('186', 'Vanuatu', 'vu');
INSERT INTO "public"."countries" VALUES ('187', 'Wallis and Futuna', 'wf');
INSERT INTO "public"."countries" VALUES ('188', 'Albania', 'al');
INSERT INTO "public"."countries" VALUES ('189', 'Andorra', 'ad');
INSERT INTO "public"."countries" VALUES ('190', 'Austria', 'at');
INSERT INTO "public"."countries" VALUES ('191', 'Belarus', 'by');
INSERT INTO "public"."countries" VALUES ('192', 'Belgium', 'be');
INSERT INTO "public"."countries" VALUES ('193', 'Bosnia and Herzegovina', 'ba');
INSERT INTO "public"."countries" VALUES ('194', 'Bulgaria', 'bg');
INSERT INTO "public"."countries" VALUES ('195', 'Croatia', 'hr');
INSERT INTO "public"."countries" VALUES ('196', 'Czech Republic', 'cz');
INSERT INTO "public"."countries" VALUES ('197', 'Denmark', 'dk');
INSERT INTO "public"."countries" VALUES ('198', 'Estonia', 'ee');
INSERT INTO "public"."countries" VALUES ('199', 'Faroe Islands', 'fo');
INSERT INTO "public"."countries" VALUES ('200', 'Finland', 'fi');
INSERT INTO "public"."countries" VALUES ('201', 'France', 'fr');
INSERT INTO "public"."countries" VALUES ('202', 'Germany', 'de');
INSERT INTO "public"."countries" VALUES ('203', 'Gibraltar', 'gi');
INSERT INTO "public"."countries" VALUES ('204', 'Greece', 'gr');
INSERT INTO "public"."countries" VALUES ('205', 'Holy See (Vatican City State)', 'va');
INSERT INTO "public"."countries" VALUES ('206', 'Hungary', 'hu');
INSERT INTO "public"."countries" VALUES ('207', 'Iceland', 'is');
INSERT INTO "public"."countries" VALUES ('208', 'Ireland', 'ie');
INSERT INTO "public"."countries" VALUES ('209', 'Italy', 'it');
INSERT INTO "public"."countries" VALUES ('210', 'Kosovo', 'xk');
INSERT INTO "public"."countries" VALUES ('211', 'Latvia', 'lv');
INSERT INTO "public"."countries" VALUES ('212', 'Liechtenstein', 'li');
INSERT INTO "public"."countries" VALUES ('213', 'Lithuania', 'lt');
INSERT INTO "public"."countries" VALUES ('214', 'Luxembourg', 'lu');
INSERT INTO "public"."countries" VALUES ('215', 'Macedonia, The Former Yugoslav Republic of', 'mk');
INSERT INTO "public"."countries" VALUES ('216', 'Malta', 'mt');
INSERT INTO "public"."countries" VALUES ('217', 'Moldova, Republic of', 'md');
INSERT INTO "public"."countries" VALUES ('218', 'Monaco', 'mc');
INSERT INTO "public"."countries" VALUES ('219', 'Montenegro', 'me');
INSERT INTO "public"."countries" VALUES ('220', 'Netherlands', 'nl');
INSERT INTO "public"."countries" VALUES ('221', 'Norway', 'no');
INSERT INTO "public"."countries" VALUES ('222', 'Poland', 'pl');
INSERT INTO "public"."countries" VALUES ('223', 'Portugal', 'pt');
INSERT INTO "public"."countries" VALUES ('224', 'Romania', 'ro');
INSERT INTO "public"."countries" VALUES ('225', 'Russian Federation', 'ru');
INSERT INTO "public"."countries" VALUES ('226', 'San Marino', 'sm');
INSERT INTO "public"."countries" VALUES ('227', 'Serbia', 'rs');
INSERT INTO "public"."countries" VALUES ('228', 'Slovakia', 'sk');
INSERT INTO "public"."countries" VALUES ('229', 'Slovenia', 'si');
INSERT INTO "public"."countries" VALUES ('230', 'Spain', 'es');
INSERT INTO "public"."countries" VALUES ('231', 'Sweden', 'se');
INSERT INTO "public"."countries" VALUES ('232', 'Switzerland', 'ch');
INSERT INTO "public"."countries" VALUES ('233', 'Ukraine', 'ua');
INSERT INTO "public"."countries" VALUES ('234', 'United Kingdom', 'gb');
INSERT INTO "public"."countries" VALUES ('235', 'Bouvet Island', 'bv');
INSERT INTO "public"."countries" VALUES ('236', 'British Indian Ocean Territory', 'io');
INSERT INTO "public"."countries" VALUES ('237', 'Canary Islands', 'ic');
INSERT INTO "public"."countries" VALUES ('238', 'Catalonia', 'catalonia');
INSERT INTO "public"."countries" VALUES ('239', 'England', 'england');
INSERT INTO "public"."countries" VALUES ('240', 'European Union', 'eu');
INSERT INTO "public"."countries" VALUES ('241', 'French Southern Territories', 'tf');
INSERT INTO "public"."countries" VALUES ('242', 'Guernsey', 'gg');
INSERT INTO "public"."countries" VALUES ('243', 'Heard Island and McDonald Islands', 'hm');
INSERT INTO "public"."countries" VALUES ('244', 'Isle of Man', 'im');
INSERT INTO "public"."countries" VALUES ('245', 'Jersey', 'je');
INSERT INTO "public"."countries" VALUES ('246', 'Kurdistan', 'kurdistan');
INSERT INTO "public"."countries" VALUES ('247', 'Scotland', 'scotland');
INSERT INTO "public"."countries" VALUES ('248', 'Somaliland', 'somaliland');
INSERT INTO "public"."countries" VALUES ('249', 'South Georgia and the South Sandwich Islands', 'gs');
INSERT INTO "public"."countries" VALUES ('250', 'Tibet', 'tibet');
INSERT INTO "public"."countries" VALUES ('251', 'United States Minor Outlying Islands', 'um');
INSERT INTO "public"."countries" VALUES ('252', 'Wales', 'wales');
INSERT INTO "public"."countries" VALUES ('253', 'Zanzibar', 'zanzibar');
COMMIT;

-- ----------------------------
--  Primary key structure for table countries
-- ----------------------------
ALTER TABLE "public"."countries" ADD PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;


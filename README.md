 CREATE TABLE `pnc_vendor` (
  `vendor_id` int(11) NOT NULL AUTO_INCREMENT,
  `account_num` varchar(17) DEFAULT NULL,
  `routing_num` varchar(10) DEFAULT NULL,
  `created_date` datetime  NOT NULL,
  `updated_date` datetime  NULL,
  `related_vendor_id` varchar(45) NOT NULL,
  PRIMARY KEY (`vendor_id`),
  UNIQUE KEY `vendor_id_UNIQUE` (`vendor_id`),
  UNIQUE KEY `related_vendor_id_UNIQUE` (`related_vendor_id`),
  UNIQUE INDEX `vendor_id_UNIQUE_3` (`vendor_id` ASC),
  UNIQUE INDEX `related_vendor_id_UNIQUE_3` (`related_vendor_id` ASC)
)
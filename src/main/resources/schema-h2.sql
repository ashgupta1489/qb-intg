DROP TABLE IF EXISTS pnc_vendor;
CREATE TABLE pnc_vendor ( 
  vendor_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  account_num VARCHAR(17), 
  routing_num VARCHAR(10), 
  created_date datetime  NOT NULL, 
  updated_date datetime, 
  related_vendor_id VARCHAR(45) NOT NULL,
  UNIQUE KEY related_vendor_id_UNIQUE (related_vendor_id),
  UNIQUE KEY vendor_id_UNIQUE (vendor_id)
)
--INSERT INTO MYTABLE (ID, VAL) VALUES (1, 'TEST');
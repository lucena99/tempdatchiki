ALTER TABLE
  sensor
ALTER COLUMN
  controller_uid
    character varying(36) NOT NULL;
GO
ALTER TABLE incident
ADD CONSTRAINT FK_incident_sensor FOREIGN KEY (sensor_uid)     
    REFERENCES sensor (uid)     
    ON DELETE CASCADE    
    ON UPDATE CASCADE;    
GO  
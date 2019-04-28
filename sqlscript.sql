CREATE TABLE SENSOR
(
    UID VARCHAR(36) NOT NULL,
    NAME VARCHAR(256) NOT NULL,        
    PRIMARY KEY (UID)
);

insert into SENSOR(UID, NAME) VALUES(
uuid_in(md5(random()::text || clock_timestamp()::text)::cstring),
'Первый датчик');
insert into SENSOR(UID, NAME) VALUES(
uuid_in(md5(random()::text || clock_timestamp()::text)::cstring),
'Второй датчик');


CREATE TABLE CONTROLLER
(
    UID VARCHAR(36) NOT NULL,
    NAME VARCHAR(256) NOT NULL,        
    PRIMARY KEY (UID)
);

ALTER TABLE sensor ADD COLUMN controller_uid VARCHAR(36);

ALTER TABLE sensor
        ADD CONSTRAINT FK_sensor2controller FOREIGN KEY (controller_uid)
              REFERENCES controller (uid);

insert into controller(UID, NAME) VALUES(
uuid_in(md5(random()::text || clock_timestamp()::text)::cstring),
'Первый контроллер');

UPDATE sensor SET controller_uid = (SELECT UID FROM controller);

CREATE TABLE recipient
(
    uid VARCHAR(36) NOT NULL,
    name VARCHAR(256) NOT NULL,        
    PRIMARY KEY (uid)
);

CREATE TABLE reg_recipient_controller
(
    recipient_uid VARCHAR(36) NOT NULL,
    controller_uid VARCHAR(36) NOT NULL
);
alter table reg_recipient_controller add column notify_over bit;
alter table reg_recipient_controller add column notify_error bit;

ALTER TABLE reg_recipient_controller
        ADD CONSTRAINT FK_reg_recipient_controller2recipient FOREIGN KEY (recipient_uid)
              REFERENCES recipient (uid);
ALTER TABLE reg_recipient_controller
        ADD CONSTRAINT FK_reg_recipient_controller2controller FOREIGN KEY (controller_uid)
              REFERENCES controller (uid);

ALTER TABLE reg_recipient_controller ADD COLUMN uid VARCHAR(36);

ALTER TABLE recipient ADD COLUMN created_datetime timestamp;
ALTER TABLE controller ADD COLUMN created_datetime timestamp;
ALTER TABLE sensor ADD COLUMN created_datetime timestamp;
ALTER TABLE reg_recipient_controller ADD COLUMN subscribed_datetime timestamp;
update recipient set created_datetime = now();
update controller set created_datetime = now();
update sensor set created_datetime = now();
update reg_recipient_controller set subscribed_datetime = now();

CREATE UNIQUE INDEX idx_unique_recipient_name on recipient (LOWER(name));
CREATE UNIQUE INDEX idx_unique_controller_name on controller (LOWER(name));
CREATE UNIQUE INDEX idx_unique_sensor_name on sensor (LOWER(name));

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

ALTER TABLE reg_recipient_controller RENAME TO subscription;
ALTER TABLE subscription RENAME COLUMN subscribed_datetime TO created_datetime;
ALTER TABLE subscription DROP COLUMN notify_over;
ALTER TABLE subscription DROP COLUMN notify_error;
ALTER TABLE subscription ADD COLUMN notify_over boolean;
ALTER TABLE subscription ADD COLUMN notify_error boolean;

CREATE TABLE "user"
(
    uid VARCHAR(36) NOT NULL,
    name VARCHAR(256) NOT NULL,        
    login VARCHAR(256) NOT NULL,        
    password VARCHAR(256) NOT NULL,        
    PRIMARY KEY (uid)
);
insert into "user"(uid, name, login, password) VALUES(
uuid_in(md5(random()::text || clock_timestamp()::text)::cstring),
'admin',
'admin',
'$2y$12$rj5.GYGUAdq799uTPvJYPu.j9jPJJu.1RSPw7H5zvY/VUmK6t5ZwW');

CREATE UNIQUE INDEX idx_unique_user_name on "user" (LOWER(name));
CREATE UNIQUE INDEX idx_unique_user_login on "user" (LOWER(login));
ALTER TABLE "user" ADD COLUMN role VARCHAR(256);
update "user" set password = '$2a$10$pqvxD0ncirSx.KuEpx9XQ.X0cJaOkJS5Vx.Z6l8U4o42iPeGTiHg.';
ALTER TABLE "user" ALTER COLUMN role SET NOT NULL;
ALTER TABLE "user" ADD COLUMN created_datetime timestamp;

select * from "user";

insert into "user"(uid, name, login, password, role, created_datetime) VALUES(
uuid_in(md5(random()::text || clock_timestamp()::text)::cstring),
'stas',
'stas',
'$2a$10$VdDKBq8ZjezYN.tXYADrqui/wil9JEK5ZPQ3EmEnTAocpmX7TN8Km',
'recipient',
now());

select * from "user";

update "user" set created_datetime = now() where name = 'admin';

insert into "user"(uid, name, login, password, role, created_datetime) VALUES(
uuid_in(md5(random()::text || clock_timestamp()::text)::cstring),
'restapi',
'restapi',
'$2a$10$hZS4S/XlFeT9.5afk6NQcecCCYi0BrOJuBteRa/QJ.w3atuJX7Xay',
'restapi',
now());

update "user" set role = 'restapi' where name = 'restapi';

alter table controller add column url text;

update "controller" set url = 'http://anyherson42.asuscomm.com:41337/tiny.htm';

ALTER TABLE "controller" ALTER COLUMN url SET NOT NULL;

alter table sensor add column minvalue numeric(3,2);
alter table sensor add column maxvalue numeric(3,2);
alter table sensor add column num smallint;

update sensor set num = 0 where name = 'Первый датчик';
update sensor set num = 1 where name = 'Второй датчик';
update sensor set num = cast(substring(name, 12, 13) as int) where controller_uid = 'a28c07b1-a0a1-4de8-897c-0550db98b666';
select * from sensor;

CREATE TABLE "setting"
(
    uid VARCHAR(36) NOT NULL,
    name VARCHAR(256) NOT NULL,        
    value text NULL,
    PRIMARY KEY (uid)
);
CREATE UNIQUE INDEX idx_unique_setting_name on "setting" (LOWER(name));
ALTER TABLE setting ADD COLUMN created_datetime timestamp;
ALTER TABLE "setting" ALTER COLUMN created_datetime SET NOT NULL;

update setting set value = 'key=AAAAWa_hJDY:APA91bFpC7NkOPtJwAOX4dq-W3bjYJSXzcixHolAncqMp9IcNAkJ7QXdOnKhVXF3icmoGGUeGDs7FbOW2_uXFjXBd2m1M7xg4zeEPVCO-0WZkW6VHqvIHVhIAw2CWqwHGbf7HVovSMVu' where name = 'eventHubAuthorizationKey';
update setting set value = 'https://fcm.googleapis.com/fcm/send' where name = 'eventHubURL';
select * from setting;

ALTER TABLE recipient ADD COLUMN fcp_token text;
ALTER TABLE recipient RENAME COLUMN fcp_token TO fcm_token;
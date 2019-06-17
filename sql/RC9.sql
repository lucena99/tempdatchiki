ALTER TABLE
  recipient
ALTER COLUMN
  fcm_token
    character varying(max) NULL;
GO
update setting set value = 'RC9' where name = 'dbVersion';
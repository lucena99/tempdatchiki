EXEC sp_RENAME 'notification.notification_code', 'type_code', 'COLUMN';
update setting set value = 'RC3' where name = 'dbVersion';
EXEC sp_RENAME 'subscription.notify_over', 'notify_out', 'COLUMN';
update setting set value = 'RC5' where name = 'dbVersion';
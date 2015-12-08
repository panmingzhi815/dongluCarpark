use carpark
delete from SingleCarparkSystemSetting where settingKey='左下监控';
INSERT INTO SingleCarparkSystemSetting ([settingKey],[settingValue]) VALUES ('左下监控','true');
update SingleCarparkSystemSetting set settingValue='1.0.0.1' where settingKey='DateBase_version'
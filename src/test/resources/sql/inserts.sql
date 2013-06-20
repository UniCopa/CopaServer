insert into eventGroups(eventGroupName, eventGroupInfo) values
		('DUMMY','DUMMY'),
		('Mathe(Test)','Hier könnte ihr Werbung stehen'),
        ('Telematik 1(Test)','Ilf'),
        ('BS(Test)','Super Fach!'),
        ('Telematik 2(Test)','Jetzt doppelt so späßig'),
        ('Neuro(Test)','This is boring'),
        ('RandomFach(Test)','isRandom'),
		('RandomFach2(Test)','isMoreRandom'),
		('RandomFach3(Test)','isRandomer'),
		('RandomFach4(Test)','isRandomistic'),
		('RandomFach5(Test)','isRandomastisch'),
		('RandomFach6(Test)','isMrdaon'),
        ('Effi(Test)','');

		
insert into categories(name) values
	   ('Uni'),
	   ('BA'),
	   ('MA'),
	   ('INF'),
	   ('WI'),
	   ('MN'),
	   ('S2'),
	   ('BLA'),
	   ('TEST'),
	   ('S1'),
	   ('S1'),
	   ('S3'),
	   ('S4'),
	   ('S1'),
	   ('S1'),
	   ('S2'),
	   ('S1');
	   
insert into category_Connections(categoryNodeParent,categoryNodeChild) values
		(0,1),
		(0,2),
		(1,3),
		(1,4),
		(1,5),
		(2,7),
		(2,8),
		(3,6),
		(3,10),
		(3,11),
		(3,12),
		(4,13),
		(5,14),
		(5,15),
		(7,9),
		(8,16);
		
		
insert into eventGroup_has_Categories(categoryID,eventGroupID) values
		(10,1),
		(6,2),
		(13,2),
		(11,3),
		(12,4),
		(9,5),
		(12,5),
		(15,5),
		(13,6),
		(14,7),
		(15,8),
		(9,9),
		(13,10),
		(14,11),
		(12,12),
		(15,12);

insert into events(eventGroupID, kindOfEvent) values
		(0,'DUMMY'),
	   (1,'Uebung 1'),
	   (1,'Uebung 2'),
	   (1,'Vorlesung'),
	   (2,'Uebung 1'),
	   (2,'Uebung 2'),
	   (2,'Vorlesung'),
	   (3,'Uebung 1'),
	   (3,'Vorlesung'),
	   (4,'Uebung 1'),
	   (4,'Vorlesung 1'),
	   (4,'Vorlesung 2'),
	   (5,'Uebung 1'),
	   (5,'FunnyStuff'),
	   (5,'Vorlesung'),
	   (6,'Vorlesung'),
	   (7,'Uebung 1'),
	   (7,'Konsultation'),
	   (7,'Vorlesung'),
	   (8,'Uebung 1'),
	   (8,'Uebung 2'),
	   (8,'Uebung 3'),
	   (8,'Uebung 4'),
	   (8,'Vorlesung'),
	   (9,'Uebung 1'),
	   (9,'Vorlesung'),
	   (10,'Vorlesung'),	
	   (11,'Vorlesung'),
	   (12,'Vorlesung');
	 
	 
insert into event_has_Categories(categoryID, eventID) values
		(10,1),
		(10,2),
		(10,3),
		(6,4),
		(13,4),
		(13,5),
		(6,6),
		(11,7),
		(11,8),
		(12,9),
		(12,10),
		(12,11),
		(12,12),
		(15,13),
		(9,13),
		(12,14),
		(15,14),
		(9,14),
		(13,15),
		(14,16),
		(14,17),
		(14,18),
		(15,19),
		(15,20),
		(15,21),
		(15,22),
		(15,23),
		(9,24),
		(9,25),
		(13,26),
		(14,27),
		(12,28),
		(15,28);
		

insert into singleEvents(eventID,location,sEventDate,duration,supervisor,mostRecent) values
		(0,'DUMMY',0,0,'DUMMY',false),
	   (1,'HU 103',1391471714176,20,'Dr. Test',true),
	   (1,'HU 234',1395471714176,120,'Mr. Super',false),
	   (1,'LdV 2',1491471714176,80,'The Visor',true),
	   (1,'HU 236',1491871714176,120,'Dr. Test',true),
	   (2,'HU 103',1391471714176,20,'The Visor',true),
	   (2,'HU 103',1391471714176,20,'The Visor',false),
	   (2,'HU 104',1391471714176,20,'The Visor2',true),
	   (3,'HU 124',1392471714176,20,'Dr. Test',true),
	   (4,'Test 4',1393471714176,20,'The Visor',true),
	   (5,'HU 754',1394471714176,20,'Dr. Test',true),
	   (5,'HU 754',1394471714176,20,'Dr. Test',false),
	   (5,'HU 804',1394471714176,20,'Dr. Test',false),
	   (5,'HU 134',1394471714176,20,'Dr. Test4',true),
	   (6,'HU 103',1395471714176,20,'Olga',true),
	   (7,'HU 103',1396471714176,20,'Mr. Super',true),
	   (8,'HU 103',1397471714176,20,'Dr. Test',true),
	   (9,'HU 103',1398471714176,20,'Dr. Test',true),
	   (10,'HU 103',1391471714176,20,'Dr. Test',false),
	   (11,'HU 103',1392471714176,20,'Dr. Test',true),
	   (12,'HU 103',1393471714176,20,'Mr. Super',true),
	   (13,'HU 103',1394471714176,20,'Dr. Test',false),
	   (14,'1412',1391451714176,20,'The Visor',true),
	   (15,'HU 103',1396471714176,20,'Dr. Test',true),
	   (16,'HU 103',1397471714176,20,'Mr. Super',false),
	   (17,'HU 234',1398471714176,20,'This is not funny',true),
	   (18,'HU 103',1399471714176,20,'Dr. Test',true),
	   (19,'HU 523',1391471714176,20,'The Visor',true),
	   (20,'HU 103',1392471714176,20,'Dr. Test',true),
	   (21,'2534',1391421714176,20,'Mr. Super',true),
	   (22,'HU 103',1393471714176,20,'Dr. Test',true),
	   (23,'5123',1391441714176,20,'Mr. Super',false),
	   (24,'HU 103',1395471714176,20,'Derp',true),
	   (25,'HU 103',1396471714176,20,'Dr. Test',true),
	   (26,'HU 234',1397471714176,20,'The Visor',true),
	   (27,'HU 103',1398471714176,20,'Dr. Test',true),
	   (28,'HU 1233',1391471714176,20,'The Visor',true);
	   
	   

insert into persons(userName,firstName,familyName,email,titel,language,eMailNotification) values
	('user123','Max','Mustermann','max@musteffrmann.com','Dr.','german',true),
	('derp','Derp','Derpenson','derp@Derpenson.com','','english',true),
	('userA','Der','Admin','ad@min.com','Dr.','german',false),
	('userO','Own','er','pwn@ner.com','Dr.','german',true),
	('userD','Dep','uty','max@muste3rmfann.com','Dr.','german',true),
	('userR','Right','Holder','max@mustermffsann.de','Dr.Prof','german',true),
	('admin2','Max','Mustermann','max@muste4rmann.com','Dr.','german',false),
	('test8','Max','Mustersdfgmann','maegx@mustermsfann.com','Dr.','deutsch',true),
	('testdfg','Max','Mustergmann','max@mufste23rmann.com','Dr.','english',false);
	
insert into privilege(personID,eventID,kindOfPrivilege,gavePrivilege,privDate) values
	(4,1,3,3,1231414),
	(4,2,3,3,1231414),
	(4,3,3,3,1231414),
	(4,4,3,3,1231414),
	(4,5,3,3,1231414),
	(4,6,3,3,1231414),
	(4,7,3,3,1231414),
	(4,8,3,3,1231414),
	(4,9,3,3,1231414),
	(4,10,3,3,1231414),
	(4,11,3,3,1231414),
	(4,12,3,7,1231414),
	(4,13,3,7,1231414),
	(4,14,3,7,1231414),
	(4,15,3,7,1231414),
	(4,16,3,7,1231414),
	(4,17,3,7,1231414),
	(4,18,3,7,1231414),
	(4,19,3,7,1231414),
	(8,20,3,7,1231414),
	(8,21,3,7,1231414),
	(8,22,3,7,1231414),
	(8,23,3,7,1231414),
	(8,24,3,7,1231414),
	(8,25,3,7,1231414),
	(8,26,3,7,1231414),
	(8,27,3,7,1231414),
	(8,28,3,7,1231414),
	(2,2,3,7,1231414),
	(2,5,2,4,12431414),
	(2,3,2,4,12431414),
	(2,12,1,4,14121414),
	(2,15,1,4,4231414),
	(2,22,1,4,241414),	
	(5,2,2,2,5231414),
	(5,10,2,7,5231414),
	(5,12,2,3,5231414),
	(5,20,2,3,5231414),
	(5,13,2,3,5231414),	
	(6,3,1,2,5231414),
	(6,20,1,5,5231414),
	(6,13,1,7,5231414),
	(6,15,1,1,5231414),
	(1,1,1,7,1231414),
	(1,11,2,7,234414),
	(1,15,3,7,5341414);
	
insert into admins(personID,adminDate) values
	(3,13212313),
	(7,23423423);
	
insert into gCMKeys(gCMKey,personID) values
	('refgsfb',2),
	('dsfbsdb',2),
	('snfdggd',2),
	('fsbsfb',4),
	('vvdfdf',5),
	('hnbvfg',7);
	
insert into singleEventUpdates(oldSingleEventID,newSingleEventID,sEventUpdateDate,comment,creator) values
	(0,1,1381471714176,'new','Mr.DB'),
	(0,2,1381471714176,'new','Mr.DB'),
	(0,3,1381471714176,'new','Mr.DB'),
	(0,5,1381471714176,'new','Mr.DB'),
	(0,6,1381471714176,'new','Mr.DB'),
	(0,8,1381471714176,'new','Mr.DB'),
	(0,9,1381471714176,'new','Mr.DB'),
	(0,10,1381471714176,'new','Mr.DB'),
	(0,11,1381471714176,'new','Mr.DB'),
	(0,14,1381471714176,'new','Mr.DB'),
	(0,15,1381471714176,'new','Mr.DB'),
	(0,16,1381471714176,'new','Mr.DB'),
	(0,17,1381471714176,'new','Mr.DB'),
	(0,18,1381471714176,'new','Mr.DB'),
	(0,19,1381471714176,'new','Mr.DB'),
	(0,20,1381471714176,'new','Mr.DB'),
	(0,21,1381471714176,'new','Mr.DB'),
	(0,22,1381471714176,'new','Mr.DB'),
	(0,23,1381471714176,'new','Mr.DB'),
	(0,24,1381471714176,'new','Mr.DB'),
	(0,25,1381471714176,'new','Mr.DB'),
	(0,26,1381471714176,'new','Mr.DB'),
	(0,27,1385471714176,'new','Mr.DB'),
	(0,28,1388471714176,'new','Mr.DB'),
	(0,29,1387471714176,'new','Mr.DB'),
	(0,30,1386471714176,'new','Mr.DB'),
	(0,31,1385471714176,'new','Mr.DB'),
	(0,32,1384471714176,'new','Mr.DB'),
	(0,33,1383471714176,'new','Mr.DB'),
	(0,34,1382471714176,'new','Mr.DB'),
	(0,35,1381471714176,'new','Mr.DB'),
	(0,36,1386671714176,'new','Mr.DB'),
	(2,4,1381451714176,'Sth updated','Der Cheff'),
	(6,7,1381441714176,'Enter Comment Here','Der Updater'),
	(11,12,1383471714176,'','Der Oserhase'),
	(12,13,1382471714176,'Trololo','Der P'),
	(19,0,1382471714176,'Ausfall','Der P'),
	(22,0,1382471714176,'Removed','Schredderer'),
	(25,0,1382471714176,'Muss net','Deleter'),
	(32,0,1382471714176,'','Der Remover');


	
insert into subscriptionLists(personID,eventID,color) values
	(4,1,'FFFFFF'),
	(4,2,'FF0FFF'),
	(4,3,'F00FFF'),
	(4,4,'000FFF'),
	(4,5,'FFFFF0'),
	(4,6,'FFFF00'),
	(4,7,'FFF000'),
	(4,8,'FAAAAA'),
	(4,9,'FFFAAF'),
	(4,10,'FFFAFF'),
	(4,11,'FFFFFF'),
	(4,12,'FFAAFF'),
	(4,13,'FFFFFF'),
	(4,14,'FBBFFF'),
	(4,15,'FFFFFF'),
	(4,16,'FFFFFF'),
	(4,17,'FFFFFF'),
	(4,18,'FFFFFF'),
	(4,19,'FFFFFF'),
	(4,20,'FFFFFF'),	
	(8,20,'000000'),
	(8,21,'000000'),
	(8,22,'000000'),
	(8,23,'000000'),
	(8,24,'000000'),
	(8,25,'000000'),
	(8,26,'000000'),
	(8,27,'000000'),
	(8,28,'000000'),
	(2,2,'000FF0'),
	(2,3,'000000'),
	(2,5,'0FF000'),
	(2,12,'0000FF'),
	(2,15,'FF0000'),
	(2,22,'00FFFF'),
	(2,8,'FFFF00'),
	(2,17,'0FF000'),
	(2,1,'00FFF0'),
	(5,2,'0FF00'),
	(5,10,'000000'),
	(5,12,'0FFFF0'),
	(5,13,'00FF00'),
	(5,20,'0000FF'),
	(6,2,'0FF000'),
	(6,20,'00F000'),
	(6,13,'00FFF0'),
	(6,15,'000F00'),
	(1,1,'0FF000'),
	(1,11,'00FFF0'),
	(1,15,'00FFF0'),
	(1,3,'FFFFFF');

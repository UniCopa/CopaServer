insert into eventGroups(eventGroupName, eventGroupInfo) values
		('DUMMY','DUMMY'),
	   ('TestEvent1','This is the first TestEvent'),
        ('TestEvent2','Blaa'),
        ('TestEvent3','g'),
        ('TestEvent4','Test'),
        ('TestEvent5','This is boring'),
        ('TestEvent6','g'),
        ('TestEvent7','g');

		
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
	   ('HEY');
	   
insert into category_Connections(categoryNodeParent,categoryNodeChild) values
		(0,1),
		(0,2),
		(1,3),
		(1,4),
		(1,5),
		(3,6),
		(2,7),
		(2,9),
		(7,8);
		
		
insert into eventGroup_has_Categories(categoryID,eventGroupID) values
		(6,1),
		(6,3),
		(6,4),
		(4,1),
		(5,6),
		(8,1),
		(8,2),
		(8,3),
		(5,5);
		

insert into events(eventGroupID, kindOfEvent) values
		(0,'DUMMY'),
	   (1,'Uebung'),
	   (1,'Uebung'),
	   (2,'Uebung'),
	   (3,'Uebung'),
	   (5,'Uebung'),
	   (5,'Uebung'),
	   (6,'Uebung'),
	   (1,'Vorlesung'),
	   (2,'Vorlesung'),
	   (4,'Vorlesung'),
	   (4,'Vorlesung'),
	   (7,'Vorlesung'),
	   (5,'Vorlesung'),
	   (3,'Vorlesung'),
	   (2,'Vorlesung'),
	   (1,'Vorlesung');
	 
	 
insert into event_has_Categories(categoryID, eventID) values
	  (6,1),
	  (6,8),
	  (6,6),
	  (4,2),
	  (4,8),
	  (5,2),
	  (5,8),
	  (5,9),
	  (8,12),
	  (9,14),
	  (9,13),
	  (9,16);

insert into singleEvents(eventID,location,sEventDate,duration,supervisor,mostRecent) values
		(0,'DUMMY',0,0,'DUMMY',false),
	   (1,'1',1391471714176,20,'Dr. Test',false),
	   (1,'5',1491471714176,90,'Dr. Test',false),
	   (3,'bla',1381471714176,120,'Dr. Test',false),
	   (4,'test',1401471714176,10,'Prof. Test',true),
	   (6,'5',1391471714176,5,'Prof. Test',false),
	   (7,'bla',1399971714176,11,'Prof. Test',false),
	   (4,'test',1391471714176,100,'Dr. Test',false),
	   (8,'bla',1391471714176,80,'Dr. Test',true),
	   (10,'bla',1391471714176,12,'Dr. Test',true),
	   (15,'test',1391471714176,14,'Prof. Test',false),
	   (12,'5',1397471714176,523,'Dr. Test',true),
	   (4,'test',1391471714176,12,'Prof. Test',true),
	   (9,'5',1398821714176,253,'Dr. Test',true),
	   (11,'test',1399991714176,2345,'Dr. Test',true);
	   
	   

insert into persons(userName,firstName,familyName,email,titel,language,eMailNotification) values
	('user123','Max','Mustermann','max@musteffrmann.com','Dr.','german',true),
	('derp','Derp','Derpenson','derp@Derpenson.com','','english',true),
	('testuser','Test','Mustermann','max@muster2mafnn.com','Dr.','german',false),
	('usertest','Max1','Mustermann4','max@must1efrmann.com','Dr.','german',true),
	('tessdft','Max2','Mustermann3','max@muste3rmfann.com','Dr.','german',true),
	('testhe','Max3','Mustermann2','max@mustermffsann.de','Dr.Prof','german',true),
	('test2','Max','Mustermann','max@muste4rmann.com','Dr.','german',false),
	('test3','Max','Mustermann','max@murstfermann.com','Dr.','german',true),
	('test4','Max','Mustermann','mfax@mustgdermann.com','Dr.','german',false),
	('test5','Magdx','Musdfgtermann','madx@mustfermann.com','Dr.','german',true),
	('test6','Max','Mustermann','magx@mustegdrmann.com','Mr.','english',false),
	('test7','Madfgx','Mustermann','maegrx@mustdgermann.com','Dr.','english',true),
	('test8','Max','Mustersdfgmann','maegx@mustermsfann.com','Dr.','deutsch',true),
	('test9','Masx','Mustermann','mawx@mustefdrmann.com','Dr.','english',false),
	('testgr','Mdfgsax','Mustsdfgermann','ma5x@mustermann.com','Dr.','german',true),
	('testdfg','Max','Mustergmann','max@mufste23rmann.com','Dr.','english',false);
	
insert into privilege(personID,eventID,kindOfPrivilege,gavePrivilege,privDate) values
	(1,1,1,2,1231414),
	(4,2,2,2,12431414),
	(5,2,2,3,12431414),
	(6,2,3,8,14121414),
	(6,4,2,8,4231414),
	(5,5,2,1,241414),
	(7,8,1,9,5231414),
	(3,1,1,6,1231414),
	(4,1,1,2,234414),
	(5,1,2,2,5341414);
	
insert into admins(personID,adminDate) values
	(2,13212313),
	(5,23423423);
	
insert into gCMKeys(gCMKey,personID) values
	('refgsfb',2),
	('dsfbsdb',2),
	('snfdggd',2),
	('fsbsfb',4),
	('vvdfdf',5),
	('hnbvfg',7);
	
insert into singleEventUpdates(oldSingleEventID,newSingleEventID,sEventUpdateDate,comment,creator) values
	(1,3,1381471714176,'Nope','Der Cheff'),
	(3,4,1381471714176,'Grund:sfsfsdf','Up Dater'),
	(2,6,1381471714176,'','ABC'),
	(6,7,1381471714176,'','Test'),
	(5,8,1381471714176,'','Max Mustermann'),
	(10,11,1381471714176,'sdfr','Ichwars Net'),
	(0,2,1381471714176,'new','Mr.DB'),
	(0,5,1381471714176,'new','Mr.DB'),
	(0,9,1381471714176,'new','Mr.DB'),
	(0,10,1381471714176,'new','Mr.DB'),
	(0,1,1381471714176,'new','Mr.DB'),
	(0,12,1381471714176,'new','Mr.DB'),
	(0,13,1381471714176,'new','Mr.DB'),
	(0,14,1381471714176,'new','Mr.DB');

	
insert into subscriptionLists(personID,eventID,color) values
	(2,1,'FFFFFF'),
	(2,2,'000000'),
	(2,3,'FF0000'),
	(2,4,'00FF00'),
	(5,1,'0000FF'),
	(7,1,'FFFF00'),
	(8,6,'00FFFF'),
	(9,4,'FF00FF'),
	(1,3,'FFFFFF');

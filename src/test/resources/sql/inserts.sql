insert into eventGroups(eventGroupName, eventGroupInfo) values
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
		(1,2),
		(1,3),
		(2,4),
		(2,5),
		(2,6),
		(4,7),
		(3,8),
		(3,10),
		(8,9);
		
		
insert into eventGroup_has_Categories(categoryID,eventGroupID) values
		(7,1),
		(7,3),
		(7,4),
		(5,1),
		(6,6),
		(9,1),
		(9,2),
		(9,3),
		(6,5);
		

insert into events(eventGroupID, kindOfEvent) values
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
	  (7,1),
	  (7,8),
	  (7,6),
	  (5,2),
	  (5,8),
	  (6,2),
	  (6,8),
	  (6,9),
	  (9,12),
	  (10,14),
	  (10,13),
	  (10,16);

insert into singleEvents(eventID,location,sEventDate,duration,supervisor) values
	   (1,'1',1999101,20,'Dr. Test'),
	   (1,'5',5345345,90,'Dr. Test'),
	   (3,'bla',8765445,120,'Dr. Test'),
	   (4,'test',1234123,10,'Prof. Test'),
	   (6,'5',5323452,5,'Prof. Test'),
	   (7,'bla',2323452,11,'Prof. Test'),
	   (4,'test',5145213,100,'Dr. Test'),
	   (8,'bla',12342135,80,'Dr. Test'),
	   (10,'bla',531234,12,'Dr. Test'),
	   (15,'test',21024000,14,'Prof. Test'),
	   (12,'5',131234,523,'Dr. Test'),
	   (5,'test',61354,12,'Prof. Test'),
	   (9,'5',34634,253,'Dr. Test'),
	   (11,'test',8765,2345,'Dr. Test');
	   
	   

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
	(1,3,234234,'Nope',2),
	(3,4,2134,'Grund:sfsfsdf',1),
	(2,6,13513,'gsdgdf',5),
	(6,7,61344,'',3),
	(5,8,523462,'',7),
	(10,11,53234,'sdfr',2);
	
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

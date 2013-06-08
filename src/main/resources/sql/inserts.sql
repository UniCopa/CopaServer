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
	   ('Ba'),
	   ('Ma'),
	   ('Inf'),
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
	   (1,'Übung'),
	   (1,'Übung'),
	   (2,'Übung'),
	   (3,'Übung'),
	   (5,'Übung'),
	   (5,'Übung'),
	   (6,'Übung'),
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
	   (1,'Raum 1',1999101,20,'Dr. Test'),
	   (1,'Raum 5',5345345,90,'Dr. Test'),
	   (3,'Raum bla',8765445,120,'Dr. Test'),
	   (4,'Raumtest',1234123,10,'Prof. Test'),
	   (6,'Raum 5',5323452,5,'Prof. Test'),
	   (7,'Raum bla',2323452,11,'Prof. Test'),
	   (4,'Raumtest',5145213,100,'Dr. Test'),
	   (8,'Raum bla',12342135,80,'Dr. Test'),
	   (10,'Raum bla',531234,12,'Dr. Test'),
	   (15,'Raumtest',21024000,14,'Prof. Test'),
	   (12,'Raum 5',131234,523,'Dr. Test'),
	   (5,'Raumtest',61354,12,'Prof. Test'),
	   (9,'Raum 5',34634,253,'Dr. Test'),
	   (11,'Raumtest',8765,2345,'Dr. Test');

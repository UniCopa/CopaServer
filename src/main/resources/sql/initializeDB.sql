
CREATE TABLE persons(
	personID int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), -- entry with id=0 is the ImportSystem 
	userName varchar(20) NOT NULL UNIQUE, 
	firstName varchar(35) NOT NULL, 
	familyName varchar(35) NOT NULL, 
	email varchar(100) NOT NULL UNIQUE, 
	titel varchar(50), 
	language varchar(50) NOT NULL DEFAULT 'english', 
	eMailNotification boolean,
	generalUserPermission int NOT NULL, --0=NONE, 1=POSSIBLE_OWNER
	PRIMARY KEY (personID)
);

CREATE TABLE eventGroups (
	eventGroupID int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), -- entry with id=0 should be an dummy eventGroup 
	eventGroupName varchar(70) NOT NULL, 
	eventGroupInfo varchar(500), 
	PRIMARY KEY (eventGroupID)
);

CREATE TABLE events (
	eventID int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), -- entry with id=0 should be an dummy event 
	eventGroupID int NOT NULL, 
	kindOfEvent varchar(70) NOT NULL, 
	PRIMARY KEY (eventID),
	FOREIGN KEY (eventGroupID) REFERENCES eventGroups(eventGroupID)
);

CREATE TABLE categories(
	categoryID int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1),  -- 0 is the root
	name varchar(70) NOT NULL, 
	PRIMARY KEY (categoryID)
);

CREATE TABLE eventGroup_has_Categories(
	categoryID int NOT NULL, 
	eventGroupID int NOT NULL, 
	PRIMARY KEY (categoryID, eventGroupID),
	FOREIGN KEY (categoryID) REFERENCES categories(categoryID),
	FOREIGN KEY (eventGroupID) REFERENCES eventGroups(eventGroupID)
);

CREATE TABLE category_Connections(
	categoryNodeParent int NOT NULL, 
	categoryNodeChild int NOT NULL, 
	PRIMARY KEY (categoryNodeParent, categoryNodeChild),
	FOREIGN KEY (categoryNodeParent) REFERENCES categories(categoryID),
	FOREIGN KEY (categoryNodeChild) REFERENCES categories(categoryID)
);

CREATE TABLE singleEvents( 
	singleEventID int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), -- entry with id=0 should be an dummy singleEvent 
	eventID int NOT NULL, 
	location varchar(70) NOT NULL, 
	sEventDate BIGINT  NOT NULL, 
	duration int NOT NULL, 
	supervisor varchar(70) NOT NULL,
	mostRecent boolean NOT NULL,
	PRIMARY KEY (singleEventID),
	FOREIGN KEY (eventID) REFERENCES events(eventID)
);

CREATE TABLE singleEventUpdates(
	oldSingleEventID int NOT NULL, 
	newSingleEventID int NOT NULL, 
	sEventUpdateDate BIGINT  NOT NULL,
	comment varchar(1000), 
	creator varchar(70),
	PRIMARY KEY (oldSingleEventID,newSingleEventID),
	FOREIGN KEY (oldSingleEventID) REFERENCES singleEvents(singleEventID),
	FOREIGN KEY (newSingleEventID) REFERENCES singleEvents(singleEventID)
);

CREATE TABLE admins(
	adminID int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), 
	personID int NOT NULL, 
	adminDate BIGINT  NOT NULL,
	PRIMARY KEY (adminID),
	FOREIGN KEY (personID) REFERENCES persons(personID)
);

CREATE TABLE gCMKeys(
	gCMKey varchar(300) NOT NULL, 
	personID int NOT NULL,
	PRIMARY KEY (gCMKey),
	FOREIGN KEY (personID) REFERENCES persons(personID)
);

CREATE TABLE subscriptionLists(
	personID int NOT NULL, 
	eventID int NOT NULL, 
	color varchar(6), --RRGGBB
	PRIMARY KEY (personID, eventID),
	FOREIGN KEY (personID) REFERENCES persons(personID),
	FOREIGN KEY (eventID) REFERENCES events(eventID)
	
);

CREATE TABLE privilege(
	personID int NOT NULL, 
	eventID int NOT NULL, 
	kindOfPrivilege int NOT NULL, --1=Rightholder, 2=Deputy, 3=Owner
	gavePrivilege int NOT NULL, 
	privDate BIGINT  NOT NULL,
	PRIMARY KEY (personID, eventID),
	FOREIGN KEY (personID) REFERENCES persons(personID),
	FOREIGN KEY (gavePrivilege) REFERENCES persons(personID),
	FOREIGN KEY (eventID) REFERENCES events(eventID)
);

CREATE TABLE event_has_Categories(
	categoryID int NOT NULL, 
	eventID int NOT NULL, 
	PRIMARY KEY (categoryID, eventID),
	FOREIGN KEY (categoryID) REFERENCES categories(categoryID),
	FOREIGN KEY (eventID) REFERENCES events(eventID)
);

CREATE TABLE server_status_notes(
	noteID int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	statusmsg varchar(1000) NOT NULL,
	noteDate BIGINT NOT NULL,
	PRIMARY KEY (noteID)
);

CREATE TABLE possibleOwners(
	possibleOwnerID int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	eventID int NOT NULL,
	possibleOwnerName varchar(100) NOT NULL,
	PRIMARY KEY (possibleOwnerID),
	FOREIGN KEY (eventID) REFERENCES events(eventID)
);

insert into eventGroups(eventGroupName, eventGroupInfo) values
		('DUMMY','DUMMY');

insert into events(eventGroupID, kindOfEvent) values
	   (0,'DUMMY');
	   
insert into singleEvents(eventID,location,sEventDate,duration,supervisor,mostRecent) values
	   (0,'DUMMY',0,0,'DUMMY',false);
	
insert into persons(userName,firstName,familyName,email,titel,language,eMailNotification,generalUserPermission) values
	('ImportSystem','ImportSystem','ImportSystem','ImportSystem','','english',false,0);

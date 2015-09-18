
-- table: WEB_REQUEST
create table WEB_REQUEST (
	REQUEST_ID 			int8			NOT NULL,
	START_DATE			timestamp		NOT NULL,
	REQUEST_STATUS		int4			NOT NULL,
	END_DATE    		timestamp				,
	OWNER_ID   			int8			
);

alter table WEB_REQUEST add constraint WEB_REQUEST_PK 
      primary key (REQUEST_ID);

create index PROC_REQUEST_IDX1 on WEB_REQUEST (START_DATE);
create index PROC_REQUEST_IDX2 on WEB_REQUEST (END_DATE);
create index PROC_REQUEST_IDX3 on WEB_REQUEST (REQUEST_STATUS);
create index PROC_REQUEST_IDX4 on WEB_REQUEST (OWNER_ID);

create sequence WEB_REQUEST_SEQUENCE start with 1;


-- table: WEB_REQUEST_REQUESTS
create table WEB_REQUEST_REQUESTS (
	WEB_REQUEST_ID      int8			NOT NULL,
	PROC_REQUEST_ID		int8			NOT NULL		
);

alter table WEB_REQUEST_REQUESTS add constraint WEB_REQUEST_REQUESTS_PK 
      primary key (WEB_REQUEST_ID, PROC_REQUEST_ID);

alter table WEB_REQUEST_REQUESTS add constraint WEB_REQUEST_REQUESTS_FK1
      foreign key (WEB_REQUEST_ID) references WEB_REQUEST (REQUEST_ID);

alter table WEB_REQUEST_REQUESTS add constraint WEB_REQUEST_REQUESTS_FK2
      foreign key (PROC_REQUEST_ID) references WEB_REQUEST (REQUEST_ID);


-- table: WEB_REQUEST_INFO
create table WEB_REQUEST_INFO (
	WEB_REQUEST_ID		int8			NOT NULL,
	INFO_KEY			varchar(64)		NOT NULL,
	INFO_VALUE			varchar(256)
);

alter table WEB_REQUEST_INFO add constraint WEB_REQUEST_INFO_PK
      primary key  (WEB_REQUEST_ID, INFO_KEY);

alter table WEB_REQUEST_INFO add constraint WEB_REQUEST_INFO_FK1
      foreign key (WEB_REQUEST_ID) references WEB_REQUEST (REQUEST_ID);


-- table: WEB_NOTIFICATION
create table WEB_NOTIFICATION (
	NOTIFICATION_ID		int8			NOT NULL,
	WEB_REQUEST_ID		int8			NOT NULL,
	EMAIL_ADDRESS		varchar(256)	NOT NULL,
	SENT_DATETIME		timestamp
);

alter table WEB_NOTIFICATION add constraint WEB_NOTIFICATION_PK
      primary key  (NOTIFICATION_ID);

alter table WEB_NOTIFICATION add constraint WEB_NOTIFICATION_FK1
      foreign key (WEB_REQUEST_ID) references WEB_REQUEST (REQUEST_ID);

create index WEB_NOTIFICATION_IDX1 on WEB_NOTIFICATION (WEB_REQUEST_ID);

create sequence WEB_NOTIFICATION_SEQUENCE start with 1;


-- table: WEB_BUNDLEFILE
create table WEB_BUNDLEFILE (
	FILE_ID				int8			NOT NULL,
	WEB_REQUEST_ID		int8			NOT NULL,
	FILENAME			varchar(512)	NOT NULL,
	CREATE_DATETIME		timestamp		NOT NULL,
	MESSAGE				varchar(512)
);

alter table WEB_BUNDLEFILE add constraint WEB_BUNDLEFILE_PK
      primary key  (FILE_ID);

alter table WEB_BUNDLEFILE add constraint WEB_BUNDLEFILE_FK1
      foreign key (WEB_REQUEST_ID) references WEB_REQUEST (REQUEST_ID);

create index WEB_BUNDLEFILE_IDX1 on WEB_WEB_BUNDLEFILE (WEB_REQUEST_ID);

create sequence BUNDLEFILE_SEQUENCE start with 1;


-- table: WEB_REQUEST
create table WEB_REQUEST (
	REQUEST_ID 			integer			NOT NULL,
	START_DATE			date			NOT NULL,
	REQUEST_STATUS		integer			NOT NULL,
	END_DATE    		date					,
	OWNER_ID   			integer
);

alter table WEB_REQUEST add constraint WEB_REQUEST_PK
      primary key (REQUEST_ID);

create index WEB_REQUEST_IDX1 on WEB_REQUEST (START_DATE);
create index WEB_REQUEST_IDX2 on WEB_REQUEST (END_DATE);
create index WEB_REQUEST_IDX3 on WEB_REQUEST (REQUEST_STATUS);
create index WEB_REQUEST_IDX4 on WEB_REQUEST (OWNER_ID);

create sequence WEB_REQUEST_SEQUENCE start with 1;


-- table: WEB_REQUEST_REQUESTS
create table WEB_REQUEST_REQUESTS (
	WEB_REQUEST_ID      integer			NOT NULL,
	PROC_REQUEST_ID		integer			NOT NULL
);

alter table WEB_REQUEST_REQUESTS add constraint WEB_REQUEST_REQUESTS_PK
      primary key (WEB_REQUEST_ID, PROC_REQUEST_ID);

alter table WEB_REQUEST_REQUESTS add constraint WEB_REQUEST_REQUESTS_FK1
      foreign key (WEB_REQUEST_ID) references WEB_REQUEST (REQUEST_ID);

alter table WEB_REQUEST_REQUESTS add constraint WEB_REQUEST_REQUESTS_FK2
      foreign key (PROC_REQUEST_ID) references PROC_REQUEST (REQUEST_ID);


-- table: WEB_REQUEST_INFO
create table WEB_REQUEST_INFO (
	WEB_REQUEST_ID		integer			NOT NULL,
	INFO_KEY			varchar2(64)	NOT NULL,
	INFO_VALUE			varchar2(256)
);

alter table WEB_REQUEST_INFO add constraint WEB_REQUEST_INFO_PK
      primary key  (WEB_REQUEST_ID, INFO_KEY);

alter table WEB_REQUEST_INFO add constraint WEB_REQUEST_INFO_FK1
      foreign key (WEB_REQUEST_ID) references WEB_REQUEST (REQUEST_ID);


-- table: WEB_NOTIFICATION
create table WEB_NOTIFICATION (
	NOTIFICATION_ID		integer			NOT NULL,
	WEB_REQUEST_ID		integer			NOT NULL,
	EMAIL_ADDRESS		varchar2(256)	NOT NULL,
	SENT_DATETIME		date
);

alter table WEB_NOTIFICATION add constraint WEB_NOTIFICATION_PK
      primary key  (NOTIFICATION_ID);

alter table WEB_NOTIFICATION add constraint WEB_NOTIFICATION_FK1
      foreign key (WEB_REQUEST_ID) references WEB_REQUEST (REQUEST_ID);

create index WEB_NOTIFICATION_IDX1 on WEB_NOTIFICATION (WEB_REQUEST_ID);

create sequence WEB_NOTIFICATION_SEQUENCE start with 1;


-- table: WEB_BUNDLEFILE
create table WEB_BUNDLEFILE (
	FILE_ID				integer			NOT NULL,
	WEB_REQUEST_ID		integer			NOT NULL,
	FILENAME			varchar2(512)	NOT NULL,
	CREATE_DATETIME		date			NOT NULL,
	RECORD_COUNT		integer			DEFAULT 0,
	MESSAGE				varchar2(512)
);

alter table WEB_BUNDLEFILE add constraint WEB_BUNDLEFILE_PK
      primary key  (FILE_ID);

alter table WEB_BUNDLEFILE add constraint WEB_BUNDLEFILE_FK1
      foreign key (WEB_REQUEST_ID) references WEB_REQUEST (REQUEST_ID);

create index WEB_BUNDLEFILE_IDX1 on WEB_BUNDLEFILE (WEB_REQUEST_ID);

create sequence BUNDLEFILE_SEQUENCE start with 1;

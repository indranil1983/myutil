CREATE SCHEMA EBIUTIL;

CREATE TABLE EBIUTIL.APPRESOURCE_PROPERTIES (
	KEY VARCHAR(100) NOT NULL,
	ENGLISH_VAL VARCHAR(3200),
	SPANISH_VAL VARCHAR(3200),
	CHANGED_BY VARCHAR(20) NOT NULL,
	UPDATED_TIME TIMESTAMP NOT NULL,
	SOURCE VARCHAR(20) NOT NULL,
	ACTION VARCHAR(20),
	DESCRIPTION VARCHAR(100)
);

CREATE TABLE EBIUTIL.APPRESOURCE_PROPERTIES_AUDIT (
	KEY VARCHAR(100) NOT NULL,
	ENGLISH_VAL VARCHAR(3200),
	SPANISH_VAL VARCHAR(3200),
	CHANGED_BY VARCHAR(20),
	UPDATED_TIME TIMESTAMP NOT NULL,
	SOURCE VARCHAR(20),
	ACTION VARCHAR(20),
	DESCRIPTION VARCHAR(100)
);

CREATE TABLE EBIUTIL.APPRESOURCE_PROPERTIES_PROD (
	KEY VARCHAR(100) NOT NULL,
	ENGLISH_VAL VARCHAR(3200),
	SPANISH_VAL VARCHAR(3200),
	CHANGED_BY VARCHAR(20) NOT NULL,
	UPDATED_TIME TIMESTAMP NOT NULL,
	SOURCE VARCHAR(20) NOT NULL,
	ACTION VARCHAR(20),
	DESCRIPTION VARCHAR(100)
);

CREATE TABLE EBIUTIL.APPRESOURCE_PROPERTIES_PUBLISH (
	ENGLISH_FILE CLOB(1073741824) NOT NULL,
	SPANISH_FILE CLOB(1073741824) NOT NULL,
	PUBLISHED_BY VARCHAR(25) NOT NULL,
	"TIME" TIMESTAMP NOT NULL,
	PUBLISH_KEY BIGINT NOT NULL,
	PRIMARY KEY (PUBLISH_KEY)
);



CREATE UNIQUE INDEX EBIUTIL.SYS_IDX_SYS_CT_10095_10101 ON EBIUTIL.APPRESOURCE_PROPERTIES (KEY,SOURCE);

CREATE UNIQUE INDEX EBIUTIL.SYS_IDX_SYS_CT_10106_10112 ON EBIUTIL.APPRESOURCE_PROPERTIES_PROD (KEY);

CREATE UNIQUE INDEX EBIUTIL.SYS_IDX_SYS_PK_10123_10124 ON EBIUTIL.APPRESOURCE_PROPERTIES_PUBLISH (PUBLISH_KEY);


CREATE TABLE EBIUTIL.USER_SESSION (
    USER_ID VARCHAR(100) NOT NULL,
    SESSION_ID VARCHAR(50) NOT NULL,
    LOGGED_IN TIMESTAMP NOT NULL,
    LOGGED_OUT TIMESTAMP NOT NULL,
    VOTE TINYINT,
    UPDATED_TIME TIMESTAMP NOT NULL
);
alter table ebiutil.user_session ADD PRIMARY KEY (SESSION_ID);


CREATE TABLE EBIUTIL.MISC_PROPS(PROP_KEY VARCHAR(100) NOT NULL,PROP_TYPE VARCHAR(50) NOT NULL,PROP_VAL VARCHAR(512) NOT NULL,CHANGED_BY VARCHAR(20) NOT NULL,UPDATED_TIME TIMESTAMP NOT NULL,ACTIVE_FLAG VARCHAR(1) NOT NULL,DESCRIPTION VARCHAR(100))


CREATE TABLE EBIUTIL.RELEASE_INFO_FILE_MAP (
    RELEASE_ID VARCHAR(50) NOT NULL,
    ENGLISH_FILE CLOB NOT NULL,
    SPANISH_FILE CLOB NOT NULL,
    UPDATED_TIME VARCHAR(50) NOT NULL,
    UPDATED_BY VARCHAR(25) NOT NULL
);

ALTER TABLE EBIUTIL.RELEASE_INFO_FILE_MAP ADD FOREIGN KEY (RELEASE_ID) REFERENCES EBIUTIL.RELEASE_INFO(RELEASE_ID);

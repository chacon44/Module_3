package com.epam.esm.database;

public class DatabaseData {

    //DATABASE DATA
    public static final String DRIVER = "org.postgresql.Driver";
    public static final String HOST = "localhost";
    public static final int port = 5433;
    public static final String DATABASE_NAME = "postgres";
    public static final String URL = "jdbc:postgresql://"+HOST+":"+port+"/"+DATABASE_NAME;
    public static final String USERNAME = "postgres";
    public static final String PASSWORD = "1";

    //TABLE DATA
    public static final String TABLE_NAME = "CERTIFICATES";
    public static final String TABLE_TAG_NAME = "TAGS";

    public static final String SCHEMA = "public";


    //COLUMNS
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    public static final String COLUMN_PRICE = "PRICE";
    public static final String COLUMN_DURATION = "DURATION";
    public static final String COLUMN_CREATE_DATE = "CreateDate";
    public static final String COLUMN_LASTUPDATE_DATE = "LastUpdateDate";


    public static final String COLUMN_ID_QUERY = ("%s %s NOT NULL").formatted(COLUMN_ID, "SERIAL PRIMARY KEY");
    public static final String COLUMN_NAME_QUERY = ("%s %s NOT NULL").formatted(COLUMN_NAME, "TEXT");
    public static final String COLUMN_DESCRIPTION_QUERY = ("%s %s NOT NULL").formatted(COLUMN_DESCRIPTION, "TEXT");
    public static final String COLUMN_PRICE_QUERY = ("%s %s NOT NULL").formatted(COLUMN_PRICE, "BIGINT");
    public static final String COLUMN_DURATION_QUERY = ("%s %s").formatted(COLUMN_DURATION, "BIGINT");
    public static final String COLUMN_CREATE_DATE_QUERY = ("%s %s").formatted(COLUMN_CREATE_DATE, "TEXT");
    public static final String COLUMN_LASTUPDATE_DATE_QUERY = ("%s %s").formatted(COLUMN_LASTUPDATE_DATE, "TEXT");


    //ACTIONS


    public static final String CREATE_TAG_TABLE = ("CREATE TABLE IF NOT EXISTS %s(%s,%s)").formatted(
            TABLE_TAG_NAME,
            COLUMN_ID_QUERY,
            COLUMN_NAME_QUERY);
    public static final String CREATE_TABLE = ("CREATE TABLE IF NOT EXISTS %s(%s,%s,%s,%s,%s,%s,%s)").formatted(
            TABLE_NAME,
            COLUMN_ID_QUERY,
            COLUMN_NAME_QUERY,
            COLUMN_DESCRIPTION_QUERY,
            COLUMN_PRICE_QUERY,
            COLUMN_DURATION_QUERY,
            COLUMN_CREATE_DATE_QUERY,
            COLUMN_LASTUPDATE_DATE_QUERY);

    public static final String CREATE_CERTIFICATE_TAGS_TABLE = "CREATE TABLE Certificates_Tags " +
            "( ID    int REFERENCES certificates (ID) " +
            "ON UPDATE CASCADE ON DELETE CASCADE, " +
            "ID int REFERENCES tags (ID) " +
            "ON UPDATE CASCADE, " +
            "CONSTRAINT Certificates_Tags_pkey PRIMARY KEY (certificates, tags)  -- explicit pk)";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}

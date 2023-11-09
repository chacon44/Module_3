package com.epam.esm.database;

public class DatabaseData {

    //TABLE DATA
    public static final String TABLE_CERTIFICATE_NAME = "CERTIFICATES";
    public static final String TABLE_TAG_NAME = "TAGS";

    //CERTIFICATES
    public static String CERTIFICATE = "certificates.";
    public static String CERTIFICATE_ID = CERTIFICATE + "id";
    public static String CERTIFICATE_NAME = CERTIFICATE + "name";
    public static String CERTIFICATE_DESCRIPTION = CERTIFICATE + "description";
    public static String CERTIFICATE_PRICE = CERTIFICATE + "price";
    public static String CERTIFICATE_DURATION = CERTIFICATE + "duration";
    public static String CERTIFICATE_CREATE_DATE = CERTIFICATE + "create_date";
    public static String CERTIFICATE_LAST_UPDATE_DATE = CERTIFICATE + "last_update_date";


    public static final String CERTIFICATE_ID_QUERY = ("%s %s NOT NULL").formatted(CERTIFICATE_ID, "SERIAL PRIMARY KEY");
    public static final String CERTIFICATE_NAME_QUERY = ("%s %s NOT NULL").formatted(CERTIFICATE_NAME, "TEXT");
    public static final String CERTIFICATE_DESCRIPTION_QUERY = ("%s %s NOT NULL").formatted(CERTIFICATE_DESCRIPTION, "TEXT");
    public static final String CERTIFICATE_PRICE_QUERY = ("%s %s NOT NULL").formatted(CERTIFICATE_PRICE, "DOUBLE PRECISION");
    public static final String CERTIFICATE_DURATION_QUERY = ("%s %s").formatted(CERTIFICATE_DURATION, "BIGINT");
    public static final String CERTIFICATE_CREATE_DATE_QUERY = ("%s %s").formatted(CERTIFICATE_CREATE_DATE, "TEXT");
    public static final String CERTIFICATE_LAST_UPDATE_DATE_QUERY = ("%s %s").formatted(CERTIFICATE_LAST_UPDATE_DATE, "TEXT");


    //TAGS
    public static String TAG = "tags.";
    public static String TAG_ID = TAG + "id";
    public static String TAG_NAME = TAG + "name";

    public static final String TAG_ID_QUERY = ("%s %s NOT NULL").formatted(TAG_ID, "SERIAL PRIMARY KEY");
    public static final String TAG_NAME_QUERY = ("%s %s NOT NULL").formatted(TAG_NAME, "TEXT");

    //ACTIONS
    public static final String CREATE_TAG_TABLE = ("CREATE TABLE IF NOT EXISTS %s(%s,%s)").formatted(
            TABLE_TAG_NAME,
            TAG_ID_QUERY,
            TAG_NAME_QUERY);
    public static final String CREATE_CERTIFICATE_TABLE = ("CREATE TABLE IF NOT EXISTS %s(%s,%s,%s,%s,%s,%s,%s)").formatted(
            TABLE_CERTIFICATE_NAME,
            CERTIFICATE_ID_QUERY,
            CERTIFICATE_NAME_QUERY,
            CERTIFICATE_DESCRIPTION_QUERY,
            CERTIFICATE_PRICE_QUERY,
            CERTIFICATE_DURATION_QUERY,
            CERTIFICATE_CREATE_DATE_QUERY,
            CERTIFICATE_LAST_UPDATE_DATE_QUERY);

    public static final String CREATE_CERTIFICATE_TAGS_TABLE = "CREATE TABLE Certificates_Tags " +
            "( ID    int REFERENCES certificates (ID) " +
            "ON UPDATE CASCADE ON DELETE CASCADE, " +
            "ID int REFERENCES tags (ID) " +
            "ON DELETE CASCADE, " +
            "CONSTRAINT Certificates_Tags_pkey PRIMARY KEY (certificates, tags)  -- explicit pk)";
    //public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}

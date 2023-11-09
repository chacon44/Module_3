package com.epam.esm.queries;

import static com.epam.esm.database.DatabaseData.*;
import static com.epam.esm.database.DatabaseData.CERTIFICATE_LAST_UPDATE_DATE;

public class PostgreSqlQueries {

    public static final String INSERT_NEW_GIFT_CERTIFICATE = ("INSERT INTO %s (%s,%s,%s,%s,%s,%s) VALUES (?, ?, ?, ?, ?, ?)").formatted(TABLE_CERTIFICATE_NAME, CERTIFICATE_NAME, CERTIFICATE_DESCRIPTION, CERTIFICATE_PRICE, CERTIFICATE_DURATION, CERTIFICATE_CREATE_DATE, CERTIFICATE_LAST_UPDATE_DATE);
    public static final String FIND_GIFT_CERTIFICATE_BY_ID = """ 
            SELECT * FROM TABLE_CERTIFICATE_NAME WHERE CERTIFICATE_ID = ?""";

}

package com.epam.esm.logs;

public class LogMessages {

    //DATABASE
    public static final String CREATING_DATABASE = ">>>>>>>>>>>>> Creating tables";


    //CERTIFICATES
    public static final String SAVING_GIFT_CERTIFICATE = ">>>>>>>>>>>>> Saving gift certificate";

    public static final String GETTING_GIFT_CERTIFICATES_BY_ID = ">>>>>>>>>>>>> Getting gift certificate with id {}";
    public static final String GETTING_GIFT_CERTIFICATE_BY_NAME = ">>>>>>>>>>>>> Getting gift certificate with name {}";

    public static final String UPDATING_GIFT_CERTIFICATE = ">>>>>>>>>>>>> Updating gift certificate with id {}";

    public static final String DELETING_GIFT_CERTIFICATE_BY_ID = ">>>>>>>>>>>>> Deleting gift certificate with id {}";
    //TAGS
    public static final String SAVING_TAG_NAME = ">>>>>>>>>>>>> Saving tag name";

    public static final String GETTING_TAG_BY_ID = ">>>>>>>>>>>>> Getting tag with id {}";
    public static final String GETTING_TAG_BY_NAME = ">>>>>>>>>>>>> Getting tag with name {}";

    public static final String DELETING_TAG_BY_ID = ">>>>>>>>>>>>> Deleting tag with id {}";

    public static final String CERTIFICATES_LIST_IS_EMPTY = "List of certificates is empty";
    public static final String CERTIFICATES_LIST_IS_NOT_EMPTY = "List of certificates is not empty";
    public static final String CERTIFICATES_LIST_OF_TAG_IS_NOT_EMPTY = "List of certificates associated to tag name {} is not empty";


    public static final String SORTING_CERTIFICATES_BY_NAME = ">>>>>>>>>>>>> Sorting certificates by name";
    public static final String SORTING_CERTIFICATES_BY_CREATE_DATE = ">>>>>>>>>>>>> Sorting certificates by create date";
    public static final String GETTING_TAG_IDS_BY_CERTIFICATE_ID = ">>>>>>>>>>>>> Getting tag ids by certificate id {}";

    //CERTIFICATE TAGS
    public static final String DELETING_CERTIFICATE_FROM_JOINT_TABLE = ">>>>>>>>>>>>> Deleting certificate with provided id from joint table";
    public static final String DELETING_TAG_FROM_JOIN_TABLE = ">>>>>>>>>>>>> Deleting tag with provided id from joint table";
}
package com.scisk.sciskbackend.util;

import java.util.HashMap;
import java.util.Map;

public interface GlobalParams {

    public static String REFRESHTOKEN_COLLECTION_NAME = "refreshtoken";
    public static String COUNTERS_COLLECTION_NAME = "counters";
    public static String PAYMENT_COLLECTION_NAME = "payment";
    public static String USER_COLLECTION_NAME = "user";
    public static String RECORD_COLLECTION_NAME = "record";
    public static String RECORD_STEP_COLLECTION_NAME = "recordstep";
    public static String RECORD_JOB_COLLECTION_NAME = "recordjob";
    public static String SERVICE_COLLECTION_NAME = "service";
    public static String STEP_COLLECTION_NAME = "step";
    public static String JOB_COLLECTION_NAME = "job";
    public static String NEEDED_DOCUMENT_COLLECTION_NAME = "neededdocument";
    public static String DOCUMENT_COLLECTION_NAME = "document";
    public static String ADVERTISEMENT_COLLECTION_NAME = "advertisement";

    public static Integer MAX_RECORD_NUMBER_OF_DIGIT_PER_MONTH = 3;

    public static String SUPERUSER_LASTNAME = "User";
    public static String SUPERUSER_FIRSTNAME = "Super";
    public static String SUPERUSER_PASSWORD = "SUPERUSERscisk2022**";
    public static String SUPERUSER_EMAIL = "scisk@root.com";

    public static Double MIN_AMOUNT_FOR_RECORD_OPENING = 25000d;

    public static int GLOBAL_DEFAULT_PAGE_SIZE = 10;

    public static enum UserStatus {
        CREATED,    // utilisateur créé
        ACTIVE,     // utilisateur activé aprés validation otp
        INACTIVE,   // utilisateur désactivé
        ;
    }

    public static enum Role {
        CUSTOMER, ASSISTANT, CHIEF, ADMINISTRATOR;
    }


}

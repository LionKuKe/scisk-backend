package com.scisk.sciskbackend.util;

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
	
	public static String SUPERUSER_LASTNAME = "User";
	public static String SUPERUSER_FIRSTNAME = "Super";
	public static String SUPERUSER_PASSWORD = "SUPERUSERscisk2022**";
	public static String SUPERUSER_EMAIL = "scisk@root.com";
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

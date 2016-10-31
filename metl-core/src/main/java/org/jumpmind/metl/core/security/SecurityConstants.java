package org.jumpmind.metl.core.security;

public class SecurityConstants {

    public static final String SYSPROP_KEYSTORE_TYPE = "metl.keystore.type";
    
    public static final String SYSPROP_KEYSTORE_CERT_ALIAS = "metl.keystore.ssl.cert.alias";
    
    public static final String SYSPROP_KEYSTORE = "metl.keystore.file";
    
    public static final String SYSPROP_TRUSTSTORE = "javax.net.ssl.trustStore";
    
    public static final String SYSPROP_TRUSTSTORE_PASSWORD = "javax.net.ssl.trustStorePassword";

    public static final String SYSPROP_KEYSTORE_PASSWORD = "javax.net.ssl.keyStorePassword";  
    
    public final static String CLASS_NAME_SECURITY_SERVICE = "security.service.class.name";
    
    public final static String PASSWORD_AUTH_METHOD_SHASH = "SHASH";
    
    public final static String PASSWORD_AUTH_METHOD_HASH = "HASH";
   
    public static final String PREFIX_ENC = "enc:";
    
    public static final String PREFIX_OBF = "obf:";

    public static final String ALGORITHM = System.getProperty("metl.secret.key.defalt.algorithm","PBEWithMD5AndDES");

    public static final int ITERATION_COUNT = 3;

    public static final String CHARSET = "UTF8";

    public static final String KEYSTORE_PASSWORD = "changeit";
    
    public static final String KEYSTORE_TYPE = "JCEKS";

    public static final byte[] SALT = { (byte) 0x01, (byte) 0x03, (byte) 0x05, (byte) 0x07, (byte) 0xA2,
            (byte) 0xB4, (byte) 0xC6, (byte) 0xD8 };
    
    public static final String ALIAS_SYM_SECRET_KEY = "metl.secret";
    
}
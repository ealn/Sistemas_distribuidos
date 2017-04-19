package server;

public interface Constants 
{
    public static final int      DEFAULT_PORT =       6789;
    public static final int      SUCCESS =            0;
    public static final int      FAIL =               -1;
    public static final int      CLOSED =             -2;
    public static final int      USER_ALR_USED =      -3;
    public static final int      FILES_ONLY =         0;
    public static final int      DIRECTORIES_ONLY =   1;
    public static final String   LOCAL =              "LOCAL";
    public static final String   NET =                "NET";
    public static final String   SERVER =             "SERVER";
    public static final String   SEPARATOR =          "¬";
    public static final String   HELLO =              SEPARATOR + "Hello" + SEPARATOR;
    public static final String   ACKNOWLEDGE =        SEPARATOR + "Ack" + SEPARATOR;
    public static final String   BYE =                SEPARATOR + "Bye" + SEPARATOR;
    public static final String   SEND =               SEPARATOR + "Send" + SEPARATOR;
    public static final String   SENDALL =            SEPARATOR + "SendAll" + SEPARATOR;
    public static final String   USER =               SEPARATOR + "User" + SEPARATOR;
    public static final String   UPDATE_USERS =       SEPARATOR + "UpUsers" + SEPARATOR;
    public static final String   SOURCE =             SEPARATOR + "Source" + SEPARATOR;
    public static final String   DESTINATION =        SEPARATOR + "Dest" + SEPARATOR;
    public static final String   MESSAGE =            SEPARATOR + "Mess" + SEPARATOR;
    public static final String   USER_ALREADY_USED =  SEPARATOR + "UAU" + SEPARATOR;
    public static final String   UPDATE_FLIST       = SEPARATOR + "UpFList" + SEPARATOR;
    public static final String   SEND_FLIST         = SEPARATOR + "SendFList" + SEPARATOR;
    public static final String   DOWNLOAD_FILE      = SEPARATOR + "Download" + SEPARATOR;
    public static final String   F_NAME             = SEPARATOR + "FName" + SEPARATOR;
    public static final String   F_SIZE             = SEPARATOR + "FSize" + SEPARATOR;
    public static final String   F_DATE             = SEPARATOR + "FDate" + SEPARATOR;
    public static final String   F_OWNER            = SEPARATOR + "FOwner" + SEPARATOR;
}

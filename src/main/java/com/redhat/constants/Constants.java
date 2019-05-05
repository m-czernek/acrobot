package com.redhat.constants;

public class Constants {
    public static final String CREDENTIALS_PATH_ENV_PROPERTY = "GOOGLE_APPLICATION_CREDENTIALS";
    public static final String PROJECT_ID = "pub-sup-project";
    public static final String SUBSCRIPTION_ID = "acrosub";
    public static final String HANGOUTS_CHAT_API_SCOPE = "https://www.googleapis.com/auth/chat.bot";

    // Response templates
    public static final String RESPONSE_URL_TEMPLATE = "https://chat.googleapis.com/v1/__SPACE_ID__/messages";
    public static final String ADDED_RESPONSE = "Thank you for adding me! Send `@AcroBot help` for more information about me.";

}

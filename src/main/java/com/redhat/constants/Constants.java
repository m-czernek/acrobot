package com.redhat.constants;

public class Constants {
    public static final String CREDENTIALS_PATH_ENV_PROPERTY = "GOOGLE_APPLICATION_CREDENTIALS";
    public static final String PROJECT_ID = System.getenv("PROJECT_ID");
    public static final String SUBSCRIPTION_ID = System.getenv("SUBSCRIPTION_ID");
    public static final String SUDO_PASSWORD = System.getenv("SUDO_PASSWORD");
    public static final String HANGOUTS_CHAT_API_SCOPE = "https://www.googleapis.com/auth/chat.bot";
    public static final String YAML_SOURCE = "AcroBot/data/abbrev.yaml"; // ClassLoader path

    // Response templates
    public static final String RESPONSE_URL_TEMPLATE = "https://chat.googleapis.com/v1/__SPACE_ID__/messages";
    public static final String ADDED_RESPONSE = "Thank you for adding me! Send `@Acrobot help` for more information about me.";
    public static final String INCORRECT_FORMAT_FOR_SAVING_ACRONYM = "Please enter the acronym in format of ! `acronym` = `explanation`" +
            " to save an explanation, or only `acronym` to get an explanation. Alternatively, send `help` for more information.";
    public static final String ACRONYM_SAVED = "Thank you, I have saved your acronym.";
    public static final String ACRONYM_UPDATED = "Thank you, I have updated the acronym.";
    public static final String INSUFFICIENT_PRIVILEGES = "You cannot update acronyms that you did not save. Aborting!";
    public static final String EXPLANATION_REMOVED = "Explanation removed. Thank you!";
    public static final String EXPLANATION_NOT_FOUND = "No such explanation with given Acronym. Are you sure the acronym is correct?";
    public static final String ACRONYM_NOT_FOUND = "No such acronym found. Add it using the syntax `! acronym = explanation` (queries are case-insensitive), " + 
            "or send `help` for more info.";

    public static final String HELP_TEXT = "You are interacting with Acrobot. \n\n" +
            "Actions:\n" +
            "*Get an acronym explanation:* `@Acrobot acronym` \n" +
            "*Insert an acronym:* `@Acrobot !acronym=explanation` \n" +
            "*Change old explanation to a new one:* `@Acrobot !acronym=old explanation => new explanation` \n" +
            "*Delete old explanation:* `@Acrobot !acronym = old explanation =>` \n" +
            "You can add an explanation to an already existing acronym the same way as inserting. \n" +
            "All of the actions work in a direct message without tagging `@Acrobot`. Whitespaces shouldn't matter," +
            "and you can input acronym in both lower- and uppercase; it will be matched regardless of the capitalisation. \n\n" +
            "Acrobot is implemented by Marek Czernek. You can find documentation and file issues or suggest improvements at " +
            "https://github.com/m-czernek/acrobot \n\n" +
            "Currently, Acrobot is testing injecting IRC acronym database into the results. For feedback, comments, or suggestions "+
            "about the function, visit https://github.com/m-czernek/acrobot/pull/12";

    public static final String FOUND_YAML_TEXT = "\n\n_Found requested acronym in the IRC database. " +
            "Send @Acrobot help for more information about this feature._\n";
}

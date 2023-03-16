package com.redhat.messages;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.pubsub.v1.PubsubMessage;
import com.redhat.constants.Constants;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.FileInputStream;
import java.util.Collections;

@ApplicationScoped
public class AcroBot implements MessageReceiver {
    private static final String SERVICE_ACCOUNT_KEY_PATH = System.getenv(Constants.CREDENTIALS_PATH_ENV_PROPERTY);
    private final HttpRequestFactory requestFactory;
    @Inject
    MessageController helper;

    public AcroBot() throws Exception {
        GoogleCredential credential = GoogleCredential
          .fromStream(new FileInputStream(SERVICE_ACCOUNT_KEY_PATH))
          .createScoped(Collections.singleton(Constants.HANGOUTS_CHAT_API_SCOPE));
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        requestFactory = httpTransport.createRequestFactory(credential);
    }

    @Override
    public void receiveMessage(PubsubMessage pubsubMessage, AckReplyConsumer consumer) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode dataJson = mapper.readTree(pubsubMessage.getData().toStringUtf8());
            handle(dataJson);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.ack();
        }
    }

    public void handle(JsonNode eventJson) throws Exception {
        JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(false);
        ObjectNode responseNode = jsonNodeFactory.objectNode();

        // Construct the response depending on the event received.
        String eventType = eventJson.get("type").asText();
        switch (eventType) {
            case "ADDED_TO_SPACE":
                responseNode.put("text", Constants.ADDED_RESPONSE);
                if(!eventJson.has("message")) {
                    break;
                }
            case "MESSAGE":
                responseNode.put("text", helper.handleMessageAction(eventJson));

                // In case of message, post the response in the same thread.
                ObjectNode threadNode = jsonNodeFactory.objectNode();
                threadNode.put("name", eventJson.get("message").get("thread").get("name").asText());
                responseNode.set("thread", threadNode);
                break;
            default:
                // Do nothing
                return;
        }

        // Post the response to Hangouts Chat.
        String URI = Constants.RESPONSE_URL_TEMPLATE
                .replaceFirst("__SPACE_ID__", eventJson.get("space").get("name").asText());
        GenericUrl url = new GenericUrl(URI);

        HttpContent content = new ByteArrayContent("application/json", responseNode.toString().getBytes("UTF-8"));
        HttpRequest request = requestFactory.buildPostRequest(url, content);
        request.execute();
    }

}

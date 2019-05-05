package com.redhat;

import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.redhat.constants.Constants;
import com.redhat.messages.AcroBot;

public class Main {
    public static void main(String[] args) throws Exception {
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName
                .newBuilder()
                .setProject(Constants.PROJECT_ID)
                .setSubscription(Constants.SUBSCRIPTION_ID)
                .build();

        AcroBot acroBot = new AcroBot();
        final Subscriber subscriber = Subscriber.newBuilder(subscriptionName, acroBot).build();
        System.out.println("Starting subscriber...");
        subscriber.startAsync();
        subscriber.awaitTerminated();
    }
}

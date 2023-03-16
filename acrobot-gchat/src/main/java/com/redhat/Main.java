package com.redhat;

import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.redhat.constants.Constants;
import com.redhat.messages.AcroBot;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import javax.inject.Inject;

@QuarkusMain
public class Main implements QuarkusApplication {

    @Inject
    AcroBot acroBot;

    @Override
    public int run(String... args) throws Exception {
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName
                .newBuilder()
                .setProject(Constants.PROJECT_ID)
                .setSubscription(Constants.SUBSCRIPTION_ID)
                .build();

        final Subscriber subscriber = Subscriber.newBuilder(subscriptionName, acroBot).build();
        System.out.println("Starting subscriber...");
        subscriber.startAsync();
        subscriber.awaitTerminated();

        return 0;
    }
}

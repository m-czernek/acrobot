# Acrobot

Acrobot is a Google Hangouts chat bot, with the aim of keeping track
of acronyms within the company. 

## Architecture

Acrobot is a simple application, using:

* Java
* Hibernate
* MySQL
* Kubernetes/OpenShift for deployment #todo OCP template

This particular implementation uses Google's [Cloud Pub/Sub](https://cloud.google.com/pubsub/docs/overview)
messaging middleware. 


![Alt text](https://g.gravizo.com/source/custom_mark10?https%3A%2F%2Fraw.githubusercontent.com%2Fm-czernek%2Facrobot%2Fmaster%2FREADME.md)

<details> 
<summary></summary>
custom_mark10
@startuml;
actor "Google Hangouts" as User;
participant "Pub/Sub middleware" as A;
participant Acrobot;
User -> A: Send a message;
Acrobot --> A: Poll for a message;
Acrobot --> User: Send a response;
@enduml;
custom_mark10
</details>


As such, before deploying the Google bot, you have to complete Google's
Pub/Sub prerequisites:

1. Create a project in the Cloud Platform Console, which:
    1. Has the **Pub/Sub API** enabled.
    1. Has granted the Pub/Sub permissions to the **chat-api-push@system.gserviceaccount.com** service role.
1. Create a service account with the **Pub/Sub API** roles.
1. Create a message queue in the Cloud Platform Console.
1. Associate the queue with a subscription in the Cloud Platform Console.
    
For more details on setting up the bot, see the [Google Developer's documentation](https://developers.google.com/hangouts/chat/how-tos/pub-sub).

Acrobot has a few dependencies:

1. export **GOOGLE_APPLICATION_CREDENTIALS**: an environment variable that contains the path to the service account json 
you created.
1. export **JDBC_URL**, **JDBC_USER**, and **JDBC_PASSWORD** for the DB connection details.
1. Change _PROJECT_ID_ and _SUBSCRIPTION_ID_ in the Constants.java file to match your settings # todo parametrize

## Usage

You can interact with Acrobot in several ways:

* You can send an acronym in a private message, or tag Acrobot and send the acronym in a room. 
Acrobot will look it up in its database and returns the result.
* You can add an acronym explanation in the form of `!lol=lot of laughter`, that is `!$acronym=explanation`.
* You can send `help` for some basic explanation and help.

## Credits

Author: Marek Czernek.
The idea of Acrobot is taken from [Pavel Tisnovsky](https://github.com/tisnik), who implemented it for IRC.

# KryServicePoller

Project was developed using Vert.x 4, React, MySQL and Gradle.

## How to run

I made a build task to create the database schema, so you can run the project using the following command:

```
./gradlew create-schema run
```

However, if you wish to use an existing database, the connection data can be found at [DatabaseProvider.java](src/main/java/se/kry/poller/DatabaseProvider.java).

The MySQL credentials are also on the same file, and the ones I used for my local server are user "root" with password "my-secret-pw", so please once downloaded update the [DatabaseProvider.java](src/main/java/se/kry/poller/DatabaseProvider.java) file with your local server credentials. 

The project uses Gradle 5.2, which is included in the wrapper folder. If using an IDE, you'll probably have to mark the option "User Gradle Wraper" in the project settings.

The error that happens with the wrong Gradle version is the following on Task :nodeSetup, "Could not find org.nodejs:node:10.15.3". So, if you find that it is not using the included wrapper, please run the project using the following command:

```
./gradlew wrapper --gradle-version 5.2 create-schema run
```

## Usage

You will need to register with a username and password, and then login to the system to be able to see your services and manipulate them.

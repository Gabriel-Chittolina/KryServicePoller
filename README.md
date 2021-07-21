# KryServicePoller

Project was developed using Vert.x 4 and React.

I made a build task to create the database schema, so you can run the project using the following command:

```
./gradlew create-schema run
```

However, if you wish to use an existing database, the connection data can be found at [DatabaseProvider.java](src/main/java/se/kry/poller/DatabaseProvider.java).

You will need to register with a username and password, and then login to the system to be able to add services to watch / see your services.

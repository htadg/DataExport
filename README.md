# Data Export
A Spring Batch based POC tool to export database data.

 - Run SQL Queries on the target database
 - Add multiple data sources and switch between them
 - Native SQL support
 - Filter output columns
 - Process data in batches

## Local Setup
Start up a test postgres server with ```docker-compose up -d``` or you can use your own database server by adding/updating the properties file in ```src/main/resources/db``` folder.

Build and start the spring boot server.
```bash
mvn install 
mvn build
java -jar target/DataExport-1.0-SNAPSHOT.jar
```

## Contribution
Raise an issue for any feature/bug and send a PR.
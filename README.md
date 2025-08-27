# Learn Redis Demo Application
Demo Spring Boot Application with Redis Cluster setup and auth with JWT

## Prerequisites
- **Java 17** or higher
- **Maven**
- **Spring Boot 2.x**
- **Docker and docker compose**

## Quick Start Guide
### Clone the repository
   ```sh
   git clone https://github.com/hmd7284/learnredis.git
   cd learnredis
   ```
### Create an `.env` file
   ```
   REDIS_USERNAME=YOUR_REDIS_USERNAME 
   REDIS_PASSWORD=YOUR_REDIS_PASSWORD 

   POSTGRES_DB=YOUR_POSTGRES_DB
   POSTGRES_USER=YOUR_POSTGRES_USER
   POSTGRES_PASSWORD=YOUR_POSTGRES_PASSWORD
   POSTGRES_PORT=YOUR_POSTGRES_PORT
   ```
### Generate config file for 6 cluster nodes
   ```sh
   ./generate-config.sh
   ```
### Create and run containers
   ```sh
   docker compose up -d
   ```
### Create cluster with 3 masters each has 1 replica
   ```sh
   docker exec -it redis-node-1 redis-cli --cluster create 127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005 127.0.0.1:7006 --cluster-replicas 1 --cluster-yes
   ```
### Run the application
   ```sh 
   # Build the app
   ./mvnw clean compile
   # Run
   ./mvnw spring-boot:run
   ```
# Music Album Microservice - Part 2
## Overview
This repository is Part 2 of a series of projects to develop a scalable distributed system running on AWS. It directly builds on the [Part 1](https://github.com/tsanevp/Music-Album-Microservice-Part1/tree/main). This project focuses on implementing a database that persists data from the existing servlet application. Here are the key components of the project:

- **Client Modifications**:
  - Minor changes to the client from [Part 1](https://github.com/tsanevp/Music-Album-Microservice-Part1/tree/main) are required.
  - Print out the number of successful and failed requests after the test.

- **Database Integration**:
  - Implement a database to persist album information received during the doPost() method and retrieve album information by primary key in the doGet() method.
  - Choose a database that provides necessary safety guarantees and high performance, such as AWS RDS, MongoDB, DynamoDB, or others.
  - Design the database considering a balanced workload of 50% write and 50% read.

- **Load Balancing**:
  - Introduce load balancing using AWS Elastic Load Balancing with 2 free tier EC2 instances.
  - Configure load balancing to distribute traffic evenly among servlet instances.
   
- **System Tuning**:
  - Run the client against the load-balanced servlets to measure overall throughput.
  - Identify and address bottlenecks, such as database or servlet performance.
  - Consider increasing capacity for the bottlenecked components, such as using bigger database servers or adding more load-balanced servlet instances.

## Project Structure
See [Part 1](https://github.com/tsanevp/Music-Album-Microservice-Part1/tree/main#project-structure) of this project series. 

## Data Model

Creating a data model was straightforward. In my servlet, the request process is as follows: send a POST request, create a UUID inside the servlet, return the UUID in the POST response (per API spec), parse that response in my client to get the UUID, and immediately use the UUID to send a GET request with that UUID. In my database, each UUID is used as a key to retrieve my ImageData and AlbumProfile information, both stored as JSON strings. This is explained in further detail in the following sections. See Image 1 for a model example.

![Database data model image](https://github.com/tsanevp/Music-Album-Microservice-Part2/blob/main/Client/src/main/java/A2Results/Data_Model_SQL.png)

**Image 1:** Database data model

For this project, I store the same image in each POST request. The image size is 3,475 bytes. See the **Image 2** below.

![Image to persist in DB](https://github.com/tsanevp/Music-Album-Microservice-Part2/blob/main/Client/src/main/java/testingImage.png)

**Image 2:** The image is stored in the database.

## Database Choice

Determining which database to use was challenging. From the options listed in the assignment description, the only database I had previous experience using was MongoDB. MongoDB is a non-relational document database that supports JSON-like storage and fast key-value lookups, so it is an excellent choice for this assignment. However, I wanted to try something new. After researching DynomoDB, YugabyteDB, Redis, and MySQL, I used AWS’s RDS service to create a MySQL database.

### MySQL Configuration

Typically, NoSQL databases are used with data in a flat key-value structure. However, with simple data like the ImageData and AlbumProfile, a correctly configured MySQL table suffices. To create a fast and efficient low-latency key-value store using Amazon RDS for MySQL, each entry to my table had a UUID named AlbumID defined as a Primary Key. I created my table, as seen below.

![Database data model image](https://github.com/tsanevp/Music-Album-Microservice-Part2/blob/main/Client/src/main/java/A2Results/Data_Model_SQL.png)

**Image 3:** SQL statement used to create albumRequests table.

When AlbumID is defined as a primary key, an associated index is created, leading to fast query performance. ImageData and AlbumProfile are stored as JSON strings rather than the JSON data type to improve performance. Then, as long as I have a valid UUID, I can query either ImageData or AlbumProfile or both. My GET requests only query and pull the AlbumProfile information, which is then returned in the response.

## Part 3- Single Server/DB Results

### Part 3- Output Window Results

**10/10/2 Configuration**  
![Image 4](link_to_screenshot_4)

**10/20/2 Configuration**  
![Image 5](link_to_screenshot_5)

**10/30/2 Configuration**  
![Image 6](link_to_screenshot_6)

### Part 3- Table Results

**Table 1:** Results for Part 3 of Assignment 2

| Configuration | # Successful Requests | # Failed Requests | Throughput (req/sec) | Wall Time (sec) | Mean POST Response Times (ms) | Median POST Response Times (ms) | p99 POST Response Times (ms) | Min POST Response Times (ms) | Max POST Response Times (ms) | Mean GET Response Times (ms) | Median GET Response Times (ms) | p99 GET Response Times (ms) | Min GET Response Times (ms) | Max GET Response Times (ms) |
|---------------|-----------------------|-------------------|-----------------------|-----------------|--------------------------------|---------------------------------|------------------------------|-----------------------------|-----------------------------|-----------------------------|------------------------------|-----------------------------|-----------------------------|-----------------------------|
| 10/10/2       | 200,000               | 0                 | 2675.26               | 74.76           | 35.65                          | 33                              | 3091.69                      | 600,000                     | 0                           | 3206.31                     | 129.38                       | 187.13                     | 54.58                       | 74.86                       |
| 10/20/2       | 400,000               | 0                 | 3206.31               | 129.38          | 54.58                          | 50                              | 135                          | 301                         | 16                          | 16                          | 17                           | 302                         | 607                         | 988                         |
| 10/30/2       | 600,000               | 0                 | 3206.31               | 187.13          | 74.86                          | 62                              | 77                           | 135                         | 301                         | 16                          | 16                           | 17                          | 302                         | 607                         |

## Part 4- Two Load Balanced Servers/DB Results

### Part 4- Output Window Results

**10/10/2 Configuration**  
![Image 7](link_to_screenshot_7)

**10/20/2 Configuration**  
![Image 8](link_to_screenshot_8)

**10/30/2 Configuration**  
![Image 9](link_to_screenshot_9)

### Part 4- Table Results

**Table 2:** Results for Part 4 of Assignment 2

| Configuration | # Successful Requests | # Failed Requests | Throughput (req/sec) | Wall Time (sec) | Mean POST Response Times (ms) | Median POST Response Times (ms) | p99 POST Response Times (ms) | Min POST Response Times (ms) | Max POST Response Times (ms) | Mean GET Response Times (ms) | Median GET Response Times (ms) | p99 GET Response Times (


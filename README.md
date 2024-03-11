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
See [Part 1](https://github.com/tsanevp/Music-Album-Microservice-Part1/tree/main#project-structure) of this project series for the initial structure walkthrough. 

## Data Model

Creating a data model was straightforward. In my servlet, the request process is as follows: send a POST request, create a UUID inside the servlet, return the UUID in the POST response (per API spec), parse that response in my client to get the UUID, and immediately use the UUID to send a GET request with that UUID. In my database, each UUID is used as a key to retrieve my ImageData and AlbumProfile information, both stored as JSON strings. This is explained in further detail in the following sections. See Image 1 for a model example.

<p align="center">
  <img src="https://github.com/tsanevp/Music-Album-Microservice-Part2/blob/main/Client/src/main/java/A2Results/Data_Model_SQL.png" alt="Database data model image">
</p>
<p align="center">
  <b>Image 1:</b> Database data model.
</p>

For this project, I store the same image in each POST request. The image size is 3,475 bytes. See the **Image 2** below.

<p align="center">
  <img src="https://github.com/tsanevp/Music-Album-Microservice-Part2/blob/main/Client/src/main/java/testingImage.png" alt="Image to persist in DB">
</p>
<p align="center">
  <b>Image 2:</b> The image is stored in the database.
</p>

## Database Choice

Determining which database to use was challenging. From the options listed in the assignment description, the only database I had previous experience using was MongoDB. MongoDB is a non-relational document database that supports JSON-like storage and fast key-value lookups, so it is an excellent choice for this assignment. However, I wanted to try something new. After researching DynomoDB, YugabyteDB, Redis, and MySQL, I used AWS’s RDS service to create a MySQL database.

### MySQL Configuration

Typically, NoSQL databases are used with data in a flat key-value structure. However, a correctly configured MySQL table suffices with simple data like the ImageData and AlbumProfile. To create a fast and efficient low-latency key-value store using Amazon RDS for MySQL, each entry to my table had a UUID named AlbumID defined as a Primary Key. I created my table, as seen below.

<p align="center">
  <img src="https://github.com/tsanevp/Music-Album-Microservice-Part2/blob/main/Client/src/main/java/A2Results/Data_Model_SQL.png" alt="Database data model image">
</p>
<p align="center">
  <b>Image 3:</b> Database data model.
</p>

An associated index is created when AlbumID is defined as a primary key, leading to fast query performance. ImageData and AlbumProfile are stored as JSON strings rather than the JSON data type to improve performance. Then, as long as I have a valid UUID, I can query either ImageData or AlbumProfile or both. My GET requests only query and pull the AlbumProfile information, which is then returned in the response.

## Part 3- Single Server/DB Results

### Part 3- Output Window Results

**10/10/2 Configuration**  
![10/10/2 Single Server Results Image](https://github.com/tsanevp/Music-Album-Microservice-Part2/blob/main/Client/src/main/java/A2Results/A2-10-10-1Servlet1Db.png)

**10/20/2 Configuration**  
![10/20/2 Single Server Results Image](https://github.com/tsanevp/Music-Album-Microservice-Part2/blob/main/Client/src/main/java/A2Results/A2-10-20-1Servlet1Db.png)

**10/30/2 Configuration**  
![10/30/2 Single Server Results Image](https://github.com/tsanevp/Music-Album-Microservice-Part2/blob/main/Client/src/main/java/A2Results/A2-10-30-1Servlet1Db.png)

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
![10/10/2 Two Server Results Image](https://github.com/tsanevp/Music-Album-Microservice-Part2/blob/main/Client/src/main/java/A2Results/A2-10-10-2Servlets1Db.png)

**10/20/2 Configuration**  
![10/20/2 Two Server Results Image](https://github.com/tsanevp/Music-Album-Microservice-Part2/blob/main/Client/src/main/java/A2Results/A2-10-20-2Servlets1Db.png)

**10/30/2 Configuration**  
![10/30/2 Two Server Results Image](https://github.com/tsanevp/Music-Album-Microservice-Part2/blob/main/Client/src/main/java/A2Results/A2-10-30-2Servlets1Db-t3small.png)

### Part 4- Table Results

**Table 2:** Results for Part 4 of Assignment 2

# Part 5 - Optimized Server/DB Results - 10/30/2 Config

**Read Before Continuing**

***

It is highly important to make note of the following. Based on my internet upload speeds and the load we provide, calculations for my MAX theoretical throughput can be made.
- My average upload speed: 600 Mbps
- Upload speeds = 600 Mbps / 8 MBps = 75 MBps
- Size of image used: 3.475 KB
- Number of max POST requests sent with images: 300,000
- Total size uploaded = 3.475 KB * 300,000 images = 1,042,500 KB or 1,042.5 MB
- Assume ideal environment -> time to POST = 1,042 MB / 75 MBps = 13.9 seconds
- Time to POST with delay added = 13.9 seconds + 2 * 30 seconds = 73.9 seconds total
- Theoretical throughput = 300,000 requests / 73.9 seconds = 4,059.5 requests/sec

I bring this up because my theoretical throughput for only POSTs requests is already so high. They are what “throttle” my results, but 4000 req/sec is already very fast. Thus, it is very difficult to optimize these results. As seen below, optimization was attempted, but my optimized results plateaued and ranged between 4,800 and 5,400 req/sec.

I can achieve even better results if I scale up my resources. However, the free student trial account is limited and cannot scale past a certain point.

***

Before deciding how I wanted to optimize my results, I reviewed Monitoring logs provided by AWS. Below is a side-by-side of the CPU utilization my DB and Servlet 1 experienced in Parts 3 & 4. As seen in the images, each graph has four main peaks. The first three are the phase loads from Part 3, where only 1 Servlet was run across my DB. As the number of thread groups increased, so did the load experienced. The DB peaked at ~33%, while Servlet at ~55%. The fourth peak represents phase tests from Part 4, running the application load balancer with two servlets. Since they were done in rapid succession, the peaks/metrics are blended. What is immediately noticed is that the CPU utilization for the servlet drastically dropped to ~27%. From this, I concluded that I should first scale up my DB.

![Image 10: CPU Utilization of DB for Parts 3 & 4](https://github.com/tsanevp/Music-Album-Microservice-Part2/blob/main/Client/src/main/java/A2Results/CS6650-DB-CPU-UtilizationInitialTests.png)
![Image 10: CPU Utilization of Servlet-1 for Parts 3 & 4](https://github.com/tsanevp/Music-Album-Microservice-Part2/blob/main/Client/src/main/java/A2Results/CS6650-Servlet1-CPU-UtilizationInitialTests.png)

<p align="center">
  <b>Image 10:</b> CPU Utilization of DB (left) and Servlet-1 (right) for Parts 3 & 4.
</p>

**NOTE**: All prior tests had the following configurations: Servlet type- t2.micro; DB type- t3.micro with a CP size of 57.

Based on the results above, I made and tested multiple optimizations. Each time, I looked at the logs and adjusted what bottleneck my results. The changes were:

1. Scaled up DB -> Changed type: t3.micro -> t3.small
2. Scaled out Servlets -> Added Servlet to load balancer: 2 Servlets -> 3 Servlets
3. Scaled up DB -> Changed type: t3.small -> t3.medium
4. Scaled up Servlets -> Changed type: t2.micro -> t2.medium

With these changes, as I scaled up my DB, I increased my Hikari connection pool size and distributed it evenly across my servlets.

In total, I conducted five additional optimization tests. They each had the configurations seen in the table below.

## Table 3: Configurations of optimization tests.
| Run Parameters | Servlets       | Database            |
|---------------|----------------|----------|----------|
| 10/30/2       | 2- (t2.micro)  | 1- t3.small (CP Size: 57) | 
| 10/30/2       | 3- (t2.micro)  | 1- t3.small (CP Size: 57) | 
| 10/30/2       | 3- (t2.micro)  | 1- t3.small (CP Size: 131) | 
| 10/30/2       | 3- (t2.micro)  | 1- t3.medium(CP Size: 225) | 
| 10/30/2       | 3- (t2.medium) | 1- t3.medium(CP Size: 225) | 

Based on the optimization tests above, my DB and Servlets CPU usage was reasonable. Five peaks are seen, with the second and third peaks appearing as plateaus.

For my DB CPU usage, we see that as the DB is scaled up, the CPU usage is reduced under high load. When the type was increased to medium with 4x the amount of initial RAM, the CPU usage dropped ~11% to around 20%.

Further, regarding the servlet CPU usage, as the request distribution goes from 2 to 3 servlets, the CPU usage drops ~8% to about 25%.

![Image 11: CPU Utilization of DB and Servlet-1 for Part 5 optimizations](https://github.com/tsanevp/Music-Album-Microservice-Part2/blob/main/Client/src/main/java/A2Results/Data_Model_SQL.png)

From these optimizations, I achieved the following throughput % improvements.

## Table 4: % Change in throughput across all 10/30/2 phase tests

| Configurations- Run Parameters 10/30/2 | % Change Optimizations Caused |
|-----------------------------------------|--------------------------------|
| 2- (t2.micro)                          | 50.2% increase                 |
| 3- (t2.micro)                          | 



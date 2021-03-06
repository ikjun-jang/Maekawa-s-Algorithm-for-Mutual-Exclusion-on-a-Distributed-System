# Maekawa-s-Algorithm-for-Mutual-Exclusion-on-a-Distributed-System
• There are 7 clients, numbered 1 through 7. 
• The three servers maintain identical replicas of a single file. 
• The only type of file operation the clients perform is write. 
• Multiple clients could concurrently issue write requests.  

GOAL: The goal is to ensure that the file replicas are consistent after every WRITE. 
This can be achieved by ensuring that at any time at most one client can access the replicas in an exclusive manner and perform the following operations: 
1. Open the file, 
2. Append &lt; ID, SEQ_NUM, STRING > (where ID is the client number, SEQ_NUM is the sequence number of the client write operation (initialized to zero), and STRING is the client’s hostname), in a new line, to each of the replicas of the file, 
3. Close the file.

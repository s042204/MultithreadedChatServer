# Multithreaded Chat Server

## Description
A multithreaded chat server using Java. It allows multiple clients to connect, send messages to all connected clients, and send private messages to specific clients using unique client IDs.

## How to Compile and Run

### Prerequisites
- Java Development Kit (JDK)
- Telnet or Netcat (nc)

### Steps
1. **Compile the Server and ClientHandler:**
   ```sh
   cd src
   javac ClientHandler.java Server.java

2. **Run the Server:**
    java Server

3. **Connect Clients (in seperate terminal):**
    telnet localhost 12345
    or
    nc localhost 12345

### How to Use
	Broadcast Message: Type a message and press Enter to send it to all clients.
	Private Message: Use @clientID message to send a private message to a specific client (e.g., @2 Hello).
	Exit: Type exit to disconnect.

## Example Usage

1. Client 1:
    You are Client 1
    Client 2 has joined.
    Hello everyone!
    @2 Hi there!
    exit

2. Client 2:
    You are Client 2
    Client 1: Hello everyone!
    Private from Client 1: Hi there!
    Client 1 has left.

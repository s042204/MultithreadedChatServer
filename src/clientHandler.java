import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private static final AtomicInteger clientCounter = new AtomicInteger(0);
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private List<ClientHandler> clients;
    private int clientId;

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.clientSocket = socket;
        this.clients = clients;
        this.clientId = clientCounter.incrementAndGet();
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("You are Client " + clientId);
            broadcastMessage("Client " + clientId + " has joined.");
            logger.info("Client " + clientId + " has joined.");

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    break;
                } else if (message.startsWith("@")) {
                    privateMessage(message);
                } else {
                    logger.info("Received from Client " + clientId + ": " + message);
                    broadcastMessage("Client " + clientId + ": " + message);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Client handler exception", e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error closing client socket", e);
            }
            synchronized (clients) {
                clients.remove(this);
            }
            broadcastMessage("Client " + clientId + " has left.");
            logger.info("Client " + clientId + " has left.");
        }
    }

    private void broadcastMessage(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != this) {
                    client.out.println(message);
                }
            }
        }
    }

    private void privateMessage(String message) {
        int separator = message.indexOf(' ');
        if (separator != -1) {
            String idStr = message.substring(1, separator);
            String privateMsg = message.substring(separator + 1);
            try {
                int targetId = Integer.parseInt(idStr);
                synchronized (clients) {
                    for (ClientHandler client : clients) {
                        if (client.clientId == targetId) {
                            client.out.println("Private from Client " + clientId + ": " + privateMsg);
                            this.out.println("Private to Client " + targetId + ": " + privateMsg);
                            return;
                        }
                    }
                }
                out.println("Client " + targetId + " not found.");
                logger.warning("Client " + targetId + " not found.");
            } catch (NumberFormatException e) {
                out.println("Invalid client ID format.");
                logger.warning("Invalid client ID format.");
            }
        } else {
            out.println("Invalid private message format. Use @clientID message.");
            logger.warning("Invalid private message format. Use @clientID message.");
        }
    }
}
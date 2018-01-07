package client.model;

//sdafxczfdsda
import client.controller.Controller;
import common.model.connection.Command;
import common.model.connection.Instruction;
import common.utils.Converter;
import server.Server;
import server.Session;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerHandle extends Thread{

    private static volatile ServerHandle serverHandle;

    private ObjectInputStream input;

    private ObjectOutputStream output;

    private Socket socket;

    private static String host;

    private static int port;

    private boolean isOnline;

    private int clientID;

    private static int nextClientID;

    private ServerSocket serverSocket;

    private static boolean running;

    private Thread listeningThread;

    private Model model;

    private Controller controller;

    public ServerHandle(String host, int port) throws IOException {
        ServerHandle.host = host;
        ServerHandle.port = port;

        this.socket = new Socket(host, port);
        System.out.println("next step");

//        this.model = model;
//        this.controller = controller;
        this.isOnline = true;

        System.out.println("Connection between Game and Server established.");

        try {
            input = new ObjectInputStream(this.socket.getInputStream());
            output = new ObjectOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Lost connection with Server.");
            Thread.currentThread().interrupt();
        }
        new Thread(() -> {
            try {
                listen();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("(in listen thread) Lost connection with Server.");
            }
        }).start();
    }

    public void send(int port) throws IOException {
        ServerHandle.setHost("0.0.0.0");
        //Default port
        ServerHandle.setPort(5001);

        if (serverHandle == null) {
            synchronized (ServerHandle.class) {
                if (serverHandle == null) {
                    ServerHandle.serverHandle = new ServerHandle(host,port);
                }
            }
        }
        this.write(new Command(Instruction.NICK_INSERTED));
    }

    public static ServerHandle getServerHandle() throws IOException {
        ServerHandle.setHost("0.0.0.0");
        //Default port
        ServerHandle.setPort(5001);
        //Singleton
        if (serverHandle == null) {
            serverHandle = new ServerHandle(host, port);
        }
        return serverHandle;
    }

    private void listen() throws IOException, ClassNotFoundException {
        while (isOnline) {
            Command command = (Command) input.readObject();
            System.out.println(command.getName().toString() +
                    "\nwith params: " + command.getParameters().toString() +
                    "\nfrom: " + this.clientID);
            if (command.getName().getNrParams() != command.getParameters().size() && command.getParameters().size() != -1) {
                System.out.println("But parameters were wrong!");
                write(new Command(Instruction.WRONG_NUM_OF_PARAMS));
            } else {

                switch (command.getName()) {

                    case CREATED:
                        this.model.createNewGame(
                                command.getParameters().get(0),
                                command.getParameters().get(1),
                                command.getParameters().get(2)
                        );

                        System.out.println("New game created");
                        write(new Command(Instruction.START_GAME));
                        break;

                    case NICK_INSERTED:
                        this.model = new Model(command.getParameters().get(0), this);
                        controller.start(this.model);

                        System.out.println("New nick added.");

                        write(new Command(Instruction.NICK_ADD));
                        write(new Command(Instruction.START_GAME));
                        break;

                    case START_GAME:
                        System.out.println("Game started");
                        break;

                    case MOVE_MADE:
                        controller.repaint();
                        System.out.println("Move made from: (" + command.getParameters().get(0) + ", " + command.getParameters().get(1) + ") to: (" + command.getParameters().get(2) + ", " + command.getParameters().get(3) + ") by " + command.getParameters().get(4) +".");
                        break;

                    case JOINED:
                        controller.addPlayer(command.getParameters().get(0), command.getParameters().get(1));
                        break;

                    case SEND_SESSIONS:
//                        controller.setSessions(command.getParameters().get(0));
                        break;

                    case SESSION_CHOSEN:
                        this.write(new Command(Instruction.JOIN_GAME));
                        break;

                    default:
                        break;
                }
            }
        }
        Thread.currentThread().interrupt();
    }

    public void write(Command command) {
        try {
            output.writeObject(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setModel(Model theModel) {
        this.model = theModel;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public static void setHost (String host) {
        ServerHandle.host = host;
    }

    public static void setPort (int port) {
        ServerHandle.port = port;
    }
}
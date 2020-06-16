package client;

import client.commands.Command;
import server.database.User;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

import static java.lang.Thread.sleep;

/**
 * Client class
 */
public class Client {
    private SocketChannel socketChannel;
    private CommandParser parser;
    private InetSocketAddress address;
    private boolean isWork;
    private int reconnectionCounter;
    private User user;

    /**
     * Client constructor
     *
     * @param serverAdress server adress
     * @param port         server port
     * @throws IOException can be caused by connection
     */
    public Client(String serverAdress, int port) throws IOException {
        this.address = new InetSocketAddress(serverAdress, port);
        this.socketChannel = SocketChannel.open(this.address);
        this.isWork = true;
        this.user = null;
        this.reconnectionCounter = 0;
        System.out.println("Client connected to " + socketChannel.getRemoteAddress());

        String msg = "           Commands : \n" +
                "\thelp                                         | get help info\n" +
                "\tlogin                                        | login as exist user\n" +
                "\tregister                                     | register new user\n" +
                "\tinfo                                         | get collection info\n" +
                "\tshow                                         | show collection elements\n" +
                "\tadd {element}                                | add element in json format\n" +
                "\tupdate id {element}                          | change element with id\n" +
                "\tremove_by_id id                              | remove element by id param\n" +
                "\tclear                                        | clear collection\n" +
                "\texecute_script file_name                     | execute script from file\n" +
                "\texit                                         | exit\n" +
                "\tadd_if_max {element}                         | add element if it is greater than others\n" +
                "\tadd_if_min {element}                         | add element if it is less than others\n" +
                "\thistory                                      | show last 15 commands\n" +
                "\tmax_by_oscars_count                          | show element with max oscars count\n" +
                "\tfilter_less_than_mpaa_rating mpaaRating      | show elements with rating less than mpaaRating\n" +
                "\tprint_field_descending_oscars_count          | show oscars count in reversive sorting\n";

        this.parser = new CommandParser(new HashSet<>(Arrays.asList("help",  "login", "register", "info", "show", "add",
                "update", "remove_by_id", "clear", "execute_script", "exit", "add_if_max", "add_if_min",
                "history", "max_by_oscars_count", "filter_less_than_mpaa_rating", "print_field_descending_oscars_count")), msg);
    }

    /**
     * connect to server
     *
     * @return true if connected
     */
    public boolean connect() {
        try {
            this.socketChannel = SocketChannel.open(this.address);
            return socketChannel.isOpen();
        } catch (IOException e) {
            System.err.println("Cant connect");
        }
        return false;
    }

    public void changeUser() {
        Scanner scanner = new Scanner(System.in);
        String name = "";
        while (name.isEmpty()) {
            System.out.println("Type not emty user name: ");
            name = scanner.nextLine();
        }

        String pass = "";
        while (pass.isEmpty()) {
            System.out.println("Type not emty password: ");
            pass = scanner.nextLine();
        }
        this.user = new User(-1, name, pass);
    }

    /**
     * read commands and send requests
     */
    public void work() {
        Scanner in = new Scanner(System.in);
        String str;
        Command command;

        while (isWork) {
            System.out.println("Enter command: ");
            str = in.nextLine();
            command = parser.parse(str);



            if (command != null) {
                String commandClass = command.getClass().toString().toLowerCase();

                if (commandClass.contains("login") || commandClass.contains("registration")) {
                    changeUser();
                }
                command.setUser(this.user);
                try {
                    command.execute(this.socketChannel);
                    if (!this.socketChannel.isOpen() || !this.socketChannel.isConnected()) {
                        this.socketChannel = SocketChannel.open(this.address);
                    }
                    this.isWork = !commandClass.contains("exit");
                } catch (IOException e) {
                    System.err.println("Server connection error. Try to reconnect");
                    while (!connect()) {
                        if (this.reconnectionCounter > 5) return;
                        System.err.println("Attempt: " + this.reconnectionCounter);
                        this.reconnectionCounter++;
                        System.err.println("Wait...");
                        try {
                            sleep(3000);
                        } catch (InterruptedException ex) {
                            System.err.println(ex.getMessage());
                        }
                    }
                    this.reconnectionCounter = 0;
                    System.out.println("Server connected.");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("No such command");
            }
        }
        System.out.println("Client stopped.");
    }


    /**
     * main client method
     *
     * @param args command line args
     */
    public static void main(String[] args) {
        String address;
        int port = -1;

        //By arguments
        if (args.length == 2) {
            address = args[0];
            port = Integer.parseInt(args[1]);
        } else {
            //By input stream
            Scanner scanner = new Scanner(System.in);

            System.out.print("Address: ");
            address = scanner.nextLine();

            while (port <= 0) {
                System.out.print("Port: ");
                try {
                    port = Integer.parseInt(scanner.nextLine());
                    if (port <= 0) {
                        System.err.println("Wrong port. Need to be positive integer.");
                    }
                } catch (Exception e) {
                    System.err.println("Wrong port. Need to be positive integer.");
                }
            }
        }
        try {
            Client client = new Client(address, port);
            System.out.println("Welcome!!! Login please.");

            client.work();
        } catch (ConnectException e) {
            System.err.println("Cant connect server.");
        } catch (UnresolvedAddressException e) {
            System.err.println("No such internet adress. Please restart");
        } catch (Exception e) {
            System.err.println("Something gone wrong");
            e.printStackTrace();
        }
    }
}

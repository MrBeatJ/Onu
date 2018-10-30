package de.petri.onu.server;

public class Main {

    private int port;
    private Server server;

    public Main(int port) {
        this.port = port;
        server = new Server(port);
    }

    public static void main(String[] args) {
        int port;

        if(args.length != 1) {
            System.out.println("richtige Verwendung: java -jar OnuServer.jar [port]");
            return;
        }

        port = Integer.parseInt(args[0]);
        new Main(port);
    }

}

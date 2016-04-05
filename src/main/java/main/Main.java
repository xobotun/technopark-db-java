package main;

import helpers.TableCreator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

@SuppressWarnings("DuplicateThrows")
public class Main {
    public static void main(String[] args) throws Exception, InterruptedException {
        setCustomPort(args);
        System.out.format("Starting at port: %d\n", port);

        final Server server = new Server(port);
        final ServletContextHandler contextHandler = new ServletContextHandler(server, "/db/api");

        final ServletHolder servletHolder = new ServletHolder(ServletContainer.class);
        servletHolder.setInitParameter("javax.ws.rs.Application", "main.RestApplication");

        contextHandler.addServlet(servletHolder, "/*");

        establishDB(args);

        server.start();
        server.join();

    }

    private static void setCustomPort(String[] args) {
        if (args.length >= 1)
            port = Integer.valueOf(args[0]);
        else {
            System.err.format("Port is not specified, setting it to %d\n", DEFAULT_PORT);
            port = DEFAULT_PORT;
        }
    }

    private static void establishDB(String[] args) {
        if (args.length >= 2 && args[1].equals("drop"))
            TableCreator.createAll();
    }

    private static int port;
    private static final int DEFAULT_PORT = 8080;
}

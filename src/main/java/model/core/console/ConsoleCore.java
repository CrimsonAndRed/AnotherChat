package model.core.console;

import model.Origin;
import model.clients.Client;
import model.exceptions.FileSystemException;
import model.exceptions.InternetException;
import model.pools.ChannelsPool;
import model.starters.Starter;
import model.starters.StartersFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ConsoleCore implements Runnable {

    private static final Logger logger = LogManager.getLogger();
    private ConsoleWriter systemWriter;
    private ConsoleReader systemReader;


    public ConsoleCore() {
        // Register console reader.
        // This one is user messages reader and user commands reader at the same time.
        systemReader = new ConsoleReader(this);
        // Register console writer.
        // This one is system writer and messages writer at the same time.
        systemWriter = new ConsoleWriter(this);
        logger.debug("Core created.");
    }

    @Override
    public void run() {
        Thread reader = new Thread(systemReader);
        Thread writer = new Thread(systemWriter);
        // An easy way to deal with closing writer thread from Core
        //TODO revise it
        writer.setDaemon(true);
        reader.start();
        writer.start();
        try {
            reader.join();
        } catch (InterruptedException e) {
            logger.error(e);
        }
        // When user types 'quit' reader stops running
        // That means the end of workflow => close all connections and die
        logger.info("Shutting down");
        try {
            ChannelsPool.clear();
            logger.info("All channels terminated");
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public void createChannel(Origin origin, String channelName) {
        Starter starter = StartersFactory.getStarter(origin);
        Client client = null;
        try {
            // this one goes synchronous
            //TODO decide if this should be async
            client = starter.prepareConnection(channelName);
            client.registerWriter(systemWriter);
            //TODO rework channelsPool
            ChannelsPool.put(channelName, client);
            new Thread(client).start();
            logger.info("Started connection to " + channelName);
        } catch (InternetException | FileSystemException e) {
            logger.error(e + "\nCould not create channel " + channelName + " on " + origin.getName());
        } catch (IllegalArgumentException e) {
            logger.warn(e.getMessage());
        }
    }

    //TODO this one should use origin obviously
    public void removeChannel(Origin origin, String channelName) {
        Client client = ChannelsPool.remove(channelName);
        if (client == null) {
            logger.warn("Channel " + channelName + " does not exist");
        } else {
            try {
                client.terminate();
                logger.info("Successfully terminated connection to " + channelName);
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

    public void writeMessage() {
        //TODO
    }
}

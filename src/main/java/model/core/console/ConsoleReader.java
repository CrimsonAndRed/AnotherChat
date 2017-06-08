package model.core.console;

import model.Origin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class ConsoleReader implements Runnable {
    private static final Logger logger = LogManager.getLogger();

    private ConsoleCore core;

    public ConsoleReader(ConsoleCore core) {
        this.core = core;
    }


    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        logger.info("ConsoleReader started");
        String string = null;
        while (true) {
            string = sc.nextLine();
            if ("quit".equals(string)) {
                break;
            }
            checkInput(string);
        }
        sc.close();
    }

    // TODO use something good like apache CLI
    private void checkInput(String string) {
        if (string.startsWith("twitch join ")) {
            String name = string.substring(string.lastIndexOf(' ') + 1);
            logger.debug("The channel you asked to join is " + name);
            //YES THIS IS SYNCHRONOUS
            core.createChannel(Origin.TWITCHTV, name.toLowerCase());
            return;
        }
        if (string.startsWith("twitch part ")) {
            String name = string.substring(string.lastIndexOf(' ') + 1);
            logger.debug("The channel you asked to kill is " + name);
            core.removeChannel(Origin.TWITCHTV, name.toLowerCase());
            return;
        }
        logger.warn("Wrong command: " + string);
    }
}

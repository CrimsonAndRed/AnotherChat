package model.core.console;

import model.messages.UserMessage;
import model.responses.ResponseType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.LinkedBlockingDeque;

public class ConsoleWriter implements Writer {
    private ConsoleCore core;
    private static final Logger logger = LogManager.getLogger();
    private final LinkedBlockingDeque<UserMessage> queue = new LinkedBlockingDeque<>();

    public ConsoleWriter(ConsoleCore core) {
        this.core = core;
    }

    @Override
    public void run() {
        logger.info("ConsoleWriter started");
        while(true) {
            try {
                UserMessage msg = queue.take();
                showMessage(msg);
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
    }

    @Override
    public void queueMessage(UserMessage msg) {
        queue.add(msg);
    }

    @Override
    public void showMessage(UserMessage message) {
        if (message.getType() == ResponseType.MSG) {
            message.parseOriginalMessage();
            System.out.println(message.getOrigin().getName() + "." + message.getChannel() + ": User " + message.getSender() + " sent: " + message.getMessage());
        } else {
            System.out.println(message.getType() + ":" + message.getOriginalMessage());
        }
    }
}

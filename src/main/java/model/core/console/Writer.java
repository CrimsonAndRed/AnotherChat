package model.core.console;

import model.messages.UserMessage;

public interface Writer extends Runnable {
    void queueMessage(UserMessage message);
    void showMessage(UserMessage message);
}

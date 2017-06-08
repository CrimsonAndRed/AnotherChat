package model.messages;

import model.Origin;
import model.responses.ResponseType;

public abstract class AbstractUserMessage implements UserMessage {

    protected String message;
    protected String channel;
    protected String sender;
    protected ResponseType type;
    protected Origin origin;
    protected String originalLine;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Origin getOrigin() {
        return origin;
    }

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public String getChannel() {
        return channel;
    }

    @Override
    public ResponseType getType() {
        return type;
    }

    @Override
    public String getOriginalMessage() {
        return originalLine;
    }
}

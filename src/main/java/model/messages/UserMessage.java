package model.messages;

import model.Origin;
import model.responses.ResponseType;

public interface UserMessage {

    String getMessage();
    Origin getOrigin();
    String getSender();
    String getChannel();
    String getOriginalMessage();
    ResponseType getType();
    void parseOriginalMessage();
}

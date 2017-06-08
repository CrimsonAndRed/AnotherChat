package model.clients;

import model.core.console.Writer;

import java.io.IOException;

public interface Client extends Runnable {
    void terminate() throws IOException;
    //TODO register multiple writers?
    void registerWriter(Writer writer);
    Writer getWriter();
}

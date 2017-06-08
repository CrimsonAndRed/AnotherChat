package model.starters;

import model.clients.Client;
import model.exceptions.FileSystemException;
import model.exceptions.InternetException;

public interface Starter {
	Client prepareConnection(String channelName) throws InternetException, FileSystemException;
}

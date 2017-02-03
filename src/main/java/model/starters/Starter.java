package model.starters;

import model.ConsoleClient;
import model.exceptions.FileSystemException;
import model.exceptions.InternetException;

public interface Starter {
	ConsoleClient prepareConnection(String channelName) throws InternetException, FileSystemException;
}

package web;

import model.exceptions.InternetException;

import java.io.BufferedWriter;
import java.io.IOException;

public class StringSender {
	public static synchronized void sendString(BufferedWriter bw, String str) throws InternetException {
		try {
			bw.write(str + "\r\n");
			bw.flush();
			//System.out.println("<<< " + str);
		} catch (IOException e) {
			throw new InternetException();
		}
	}
}

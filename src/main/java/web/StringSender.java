package web;

import java.io.BufferedWriter;
import java.io.IOException;

public class StringSender {
	public static synchronized void sendString(BufferedWriter bw, String str) {
		try {
			bw.write(str + "\r\n");
			bw.flush();
			System.out.println("<<< " + str);
		} catch (IOException e) {
			System.out.println("Exception: " + e);
		}
	}
}

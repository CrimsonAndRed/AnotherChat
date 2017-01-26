package util;

import java.io.BufferedWriter;
import java.util.Scanner;

import static web.StringSender.sendString;

public class ConsoleWriter implements Runnable {

	private BufferedWriter br;

	public ConsoleWriter(BufferedWriter br) {
		this.br = br;
	}

	@Override
	public void run() {
		Scanner sc = new Scanner(System.in);
		String string = null;
		while (true) {
			string = sc.nextLine();
			if ("quit".equals(string)) {
				break;
			}
			sendString(br, string);
		}
	}
}

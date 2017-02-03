package model.exceptions;

public class InternetException extends Exception {
	public InternetException() {
	}

	public InternetException(String message) {
		super(message);
	}

	public InternetException(String message, Throwable cause) {
		super(message, cause);
	}

	public InternetException(Throwable cause) {
		super(cause);
	}
}

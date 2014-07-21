package net.mdp3.java.util;

/**
 * util
 * @author Mikel
 *
 * Utils Test class
 */
public class Util {

	private ConsoleReader cr;
	
	/**
	 * Main entry point for Util package when testing.
	 * 
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Util u = new Util();
	}

	public Util() {
		cr = new ConsoleReader("Utils", this);
	}
	
	public void consoleTest() {
		System.out.println("ConsoleTest!");
	}
	
	public void consoleTest2(String s) {
		System.out.println("ConsoleTest2: " + s);
	}
	
	public void quit() {
		cr.quit();
	}
}

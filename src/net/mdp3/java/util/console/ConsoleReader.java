/**
 * 
 */
package net.mdp3.java.util.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

/**
 * @author Mikel Duke
 *
 * ConsoleReader class easily adds a threaded BufferedInput Console reader to a 
 * Java app, to enable simple call backs to methods on the implementing object. 
 * 
 * All methods used by the lookups should implement all Strings in the parameter 
 * list and do type conversion within the callback methods.
 * 
 * Example:
 * 
 * For some object o with methods:
 * 
 * ConsoleReader cr = new ConsoleReader("App Name", o);
 * 
 * Will instantiate and start an instance of the ConsoleReader which will call 
 * back to methods on o when the method names and parameter counts match, 
 * parameter types are unchecked and should all be String.
 */
public class ConsoleReader extends Thread {
	
	private BufferedReader br;
	private boolean run = true;
	private String name = "";
	private Object o;
	private ConsoleBatch consoleBatch = null;
	
	public ConsoleReader(String name, Object o) {
		this.name = name;
		this.o = o;
		this.start();
	}
	
	/**
	 * Run is the threaded method required when extending Thread. It outputs 
	 * the Name set in the constructor and waits for console input.
	 */
	public void run() {
		br = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			while (run) {
				System.out.print(name + ": ");
				parseInput(br.readLine());
			}
		}
		catch (Exception e) {
			System.out.println("Error in " + name + " Thread: " + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * parseInput handles the keyboard console input. It looks up the input 
	 * string to check for matching methods on object o.
	 * 
	 * Note: Parameters should be separated by a space character, and extra 
	 * parameters are ignored.
	 * 
	 * @param input
	 */
	protected void parseInput(String input) {
		Method methods[] = o.getClass().getMethods();
		Object args[] = null;
		
		String inputAr[] = input.split(" ");
		if (inputAr.length == 0) return;
		
		for (int i = 0; i < methods.length; i++) {
			//find matching public method of same name
			if (methods[i].getName().equalsIgnoreCase(inputAr[0]) 
					&& methods[i].getModifiers() == Modifier.PUBLIC) {
				try {
					Class<?> params[] = methods[i].getParameterTypes();
					if (params.length == 0) {
						args = null;
					} else if (inputAr.length >= params.length + 1) {
						args = new Object[params.length];
						for (int j = 0; j < params.length; j++) {
							args[j] = inputAr[j+1];
						}
						if (inputAr.length + 1 > params.length) {
							//combine extra params to last one
							for (int j = params.length; j < inputAr.length - 1; j++) {
								String s = ((String) args[args.length - 1]);
								s += " " + inputAr[j + 1];
								args[args.length - 1] = s;
							}
						}
					} else {
						System.out.println("Error: Not Enough Arguments");
						return;
					}
					methods[i].invoke(o, args);
				} catch (IllegalAccessException e) {
					System.out.println("Error: " + e);
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					System.out.println("Error: " + e);
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					System.out.println("Error: " + e);
					e.printStackTrace();
				}
				return;
			}
		}
		
		//default implementations for common methods, these items don't get 
		//called if already implemented in object o
		if (input.equalsIgnoreCase("help")) {
			showDefaultHelp();
		} else if (inputAr[0].equalsIgnoreCase("openBatch") && inputAr.length == 2) {
			openBatch(inputAr[1]);
		} else if (inputAr[0].equalsIgnoreCase("startBatch")) {
			startBatch();
		} else if (inputAr[0].equalsIgnoreCase("loopBatch")) {
			loopBatch();
		} else if (inputAr[0].equalsIgnoreCase("stopBatch")) {
			stopBatch();
		} else if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
			quit();
		} else {
			System.out.println("Invalid Command");
		}
	}
	
	/**
	 * Default quit implementation to stop the thread and close the ConsoleReader.
	 * 
	 * If this method is implemented in o, it should call back to this method to 
	 * stop the ConsoleReader thread correctly.
	 */
	public void quit() {
		run = false;
	}
	
	/**
	 * Default help implementation. This method will not be used if a help 
	 * method is implemented in object o.
	 * 
	 * This method will output a sorted list of public method names on object o 
	 * with the parameter count.
	 */
	private void showDefaultHelp() {
		Method methods[] = o.getClass().getMethods();
		SortedSet<String> methodNames = new TreeSet<String>();
		
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getModifiers() == Modifier.PUBLIC) {
				Class<?> params[] = methods[i].getParameterTypes();
				String str = methods[i].getName();
				if (!(str.equalsIgnoreCase("toString") || str.equalsIgnoreCase("equals")))
					methodNames.add(str + " " + params.length);
			}
		}
		
		if (!methodNames.isEmpty()) {
			Object methodNamesAr[] = methodNames.toArray();
			for (int i = 0; i < methodNamesAr.length; i++)
				System.out.println((String)methodNamesAr[i]);
		}
	}
	
	public void openBatch(String fileName) {
		File file = new File(fileName);
		this.openBatch(file);
	}
	
	public void openBatch(File file) {
		Vector<ConsoleCommand> commandVector = new Vector<ConsoleCommand>();
		String command = "";
		long delay = 100;
		String input = "";
		
		try {
			Scanner sc = new Scanner(file);
			while (sc.hasNext()) input += sc.nextLine() + "\n";
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("Settings File Not Found");
			//e.printStackTrace();
		}
		
		if (!input.equals("")) {
			String line[] = input.split("\n");
			for (int i = 0; i < line.length; i++) {
				if (line[i].length() < 1) continue;
				if (line[i].charAt(0) == '#') {
					continue;
				} else if (line[i].toLowerCase().contains("batch")) {
					continue;
				}
				
				String parts[] = line[i].split(" ");
				if (parts.length < 2) {
					delay = 100;
					command = line[i];
				} else try {
					Long l = Long.parseLong(parts[parts.length - 1]);
					delay = l.longValue();
					
					command = "";
					for (int j = 0; j < parts.length - 1; j++) {
						command += parts[j];
					}
				} catch (NumberFormatException e) {
					delay = 100;
					command = line[i];
				}
				
				commandVector.add(new ConsoleCommand(command, delay));
			}
		}
		
		if (commandVector.size() > 0) {
			this.consoleBatch = new ConsoleBatch(this, commandVector);
		} else {
			System.out.println("No Commands Loaded");
		}
	}
	
	public void startBatch() {
		if (this.consoleBatch != null) {
			this.consoleBatch.startBatch();
		}
	}
	
	public void loopBatch() {
		if (this.consoleBatch != null) {
			this.consoleBatch.setLooping(true);
			this.consoleBatch.startBatch();
		}
	}
	
	public void stopBatch() {
		if (this.consoleBatch != null) {
			this.consoleBatch.stopBatch();
		}
	}
	
	public boolean isBatchRunning() {
		if (this.consoleBatch != null) {
			return this.consoleBatch.isRunning();
		} else {
			return false;
		}
	}
}

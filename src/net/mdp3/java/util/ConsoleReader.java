/**
 * 
 */
package net.mdp3.java.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.SortedSet;
import java.util.TreeSet;

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
	private void parseInput(String input) {
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
}

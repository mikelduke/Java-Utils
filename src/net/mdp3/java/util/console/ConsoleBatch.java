/**
 * 
 */
package net.mdp3.java.util.console;

import java.util.Vector;

/**
 * @author Mikel
 *
 */
public class ConsoleBatch extends Thread {
	private ConsoleReader consoleHandler;
	private Vector<ConsoleCommand> batch;
	private boolean run = false;
	private int commandIndex = 0;
	private boolean looping = false;
	
	public ConsoleBatch(ConsoleReader cr, Vector<ConsoleCommand> v) {
		this.consoleHandler = cr;
		this.batch = v;
	}
	
	public void run() {
		while(run) {
			if (this.commandIndex >= batch.size() && this.looping) this.commandIndex = 0;
			else if (this.commandIndex >= batch.size()) {
				run = false;
				return;
			}
			
			System.out.println("Batch Command: " + batch.get(this.commandIndex).getCommand());
			this.consoleHandler.parseInput(batch.get(this.commandIndex).getCommand());
			
			try {
				Thread.sleep(batch.get(this.commandIndex).getDelay());
			} catch (InterruptedException e) {
				System.out.println("Error sleeping Command Batch " + e);
				e.printStackTrace();
			}
			this.commandIndex++;
		}
	}
	
	public void startBatch() {
		if (batch.size() > 0 && !run) {
			run = true;
			this.start();
		}
	}
	
	public void stopBatch() {
		run = false;
	}
	
	public void setLooping(boolean b) {
		this.looping = b;
	}
	
	public boolean isRunning() {
		return run;
	}
}

class ConsoleCommand {
	private String command = "";
	private long waitTime = 100;
	
	public ConsoleCommand(String c, long l) {
		this.command = c;
		this.waitTime = l;
	}
	
	public String getCommand() {
		return this.command;
	}
	
	public long getDelay() {
		return this.waitTime;
	}
}
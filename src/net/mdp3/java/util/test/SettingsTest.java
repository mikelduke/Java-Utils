/**
 * 
 */
package net.mdp3.java.util.test;

import java.io.File;
import java.net.URL;

import net.mdp3.java.util.settings.Settings;

/**
 * @author Mikel
 *
 */
public class SettingsTest {

	/**
	 * @param args
	 */
	
	public static int staticIntParm = 0;
	public static String staticStringParm = "STATIC";
	public static boolean staticBoolParm = false;
	public static long staticLongParm = 123456789;
	public static double staticDoubleParm = 1.55;
	
	public int intParm = 0;
	public String stringParm = "String";
	public boolean boolParm = false;
	public static long longParm = 123456789;
	public static double doubleParm = 1.55;
	
	public static void main(String[] args) {
		System.out.println("Static Test: ");
		Settings set = new Settings(SettingsTest.class, true);
		
		URL url = SettingsTest.class.getResource("SettingsTest.txt");
		File file = new File(url.getPath());
		set.loadSettings(file);
		showStaticSettings();

		System.out.println("\nObject Test:");
		SettingsTest st = new SettingsTest();
	}
	
	public SettingsTest() {
		Settings set = new Settings(this, true);
		
		URL url = SettingsTest.class.getResource("SettingsTest.txt");
		File file = new File(url.getPath());
		set.loadSettings(file);
		showSettings();
	}
	
	public static void showStaticSettings() {
		System.out.println("Settings after load:");
		System.out.println("staticIntParm: " + staticIntParm);
		System.out.println("staticStringParm: " + staticStringParm);
		System.out.println("staticBoolParm: " + staticBoolParm);
		System.out.println("staticLongParm: " + staticLongParm);
		System.out.println("staticDoubleParm: " + staticDoubleParm);
	}
	
	public void showSettings() {
		System.out.println("Settings after load:");
		System.out.println("intParm: " + intParm);
		System.out.println("stringParm: " + stringParm);
		System.out.println("boolParm: " + boolParm);
		System.out.println("longParm: " + longParm);
		System.out.println("doubleParm: " + doubleParm);
	}
}

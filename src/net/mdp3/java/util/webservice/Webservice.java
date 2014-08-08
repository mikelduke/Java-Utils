/**
 * 
 */
package net.mdp3.java.util.webservice;

import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.HashMap;

//These imports require a sun/oracle jvm
// http://stackoverflow.com/questions/9579970/can-not-use-the-com-sun-net-httpserver-httpserver
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Webservice
 * 
 * Simple Java Webservice using the Sun HTTP Server implementation. The 
 * necessary packages may not be included in all JVMs. 
 * 
 * This class creates a webserver at the specified port and context, which 
 * listens for connections, builds the access URL parameters into a HashMap
 * and finally calls the wsAccess callback specified by the WebserviceListener
 * interface. The String returned by wsAccess is returned back to the browser.
 * 
 * @author Mikel
 * @see http://stackoverflow.com/questions/9579970/can-not-use-the-com-sun-net-httpserver-httpserver
 */
@SuppressWarnings("restriction")
public class Webservice {
	//private boolean running = true;
	private int port;
	private String name = "ws";
	private WebserviceListener wsl;
	
	private HttpServer server;

	public Webservice(int port, String name, WebserviceListener listener) throws BindException, IOException {
		if (name.charAt(0) != '/') name = "/" + name; //HttpServer.createContext arg0 is required to begin with /
		
		this.port = port;
		this.name = name;
		this.wsl = listener;
		
		startServer();
	}

	public void startServer() throws BindException, IOException {
		server = HttpServer.create(new InetSocketAddress(this.port), 0);
		server.createContext(this.name, new GetHandler(wsl));
		server.setExecutor(null);
		server.start();
	}

	public void stopServer(int delay) throws Exception {
		server.stop(delay);
	}

	public void restart(int delay, int newPort) throws Exception {
		stopServer(delay);
		this.port = newPort;
		startServer();
	}

	static class GetHandler implements HttpHandler {
		// called by http://ip/name/~
		// parses url to get user info and file to send

		HttpExchange t;
		WebserviceListener wsl;
		
		public GetHandler(WebserviceListener listener) {
			this.wsl = listener;
		}
		
		public void handle(HttpExchange t) throws IOException {
			this.t = t;
			//System.out.println("Server: Get handler Called");

			HashMap<String, String> map = new HashMap<String, String>();
			
			//get request url /get/command info
			String command = t.getRequestURI().toString();
			System.out.println("Command: " + command);
			
			/*String method = t.getRequestMethod();
			System.out.println("Mthod: " + method);*/

			String[] vars  = command.split("&");
			for (int i = 1; i < vars.length; i++) {
				//System.out.println("var " + i + ": " + vars[i]);
				String[] parmPair = vars[i].split("=");
				if (parmPair.length == 2) {
					map.put(parmPair[0], parmPair[1]);
				}
			}

			//Headers h = t.getResponseHeaders();
			String response = wsl.wsAccess(map);
			
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}
}

/**
 * To get the request and response string, need to execute method sendRequest
 * first. The response time was calculated from ending time of send request to
 * finished receiving response from server.
 * 
 * @author gaozhan
 * 
 */

package angil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class HttpClient {

	private String requestString = "GET /examples/homebanking/verify.jsp?username=auckland&password=a&login=login HTTP/1.1";
	private String response = "";
	private long serverResponseTime = 0;

	public HttpClient(String requestString) {
		this.requestString = requestString;
	}

	public String getRequest() {
		return requestString;
	}

	public String getResponse() {
		return response;
	}

	public long getResponseTime() {
		return serverResponseTime;
	}

	/**
	 * Send http request to server and receive reponse. Calculate the server
	 * response time by nano seconds.
	 * 
	 * @return The server response time in nano seconds.
	 */
	public String sendRequest() {
		String estimatedTime = "0";
		try {
			Socket socket = new Socket("127.0.0.1", 8080);
			OutputStream os = socket.getOutputStream();
			boolean autoflush = true;
			PrintWriter out = new PrintWriter(os, autoflush);
			// Receive response
			BufferedReader in = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			// Send the HTTP request to the server
			out.println(requestString);
			out.println("Host: localhost:8080");
			out.println("Connection: Close");
			out.println();
			long startTime = System.nanoTime();
			// Receive and read the response from server
			boolean notEnd = true;
			// limited size
			StringBuffer sb = new StringBuffer(8096);
			while (notEnd) {
				int i = 0;
				while (i != -1) {
					i = in.read();
					sb.append((char) i);
				}
				notEnd = false;
				Thread.currentThread();
				Thread.sleep(50);
			}
			double nanoSecond = (double) (System.nanoTime() - startTime);
			double t = nanoSecond/1000000000;
			estimatedTime = t + "";

			this.response = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return estimatedTime;
	}
}

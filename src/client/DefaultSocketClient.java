package client;

import java.net.*;

import adapter.BuildAuto;
import exception.AutoException;

import java.io.*;

public class DefaultSocketClient extends Thread implements SocketClientInterface, SocketClientConstants {
	private BufferedReader stdIn;
	private Socket socketClient;
	private InputStream socketClientInputStream;
	private OutputStream socketClientOutputStream;
	private BufferedReader reader;
	private BufferedWriter writer;
	private String strHost;
	private int iPort;
	private CarModelOptionsIO carOptionsMenu;

	public DefaultSocketClient(String strHost, int iPort, BufferedReader stdIn_) {
		stdIn = stdIn_;
		setPort(iPort);
		setHost(strHost);
		carOptionsMenu = new CarModelOptionsIO(stdIn_);
	}// constructor

	public void run() {
		if (openConnection()) {
			handleSession();
			closeSession();
		}
	}// run

	public boolean openConnection() {
		try {
			socketClient = new Socket(strHost, iPort);
		} catch (IOException socketError) {
			if (DEBUG)
				System.err.println("Unable to connect to " + strHost);
			return false;
		}
		try {
			socketClientInputStream = socketClient.getInputStream();
			socketClientOutputStream = socketClient.getOutputStream();
			reader = new BufferedReader(new InputStreamReader(socketClientInputStream));
			writer = new BufferedWriter(new OutputStreamWriter(socketClientOutputStream));
			carOptionsMenu.openConnection(socketClientInputStream, socketClientOutputStream);
		} catch (AutoException e) {
			if (DEBUG)
				System.err.println("Unable to obtain stream to/from " + strHost);
			return false;
		} catch (Exception e) {
			if (DEBUG)
				System.err.println("Unable to obtain stream to/from " + strHost);
			return false;
		}
		return true;
	}

	public void handleSession() {
		// BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String strInput = "";
		String fromServer = "";
		if (DEBUG)
			System.out.println("Handling session with " + strHost + ":" + iPort);
		try {
			// block on client command
			carOptionsMenu.displayMenu();
			while ((strInput = stdIn.readLine()) != null) {
				if (carOptionsMenu.getMenuOption(strInput)) {
					// block on server response
					fromServer = reader.readLine();
					handleInput(fromServer);
				}
				carOptionsMenu.displayMenu();
			}
		} catch (IOException e) {
			if (DEBUG)
				System.out.println("client unexpectedly closed");
		}
	}

	public void sendOutput(String strOutput) {
		try {
			// escape new lines so we can send this in one go
			strOutput = strOutput.replace("\n","\\n");
			writer.write(strOutput, 0, strOutput.length());
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			if (DEBUG)
				System.out.println("Error writing to " + strHost);
		}
	}

	public void handleInput(String strInput) {
		// unescape new lines
		strInput = strInput.replace("\\n","\n");
		System.out.println(strInput);
	}

	public void closeSession() {
		try {
			writer = null;
			reader = null;
			socketClient.close();
		} catch (IOException e) {
			if (DEBUG)
				System.err.println("Error closing socket to " + strHost);
		}
	}

	public void setHost(String strHost) {
		this.strHost = strHost;
	}

	public void setPort(int iPort) {
		this.iPort = iPort;
	}

	public static void main(String arg[]) {
		/* debug main; does daytime on local host */
		String strLocalHost = "";
		try {
			strLocalHost = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			System.err.println("Unable to find local host");
		}
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		DefaultSocketClient d = new DefaultSocketClient(strLocalHost, iDAYTIME_PORT, stdIn);
		d.start();
	}

}// class DefaultSocketClient

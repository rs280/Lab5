package server;

import java.net.*;

import adapter.BuildAuto;
import exception.AutoException;

import java.io.*;

public class SocketClientHandler extends Thread implements SocketClientInterface, SocketClientConstants {
	private BufferedReader reader;
	private BufferedWriter writer;
	private Socket socketClient;
	private DefaultSocketServer socketServer;
	private String strHost;
	private int iPort;
	private BuildAuto buildAutoInterface;

	public SocketClientHandler(Socket socketClient_, DefaultSocketServer socketServer_, BuildAuto buildAutoInterface_) {
		socketClient = socketClient_;
		socketServer = socketServer_;
		buildAutoInterface = buildAutoInterface_;
	}// constructor

	public void run() {
		if (openConnection()) {
			handleSession();
			closeSession();
		}
	}// run

	public boolean openConnection() {
		try {
			reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
		} catch (Exception e) {
			if (DEBUG)
				System.err.println("Unable to obtain stream to/from " + strHost);
			return false;
		}
		return true;
	}

	public void handleSession() {
		String strInput = "";
		if (DEBUG)
			System.out.println(socketClient.getRemoteSocketAddress() + ": Started a session.");
		try {
			while ((strInput = reader.readLine()) != null)
				handleInput(strInput);
		} catch (IOException e) {
			if (DEBUG)
				System.out.println(socketClient.getRemoteSocketAddress() + ": Closed the session.");
		}
	}

	public void sendOutput(String strOutput) {
		try {
			// escape new lines so we can send this in one go
			strOutput = strOutput.replace("\n", "\\n");
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
		strInput = strInput.replace("\\n", "\n");
		String automobileKey = null;
		if (DEBUG)
			System.out.println(socketClient.getRemoteSocketAddress() + ": " + strInput);
		switch (strInput) {
		case "send properties":
			try {
				automobileKey = buildAutoInterface.buildAutomobileFromProperties(
					buildAutoInterface.propertiesFromStream(socketClient.getInputStream()));
			} catch (AutoException e) {
				if (DEBUG)
					System.out.println(socketClient.getRemoteSocketAddress() + ": " + e.getMessage());
			} catch (IOException e) {
				if (DEBUG)
					System.out.println(socketClient.getRemoteSocketAddress() + ": error receiving properties");
			}
			if (automobileKey == null) {
				sendOutput("Failed to get properties.");
			} else {
				sendOutput("Successfully added the automobile with key: " + automobileKey);
			}
			break;
		case "send automobile":
			try {
				automobileKey = buildAutoInterface.automobileFromStream(socketClient.getInputStream());
			} catch (AutoException e) {
				if (DEBUG)
					System.out.println(socketClient.getRemoteSocketAddress() + ": " + e.getMessage());
			} catch (IOException e) {
				if (DEBUG)
					System.out.println(socketClient.getRemoteSocketAddress() + ": error receiving properties");
			}
			if (automobileKey == null) {
				sendOutput("Failed to get properties.");
			} else {
				sendOutput("Successfully added the automobile with key: " + automobileKey);
			}
			break;
		case "get automobile list":
			sendOutput(buildAutoInterface.getAutomobileList() + "\n");
			break;
		case "begin customization":
			try {
				strInput = reader.readLine();
				// remove new lines and white space
				strInput = strInput.replace("\\n", "\n").replace("\n", "").replace(" ", "");
			} catch (IOException e) {
				sendOutput("Failed to get request.");
				break;
			}
			if (DEBUG)
				System.out.println(socketClient.getRemoteSocketAddress() + ": " + strInput);
			try {
				buildAutoInterface.automobileToStream(socketClient.getOutputStream(), strInput);
			} catch (AutoException e) {
				if (DEBUG)
					System.out.println(socketClient.getRemoteSocketAddress() + ": " + e.getMessage());
				sendOutput("Failed to get automobile.");

			} catch (IOException e) {
				if (DEBUG)
					System.out.println(socketClient.getRemoteSocketAddress() + ": error sending automobile");
				sendOutput("Failed to get automobile.");
			}
			break;
		case "pick up car":
			sendOutput("Your order has been sent.");
			break;
		case "exit":
			// closes the client
			closeSession();
			break;
		case "shut down":
			// closes the server
			closeServer();
			break;
		default:
			// do not echo
			// sendOutput("Received: " + strInput);
		}
	}

	/** Closes the client socket */
	public void closeSession() {
		try {
			writer.close();
			reader.close();
			socketClient.close();
		} catch (IOException e) {
			if (DEBUG)
				System.out.println(socketClient.getRemoteSocketAddress() + ": error closing socket");
		}
	}

	/** Closes the server */
	public void closeServer() {
		socketServer.close();
	}

	public void setHost(String strHost) {
		this.strHost = strHost;
	}

	public void setPort(int iPort) {
		this.iPort = iPort;
	}

}// class DefaultSocketClient

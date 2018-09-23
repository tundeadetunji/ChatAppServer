
package chatAppServer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Server extends JFrame {

	private JTextField chatMessage;
	private JTextArea messageHistory;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private ServerSocket server;
	private Socket connection;
	private String input;

	public Server() {
		super("Sample Chat App");
		chatMessage = new JTextField();
		chatMessage.setEditable(false);
		chatMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				sendMessageOut(event.getActionCommand());
				chatMessage.setText("");
			}
		});
		add(chatMessage, BorderLayout.SOUTH);
		messageHistory = new JTextArea();
		messageHistory.setEditable(false);
		add(new JScrollPane(messageHistory));
		setSize(400, 250);
		setVisible(true);
	}

	public void startNow() {
		try {
			server = new ServerSocket(6789, 100);
			while (true) {
				try {
					standbyForCommunication();
					setupCommunicationPathway();
					performCommunication();
				} catch (EOFException eofException) {
					appendMessage("\nConnection terminated.");
				} finally {
					closeCommunicationPathway();
				}
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private void standbyForCommunication() throws IOException {
		appendMessage("Standing by for connection. No incoming stream yet. \n");
		connection = server.accept();
		appendMessage("Connection established with " + connection.getInetAddress().getHostName());
	}

	private void setupCommunicationPathway() throws IOException {
		outputStream = new ObjectOutputStream(connection.getOutputStream());
		outputStream.flush();
		inputStream = new ObjectInputStream(connection.getInputStream());
		// appendMessage("\n ")
	}

	private void performCommunication() throws IOException {
		chatMessage.setEditable(true);
		do {
			try {
				input = (String) inputStream.readObject();
				appendMessage("\n" + input);
			} catch (ClassNotFoundException classNotFoundException) {
				appendMessage("Error occured during communication.");
			}
		} while (!input.equals("Client - end"));
	}

	private void closeCommunicationPathway() {
		appendMessage("\nEnding Communication... \n");
		chatMessage.setEditable(false);
		try {
			outputStream.close();
			inputStream.close();
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

	}

	private void sendMessageOut(String message) {
		try {
			outputStream.writeObject("Server - " + message);
			outputStream.flush();
			appendMessage("\nServer - " + message);
		} catch (IOException ioException) {
			messageHistory.append("\nError sending message");
		}

	}

	private void appendMessage(final String messageToAppend) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				messageHistory.append(messageToAppend);
			}
		});
	}

}

package chatAppServer;

import javax.swing.JFrame;

class RunServer {
	public static void main(String[] args) {
		Server serverWindow = new Server();
		serverWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		serverWindow.startNow();
	}

}

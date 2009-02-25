/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  srv1console - Android G1 phone console for Surveyor SRV-1 
 *    Copyright (C) 2005-2009  Surveyor Corporation and Jeffrey Nelson
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details (www.gnu.org/licenses)
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.macpod.srv1console;

/**
 * Notes:
 * May need to wrap bitmap of video command in an object so we can pass it.
 * Do something about multiple instances of same object being in queue?
 * Handle interrupts when adding/removing things from the queue.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SRV1Communicator {
	private SRV1CommunicatorThreader communicator = null;

	public boolean connect(String host, Handler new_handler,
			BlockingQueue<SRV1Command> commandQueue) {
		if (communicator != null && communicator.connected()) {
			return false;
		}
		communicator = new SRV1CommunicatorThreader();
		return communicator.connect(host, new_handler, commandQueue);
	}

	public void disconnect() {
		if (!connected())
			return;
		communicator.disconnect();
		communicator = null;
	}

	public boolean connected() {
		if (communicator == null)
			return false;
		return communicator.connected();
	}

	public void putCommand(SRV1Command command) {
		if (communicator == null)
			return;
		communicator.putCommand(command);
	}

	private class SRV1CommunicatorThreader extends Thread {
		public static final int UPDATE_INTERFACE = 0;
		public static final int SRV1_PORT = 10001;
		private Socket connection = null;
		private Handler interface_handler;
		private LinkedBlockingQueue<SRV1Command> commandQueue = new LinkedBlockingQueue<SRV1Command>();

		public void run() {
			// Update the client's interface to show we are running.
			try {
				Message m = Message.obtain();
				m.what = UPDATE_INTERFACE;
				interface_handler.sendMessage(m);
			} catch (Exception e) {
			}

			try {
				boolean results;
				DataOutputStream out = new DataOutputStream(connection
						.getOutputStream());
				DataInputStream in = new DataInputStream(connection
						.getInputStream());
				Log.d("SRV1", "Connection is up!");

				while (true) { // Read and display images as fast as possible.
					// Wait for, then take next command in queue.
					SRV1Command command = commandQueue.take();
					// Log.d("SRV1", "Running a command.");
					// Run the command once, more if it needs to because it
					// failed.
					do {
						results = command.process(in, out);
					} while (results == false && command.repeatOnFail());
					// Log.d("SRV1", "Done running a command");
					// Add it back if it should be repeated.
					if (command.repeat()) {
						commandQueue.put(command);
					}

				}
			} catch (Exception e) {
				try {
					connection.shutdownInput();
				} catch (Exception ee) {
				}
				try {
					connection.shutdownOutput();
				} catch (Exception ee) {
				}
				try {
					connection.close();
				} catch (Exception ee) {
				}
			} finally {
				connection = null;
			}

			// Update the clients interface to show we are disconnected.
			try {
				Message m = Message.obtain();
				m.what = UPDATE_INTERFACE;
				interface_handler.sendMessage(m);
			} catch (Exception e) {
			}

		}

		public void putCommand(SRV1Command command) {
			boolean retry = true;
			while (retry) {
				try {
					commandQueue.put(command);
					retry = false;
				} catch (InterruptedException e) {
				}
			}
		}

		public boolean connected() {
			return (connection != null);
		}

		public boolean connect(String host, Handler new_handler,
				BlockingQueue<SRV1Command> commandQueue) {
			boolean err = false;
			if (connected()) {
				return false;
			}

			try {
				InetAddress addr = InetAddress.getByName(host);
				connection = new Socket(addr, SRV1_PORT);
				interface_handler = new_handler;
				this.commandQueue.clear();
				commandQueue.drainTo(this.commandQueue);
				start();
			} catch (Exception e) {
				connection = null;
				err = true;
			}

			return !err;
		}

		public void disconnect() {
			if (connection == null)
				return;
			commandQueue.clear();
			try {
				connection.shutdownInput();
			} catch (IOException e) {
			}
			try {
				connection.shutdownOutput();
			} catch (IOException e) {
			}
			try {
				connection.close();
			} catch (IOException e) {
			}
		}
	}
}

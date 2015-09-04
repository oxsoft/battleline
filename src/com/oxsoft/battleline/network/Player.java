package com.oxsoft.battleline.network;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.oxsoft.battleline.model.Side;

public class Player {
	private static final int READ_MAX = 16777216;

	private final Socket socket;
	private final Side side;
	private final InputStreamReader inputStreamReader;
	private final OutputStreamWriter outputStreamWriter;

	public Player(Socket socket, Side side) throws IOException {
		this.socket = socket;
		this.side = side;
		inputStreamReader = new InputStreamReader(socket.getInputStream(), Constants.CHARSET);
		outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), Constants.CHARSET);
	}

	public void close() throws IOException {
		outputStreamWriter.close();
		socket.close();
	}

	public void closeInput() throws IOException {
		inputStreamReader.close();
	}

	public Side getSide() {
		return side;
	}

	public String read() throws IOException {
		char[] data = new char[READ_MAX];
		int length = inputStreamReader.read(data, 0, READ_MAX);
		if (length == -1) return null;
		return new String(data, 0, length);
	}

	public void write(String data) throws IOException {
		outputStreamWriter.write(data);
		outputStreamWriter.flush();
	}
}

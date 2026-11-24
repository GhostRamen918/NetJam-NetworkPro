package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

// 인게임 소켓 - 클라이언트 한 명당 하나 (문자열 한 줄씩, UTF-8)
public class GameHandler implements Runnable {

	private GameServer server;
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private String userId;

	public GameHandler(GameServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			socket.setTcpNoDelay(true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

			// 첫 줄은 CONN|유저ID
			String[] first = in.readLine().split("\\|");
			if (first[0].equals("CONN") && server.connectGame(first[1], this)) {
				userId = first[1];
				sendLine("CONN_OK");
			} else {
				sendLine("CONN_FAIL|NOT_IN_ROOM");
				close();
				return;
			}

			String line;
			while ((line = in.readLine()) != null) {
				String[] p = line.split("\\|");
				// NOTE_JUDGE|유저ID|악기|판정|점수
				if (p[0].equals("NOTE_JUDGE") && p.length == 5) {
					server.noteJudge(userId, p[2], p[3], Integer.parseInt(p[4]));
				} else {
					sendLine("ERR|BAD_FORMAT");
				}
			}
		} catch (Exception e) {
			// 연결 끊김 또는 형식 오류
		} finally {
			close();
		}
	}

	public synchronized void sendLine(String line) {
		try {
			out.write(line + "\n"); // 윈도우에서도 \n 으로 통일
			out.flush();
		} catch (IOException e) {
		}
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
		}
	}
}

package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

import protocol.ErrorMsg;
import protocol.Msg;
import protocol.MsgType;
import protocol.PlayerInfo;
import protocol.ReadyMsg;
import protocol.RoomJoinMsg;
import protocol.RoomStateMsg;
import protocol.SelectInstMsg;

// 서버 테스트용 콘솔 클라이언트 
// 실행하면 ID 입력 → 자동으로 입장
// 명령: inst DRUM / ready / judge PERFECT 300 / exit
public class TestClient {

	static String host = "localhost"; 
	static String userId;
	static ObjectOutputStream out;
	static BufferedWriter gameOut;

	public static void main(String[] args) throws Exception {
		if (args.length > 0) host = args[0];
		Scanner sc = new Scanner(System.in);
		System.out.print("ID: ");
		userId = sc.nextLine();

		Socket socket = new Socket(host, GameServer.CONTROL_PORT);
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

		// 서버에서 오는 메시지 받는 스레드
		new Thread(() -> {
			try {
				while (true) {
					Msg msg = (Msg) in.readObject();
					print(msg);
					if (msg.getType().equals(MsgType.ROOM_JOIN_OK)) {
						connectGame(); // 입장 성공하면 인게임 소켓 연결
					}
				}
			} catch (Exception e) {
				System.out.println("서버 연결 끊김");
			}
		}).start();

		send(new RoomJoinMsg(MsgType.ROOM_JOIN, userId, userId)); // 입장 요청

		while (sc.hasNextLine()) {
			String[] cmd = sc.nextLine().trim().split(" ", 2);
			switch (cmd[0]) {
			case "inst":
				send(new SelectInstMsg(userId, cmd[1]));
				break;
			case "ready":
				send(new ReadyMsg(userId, true));
				break;
			case "judge": // judge PERFECT 300
				String[] j = cmd[1].split(" ");
				gameOut.write("NOTE_JUDGE|" + userId + "|DRUM|" + j[0] + "|" + j[1] + "\n");
				gameOut.flush();
				break;
			case "exit":
				send(new RoomJoinMsg(MsgType.ROOM_EXIT, userId, userId));
				Thread.sleep(300);
				System.exit(0);
			}
		}
		socket.close();
	}

	static void send(Msg msg) throws IOException {
		out.reset();
		out.writeObject(msg);
		out.flush();
	}

	static void connectGame() throws IOException {
		Socket gameSocket = new Socket(host, GameServer.GAME_PORT);
		gameOut = new BufferedWriter(new OutputStreamWriter(gameSocket.getOutputStream(), "UTF-8"));
		BufferedReader gameIn = new BufferedReader(new InputStreamReader(gameSocket.getInputStream(), "UTF-8"));
		gameOut.write("CONN|" + userId + "\n");
		gameOut.flush();

		new Thread(() -> {
			try {
				String line;
				while ((line = gameIn.readLine()) != null) {
					System.out.println("[인게임] " + line);
				}
			} catch (IOException e) {
			}
		}).start();
	}

	// 받은 메시지 보기 좋게 출력
	static void print(Msg msg) {
		System.out.print("[받음] " + msg.getType());
		if (msg instanceof RoomStateMsg) {
			RoomStateMsg s = (RoomStateMsg) msg;
			System.out.print("  방장=" + s.getHostId());
			for (PlayerInfo p : s.getPlayers()) {
				System.out.print("  [" + p.getUserId() + " " + p.getInst() + (p.isReady() ? " 준비" : "") + "]");
			}
		} else if (msg instanceof ErrorMsg) {
			System.out.print("  " + ((ErrorMsg) msg).getErrorCode() + " - " + ((ErrorMsg) msg).getReason());
		}
		System.out.println();
	}
}

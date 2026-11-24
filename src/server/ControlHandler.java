package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import protocol.Msg;
import protocol.MsgType;
import protocol.ReadyMsg;
import protocol.RoomJoinMsg;
import protocol.SelectInstMsg;

// 제어 소켓 - 클라이언트 한 명당 하나 (객체 스트림)
public class ControlHandler implements Runnable {

	private GameServer server;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String userId; // 입장하면 설정됨

	public ControlHandler(GameServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			// Output을 먼저 만들고 flush 해야 함 (Input 먼저 만들면 서로 기다리다 멈춤)
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());

			while (true) {
				Msg msg = (Msg) in.readObject();
				server.log("[제어] 받음: " + msg.getType() + " (" + msg.getUserId() + ")");
				handle(msg);
			}
		} catch (IOException | ClassNotFoundException e) {
			// 연결 끊김
		} finally {
			if (userId != null) server.leave(userId);
			close();
		}
	}

	private void handle(Msg msg) {
		switch (msg.getType()) {
		case MsgType.ROOM_JOIN: {
			RoomJoinMsg m = (RoomJoinMsg) msg;
			if (server.join(this, m.getUserId(), m.getNickname())) {
				userId = m.getUserId();
			}
			break;
		}

		case MsgType.ROOM_EXIT:
			server.leave(userId);
			userId = null;
			close(); // 명세서대로 퇴장하면 소켓 종료
			break;

		case MsgType.SELECT_INST:
			server.selectInst(userId, ((SelectInstMsg) msg).getInst());
			break;

		case MsgType.READY_TOGGLE:
			server.setReady(userId, ((ReadyMsg) msg).isReady());
			break;

		default:
			server.log("아직 처리 안 하는 메시지: " + msg.getType());
		}
	}

	// 여러 스레드에서 보낼 수 있어서 synchronized
	public synchronized void send(Msg msg) {
		try {
			out.reset(); // 이전에 보낸 객체 기억 지우기 (안 하면 바뀐 값이 안 감)
			out.writeObject(msg);
			out.flush();
		} catch (IOException e) {
			server.log("[제어] 보내기 실패: " + e.getMessage());
		}
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
		}
	}
}

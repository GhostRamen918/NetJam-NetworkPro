package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import protocol.ErrorMsg;
import protocol.Msg;
import protocol.MsgType;
import protocol.PlayerInfo;
import protocol.RoomStateMsg;

// 게임 서버 (방 1개, 최대 4명)
// 제어 소켓(6000), 인게임 소켓(6001) 두 개를 열고 접속한 클라이언트마다 스레드를 만든다.
public class GameServer {

	public static final int CONTROL_PORT = 6000;
	public static final int GAME_PORT = 6001;
	public static final int MAX_PLAYERS = 4;

	private ServerFrame frame; 
	private ServerSocket controlServer;
	private ServerSocket gameServer;
	private boolean running = false;

	private Map<String, Player> players = new LinkedHashMap<>(); // 유저 ID → 유저 (들어온 순서대로)
	private String hostId; 
	private String songId = "0";
	private String difficulty = "EASY";

	private int combo = 0;
	private int teamScore = 0;

	public GameServer(ServerFrame frame) {
		this.frame = frame;
	}

	// 서버 시작 / 중지 함수
	public void start() throws IOException {
		controlServer = new ServerSocket(CONTROL_PORT);
		gameServer = new ServerSocket(GAME_PORT);
		running = true;
		log("서버 시작 (제어 " + CONTROL_PORT + ", 인게임 " + GAME_PORT + ")");

		// 제어 소켓 접속 받기
		new Thread(() -> {
			while (running) {
				try {
					Socket socket = controlServer.accept();
					log("[제어] 접속: " + socket.getInetAddress());
					new Thread(new ControlHandler(this, socket)).start();
				} catch (IOException e) {
					if (running) log("[제어] 접속 오류: " + e.getMessage());
				}
			}
		}).start();

		// 인게임 소켓 접속 받기
		new Thread(() -> {
			while (running) {
				try {
					Socket socket = gameServer.accept();
					new Thread(new GameHandler(this, socket)).start();
				} catch (IOException e) {
					if (running) log("[인게임] 접속 오류: " + e.getMessage());
				}
			}
		}).start();
	}

	public synchronized void stop() {
		running = false;
		try {
			controlServer.close();
			gameServer.close();
		} catch (IOException e) {
		}
		for (Player p : players.values()) {
			p.control.close();
			if (p.game != null) p.game.close();
		}
		players.clear();
		hostId = null;
		log("서버 중지");
		updateFrame();
	}

	// ===== 대기실 =====

	// ROOM_JOIN: ID 중복, 정원(4명) 확인
	public synchronized boolean join(ControlHandler h, String userId, String nickname) {
		if (players.containsKey(userId)) {
			h.send(new ErrorMsg(MsgType.ROOM_JOIN_FAIL, userId, MsgType.E_DUPLICATE_ID, "이미 있는 ID"));
			log("입장 거부(중복 ID): " + userId);
			return false;
		}
		if (players.size() >= MAX_PLAYERS) {
			h.send(new ErrorMsg(MsgType.ROOM_JOIN_FAIL, userId, MsgType.E_FULL, "방이 꽉 참"));
			log("입장 거부(정원 초과): " + userId);
			return false;
		}

		players.put(userId, new Player(userId, nickname, h));
		if (hostId == null) hostId = userId; // 처음 들어온 사람이 방장
		log("입장: " + userId + " (" + nickname + ")");

		h.send(new Msg(MsgType.ROOM_JOIN_OK, userId));
		sendRoomState();
		return true;
	}

	// ROOM_EXIT 또는 연결 끊김
	public synchronized void leave(String userId) {
		Player p = players.remove(userId);
		if (p == null) return;
		if (p.game != null) p.game.close();
		log("퇴장: " + userId);

		if (players.isEmpty()) {
			hostId = null;
			combo = 0;
			teamScore = 0;
		} else if (userId.equals(hostId)) {
			hostId = players.keySet().iterator().next(); // 다음 사람에게 방장 위임
			log("방장 위임: " + hostId);
		}
		sendRoomState();
	}

	// SELECT_INST: 다른 사람이 고른 악기인지 확인
	public synchronized void selectInst(String userId, String inst) {
		Player p = players.get(userId);
		if (p == null) return;
		for (Player other : players.values()) {
			if (other != p && inst.equals(other.inst)) {
				p.control.send(new ErrorMsg(MsgType.SELECT_INST_FAIL, userId, MsgType.E_INST_TAKEN, "이미 선택된 악기"));
				return;
			}
		}
		p.inst = inst;
		log("악기 선택: " + userId + " → " + inst);
		sendRoomState();
	}

	// READY_TOGGLE: 악기를 골랐는지 확인
	public synchronized void setReady(String userId, boolean ready) {
		Player p = players.get(userId);
		if (p == null) return;
		if (p.inst == null) {
			p.control.send(new ErrorMsg(MsgType.READY_FAIL, userId, MsgType.E_NO_INST, "악기를 먼저 선택"));
			return;
		}
		p.ready = ready;
		log("준비: " + userId + " → " + ready);
		sendRoomState();
	}

	// 전체에게 ROOM_STATE
	private void sendRoomState() {
		for (Player p : players.values()) {
			p.control.send(makeRoomState()); // 같은 객체를 다시 보내면 예전 값이 가서 매번 새로 만든다
		}
		updateFrame();
	}

	private RoomStateMsg makeRoomState() {
		List<PlayerInfo> list = new ArrayList<>();
		for (Player p : players.values()) {
			list.add(new PlayerInfo(p.userId, p.nickname, p.inst, p.ready));
		}
		return new RoomStateMsg(1, "NetJam", hostId, songId, difficulty, list); // 방이 1개라 번호는 1로 고정
	}

	// ===== 인게임 =====

	// CONN|유저ID: 대기실에 있는 ID인지 확인
	public synchronized boolean connectGame(String userId, GameHandler g) {
		Player p = players.get(userId);
		if (p == null) return false;
		p.game = g;
		log("[인게임] 연결: " + userId);
		return true;
	}

	// NOTE_JUDGE|유저ID|악기|판정|점수 → 전체에 SYNC_STATUS
	public synchronized void noteJudge(String userId, String inst, String judge, int score) {
		if (judge.equals("MISS")) combo = 0;
		else combo++;
		int multiplier = 1 + combo / 10; // TODO 배율 규칙 정해지면 수정 (지금은 콤보 10마다 +1)
		if (multiplier > 4) multiplier = 4;
		teamScore += score * multiplier;

		String line = "SYNC_STATUS|" + userId + "|" + inst + "|" + judge + "|" + combo + "|" + multiplier + "|"
				+ teamScore;
		for (Player p : players.values()) {
			if (p.game != null) p.game.sendLine(line);
		}
		log("[인게임] " + line);
	}

	// ===== 화면 / 로그 =====

	public void log(String text) {
		if (frame != null) frame.addLog(text);
		else System.out.println(text);
	}

	// 서버 화면의 표 갱신 (방장, ID, 닉네임, 악기, 준비)
	private void updateFrame() {
		if (frame == null) return;
		List<Object[]> rows = new ArrayList<>();
		for (Player p : players.values()) {
			rows.add(new Object[] { p.userId.equals(hostId) ? "★" : "", p.userId, p.nickname,
					p.inst == null ? "-" : p.inst, p.ready ? "O" : "" });
		}
		frame.showPlayers(rows);
	}

}

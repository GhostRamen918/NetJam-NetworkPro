package server;

// 접속한 유저 한 명 (제어 소켓 + 인게임 소켓)
public class Player {
	String userId;
	String nickname;
	String inst; // 아직 안 골랐으면 null
	boolean ready;

	ControlHandler control;
	GameHandler game; // CONN 하기 전에는 null

	Player(String userId, String nickname, ControlHandler control) {
		this.userId = userId;
		this.nickname = nickname;
		this.control = control;
	}
}

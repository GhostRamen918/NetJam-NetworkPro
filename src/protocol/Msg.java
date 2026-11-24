package protocol;

import java.io.Serializable;

/**
 * 모든 메시지의 공통 부모
 * 필드가 없는 메시지(ROOM_JOIN_OK, GAME_START_REQ, LOAD_DONE 등)는 Msg를 그대로 사용한다.
 */
public class Msg implements Serializable {
	// 서버(맥/Eclipse)와 클라이언트(윈도우/IntelliJ)가 따로 컴파일해도 같은 클래스로 인식되도록 고정
	private static final long serialVersionUID = 1L;

	private String type;
	private String userId;

	public Msg(String type, String userId) {
		this.type = type;
		this.userId = userId;
	}

	public String getType() {
		return type;
	}

	public String getUserId() {
		return userId;
	}
}

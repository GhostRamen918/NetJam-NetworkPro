package protocol;

import java.io.Serializable;

/**
 * 모든 메시지의 공통 부모
 * 필드가 없는 메시지(ROOM_JOIN_OK, GAME_START_REQ, LOAD_DONE 등)는 Msg를 그대로 사용한다.
 */
public class Msg implements Serializable {
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

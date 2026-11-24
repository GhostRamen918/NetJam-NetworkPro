package protocol;

import java.io.Serializable;

/** ROOM_STATE 안에 들어가는 유저 한 명의 정보 */
public class PlayerInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userId;
	private String nickname;
	private String inst; // 아직 안 골랐으면 null
	private boolean ready;

	public PlayerInfo(String userId, String nickname, String inst, boolean ready) {
		this.userId = userId;
		this.nickname = nickname;
		this.inst = inst;
		this.ready = ready;
	}

	public String getUserId() {
		return userId;
	}

	public String getNickname() {
		return nickname;
	}

	public String getInst() {
		return inst;
	}

	public boolean isReady() {
		return ready;
	}
}

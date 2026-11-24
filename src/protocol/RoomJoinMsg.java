package protocol;

/** ROOM_JOIN
 *  ROOM_EXIT
 **/
public class RoomJoinMsg extends Msg {
	private String nickname;
	private int roomId;

	public RoomJoinMsg(String type, String userId, String nickname, int roomId) {
		super(type, userId);
		this.nickname = nickname;
		this.roomId = roomId;
	}

	// ROOM_EXIT처럼 방 번호가 필요 없을 때
	public RoomJoinMsg(String type, String userId, String nickname) {
		this(type, userId, nickname, 0);
	}

	public String getNickname() {
		return nickname;
	}

	public int getRoomId() {
		return roomId;
	}
}

package protocol;

/** ROOM_CREATE: 새 방을 만들고 방장으로 입장 */
public class RoomCreateMsg extends Msg {
	private static final long serialVersionUID = 1L;

	private String nickname;
	private String title;

	public RoomCreateMsg(String userId, String nickname, String title) {
		super(MsgType.ROOM_CREATE, userId);
		this.nickname = nickname;
		this.title = title;
	}

	public String getNickname() {
		return nickname;
	}

	public String getTitle() {
		return title;
	}
}

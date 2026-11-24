package protocol;

import java.util.List;

/** ROOM_LIST: 방 목록 (서버 → 방에 안 들어간 로비 클라이언트) */
public class RoomListMsg extends Msg {
	private static final long serialVersionUID = 1L;

	private List<RoomInfo> rooms;

	public RoomListMsg(List<RoomInfo> rooms) {
		super(MsgType.ROOM_LIST, null);
		this.rooms = rooms;
	}

	public List<RoomInfo> getRooms() {
		return rooms;
	}
}

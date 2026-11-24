package protocol;

import java.util.List;

/** ROOM_STATE: 내가 들어간 방의 전체 상태 (서버 → 그 방 사람 전체) */
public class RoomStateMsg extends Msg {
	private static final long serialVersionUID = 1L;

	private int roomId;
	private String title;
	private String hostId;
	private String songId;
	private String difficulty;
	private List<PlayerInfo> players;

	public RoomStateMsg(int roomId, String title, String hostId, String songId, String difficulty, List<PlayerInfo> players) {
		super(MsgType.ROOM_STATE, null);
		this.roomId = roomId;
		this.title = title;
		this.hostId = hostId;
		this.songId = songId;
		this.difficulty = difficulty;
		this.players = players;
	}

	public int getRoomId() {
		return roomId;
	}

	public String getTitle() {
		return title;
	}

	public String getHostId() {
		return hostId;
	}

	public String getSongId() {
		return songId;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public List<PlayerInfo> getPlayers() {
		return players;
	}
}

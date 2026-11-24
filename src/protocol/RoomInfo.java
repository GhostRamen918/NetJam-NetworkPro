package protocol;

import java.io.Serializable;

/** ROOM_LIST 안에 들어가는 방 하나의 요약 정보 */
public class RoomInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private int roomId;
	private String title;
	private String hostNickname;
	private int playerCount;
	private int maxPlayers;

	public RoomInfo(int roomId, String title, String hostNickname, int playerCount, int maxPlayers) {
		this.roomId = roomId;
		this.title = title;
		this.hostNickname = hostNickname;
		this.playerCount = playerCount;
		this.maxPlayers = maxPlayers;
	}

	public int getRoomId() {
		return roomId;
	}

	public String getTitle() {
		return title;
	}

	public String getHostNickname() {
		return hostNickname;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}
}

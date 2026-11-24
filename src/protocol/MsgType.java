package protocol;

/**
 * 메시지 type 값 모음
 */
public final class MsgType {

	private MsgType() {
	}

	// ===== [제어] 로비 (여러 방) - 명세표에 추가 필요 =====
	public static final String ROOM_LIST_REQ = "ROOM_LIST_REQ"; // C→S 방 목록 요청 (Msg)
	public static final String ROOM_LIST = "ROOM_LIST"; // S→C 방 목록 (RoomListMsg)
	public static final String ROOM_CREATE = "ROOM_CREATE"; // C→S 방 만들기 (RoomCreateMsg)

	// ===== [제어] 대기실 =====
	public static final String ROOM_JOIN = "ROOM_JOIN";
	public static final String ROOM_JOIN_OK = "ROOM_JOIN_OK";
	public static final String ROOM_JOIN_FAIL = "ROOM_JOIN_FAIL";
	public static final String ROOM_EXIT = "ROOM_EXIT";
	public static final String ROOM_STATE = "ROOM_STATE";
	public static final String SELECT_INST = "SELECT_INST";
	public static final String SELECT_INST_FAIL = "SELECT_INST_FAIL";
	public static final String READY_TOGGLE = "READY_TOGGLE";
	public static final String READY_FAIL = "READY_FAIL"; // 악기 없이 준비했을 때 (명세표에 추가 필요)

	// ===== [제어] 게임 시작 ~ 결과=====
	public static final String TIME_SYNC = "TIME_SYNC";
	public static final String GAME_START_REQ = "GAME_START_REQ";
	public static final String GAME_LOAD = "GAME_LOAD";
	public static final String LOAD_DONE = "LOAD_DONE";
	public static final String PLAY_SYNC = "PLAY_SYNC";
	public static final String GAME_ABORT = "GAME_ABORT";
	public static final String SONG_END = "SONG_END";
	public static final String GAME_RESULT = "GAME_RESULT";

	// ===== [인게임] 문자열 한 줄의 첫 필드 =====
	public static final String CONN = "CONN";
	public static final String CONN_OK = "CONN_OK";
	public static final String CONN_FAIL = "CONN_FAIL";
	public static final String NOTE_JUDGE = "NOTE_JUDGE";
	public static final String SYNC_STATUS = "SYNC_STATUS";
	public static final String ERR = "ERR";

	// ===== ErrorMsg.errorCode =====
	public static final String E_DUPLICATE_ID = "DUPLICATE_ID";
	public static final String E_FULL = "FULL";
	public static final String E_NO_SUCH_ROOM = "NO_SUCH_ROOM";
	public static final String E_ALREADY_IN_ROOM = "ALREADY_IN_ROOM";
	public static final String E_INVALID_NAME = "INVALID_NAME";
	public static final String E_INST_TAKEN = "INST_TAKEN";
	public static final String E_INVALID_INST = "INVALID_INST";
	public static final String E_NO_INST = "NO_INST";
	public static final String E_NOT_IN_ROOM = "NOT_IN_ROOM";
	public static final String E_ALREADY_CONNECTED = "ALREADY_CONNECTED";
	public static final String E_BAD_FORMAT = "BAD_FORMAT";
	public static final String E_LOAD_TIMEOUT = "LOAD_TIMEOUT";
}

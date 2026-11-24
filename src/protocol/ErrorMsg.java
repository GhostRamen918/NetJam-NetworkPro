package protocol;

/** 모든 *_FAIL, GAME_ABORT */
public class ErrorMsg extends Msg {
	private static final long serialVersionUID = 1L;

	private String errorCode;
	private String reason;

	public ErrorMsg(String type, String userId, String errorCode, String reason) {
		super(type, userId);
		this.errorCode = errorCode;
		this.reason = reason;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getReason() {
		return reason;
	}
}

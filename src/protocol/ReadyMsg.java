package protocol;

/** READY_TOGGLE */
public class ReadyMsg extends Msg {
	private boolean ready;

	public ReadyMsg(String userId, boolean ready) {
		super(MsgType.READY_TOGGLE, userId);
		this.ready = ready;
	}

	public boolean isReady() {
		return ready;
	}
}

package protocol;

/** SELECT_INST: DRUM, GUITAR, KEYBOARD, BASS */
public class SelectInstMsg extends Msg {
	private static final long serialVersionUID = 1L;

	private String inst;

	public SelectInstMsg(String userId, String inst) {
		super(MsgType.SELECT_INST, userId);
		this.inst = inst;
	}

	public String getInst() {
		return inst;
	}
}

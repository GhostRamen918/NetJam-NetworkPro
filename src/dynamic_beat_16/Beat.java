// Beat 클래스 : 박자 타이밍과 노트의 종류에 대한 정보를 담는 클래스. 
// 곡별로 음표와 시간 설정 (like 악보 그리기)
package dynamic_beat_16;

public class Beat {
	private final int time;
	private final String noteName;
	
	public int getTime() {
		return time;
	}
	public String getNoteName() {
		return noteName;
	}
	
	public Beat(int time, String noteName) {
		super();
		this.time = time;
		this.noteName = noteName;
	}
	

}

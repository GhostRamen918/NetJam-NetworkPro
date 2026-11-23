// Beat 클래스 : 박자 타이밍과 노트의 종류에 대한 정보를 담는 클래스. 
// 곡별로 음표와 시간 설정 (like 악보 그리기)
package dynamic_beat_16;

public class Beat {
	private int time;
	private String noteName;
	public static int START_TIME = 0;
	
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public String getNoteName() {
		return noteName;
	}
	public void setNoteName(String noteName) {
		this.noteName = noteName;
	}
	
	public Beat(int time, String noteName) {
		super();
		this.time = START_TIME + time;
		this.noteName = noteName;
	}
	

}

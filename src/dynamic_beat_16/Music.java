package dynamic_beat_16;

import java.io.BufferedInputStream;

import javazoom.jl.player.Player;

public class Music extends Thread{
	
	private Player player;
	private boolean isLoop;
	private String name; // 음악 파일 이름 (반복 재생할 때 다시 열기 위해 저장)
	
	public Music(String name, boolean isLoop) {
		try {
			this.isLoop = isLoop;
			this.name = name;
			// jar 안에서도 열리도록 File 대신 getResourceAsStream 사용
			player = new Player(new BufferedInputStream(Main.class.getResourceAsStream("music/" + name)));
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public int getTime() {
		if (player ==null)
			return 0;
		return player.getPosition();
	}
	
	public void close() {
		isLoop = false;
		player.close();
		this.interrupt(); 
	}
	
	@Override
	public void run() {
		try {
			do {
				player.play();
				player = new Player(new BufferedInputStream(Main.class.getResourceAsStream("music/" + name)));
			}while(isLoop);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}

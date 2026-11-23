// Game 클래스 : Game 클래스에서 만들어진 instance 변수를 이용해서 게임을 컨트롤. 
package dynamic_beat_16;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import dynamic_beat_16.NoteCharts.Instrument;
import dynamic_beat_16.NoteCharts.Lane;

public class Game extends Thread {
	// Thread : 하나의 프로그램 안에서 실행되는 작은 프로그램.
	// 즉, 전반적인 게임 틀 안에서 하나의 게임이 하나의 단위로써 동작 ==> Thread 이용 
	// 2. note 경로 구분선 이미지 
	private Image noteRouteLineImage = new ImageIcon(Main.class.getResource("images/noteRouteLine.png")).getImage();
	// 3. 게임정보 및 4. 판정선 이미지
	private Image judgementLineImage = new ImageIcon(Main.class.getResource("images/judgementLine.png")).getImage();
	private Image gameInfoImage = new ImageIcon(Main.class.getResource("images/gameInfo.png")).getImage();
	private final Image noteRouteImage = new ImageIcon(Main.class.getResource("images/noteRoute.png")).getImage();
	private final Image noteRoutePressedImage = new ImageIcon(Main.class.getResource("images/noteRoutePressed.png")).getImage();
	private final Image keyPadImage = new ImageIcon(Main.class.getResource("images/keyPadBasic.png")).getImage();
	private final Image keyPadPressedImage = new ImageIcon(Main.class.getResource("images/keyPadPressed.png")).getImage();
	private final Instrument instrument;
	private final boolean[] pressedLanes;
	// 8. 판정 이미지 - 판정 결과(Miss, Late, Good, Great, Perfect, Early) 및 판정결과 아래의 노란 불꽃 이미지 
	private Image yellowFlareImage;
	private Image judgeImage;
	// 7. 곡정보 - 1) 곡 제목 , 3) 난이도 
	private String titleName;
	private String difficulty;
	private final Beat[] beats;
	
	// 게임 음악 
	private Music gameMusic;
	
	// 점수
	private int totalScore=0;
	private int combo=0;
	private int maxCombo=0;
	
	private final ArrayList<Note> noteList = new ArrayList<>();
	private boolean allNotesSpawned;

	public boolean hasFinishedNotes() {
		// 다른 스레드가 같이 쓰고 있어서 안전하게 쓰기 위해
		synchronized (noteList) {
			return allNotesSpawned && noteList.isEmpty();
		}
	}

	public void drawResult(Graphics2D g) {
		Grade grade = new Grade(totalScore, maxCombo, beats.length);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Dialog", Font.BOLD, 52));
		drawCentered(g, "결과", 180);
		g.setFont(new Font("Dialog", Font.PLAIN, 26));
		drawCentered(g, titleName + " / " + difficulty, 228);
		drawCentered(g, "랭크", 290);
		g.setColor(Color.YELLOW);
		g.setFont(new Font("Dialog", Font.BOLD, 96));
		drawCentered(g, grade.rank, 388);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Dialog", Font.BOLD, 36));
		drawCentered(g, "최종 점수  " + grade.total, 467);
		drawCentered(g, "최고 콤보  " + grade.maxCombo, 532);
	}

	private void drawCentered(Graphics2D g, String text, int y) {
		int x = (Main.SCREEN_WIDTH - g.getFontMetrics().stringWidth(text)) / 2;
		g.drawString(text, x, y);
	}
	
	// <생성자> - 초기화 
	public Game(String titleName, String difficulty, String musicTitle) {
		this(titleName, difficulty, musicTitle, Instrument.DEFAULT);
	}

	public Game(String titleName, String difficulty, String musicTitle, Instrument instrument) {
		this.titleName = titleName;
		this.difficulty = difficulty;
		this.beats = NoteCharts.getBeats(musicTitle, difficulty, instrument);
		this.instrument = instrument;
		this.pressedLanes = new boolean[instrument.lanes.size()];
		gameMusic = new Music(musicTitle, false);
	}
	
	// 게임 화면에서 그려줘야 하는 요소들 
	public void screenDraw(Graphics2D g) {
		drawLanes(g);
		// 3. 게임정보 및  4. 판정선 이미지
		g.drawImage(gameInfoImage, 0, 660, null); // 게임 정보 이미지 - 가수, 곡명, 점수 등이 나오는 반투명 검정색 영역
		g.drawImage(judgementLineImage, 0, instrument.judgementY, null); // 판정선(빨간색 위아래 두 줄) 이미지
		// 5. note 이미지 - noteList(ArrayList)
		Graphics2D noteGraphics = (Graphics2D) g.create();
		synchronized (noteList) {
			for (int i = 0; i < noteList.size(); i++) {
				Note note = noteList.get(i);
				if (note.hasMissed()) {
					judgeImage = new ImageIcon(Main.class.getResource("images/judgeMiss.png")).getImage();
				}
				if (!note.isProceeded()) {
					noteList.remove(i);
					i--;
				} else {
					note.screenDraw(noteGraphics);
				}
			}
		}
		noteGraphics.dispose();
		g.setColor(Color.white);
		// Antialiasing - 글자 안 깨지게.
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		// 7. 곡정보 - 1) 곡 제목 
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 30));
		g.drawString(titleName, 20, 702);
		// 7. 곡정보 - 2) 점수 
		g.setColor(Color.LIGHT_GRAY);
		g.setFont(new Font("Arial", Font.BOLD, 30));
		g.drawString(Integer.toString(totalScore), 600, 702);
		// 7. 곡정보 - 3) 난이도 
		g.drawString(difficulty, 1190, 702);
		// 7. 곡정보 - 4) 콤보
		g.setColor(Color.YELLOW);
		g.setFont(new Font("Arial", Font.BOLD, 30));
		g.drawString(Integer.toString(combo), 1140, 350);
		// 8. 판정 이미지 - 판정 결과(Miss, Late, Good, Great, Perfect, Early) 및 판정결과 아래의 노란 불꽃 이미지 
		g.drawImage(yellowFlareImage, 335, 330, null);
		g.drawImage(judgeImage, 460, 420, null);
		drawKeyPads(g);
	}

	private void drawLanes(Graphics2D g) {
		for (int i = 0; i < instrument.lanes.size(); i++) {
			Lane lane = instrument.lanes.get(i);
			Image route = pressedLanes[i] ? noteRoutePressedImage : noteRouteImage;
			for (int tile = 0; tile < lane.widthInTiles; tile++) {
				g.drawImage(route, lane.x + tile * Lane.TILE_WIDTH, 30, null);
			}
			g.drawImage(noteRouteLineImage, lane.x - 4, 30, null);
		}
		Lane last = instrument.lanes.get(instrument.lanes.size() - 1);
		g.drawImage(noteRouteLineImage, last.x + last.widthInTiles * Lane.TILE_WIDTH, 30, null);
	}

	private void drawKeyPads(Graphics2D g) {
		g.setFont(new Font("Arial", Font.PLAIN, 26));
		g.setColor(Color.DARK_GRAY);
		for (int i = 0; i < instrument.lanes.size(); i++) {
			Lane lane = instrument.lanes.get(i);
			String label = lane.noteName.equals("Space") ? "Space Bar" : lane.noteName;
			int centerX = lane.x + lane.widthInTiles * Lane.TILE_WIDTH / 2;
			g.drawString(label, centerX - g.getFontMetrics().stringWidth(label) / 2, instrument.judgementY + 29);
			Image pad = pressedLanes[i] ? keyPadPressedImage : keyPadImage;
			for (int tile = 0; tile < lane.widthInTiles; tile++) {
				g.drawImage(pad, lane.x + tile * Lane.TILE_WIDTH, instrument.judgementY, null);
			}
		}
	}

	public void keyPressed(int keyCode) {
		for (int i = 0; i < instrument.lanes.size(); i++) {
			Lane lane = instrument.lanes.get(i);
			if (lane.keyCode == keyCode) {
				pressedLanes[i] = true;
				judge(lane.noteName);
				if (lane.soundFile != null) {
					new Music(lane.soundFile, false).start();
				}
				return;
			}
		}
	}

	public void keyReleased(int keyCode) {
		for (int i = 0; i < instrument.lanes.size(); i++) {
			if (instrument.lanes.get(i).keyCode == keyCode) {
				pressedLanes[i] = false;
				return;
			}
		}
	}
	// <run 함수> Game 클래스에서 만든 instance 변수는 run 메소드 안에 있는 내용을 실행한다. 
	@Override
	public void run() {
		dropNotes();
	}
	
	// <Game 쓰레드 종료 함수> 
	public void close() { 
		gameMusic.close();
		this.interrupt();
	}
	
	// <note 떨어뜨리는 함수>
	private void dropNotes() {
		// note 생성
		int i=0;
		gameMusic.start();
		while(i < beats.length && !isInterrupted()) {
			boolean dropped = false;
			if(beats[i].getTime() <= gameMusic.getTime()) { // 비트가 떨어지는 시간대가 gameMusic의 시간보다 작다면 (곡이 진행되고 있다면)  
				Note note = new Note(beats[i].getNoteName(), instrument);  // 노트 만든다
				note.start();
				synchronized (noteList) {
					noteList.add(note);
				}
				i++;
			}
			if(!dropped) {
				try {
					Thread.sleep(5); //5ms 쉬도록. 
				}catch (InterruptedException e) {
					return;
				}
			}
		}
		allNotesSpawned = i == beats.length;
		// note가 떨어지지 않은 경우에는 무한정 반복하는 것이 아니라, 텀을 두면서 note를 떨어뜨림
		// ==> 더 효율적. 자원의 낭비를 줄일 수 있음. 애니메이션이 더 매끄럽게 나온다. 
	}
	
	//<판정 함수> - 유저가 입력한 키가 실제 note의 키와 동일하면 Note.java의 judge함수에서 값을 받아서 judgeEvent 함수에 넘긴다. 
	public void judge(String input) {
		synchronized (noteList) {
			for (Note note : noteList) {
				if (note.isProceeded() && input.equals(note.getNoteType())) {
					String result = note.judge();
					judgeEvent(result);
					comboEvent(result);
					totalScore += Grade.pointsFor(result);
					break;
				}
			}
		}
	}
	
	//<판정 이벤트 함수> - Note.java의 judge결과에 따라 이미지를 노출한다. 
	public void judgeEvent(String judge) {
		if(!judge.equals("None")) { // 판정 결과가 none이 아니라면 노란불꽃 이미지 넣어준다 
			yellowFlareImage = new ImageIcon(Main.class.getResource("images/yellowFlare.png")).getImage();
		}
		if(judge.equals("Miss")) {
			judgeImage = new ImageIcon(Main.class.getResource("images/judgeMiss.png")).getImage();
		}
		else if(judge.equals("Late")) {
			judgeImage = new ImageIcon(Main.class.getResource("images/judgeLate.png")).getImage();
		}
		else if(judge.equals("Good")) {
			judgeImage = new ImageIcon(Main.class.getResource("images/judgeGood.png")).getImage();
		}
		else if(judge.equals("Great")) {
			judgeImage = new ImageIcon(Main.class.getResource("images/judgeGreat.png")).getImage();
		}
		else if(judge.equals("Perfect")) {
			judgeImage = new ImageIcon(Main.class.getResource("images/judgePerfect.png")).getImage();
		}
		else if(judge.equals("Early")) {
			judgeImage = new ImageIcon(Main.class.getResource("images/judgeEarly.png")).getImage();
		}		
	}
	
//	//<채점 함수> - 유저가 입력한 키가 실제 note의 키와 동일하면 Note.java의 score함수에서 값을 받아서 scoreEvent 함수에 넘긴다. 
//	public void score(String input) {
//		for(int i=0; i<noteList.size(); i++) {
//			Note note = noteList.get(i);
//			if(input.equals(note.getNoteType())) {
//				scoreEvent(note.score());
//				break;
//			}
//		}
//	}
	
//	//<채점 이벤트 함수> - Note.java의 judge결과에 따라 이미지를 노출한다.
//	public void scoreEvent(int score) {
//		if(score==10) {
//			totalScore+=10;
//			//System.out.println("Total score="+totalScore);
//		}
//		else if(score==20) {
//			totalScore+=20;
//			//System.out.println("Total score="+totalScore);
//		}
//		else if(score==30) {
//			totalScore+=30;
//			//System.out.println("Total score="+totalScore);
//		}
//		else if(score==40) {
//			totalScore+=40;
//			//System.out.println("Total score="+totalScore);
//		}
//	}
	
	//<콤보 이벤트 함수> - Note.java의 judge결과에 따라 콤보 수를 계산한다.
	public void comboEvent(String judge) {
		if(judge.equals("Good") || judge.equals("Great") || judge.equals("Perfect"))
			combo++;
		else
			combo = 0;
		
		if(maxCombo < combo)
			maxCombo = combo;
	}
}

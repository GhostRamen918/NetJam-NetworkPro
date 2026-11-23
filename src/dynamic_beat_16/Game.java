// Game 클래스 : Game 클래스에서 만들어진 instance 변수를 이용해서 게임을 컨트롤. 
package dynamic_beat_16;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Game extends Thread {
	// Thread : 하나의 프로그램 안에서 실행되는 작은 프로그램.
	// 즉, 전반적인 게임 틀 안에서 하나의 게임이 하나의 단위로써 동작 ==> Thread 이용 
	// 2. note 경로 구분선 이미지 
	private Image noteRouteLineImage = new ImageIcon(Main.class.getResource("images/noteRouteLine.png")).getImage();
	// 3. 게임정보 및 4. 판정선 이미지
	private Image judgementLineImage = new ImageIcon(Main.class.getResource("images/judgementLine.png")).getImage();
	private Image gameInfoImage = new ImageIcon(Main.class.getResource("images/gameInfo.png")).getImage();
	// 1. note 경로 이미지 
	private Image noteRouteSImage = new ImageIcon(Main.class.getResource("images/noteRoute.png")).getImage();
	private Image noteRouteDImage = new ImageIcon(Main.class.getResource("images/noteRoute.png")).getImage();
	private Image noteRouteFImage = new ImageIcon(Main.class.getResource("images/noteRoute.png")).getImage();
	private Image noteRouteSpace1Image = new ImageIcon(Main.class.getResource("images/noteRoute.png")).getImage();
	private Image noteRouteSpace2Image = new ImageIcon(Main.class.getResource("images/noteRoute.png")).getImage();
	private Image noteRouteJImage = new ImageIcon(Main.class.getResource("images/noteRoute.png")).getImage();
	private Image noteRouteKImage = new ImageIcon(Main.class.getResource("images/noteRoute.png")).getImage();
	private Image noteRouteLImage = new ImageIcon(Main.class.getResource("images/noteRoute.png")).getImage();
	// 8. 판정 이미지 - 판정 결과(Miss, Late, Good, Great, Perfect, Early) 및 판정결과 아래의 노란 불꽃 이미지 
	private Image yellowFlareImage;
	private Image judgeImage;
	// 9. 각 키별 키패드 눌렀을 때(빨간색으로), 뗐을 때 이미지 변경 (원래대로)
	private Image keyPadSImage = new ImageIcon(Main.class.getResource("images/keyPadBasic.png")).getImage();
	private Image keyPadDImage = new ImageIcon(Main.class.getResource("images/keyPadBasic.png")).getImage();
	private Image keyPadFImage = new ImageIcon(Main.class.getResource("images/keyPadBasic.png")).getImage();
	private Image keyPadSpace1Image = new ImageIcon(Main.class.getResource("images/keyPadBasic.png")).getImage();
	private Image keyPadSpace2Image = new ImageIcon(Main.class.getResource("images/keyPadBasic.png")).getImage();
	private Image keyPadJImage = new ImageIcon(Main.class.getResource("images/keyPadBasic.png")).getImage();
	private Image keyPadKImage = new ImageIcon(Main.class.getResource("images/keyPadBasic.png")).getImage();
	private Image keyPadLImage = new ImageIcon(Main.class.getResource("images/keyPadBasic.png")).getImage();
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
	
	ArrayList<Note> noteList = new ArrayList<Note>();
	private boolean allNotesSpawned;

	public boolean hasFinishedNotes() {
		return allNotesSpawned && noteList.isEmpty();
	}

	public void drawResult(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Dialog", Font.BOLD, 52));
		drawCentered(g, "결과", 235);
		g.setFont(new Font("Dialog", Font.PLAIN, 26));
		drawCentered(g, titleName + " / " + difficulty, 287);
		g.setFont(new Font("Dialog", Font.BOLD, 36));
		drawCentered(g, "최종 점수  " + totalScore, 395);
		drawCentered(g, "최고 콤보  " + maxCombo, 460);
	}

	private void drawCentered(Graphics2D g, String text, int y) {
		int x = (Main.SCREEN_WIDTH - g.getFontMetrics().stringWidth(text)) / 2;
		g.drawString(text, x, y);
	}
	
	// <생성자> - 초기화 
	public Game(String titleName, String difficulty, String musicTitle) {
		this(titleName, difficulty, musicTitle, NoteCharts.Instrument.DEFAULT);
	}

	public Game(String titleName, String difficulty, String musicTitle, NoteCharts.Instrument instrument) {
		this.titleName = titleName;
		this.difficulty = difficulty;
		this.beats = NoteCharts.getBeats(musicTitle, difficulty, instrument);
		gameMusic = new Music(musicTitle, false);
	}
	
	// 게임 화면에서 그려줘야 하는 요소들 
	public void screenDraw(Graphics2D g) {
		// 1. note 경로 이미지
		g.drawImage(noteRouteSImage, 228, 30, null);
		g.drawImage(noteRouteDImage, 332, 30, null);
		g.drawImage(noteRouteFImage, 436, 30, null);
		g.drawImage(noteRouteSpace1Image, 540, 30, null);
		g.drawImage(noteRouteSpace2Image, 640, 30, null);
		g.drawImage(noteRouteJImage, 744, 30, null);
		g.drawImage(noteRouteKImage, 848, 30, null);
		g.drawImage(noteRouteLImage, 952, 30, null);
		// 2. note 경로 구분선 이미지 
		g.drawImage(noteRouteLineImage, 224, 30, null);
		g.drawImage(noteRouteLineImage, 328, 30, null);
		g.drawImage(noteRouteLineImage, 432, 30, null);
		g.drawImage(noteRouteLineImage, 536, 30, null);
		g.drawImage(noteRouteLineImage, 740, 30, null);
		g.drawImage(noteRouteLineImage, 844, 30, null);
		g.drawImage(noteRouteLineImage, 948, 30, null);
		g.drawImage(noteRouteLineImage, 1052, 30, null);
		// 3. 게임정보 및  4. 판정선 이미지
		g.drawImage(gameInfoImage, 0, 660, null); // 게임 정보 이미지 - 가수, 곡명, 점수 등이 나오는 반투명 검정색 영역
		g.drawImage(judgementLineImage, 0, 580, null); // 판정선(빨간색 위아래 두 줄) 이미지
		// 5. note 이미지 - noteList(ArrayList)
		for(int i=0; i<noteList.size(); i++){  
			Note note = noteList.get(i);
			// Note.java에서 close가 이루어진 note는  더 이상 그려줄 필요가 없음
			// ==> note가 작동하고 있는 상태가 아니라면 noteList에서 지우고 i--해준다. 
			if(note.getY()>620) {
				judgeImage = new ImageIcon(Main.class.getResource("images/judgeMiss.png")).getImage();
			}
			if(!note.isProceeded()) {
				noteList.remove(i);
				i--;
			}
			else {
				note.screenDraw(g);
			}
			// 노트들을 그려준다    
			// 나중에 그릴수록 레이어가 더 위에 올라옴 ==> 판정라인보다 노트가 더 위에 있어야 하니까 노트를 더 뒤에서 그려줌 
		}
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
		// 6. 각각의 note가 키보드에서 어떤 키인지 알려주는 문구  
		g.setFont(new Font("Arial", Font.PLAIN, 26));
		g.setColor(Color.DARK_GRAY);
		g.drawString("S", 270, 609);
		g.drawString("D", 374, 609);
		g.drawString("F", 478, 609);
		g.drawString("Space Bar", 580, 609);
		g.drawString("J", 784, 609);
		g.drawString("K", 889, 609);
		g.drawString("L", 993, 609);
		// 8. 판정 이미지 - 판정 결과(Miss, Late, Good, Great, Perfect, Early) 및 판정결과 아래의 노란 불꽃 이미지 
		g.drawImage(yellowFlareImage, 335, 330, null);
		g.drawImage(judgeImage, 460, 420, null);
		// 9. 각 키별 영역
		g.drawImage(keyPadSImage, 228, 580, null);
		g.drawImage(keyPadDImage, 332, 580, null);
		g.drawImage(keyPadFImage, 436, 580, null);
		g.drawImage(keyPadSpace1Image, 540, 580, null);
		g.drawImage(keyPadSpace2Image, 640, 580, null);
		g.drawImage(keyPadJImage, 744, 580, null);
		g.drawImage(keyPadKImage, 848, 580, null);
		g.drawImage(keyPadLImage, 952, 580, null);
	}
	
	public void pressS() { // S 키 눌렀을 때의 처리
		judge("S");
		noteRouteSImage = new ImageIcon(Main.class.getResource("images/noteRoutePressed.png")).getImage();
		keyPadSImage = new ImageIcon(Main.class.getResource("images/keyPadPressed.png")).getImage();
		// 효과음 삽입 
		new Music("drumSmall1.mp3",false).start();
	}
	public void releaseS() { // S 키 뗐을 때의 처리 
		noteRouteSImage = new ImageIcon(Main.class.getResource("images/noteRoute.png")).getImage();
		keyPadSImage = new ImageIcon(Main.class.getResource("images/keyPadBasic.png")).getImage();
	}
	
	public void pressD() { 
		judge("D");
		noteRouteDImage = new ImageIcon(Main.class.getResource("images/noteRoutePressed.png")).getImage();
		keyPadDImage = new ImageIcon(Main.class.getResource("images/keyPadPressed.png")).getImage();
		new Music("drumSmall1.mp3",false).start();
	}
	public void releaseD() { 
		noteRouteDImage = new ImageIcon(Main.class.getResource("images/noteRoute.png")).getImage();
		keyPadDImage = new ImageIcon(Main.class.getResource("images/keyPadBasic.png")).getImage();
	}
	
	public void pressF() { 
		judge("F");
		noteRouteFImage = new ImageIcon(Main.class.getResource("images/noteRoutePressed.png")).getImage();
		keyPadFImage = new ImageIcon(Main.class.getResource("images/keyPadPressed.png")).getImage();
		new Music("drumSmall1.mp3",false).start();
	}
	public void releaseF() {  
		noteRouteFImage = new ImageIcon(Main.class.getResource("images/noteRoute.png")).getImage();
		keyPadFImage = new ImageIcon(Main.class.getResource("images/keyPadBasic.png")).getImage();
	}
	
	public void pressSpace() { 
		judge("Space");
		noteRouteSpace1Image = new ImageIcon(Main.class.getResource("images/noteRoutePressed.png")).getImage();
		keyPadSpace1Image = new ImageIcon(Main.class.getResource("images/keyPadPressed.png")).getImage();
		noteRouteSpace2Image = new ImageIcon(Main.class.getResource("images/noteRoutePressed.png")).getImage();
		keyPadSpace2Image = new ImageIcon(Main.class.getResource("images/keyPadPressed.png")).getImage();
		new Music("drumBig1.mp3",false).start();
	}
	public void releaseSpace() { 
		noteRouteSpace1Image = new ImageIcon(Main.class.getResource("images/noteRoute.png")).getImage();
		keyPadSpace1Image = new ImageIcon(Main.class.getResource("images/keyPadBasic.png")).getImage();
		noteRouteSpace2Image = new ImageIcon(Main.class.getResource("images/noteRoute.png")).getImage();
		keyPadSpace2Image = new ImageIcon(Main.class.getResource("images/keyPadBasic.png")).getImage();
	}
	public void pressJ() { 
		judge("J");
		noteRouteJImage = new ImageIcon(Main.class.getResource("images/noteRoutePressed.png")).getImage();
		keyPadJImage = new ImageIcon(Main.class.getResource("images/keyPadPressed.png")).getImage();
		new Music("drumSmall1.mp3",false).start();
	}
	public void releaseJ() { 
		noteRouteJImage = new ImageIcon(Main.class.getResource("images/noteRoute.png")).getImage();
		keyPadJImage = new ImageIcon(Main.class.getResource("images/keyPadBasic.png")).getImage();
	}

	public void pressK() { 
		judge("K");
		noteRouteKImage = new ImageIcon(Main.class.getResource("images/noteRoutePressed.png")).getImage();
		keyPadKImage = new ImageIcon(Main.class.getResource("images/keyPadPressed.png")).getImage();
		new Music("drumSmall1.mp3",false).start();
	}
	public void releaseK() { 
		noteRouteKImage = new ImageIcon(Main.class.getResource("images/noteRoute.png")).getImage();
		keyPadKImage = new ImageIcon(Main.class.getResource("images/keyPadBasic.png")).getImage();
	}
	
	public void pressL() {  
		judge("L");
		noteRouteLImage = new ImageIcon(Main.class.getResource("images/noteRoutePressed.png")).getImage();
		keyPadLImage = new ImageIcon(Main.class.getResource("images/keyPadPressed.png")).getImage();
		new Music("drumSmall1.mp3",false).start();
	}
	public void releaseL() { 
		noteRouteLImage = new ImageIcon(Main.class.getResource("images/noteRoute.png")).getImage();
		keyPadLImage = new ImageIcon(Main.class.getResource("images/keyPadBasic.png")).getImage();
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
				Note note = new Note(beats[i].getNoteName());  // 노트 만든다
				note.start();
				noteList.add(note);
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
		for(int i=0; i<noteList.size(); i++) {
			Note note = noteList.get(i);
			if(input.equals(note.getNoteType())) {
				String result = note.judge();
				judgeEvent(result);
				comboEvent(result);
				scoreEvent(note.score());
				break;
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
	
	//<채점 이벤트 함수> - Note.java의 judge결과에 따라 이미지를 노출한다. 
	public void scoreEvent(int score) {
		if(score==10) {
			totalScore+=10;
			//System.out.println("Total score="+totalScore);
		}
		else if(score==20) {
			totalScore+=20;
			//System.out.println("Total score="+totalScore);
		}
		else if(score==30) {
			totalScore+=30;
			//System.out.println("Total score="+totalScore);
		}
		else if(score==40) {
			totalScore+=40;
			//System.out.println("Total score="+totalScore);
		}
	}
	
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

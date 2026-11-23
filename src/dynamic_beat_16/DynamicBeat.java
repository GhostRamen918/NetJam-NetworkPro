// [작업요약] note 판정에 대한 디자인 처리 
package dynamic_beat_16;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import dynamic_beat_16.NoteCharts.Instrument;

public class DynamicBeat extends JFrame {

	private enum GameState {
		INTRO, MAIN, SELECT, PLAYING, FADING_OUT, RESULT, MULTIPLAYER
	}

	private ImageIcon exitButtonEnteredImage = new ImageIcon(Main.class.getResource("images/exitButtonEntered.png"));
	private ImageIcon exitButtonBasicImage = new ImageIcon(Main.class.getResource("images/exitButtonBasic.png"));
	private ImageIcon startButtonEnteredImage = new ImageIcon(
			Main.class.getResource("images/startButtonEntered.png"));
	private ImageIcon startButtonBasicImage = new ImageIcon(Main.class.getResource("images/startButtonBasic.png"));
	private ImageIcon quitButtonEnteredImage = new ImageIcon(Main.class.getResource("images/quitButtonEntered.png"));
	private ImageIcon quitButtonBasicImage = new ImageIcon(Main.class.getResource("images/quitButtonBasic.png"));
	private ImageIcon leftButtonEnteredImage = new ImageIcon(Main.class.getResource("images/leftButtonEntered.png"));
	private ImageIcon leftButtonBasicImage = new ImageIcon(Main.class.getResource("images/leftButtonBasic.png"));
	private ImageIcon rightButtonEnteredImage = new ImageIcon(
			Main.class.getResource("images/rightButtonEntered.png"));
	private ImageIcon rightButtonBasicImage = new ImageIcon(Main.class.getResource("images/rightButtonBasic.png"));
	private ImageIcon easyButtonEnteredImage = new ImageIcon(Main.class.getResource("images/easyButtonEntered.png"));
	private ImageIcon easyButtonBasicImage = new ImageIcon(Main.class.getResource("images/easyButtonBasic.png"));
	private ImageIcon hardButtonEnteredImage = new ImageIcon(
			Main.class.getResource("images/hardButtonEntered.png"));
	private ImageIcon hardButtonBasicImage = new ImageIcon(Main.class.getResource("images/hardButtonBasic.png"));
	private ImageIcon backButtonBasicImage = new ImageIcon(Main.class.getResource("images/backButtonBasic.png"));
	private ImageIcon backButtonEnteredImage = new ImageIcon(Main.class.getResource("images/backButtonEntered.png"));

	private Image background = new ImageIcon(Main.class.getResource("images/introBackground(Title2).jpg"))
			.getImage();
	private JLabel menuBarLabel = new JLabel(new ImageIcon(Main.class.getResource("images/menuBar.png")));

	private JButton exitButton = new JButton(exitButtonBasicImage);
	private JButton startButton = new JButton(startButtonBasicImage);
	private JButton quitButton = new JButton(quitButtonBasicImage);
	private JButton leftButton = new JButton(leftButtonBasicImage);
	private JButton rightButton = new JButton(rightButtonBasicImage);
	private JButton easyButton = new JButton(easyButtonBasicImage);
	private JButton hardButton = new JButton(hardButtonBasicImage);
	private JButton backButton = new JButton(backButtonBasicImage);
	private final JButton singlePlayerButton = new JButton("싱글플레이");
	private final JButton multiplayerButton = new JButton("멀티플레이");
	private final InstSelectPanel instrumentPanel = new InstSelectPanel(() -> refreshInstrumentSelect());
	private final JLabel instrumentChartInfoLabel = new JLabel("Bass");
	private final MultiplayerPanel multiplayerPanel = new MultiplayerPanel(() -> enterMain());
	
	private int mouseX, mouseY;

	private GameState state = GameState.INTRO;
	private static final long RESULT_FADE_NANOS = 1_500_000_000L;
	private Game completedGame;
	private long fadeStartedAt;
	private int fadeOpacity;

	ArrayList<Track> trackList = new ArrayList<Track>();
	
	private Image titleImage;
	private Image selectedImage;
	private Music selectedMusic;
	private Music introMusic = new Music("introMusic.mp3", true);
	private int	nowSelected = 0; 
	
	public static Game game; // 초기화
	// 하나의 게임은, 게임 프로그램 하나가 실행됐을 때, 단 하나의 게임만 진행 가능
	// 즉, 동시에 여러곡을 재생시키면서 게임할 순 없음. 
	// 즉, game이라는 변수는 게임 프로젝트 내에서 통용되는 하나의 변수이므로 public static 붙여주었음. 
	
	private JPanel screenPanel = new JPanel(null) {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			screenDraw((Graphics2D) g);
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			if (state == GameState.FADING_OUT) {
				Graphics overlay = g.create();
				overlay.setColor(new Color(0, 0, 0, fadeOpacity));
				overlay.fillRect(0, 0, getWidth(), getHeight());
				overlay.dispose();
			}
		}
	};
	
	public DynamicBeat() {
		// trackList에 add
		trackList.add(new Track("Energy Title Image.png", 
				"Energy Start Image.png", "Energy Game Image.png", 
				"Energy Selected.mp3", "Bensound - Energy.mp3", "Bensound - Energy"));
		trackList.add(new Track("Wild Flower Title Image.png", 
				"Wild Flower Start Image.png", "Wild Flower Game Image.png", 
				"Wild Flower Selected.mp3", "Joakim Karud - Wild Flower.mp3","Wild Flower - Joakim Karud"));
		trackList.add(new Track("Mighty Love Title Image.png", 
				"Mighty Love Start Image.png", "Mighty Love Game Image.png", 
				"Mighty Love Selected.mp3", "Joakim Karud - Mighty Love.mp3","Joakim Karud - Mighty Love"));

		setUndecorated(true); // 윈도우창 메뉴바 안 보이게끔 
		setTitle("Dynamic Beat");
		setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		setResizable(false); // 창 크기 변경 불가능하게
		setLocationRelativeTo(null); // 창이 정 중앙에 뜨게 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 종료 시 게임 종료되도록
		setContentPane(screenPanel); // 버튼 add 하기 전에 패널 지정 (투명 배경 설정은 삭제, setVisible은 맨 아래로)
		
		addKeyListener(new KeyListener());
		
		introMusic.start();
		
		// exitButton
		exitButton.setBounds(1245, 0, 30, 30);
		exitButton.setBorderPainted(false);
		exitButton.setContentAreaFilled(false);
		exitButton.setFocusPainted(false);
		exitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				exitButton.setIcon(exitButtonEnteredImage);
				exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
				Music buttonEnteredMusic = new Music("buttonEnteredMusic.mp3", false);
				buttonEnteredMusic.start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				exitButton.setIcon(exitButtonBasicImage);
				exitButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				Music buttonEnteredMusic = new Music("buttonPressedMusic.mp3", false);
				buttonEnteredMusic.start();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				System.exit(0);
			}
		});
		add(exitButton);

		// startButton
		startButton.setBounds(40, 200, 300, 75);
		startButton.setBorderPainted(false);
		startButton.setContentAreaFilled(false);
		startButton.setFocusPainted(false);
		startButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				startButton.setIcon(startButtonEnteredImage);
				startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
				Music buttonEnteredMusic = new Music("buttonEnteredMusic.mp3", false);
				buttonEnteredMusic.start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				startButton.setIcon(startButtonBasicImage);
				startButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				Music buttonEnteredMusic = new Music("buttonPressedMusic.mp3", false);
				buttonEnteredMusic.start();
				// 시작버튼 이벤트 
				enterMain(); // 시작버튼 클릭 시 enterMain 함수(메서드) 실행 

			}
		});
		add(startButton);

		// quitButton
		quitButton.setBounds(40, 300, 300, 75);
		quitButton.setBorderPainted(false);
		quitButton.setContentAreaFilled(false);
		quitButton.setFocusPainted(false);
		quitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				quitButton.setIcon(quitButtonEnteredImage);
				quitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
				Music buttonEnteredMusic = new Music("buttonEnteredMusic.mp3", false);
				buttonEnteredMusic.start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				quitButton.setIcon(quitButtonBasicImage);
				quitButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				Music buttonEnteredMusic = new Music("buttonPressedMusic.mp3", false);
				buttonEnteredMusic.start();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				System.exit(0);
			}
		});
		add(quitButton);

		Font modeButtonFont = new Font("Dialog", Font.BOLD, 26);
		singlePlayerButton.setBounds(490, 260, 300, 70);
		singlePlayerButton.setFont(modeButtonFont);
		singlePlayerButton.setVisible(false);
		singlePlayerButton.addActionListener(e -> enterTrackSelection());
		add(singlePlayerButton);

		multiplayerButton.setBounds(490, 350, 300, 70);
		multiplayerButton.setFont(modeButtonFont);
		multiplayerButton.setVisible(false);
		multiplayerButton.addActionListener(e -> enterMultiplayer());
		add(multiplayerButton);
		multiplayerPanel.setBounds(0, 30, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT - 30);
		multiplayerPanel.setVisible(false);
		add(multiplayerPanel);

		instrumentPanel.setBounds(430, 35, 420, 60);
		instrumentPanel.setVisible(false);
		add(instrumentPanel);

		// leftButton
		leftButton.setVisible(false);
		leftButton.setBounds(140, 310, 60, 60);
		leftButton.setBorderPainted(false);
		leftButton.setContentAreaFilled(false);
		leftButton.setFocusPainted(false);
		leftButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				leftButton.setIcon(leftButtonEnteredImage);
				leftButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
				Music buttonEnteredMusic = new Music("buttonEnteredMusic.mp3", false);
				buttonEnteredMusic.start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				leftButton.setIcon(leftButtonBasicImage);
				leftButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				Music buttonEnteredMusic = new Music("buttonPressedMusic.mp3", false);
				buttonEnteredMusic.start();
				// 왼쪽 버튼 이벤트 
				selectLeft();
			}
		});
		add(leftButton);
		
		// rightButton
		rightButton.setVisible(false);
		rightButton.setBounds(1080, 310, 60, 60);
		rightButton.setBorderPainted(false);
		rightButton.setContentAreaFilled(false);
		rightButton.setFocusPainted(false);
		rightButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				rightButton.setIcon(rightButtonEnteredImage);
				rightButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
				Music buttonEnteredMusic = new Music("buttonEnteredMusic.mp3", false);
				buttonEnteredMusic.start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				rightButton.setIcon(rightButtonBasicImage);
				rightButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				Music buttonEnteredMusic = new Music("buttonPressedMusic.mp3", false);
				buttonEnteredMusic.start();
				// 오른쪽 버튼 이벤트 
				selectRight();
			}
		});
		add(rightButton);
		
		// easyButton
		easyButton.setVisible(false);
		easyButton.setBounds(375, 580, 250, 67);
		easyButton.setBorderPainted(false);
		easyButton.setContentAreaFilled(false);
		easyButton.setFocusPainted(false);
		easyButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				easyButton.setIcon(easyButtonEnteredImage);
				easyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
				Music buttonEnteredMusic = new Music("buttonEnteredMusic.mp3", false);
				buttonEnteredMusic.start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				easyButton.setIcon(easyButtonBasicImage);
				easyButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				Music buttonEnteredMusic = new Music("buttonPressedMusic.mp3", false);
				buttonEnteredMusic.start();
				// easy 버튼 이벤트 
				gameStart(nowSelected, "Easy");

			}
		});
		add(easyButton);
		
		// hardButton
		hardButton.setVisible(false);
		hardButton.setBounds(655, 580, 250 ,67);
		hardButton.setBorderPainted(false);
		hardButton.setContentAreaFilled(false);
		hardButton.setFocusPainted(false);
		hardButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				hardButton.setIcon(hardButtonEnteredImage);
				hardButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
				Music buttonEnteredMusic = new Music("buttonEnteredMusic.mp3", false);
				buttonEnteredMusic.start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				hardButton.setIcon(hardButtonBasicImage);
				hardButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				Music buttonEnteredMusic = new Music("buttonPressedMusic.mp3", false);
				buttonEnteredMusic.start();
				// hard 버튼 이벤트
				gameStart(nowSelected, "Hard");

			}
		});
		add(hardButton);
		
		// backButton
		backButton.setVisible(false);
		backButton.setBounds(20, 50, 60 ,60);
		backButton.setBorderPainted(false);
		backButton.setContentAreaFilled(false);
		backButton.setFocusPainted(false);
		backButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				backButton.setIcon(backButtonEnteredImage);
				backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
				Music buttonEnteredMusic = new Music("buttonEnteredMusic.mp3", false);
				buttonEnteredMusic.start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				backButton.setIcon(backButtonBasicImage);
				backButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				Music buttonEnteredMusic = new Music("buttonPressedMusic.mp3", false);
				buttonEnteredMusic.start();
				// 백버튼 이벤트 
				goBack();
			}
		});
		add(backButton);

		menuBarLabel.setBounds(0, 0, 1280, 30);
		menuBarLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
			}
		});
		menuBarLabel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int x = e.getXOnScreen();
				int y = e.getYOnScreen();
				setLocation(x - mouseX, y - mouseY);
			}
		});
		add(menuBarLabel);

		new Timer(Main.SLEEP_TIME, e -> {
			updateGameScreen();
			screenPanel.repaint();
		}).start();
		setVisible(true); // 모든 설정이 끝난 뒤 마지막에 창 띄우기
	}

	public void screenDraw(Graphics2D g) {
		if (state == GameState.RESULT) {
			completedGame.drawResult(g);
			return;
		}
		g.drawImage(background, 0, 0, null);
		if (state == GameState.SELECT) {
			g.drawImage(selectedImage, 340, 100, null);
			g.drawImage(titleImage, 340, 110, null);
		} else if (state == GameState.PLAYING) {
			game.screenDraw(g);
		} else if (state == GameState.FADING_OUT) {
			completedGame.screenDraw(g);
		}
		// 버튼은 패널이 알아서 그리고, 반복해서 다시 그리는 건 Timer가 담당
	}
	
	private void updateGameScreen() {
		if (state == GameState.PLAYING) {
			//game.update();
			if (game.hasFinishedNotes()) {
				completedGame = game;
				game = null;
				fadeOpacity = 0;
				fadeStartedAt = System.nanoTime();
				state = GameState.FADING_OUT;
			}
		} else if (state == GameState.FADING_OUT) {
			if (fadeOpacity == 255) {
				completedGame.close();
				state = GameState.RESULT;
			} else {
				long elapsed = System.nanoTime() - fadeStartedAt;
				fadeOpacity = (int) Math.min(255, elapsed * 255 / RESULT_FADE_NANOS);
			}
		}
	}

	// <곡 선택 함수>
	public void selectTrack(int nowSelected) {
		if(selectedMusic != null) // 이미 어떤 곡이 실행되고 있었다면 실행되고 있던 곡을 종료시킴
			selectedMusic.close();
		titleImage = new ImageIcon(Main.class.getResource("images/"+trackList.get(nowSelected).getTitleImage()))
				.getImage();
		selectedImage = new ImageIcon(Main.class.getResource("images/"+trackList.get(nowSelected).getStartImage()))
				.getImage();
		selectedMusic = new Music(trackList.get(nowSelected).getStartMusic(),true);
		selectedMusic.start();
		updateDifficultyButtons(nowSelected);
	}

	private void updateDifficultyButtons(int trackIndex) {
		String musicFile = trackList.get(trackIndex).getGameMusic();
		Instrument instrument = instrumentPanel.getSelectedInstrument();
		boolean hasEasy = NoteCharts.hasChart(musicFile, "Easy", instrument);
		boolean hasHard = NoteCharts.hasChart(musicFile, "Hard", instrument);
		easyButton.setEnabled(hasEasy);
		hardButton.setEnabled(hasHard);
	}
	
	// <leftButton 이벤트 처리 함수>
	public void selectLeft() {
		// 0번째 곡일때 왼쪽 버튼을 누르면 가장 마지막 곡이 나오도록. 
		if(nowSelected == 0)
			nowSelected = trackList.size() -1;
		else 
			nowSelected --;
		selectTrack(nowSelected);

	}
	
	// <rightButton 이벤트 처리 함수>
	public void selectRight() {
		// 제일 마지막 곡일 때 오른쪽 버튼을 누르면 가장 첫번째 곡이 나오도록.
		if (nowSelected == trackList.size() - 1)
			nowSelected = 0;
		else
			nowSelected++;
		selectTrack(nowSelected);

	}

	// <게임시작 함수> 
	public void gameStart(int nowSelected, String difficulty) {
		Track track = trackList.get(nowSelected);
		Instrument instrument = instrumentPanel.getSelectedInstrument();
		if (!NoteCharts.hasChart(track.getGameMusic(), difficulty, instrument)) {
			return;
		}
		if(selectedMusic != null) // 이미 어떤 음악이 실행중이라면 그 음악 중지시킴 
			selectedMusic.close();
		selectedMusic = null;
		setTrackButtonsVisible(false);
		// 게임화면으로 넘어갔으니 배경 이미지 바꿔줌
		background = new ImageIcon(Main.class.getResource("images/" + track.getGameImage())).getImage();
		backButton.setVisible(true);
		state = GameState.PLAYING;
		game = new Game(track.getTitleName(), difficulty, track.getGameMusic(), instrument);
		game.start();
		setFocusable(true); //게임창 (메인 프레임)에 키보드 포커스가 맞춰져 있도록 
		requestFocusInWindow();
		// 화면이 바뀜에 따라 focus가 맞지 않을 수도 있음.
		// 게임 시작 시에 항상 main에 키보드 포커스가 맞춰져 있어야 키보드를 눌렀을 때 키보드 이벤트가 정상적으로 작동함.
	}
	
	private void goBack() {
		if (state == GameState.SELECT) {
			selectedMusic.close();
			selectedMusic = null;
			enterMain();
			return;
		}
		if (state == GameState.PLAYING) {
			game.close();
			game = null;
		} else if (state == GameState.FADING_OUT) {
			completedGame.close();
		}
		completedGame = null;
		fadeOpacity = 0;
		enterTrackSelection();
	}
	
	// <시작버튼 이벤트 처리 함수> 
	// : intro화면 → 메인 화면 
	public void enterMain() {
		if (state == GameState.INTRO) {
			introMusic.close();
		}
		state = GameState.MAIN;
		multiplayerPanel.setVisible(false);
		startButton.setVisible(false);
		quitButton.setVisible(false);
		setTrackButtonsVisible(false);
		backButton.setVisible(false);
		background = new ImageIcon(Main.class.getResource("images/mainBackground.jpg")).getImage();
		singlePlayerButton.setVisible(true);
		multiplayerButton.setVisible(true);
	}

	private void enterMultiplayer() {
		state = GameState.MULTIPLAYER;
		singlePlayerButton.setVisible(false);
		multiplayerButton.setVisible(false);
		multiplayerPanel.showCreateRoom();
		multiplayerPanel.setVisible(true);
	}

	private void refreshInstrumentSelect() {
		updateDifficultyButtons(nowSelected);
	}

	private void enterTrackSelection() {
		state = GameState.SELECT;
		singlePlayerButton.setVisible(false);
		multiplayerButton.setVisible(false);
		setTrackButtonsVisible(true);
		backButton.setVisible(true);
		background = new ImageIcon(Main.class.getResource("images/mainBackground.jpg")).getImage();
		selectTrack(nowSelected);
	}

	private void setTrackButtonsVisible(boolean visible) {
		instrumentPanel.setVisible(visible);
		leftButton.setVisible(visible);
		rightButton.setVisible(visible);
		easyButton.setVisible(visible);
		hardButton.setVisible(visible);
		instrumentChartInfoLabel.setVisible(visible && instrumentPanel.getSelectedInstrument() == Instrument.BASS);
	}

}



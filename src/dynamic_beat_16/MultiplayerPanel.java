package dynamic_beat_16;

import java.awt.*;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

final class MultiplayerPanel extends JPanel {
	private Room[] previewRooms = {
		new Room("012", "예시용", 2, 6)
	};

	private static final class Room {
		private final String number;
		private final String name;
		private final int occupants;
		private final int capacity;

		Room(String number, String name, int occupants, int capacity) {
			this.number = number;
			this.name = name;
			this.occupants = occupants;
			this.capacity = capacity;
		}

		@Override
		public String toString() {
			return "방 " + number + "  " + name + "  " + occupants + " / " + capacity + "명";
		}
	}

	private final CardLayout cards = new CardLayout();
	private final JTextField roomNameTextField = new JTextField(25);
	private final JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(6, 2, 6, 1));
	private final JLabel errorLabel = new JLabel(" ");
	private final JPanel lobbyPanel = new JPanel(new BorderLayout(0, 16));

	MultiplayerPanel(Runnable leaveMultiplayer) {
		setLayout(cards);
		add(createRoomPanel(leaveMultiplayer), "create");
		add(lobbyPanel, "lobby");
	}

	void showCreateRoom() {
		capacitySpinner.setValue(6);
		errorLabel.setText(" ");
		lobbyPanel.removeAll();
		cards.show(this, "create");
	}

	private JPanel createRoomPanel(Runnable leaveMultiplayer) {
		JPanel pagePanel = new JPanel(new BorderLayout(0, 16));
		JButton backButton = new JButton("메인 메뉴");
		backButton.addActionListener(e -> leaveMultiplayer.run());
		pagePanel.add(headerPanel("멀티플레이", backButton), BorderLayout.NORTH);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		JPanel roomSelectionPanel = new JPanel(new GridLayout(1, 2, 20, 0));
		roomSelectionPanel.add(openRooms());
		JPanel formPanel = new JPanel();
		formPanel.setLayout(new BorderLayout(0, 18));
		formPanel.add(new JLabel("방 만들기"), BorderLayout.NORTH);

		JPanel roomMakePanel = new JPanel(new GridLayout(2, 1, 0, 14));
		// 인원수는 스피너의 화살표로 선택한다.
		((JSpinner.DefaultEditor) capacitySpinner.getEditor()).getTextField().setEditable(false);
		roomMakePanel.add(formFieldPanel("방 이름", roomNameTextField));
		roomMakePanel.add(formFieldPanel("최대 인원", capacitySpinner));
		formPanel.add(roomMakePanel, BorderLayout.CENTER);

		JButton createRoomButton = new JButton("방 만들기");
		createRoomButton.addActionListener(e -> {
			String name = roomNameTextField.getText().trim();
			if (name.isEmpty() || name.length() > 24) {
				errorLabel.setText("방 이름을 1~24자로 입력해 주세요.");
				roomNameTextField.requestFocusInWindow();
				return;
			}
			showLobby(new Room(".", name, 0, (Integer) capacitySpinner.getValue()));
		});
		roomNameTextField.addActionListener(e -> createRoomButton.doClick());
		JPanel actionPanel = new JPanel(new BorderLayout(12, 0));
		actionPanel.add(errorLabel, BorderLayout.CENTER);
		actionPanel.add(createRoomButton, BorderLayout.EAST);

		formPanel.add(actionPanel, BorderLayout.SOUTH);
		roomSelectionPanel.add(formPanel);
		roomSelectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, roomSelectionPanel.getPreferredSize().height));
		centerPanel.add(roomSelectionPanel);
		pagePanel.add(centerPanel, BorderLayout.CENTER);
		return pagePanel;
	}

	private JPanel openRooms() {
		JPanel roomListPanel = new JPanel();
		roomListPanel.setLayout(new BorderLayout(0, 18));
		roomListPanel.add(new JLabel("열린 방"), BorderLayout.NORTH);

		JList<Room> roomList = new JList<>(previewRooms);
		roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane roomScrollPane = new JScrollPane(roomList);
		roomListPanel.add(roomScrollPane, BorderLayout.CENTER);

		JButton joinButton = new JButton("입장하기");
		joinButton.setEnabled(false);
		roomList.addListSelectionListener(e -> joinButton.setEnabled(!roomList.isSelectionEmpty()));
		joinButton.addActionListener(e -> {
			Room selectedRoom = roomList.getSelectedValue();
			if (selectedRoom != null) {
				showLobby(selectedRoom);
			}
		});
		JPanel roomPanel = new JPanel(new BorderLayout(12, 0));
		roomPanel.add(new JLabel("예시 방 목록"), BorderLayout.WEST);
		roomPanel.add(joinButton, BorderLayout.EAST);
		roomListPanel.add(roomPanel, BorderLayout.SOUTH);
		return roomListPanel;
	}

	private void showLobby(Room selectedRoom) {
		int capacity = selectedRoom.capacity;
		int occupied = selectedRoom.occupants;
		int participantsCount = occupied + 1;
		lobbyPanel.removeAll();
		JButton leaveButton = new JButton("나가기");
		leaveButton.addActionListener(e -> cards.show(this, "create"));
		JButton settingsButton = new JButton("방 설정");
		settingsButton.setEnabled(false);

		JPanel roomActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		roomActionPanel.add(settingsButton);
		roomActionPanel.add(leaveButton);
		JPanel topPanel = new JPanel(new BorderLayout(0, 16));
		topPanel.add(headerPanel("멀티플레이 / 대기실", roomActionPanel), BorderLayout.NORTH);
		JPanel roomPanel = new JPanel();
		roomPanel.setLayout(new BorderLayout(18, 0));
		roomPanel.add(new JLabel("방 번호  " + selectedRoom.number), BorderLayout.WEST);
		roomPanel.add(new JLabel(selectedRoom.name), BorderLayout.CENTER);
		roomPanel.add(new JLabel("미리보기"), BorderLayout.EAST);
		topPanel.add(roomPanel, BorderLayout.CENTER);
		lobbyPanel.add(topPanel, BorderLayout.NORTH);

		JPanel participantPanel = new JPanel();
		participantPanel.setLayout(new BorderLayout(0, 12));
		JPanel summaryPanel = new JPanel(new BorderLayout());
		summaryPanel.add(new JLabel("참가자  " + participantsCount + " / " + capacity), BorderLayout.WEST);
		summaryPanel.add(new JLabel("0명 준비 완료"), BorderLayout.EAST);
		participantPanel.add(summaryPanel, BorderLayout.NORTH);

		JPanel slotPanel = new JPanel(new GridLayout((capacity + 1) / 2, 2, 12, 10));
		if (occupied > 0) {
			slotPanel.add(participantSlot("방장", "준비 전"));
		}
		for (int i = 1; i < occupied; i++) {
			slotPanel.add(participantSlot("참가자 " + (i + 1), "준비 전"));
		}
		slotPanel.add(participantSlot(occupied == 0 ? "나" : "나", "준비 전"));
		for (int i = participantsCount; i < capacity; i++) {
			slotPanel.add(participantSlot("참가자 기다리는 중", ""));
		}
		JPanel slotAreaPanel = new JPanel(new BorderLayout());
		slotAreaPanel.add(slotPanel, BorderLayout.NORTH);
		participantPanel.add(slotAreaPanel, BorderLayout.CENTER);
		lobbyPanel.add(participantPanel, BorderLayout.CENTER);

		JPanel footerPanel = new JPanel();
		footerPanel.setLayout(new BorderLayout(12, 0));
		footerPanel.add(new JLabel("준비 완료  0 / " + participantsCount), BorderLayout.WEST);
		JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
		JButton readyButton = new JButton("준비하기");
		readyButton.setEnabled(false);
		JButton startButton = new JButton("게임 시작");
		startButton.setEnabled(false);
		controlPanel.add(readyButton);
		controlPanel.add(startButton);
		footerPanel.add(controlPanel, BorderLayout.EAST);
		lobbyPanel.add(footerPanel, BorderLayout.SOUTH);
		cards.show(this, "lobby");
		lobbyPanel.revalidate();
		lobbyPanel.repaint();
	}

	private static JPanel participantSlot(String name, String status) {
		JPanel slotPanel = new JPanel();
		slotPanel.setLayout(new BorderLayout(10, 0));
		slotPanel.add(new JLabel(name), BorderLayout.CENTER);
		slotPanel.add(new JLabel(status), BorderLayout.EAST);
		slotPanel.setBackground(Color.WHITE);
		return slotPanel;
	}

	private static JPanel headerPanel(String title, JComponent actionComponent) {
		JPanel headerPanel = new JPanel(new BorderLayout(12, 0));
		headerPanel.add(new JLabel(title), BorderLayout.EAST);
		headerPanel.add(actionComponent, BorderLayout.WEST);
		return headerPanel;
	}

	private static JPanel formFieldPanel(String name, JComponent inputComponent) {
		JPanel fieldPanel = new JPanel(new BorderLayout(0, 6));
		JLabel captionLabel = new JLabel(name);
		fieldPanel.add(captionLabel, BorderLayout.NORTH);
		fieldPanel.add(inputComponent, BorderLayout.CENTER);
		return fieldPanel;
	}

}

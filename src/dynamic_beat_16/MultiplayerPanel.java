package dynamic_beat_16;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
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
	private static final Color BACKGROUND = Color.WHITE;
	private static final Color TEXT = Color.BLACK;
	private static final Color MUTED = Color.GRAY;
	private static final Color BORDER = Color.WHITE;
	private static final Color LOCAL_PLAYER_BACKGROUND = Color.LIGHT_GRAY;
	private static final Color TEAL = new Color(0, 107, 124);
	private static final Room[] PREVIEW_ROOMS = {
		new Room("012", "예시용", 2, 6)
	};

	private record Room(String number, String name, int occupants, int capacity) {
		public String toString() {
			return "방 " + number + "   ·   " + name + "   ·   " + occupants + " / " + capacity + "명";
		}
	}

	private final CardLayout cards = new CardLayout();
	private final JTextField roomNameField = new JTextField(25);
	private final JSpinner capacityField = new JSpinner(new SpinnerNumberModel(6, 2, 6, 1));
	private final JLabel errorLabel = label(" ", 14, Font.PLAIN, Color.red);
	private final JPanel lobbyPanel = MPanel();

	MultiplayerPanel(Runnable leaveMultiplayer) {
		setLayout(cards);
		add(createRoomPanel(leaveMultiplayer), "create");
		add(lobbyPanel, "lobby");
	}

	void showCreateRoom() {
		capacityField.setValue(6);
		errorLabel.setText(" ");
		lobbyPanel.removeAll();
		cards.show(this, "create");
	}

	private JPanel createRoomPanel(Runnable leaveMultiplayer) {
		JPanel p = MPanel();
		JButton backButton = button("메인 메뉴");
		backButton.addActionListener(e -> leaveMultiplayer.run());
		p.add(header("멀티플레이", backButton), BorderLayout.NORTH);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setOpaque(false);
		JPanel roomSelectionPanel = new JPanel(new GridLayout(1, 2, 20, 0));
		roomSelectionPanel.setOpaque(false);
		roomSelectionPanel.add(openRooms());
		JPanel formPanel = cardPanel(28);
		formPanel.setLayout(new BorderLayout(0, 18));
		formPanel.add(label("방 만들기", 28, Font.BOLD, TEXT), BorderLayout.NORTH);

		JPanel roomMakePanel = new JPanel(new GridLayout(2, 1, 0, 14));
		roomMakePanel.setOpaque(false);
		roomNameField.setFont(font(18, Font.PLAIN));
		capacityField.setFont(font(18, Font.PLAIN));
		// 인원수는 스피너의 화살표로 선택한다.
		((JSpinner.DefaultEditor) capacityField.getEditor()).getTextField().setEditable(false);
		roomMakePanel.add(formField("방 이름", roomNameField));
		roomMakePanel.add(formField("최대 인원", capacityField));
		formPanel.add(roomMakePanel, BorderLayout.CENTER);

		JButton create = button("방 만들기");
		create.setBackground(TEAL);
		create.setForeground(Color.WHITE);
		create.addActionListener(e -> {
			String name = roomNameField.getText().strip();
			if (name.isEmpty() || name.codePointCount(0, name.length()) > 24) {
				errorLabel.setText("방 이름을 1~24자로 입력해 주세요.");
				roomNameField.requestFocusInWindow();
				return;
			}
			showLobby(new Room("—", name, 0, (Integer) capacityField.getValue()));
		});
		roomNameField.addActionListener(e -> create.doClick());
		JPanel actionPanel = new JPanel(new BorderLayout(12, 0));
		actionPanel.setOpaque(false);
		actionPanel.add(errorLabel, BorderLayout.CENTER);
		actionPanel.add(create, BorderLayout.EAST);
		formPanel.add(actionPanel, BorderLayout.SOUTH);
		roomSelectionPanel.add(formPanel);
		roomSelectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, roomSelectionPanel.getPreferredSize().height));
		centerPanel.add(Box.createVerticalGlue());
		centerPanel.add(roomSelectionPanel);
		centerPanel.add(Box.createVerticalGlue());
		p.add(centerPanel, BorderLayout.CENTER);
		return p;
	}

	private JPanel openRooms() {
		JPanel RoomListPanel = cardPanel(28);
		RoomListPanel.setLayout(new BorderLayout(0, 18));
		RoomListPanel.add(label("열린 방", 28, Font.BOLD, TEXT), BorderLayout.NORTH);

		JList<Room> rooms = new JList<>(PREVIEW_ROOMS);
		rooms.setFont(font(16, Font.PLAIN));
		rooms.setFixedCellHeight(40);
		rooms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane roomScroll = new JScrollPane(rooms);
		roomScroll.setPreferredSize(new Dimension(400, 202));
		RoomListPanel.add(roomScroll, BorderLayout.CENTER);

		JButton join = button("입장하기");
		join.setEnabled(false);
		rooms.addListSelectionListener(e -> join.setEnabled(!rooms.isSelectionEmpty()));
		join.addActionListener(e -> {
			Room selectedRoom = rooms.getSelectedValue();
			if (selectedRoom != null) {
				showLobby(selectedRoom);
			}
		});
		JPanel roomPanel = new JPanel(new BorderLayout(12, 0));
		roomPanel.setOpaque(false);
		roomPanel.add(label("예시 방 목록", 14, Font.PLAIN, MUTED), BorderLayout.WEST);
		roomPanel.add(join, BorderLayout.EAST);
		RoomListPanel.add(roomPanel, BorderLayout.SOUTH);
		return RoomListPanel;
	}

	private void showLobby(Room selectedRoom) {
		int capacity = selectedRoom.capacity();
		int occupied = selectedRoom.occupants();
		int participantsCount = occupied + 1;
		lobbyPanel.removeAll();
		JButton leave = button("나가기");
		leave.addActionListener(e -> cards.show(this, "create"));
		JButton settings = button("방 설정");
		settings.setEnabled(false);
		JPanel roomActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		roomActionPanel.setOpaque(false);
		roomActionPanel.add(settings);
		roomActionPanel.add(leave);
		JPanel topPanel = new JPanel(new BorderLayout(0, 16));
		topPanel.setOpaque(false);
		topPanel.add(header("멀티플레이 / 대기실", roomActionPanel), BorderLayout.NORTH);
		JPanel roomPanel = cardPanel(16);
		roomPanel.setLayout(new BorderLayout(18, 0));
		roomPanel.add(label("방 번호  " + selectedRoom.number(), 16, Font.BOLD, MUTED), BorderLayout.WEST);
		roomPanel.add(label(selectedRoom.name(), 28, Font.BOLD, TEXT), BorderLayout.CENTER);
		roomPanel.add(label("미리보기", 16, Font.BOLD, TEAL), BorderLayout.EAST);
		topPanel.add(roomPanel, BorderLayout.CENTER);
		lobbyPanel.add(topPanel, BorderLayout.NORTH);

		JPanel participantPanel = cardPanel(16);
		participantPanel.setLayout(new BorderLayout(0, 12));
		JPanel summaryPanel = new JPanel(new BorderLayout());
		summaryPanel.setOpaque(false);
		summaryPanel.add(label("참가자  " + participantsCount + " / " + capacity, 20, Font.BOLD, TEXT), BorderLayout.WEST);
		summaryPanel.add(label("0명 준비 완료", 16, Font.PLAIN, TEAL), BorderLayout.EAST);
		participantPanel.add(summaryPanel, BorderLayout.NORTH);

		JPanel slotPanel = new JPanel(new GridLayout((capacity + 1) / 2, 2, 12, 10));
		slotPanel.setOpaque(false);
		if (occupied > 0) {
			slotPanel.add(participantSlot("방장", "준비 전", false));
		}
		for (int i = 1; i < occupied; i++) {
			slotPanel.add(participantSlot("참가자 " + (i + 1), "준비 전", false));
		}
		slotPanel.add(participantSlot(occupied == 0 ? "나 · 방장" : "나", "준비 전", true));
		for (int i = participantsCount; i < capacity; i++) {
			slotPanel.add(participantSlot("참가자 기다리는 중", "", false));
		}
		int rows = (capacity + 1) / 2;
		slotPanel.setPreferredSize(new Dimension(0, rows * 80 + (rows - 1) * 10));
		JPanel slotAreaPanel = new JPanel(new BorderLayout());
		slotAreaPanel.setOpaque(false);
		slotAreaPanel.add(slotPanel, BorderLayout.NORTH);
		participantPanel.add(slotAreaPanel, BorderLayout.CENTER);
		lobbyPanel.add(participantPanel, BorderLayout.CENTER);

		JPanel footerPanel = cardPanel(16);
		footerPanel.setLayout(new BorderLayout(12, 0));
		footerPanel.add(label("준비 완료  0 / " + participantsCount, 20, Font.BOLD, TEXT), BorderLayout.WEST);
		JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
		controlPanel.setOpaque(false);
		JButton ready = button("준비하기");
		ready.setEnabled(false);
		JButton start = button("게임 시작");
		start.setEnabled(false);
		controlPanel.add(ready);
		controlPanel.add(start);
		footerPanel.add(controlPanel, BorderLayout.EAST);
		lobbyPanel.add(footerPanel, BorderLayout.SOUTH);
		cards.show(this, "lobby");
		lobbyPanel.revalidate();
		lobbyPanel.repaint();
	}

	private static JPanel participantSlot(String name, String status, boolean localPlayer) {
		JPanel slotPanel = cardPanel(12);
		slotPanel.setLayout(new BorderLayout(10, 0));
		slotPanel.setBackground(localPlayer ? LOCAL_PLAYER_BACKGROUND : BACKGROUND);
		slotPanel.add(label(name, 18, localPlayer ? Font.BOLD : Font.PLAIN, localPlayer ? TEXT : MUTED), BorderLayout.CENTER);
		slotPanel.add(label(status, 16, Font.BOLD, TEAL), BorderLayout.EAST);
		return slotPanel;
	}

	private static JPanel MPanel() {
		JPanel p = new JPanel(new BorderLayout(0, 16));
		p.setBackground(BACKGROUND);
		p.setBorder(BorderFactory.createEmptyBorder(22, 36, 22, 36));
		return p;
	}

	private static JPanel header(String title, JComponent action) {
		JPanel headerPanel = new JPanel(new BorderLayout(12, 0));
		headerPanel.setOpaque(false);
		headerPanel.add(label(title, 22, Font.BOLD, TEXT), BorderLayout.EAST);
		headerPanel.add(action, BorderLayout.WEST);
		return headerPanel;
	}

	private static JPanel formField(String name, JComponent input) {
		JPanel fieldPanel = new JPanel(new BorderLayout(0, 6));
		fieldPanel.setOpaque(false);
		JLabel captionLabel = label(name, 16, Font.BOLD, TEXT);
		captionLabel.setLabelFor(input);
		fieldPanel.add(captionLabel, BorderLayout.NORTH);
		input.setPreferredSize(new Dimension(380, 38));
		fieldPanel.add(input, BorderLayout.CENTER);
		return fieldPanel;
	}

	private static JPanel cardPanel(int padding) {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),
			BorderFactory.createEmptyBorder(padding, padding, padding, padding)));
		return panel;
	}

	private static JLabel label(String text, int size, int style, Color color) {
		JLabel label = new JLabel();
		label.setText(text);
		label.setFont(font(size, style));
		label.setForeground(color);
		return label;
	}

	private static JButton button(String text) {
		JButton button = new JButton(text);
		button.setFont(font(16, Font.BOLD));
		button.setForeground(TEXT);
		return button;
	}

	private static Font font(int size, int style) {
		return new Font("Dialog", style, size);
	}
}

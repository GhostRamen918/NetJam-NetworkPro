package dynamic_beat_16;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

final class MultiplayerPanel extends JPanel {
	private static final Color BACKGROUND = new Color(242, 247, 251);
	private static final Color TEXT = new Color(19, 32, 53);
	private static final Color MUTED = new Color(74, 94, 120);
	private static final Color BORDER = new Color(207, 220, 233);
	private static final Color LOCAL_PLAYER_BACKGROUND = Color.white;
	private static final Color TEAL = new Color(0, 107, 124);
	private static final Room[] PREVIEW_ROOMS = {
		new Room("024", "예시용", 2, 6)
	};

	private record Room(String number, String name, int occupants, int capacity) {}

	private final CardLayout cards = new CardLayout();
	private final JTextField roomNameField = new JTextField(25);
	private final JSpinner capacityField = new JSpinner(new SpinnerNumberModel(6, 2, 6, 1));
	private final JLabel roomNameError = label(" ", 14, Font.PLAIN, Color.red);
	private final JPanel lobbyPage = page();

	MultiplayerPanel(Runnable leaveMultiplayer) {
		setLayout(cards);
		add(createRoomPage(leaveMultiplayer), "create");
		add(lobbyPage, "lobby");
	}

	void showCreateRoom() {
		capacityField.setValue(6);
		roomNameError.setText(" ");
		lobbyPage.removeAll();
		cards.show(this, "create");
	}

	private JPanel createRoomPage(Runnable leaveMultiplayer) {
		JPanel page = page();
		JButton back = button("메인 메뉴");
		back.addActionListener(e -> leaveMultiplayer.run());
		page.add(header("멀티플레이", back), BorderLayout.NORTH);

		JPanel center = new JPanel(new GridBagLayout());
		center.setOpaque(false);
		JPanel roomSelection = new JPanel(new GridLayout(1, 2, 20, 0));
		roomSelection.setOpaque(false);
		roomSelection.add(openRooms());
		JPanel form = card(28);
		form.setLayout(new BorderLayout(0, 18));
		form.add(label("방 만들기", 28, Font.BOLD, TEXT), BorderLayout.NORTH);

		JPanel fields = new JPanel(new GridLayout(2, 1, 0, 14));
		fields.setOpaque(false);
		roomNameField.setFont(font(18, Font.PLAIN));
		capacityField.setFont(font(18, Font.PLAIN));
		// 인원수는 스피너의 화살표로 선택한다.
		((JSpinner.DefaultEditor) capacityField.getEditor()).getTextField().setEditable(false);
		fields.add(formField("방 이름", roomNameField));
		fields.add(formField("최대 인원", capacityField));
		form.add(fields, BorderLayout.CENTER);

		JButton create = button("방 만들기");
		create.setBackground(TEAL);
		create.setForeground(Color.WHITE);
		create.addActionListener(e -> {
			String name = roomNameField.getText().strip();
			if (name.isEmpty() || name.codePointCount(0, name.length()) > 24) {
				roomNameError.setText("방 이름을 1~24자로 입력해 주세요.");
				roomNameField.requestFocusInWindow();
				return;
			}
			showLobby(new Room("—", name, 0, (Integer) capacityField.getValue()));
		});
		roomNameField.addActionListener(e -> create.doClick());
		JPanel actions = new JPanel(new BorderLayout(12, 0));
		actions.setOpaque(false);
		actions.add(roomNameError, BorderLayout.CENTER);
		actions.add(create, BorderLayout.EAST);
		form.add(actions, BorderLayout.SOUTH);
		roomSelection.add(form);
		GridBagConstraints layout = new GridBagConstraints();
		layout.fill = GridBagConstraints.HORIZONTAL;
		layout.weightx = 1;
		center.add(roomSelection, layout);
		page.add(center, BorderLayout.CENTER);
		return page;
	}

	private JPanel openRooms() {
		JPanel list = card(28);
		list.setLayout(new BorderLayout(0, 18));
		list.add(label("열린 방", 28, Font.BOLD, TEXT), BorderLayout.NORTH);

		DefaultTableModel model = new DefaultTableModel(new String[] { "방 번호", "방 이름", "인원" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		for (Room room : PREVIEW_ROOMS) {
			model.addRow(new Object[] { room.number(), room.name(), room.occupants() + " / " + room.capacity() });
		}
		JTable rooms = new JTable(model);
		rooms.setFont(font(16, Font.PLAIN));
		rooms.getTableHeader().setFont(font(15, Font.BOLD));
		rooms.getTableHeader().setReorderingAllowed(false);
		rooms.setRowHeight(40);
		rooms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rooms.setFillsViewportHeight(true);
		rooms.getColumnModel().getColumn(0).setPreferredWidth(70);
		rooms.getColumnModel().getColumn(1).setPreferredWidth(260);
		rooms.getColumnModel().getColumn(2).setPreferredWidth(70);
		rooms.setPreferredScrollableViewportSize(new Dimension(400, 182));
		list.add(new JScrollPane(rooms), BorderLayout.CENTER);

		JButton join = button("입장하기");
		join.setEnabled(false);
		rooms.getSelectionModel().addListSelectionListener(e -> join.setEnabled(rooms.getSelectedRow() >= 0));
		join.addActionListener(e -> {
			int row = rooms.getSelectedRow();
			if (row >= 0) {
				showLobby(PREVIEW_ROOMS[rooms.convertRowIndexToModel(row)]);
			}
		});
		JPanel actions = new JPanel(new BorderLayout(12, 0));
		actions.setOpaque(false);
		actions.add(label("예시 방 목록", 14, Font.PLAIN, MUTED), BorderLayout.WEST);
		actions.add(join, BorderLayout.EAST);
		list.add(actions, BorderLayout.SOUTH);
		return list;
	}

	private void showLobby(Room selectedRoom) {
		int capacity = selectedRoom.capacity();
		int occupied = selectedRoom.occupants();
		int participantsCount = occupied + 1;
		lobbyPage.removeAll();
		JButton leave = button("나가기");
		leave.addActionListener(e -> cards.show(this, "create"));
		JButton settings = button("방 설정");
		settings.setEnabled(false);
		JPanel roomActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		roomActions.setOpaque(false);
		roomActions.add(settings);
		roomActions.add(leave);
		JPanel top = new JPanel(new BorderLayout(0, 16));
		top.setOpaque(false);
		top.add(header("멀티플레이 / 대기실", roomActions), BorderLayout.NORTH);
		JPanel room = card(16);
		room.setLayout(new BorderLayout(18, 0));
		room.add(label("방 번호  " + selectedRoom.number(), 16, Font.BOLD, MUTED), BorderLayout.WEST);
		room.add(label(selectedRoom.name(), 28, Font.BOLD, TEXT), BorderLayout.CENTER);
		room.add(label("미리보기", 16, Font.BOLD, TEAL), BorderLayout.EAST);
		top.add(room, BorderLayout.CENTER);
		lobbyPage.add(top, BorderLayout.NORTH);

		JPanel participants = card(16);
		participants.setLayout(new BorderLayout(0, 12));
		JPanel summary = new JPanel(new BorderLayout());
		summary.setOpaque(false);
		summary.add(label("참가자  " + participantsCount + " / " + capacity, 20, Font.BOLD, TEXT), BorderLayout.WEST);
		summary.add(label("0명 준비 완료", 16, Font.PLAIN, TEAL), BorderLayout.EAST);
		participants.add(summary, BorderLayout.NORTH);

		JPanel slots = new JPanel(new GridLayout((capacity + 1) / 2, 2, 12, 10));
		slots.setOpaque(false);
		if (occupied > 0) {
			slots.add(participantSlot("방장", "준비 전", false));
		}
		for (int i = 1; i < occupied; i++) {
			slots.add(participantSlot("참가자 " + (i + 1), "준비 전", false));
		}
		slots.add(participantSlot(occupied == 0 ? "나 · 방장" : "나", "준비 전", true));
		for (int i = participantsCount; i < capacity; i++) {
			slots.add(participantSlot("참가자 기다리는 중", "", false));
		}
		int rows = (capacity + 1) / 2;
		slots.setPreferredSize(new Dimension(0, rows * 80 + (rows - 1) * 10));
		JPanel slotArea = new JPanel(new BorderLayout());
		slotArea.setOpaque(false);
		slotArea.add(slots, BorderLayout.NORTH);
		participants.add(slotArea, BorderLayout.CENTER);
		lobbyPage.add(participants, BorderLayout.CENTER);

		JPanel footer = card(16);
		footer.setLayout(new BorderLayout(12, 0));
		footer.add(label("준비 완료  0 / " + participantsCount, 20, Font.BOLD, TEXT), BorderLayout.WEST);
		JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
		controls.setOpaque(false);
		JButton ready = button("준비하기");
		ready.setEnabled(false);
		JButton start = button("게임 시작");
		start.setEnabled(false);
		controls.add(ready);
		controls.add(start);
		footer.add(controls, BorderLayout.EAST);
		lobbyPage.add(footer, BorderLayout.SOUTH);
		cards.show(this, "lobby");
		lobbyPage.revalidate();
		lobbyPage.repaint();
	}

	private static JPanel participantSlot(String name, String status, boolean localPlayer) {
		JPanel slot = card(12);
		slot.setLayout(new BorderLayout(10, 0));
		slot.setBackground(localPlayer ? LOCAL_PLAYER_BACKGROUND : BACKGROUND);
		slot.add(label(name, 18, localPlayer ? Font.BOLD : Font.PLAIN, localPlayer ? TEXT : MUTED), BorderLayout.CENTER);
		slot.add(label(status, 16, Font.BOLD, TEAL), BorderLayout.EAST);
		return slot;
	}

	private static JPanel page() {
		JPanel page = new JPanel(new BorderLayout(0, 16));
		page.setBackground(BACKGROUND);
		page.setBorder(BorderFactory.createEmptyBorder(22, 36, 22, 36));
		return page;
	}

	private static JPanel header(String title, JComponent action) {
		JPanel header = new JPanel(new BorderLayout(12, 0));
		header.setOpaque(false);
		header.add(label(title, 22, Font.BOLD, TEXT), BorderLayout.EAST);
		header.add(action, BorderLayout.WEST);
		return header;
	}

	private static JPanel formField(String name, JComponent input) {
		JPanel field = new JPanel(new BorderLayout(0, 6));
		field.setOpaque(false);
		JLabel caption = label(name, 16, Font.BOLD, TEXT);
		caption.setLabelFor(input);
		field.add(caption, BorderLayout.NORTH);
		input.setPreferredSize(new Dimension(380, 38));
		field.add(input, BorderLayout.CENTER);
		return field;
	}

	private static JPanel card(int padding) {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),
			BorderFactory.createEmptyBorder(padding, padding, padding, padding)));
		return panel;
	}

	private static JLabel label(String text, int size, int style, Color color) {
		JLabel label = new JLabel();
		label.putClientProperty("html.disable", true);
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

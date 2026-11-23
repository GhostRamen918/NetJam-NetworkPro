package dynamic_beat_16;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dynamic_beat_16.NoteCharts.Instrument;

final class InstrumentSelectionPanel extends JPanel {
	private static final Instrument[] INSTRUMENTS = { Instrument.DEFAULT, Instrument.BASS };
	private final JLabel instrumentLabel = new JLabel("드럼 7키", JLabel.CENTER);
	private int selectedIndex;

	InstrumentSelectionPanel(Consumer<Instrument> onSelect) {
		setOpaque(false);
		setLayout(new BorderLayout(8, 0));
		instrumentLabel.setFont(new Font("Dialog", Font.BOLD, 22));
		instrumentLabel.setForeground(Color.BLACK);
		add(instrumentLabel, BorderLayout.CENTER);
		add(arrowButton("left", "이전 악기", () -> moveSelection(-1, onSelect)), BorderLayout.WEST);
		add(arrowButton("right", "다음 악기", () -> moveSelection(1, onSelect)), BorderLayout.EAST);
	}

	private void moveSelection(int step, Consumer<Instrument> onSelect) {
		selectedIndex = Math.floorMod(selectedIndex + step, INSTRUMENTS.length);
		Instrument selected = INSTRUMENTS[selectedIndex];
		instrumentLabel.setText(selected == Instrument.BASS ? "베이스 4키" : "드럼 7키");
		onSelect.accept(selected);
	}

	private static JButton arrowButton(String direction, String label, Runnable onClick) {
		ImageIcon basic = new ImageIcon(Main.class.getResource("images/" + direction + "ButtonBasic.png"));
		ImageIcon hover = new ImageIcon(Main.class.getResource("images/" + direction + "ButtonEntered.png"));
		JButton arrowButton = new JButton(basic);
		arrowButton.setRolloverIcon(hover);
		arrowButton.setPreferredSize(new Dimension(60, 60));
		arrowButton.setContentAreaFilled(false);
		arrowButton.setBorderPainted(false);
		arrowButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		arrowButton.setToolTipText(label);
		arrowButton.getAccessibleContext().setAccessibleName(label);
		arrowButton.addActionListener(e -> onClick.run());
		return arrowButton;
	}
}

package netJam;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import netJam.NoteCharts.Instrument;

final class InstSelectPanel extends JPanel {
	private static final Instrument[] INSTRUMENTS = { Instrument.DEFAULT, Instrument.BASS };
	private final JLabel instrumentLabel = new JLabel("드럼 7키", JLabel.CENTER);
	private int selectedIndex;

	InstSelectPanel(Runnable onSelectionChanged) {
		setOpaque(false);
		setLayout(new BorderLayout(8, 0));
		instrumentLabel.setFont(new Font("Dialog", Font.BOLD, 22));
		instrumentLabel.setForeground(Color.BLACK);
		add(instrumentLabel, BorderLayout.CENTER);
		add(arrowButton("left", () -> moveSelection(-1, onSelectionChanged)), BorderLayout.WEST);
		add(arrowButton("right", () -> moveSelection(1, onSelectionChanged)), BorderLayout.EAST);
	}

	Instrument getSelectedInstrument() {
		return INSTRUMENTS[selectedIndex];
	}

	private void moveSelection(int step, Runnable onSelectionChanged) {
		selectedIndex += step;
		if (selectedIndex < 0) {
			selectedIndex = INSTRUMENTS.length - 1;
		} else if (selectedIndex >= INSTRUMENTS.length) {
			selectedIndex = 0;
		}
		instrumentLabel.setText(getSelectedInstrument() == Instrument.BASS ? "베이스 4키" : "드럼 7키");
		onSelectionChanged.run();
	}

	private static JButton arrowButton(String direction, Runnable onClick) {
		ImageIcon basic = new ImageIcon(Main.class.getResource("images/" + direction + "ButtonBasic.png"));
		ImageIcon hover = new ImageIcon(Main.class.getResource("images/" + direction + "ButtonEntered.png"));
		JButton arrowButton = new JButton(basic);
		arrowButton.setRolloverIcon(hover);
		arrowButton.setPreferredSize(new Dimension(60, 60));
		arrowButton.setContentAreaFilled(false);
		arrowButton.setBorderPainted(false);
		arrowButton.addActionListener(e -> onClick.run());
		return arrowButton;
	}
}

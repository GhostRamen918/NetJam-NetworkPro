package server;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

// 서버 화면
public class ServerFrame extends JFrame {

	private JPanel contentPane;
	private JButton startButton;
	private JButton stopButton;
	private JLabel statusLabel;
	private JLabel countLabel;
	private JTable table;
	private DefaultTableModel tableModel;
	private JTextArea logArea;

	private GameServer server;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				ServerFrame frame = new ServerFrame();
				frame.setVisible(true);
			}
		});
	}

	public ServerFrame() {
		setTitle("NetJam 서버");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 520);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		startButton = new JButton("서버 시작");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startServer();
			}
		});
		startButton.setBounds(15, 15, 110, 28);
		contentPane.add(startButton);

		stopButton = new JButton("서버 중지");
		stopButton.setEnabled(false);
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopServer();
			}
		});
		stopButton.setBounds(135, 15, 110, 28);
		contentPane.add(stopButton);

		statusLabel = new JLabel("중지됨 (포트 " + GameServer.CONTROL_PORT + " / " + GameServer.GAME_PORT + ")");
		statusLabel.setBounds(260, 15, 300, 28);
		contentPane.add(statusLabel);

		countLabel = new JLabel("접속자 0 / " + GameServer.MAX_PLAYERS);
		countLabel.setBounds(15, 55, 300, 20);
		contentPane.add(countLabel);

		tableModel = new DefaultTableModel(new Object[][] {}, new String[] { "방장", "ID", "닉네임", "악기", "준비" });
		table = new JTable(tableModel);
		JScrollPane tableScroll = new JScrollPane(table);
		tableScroll.setBounds(15, 80, 595, 120);
		contentPane.add(tableScroll);

		JLabel logLabel = new JLabel("로그");
		logLabel.setBounds(15, 210, 100, 20);
		contentPane.add(logLabel);

		logArea = new JTextArea();
		logArea.setEditable(false);
		JScrollPane logScroll = new JScrollPane(logArea);
		logScroll.setBounds(15, 235, 595, 230);
		contentPane.add(logScroll);
	}

	private void startServer() {
		server = new GameServer(this);
		try {
			server.start();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "서버 시작 실패 (포트가 이미 사용 중일 수 있음)\n" + e.getMessage());
			return;
		}
		startButton.setEnabled(false);
		stopButton.setEnabled(true);
		statusLabel.setText("실행 중 (포트 " + GameServer.CONTROL_PORT + " / " + GameServer.GAME_PORT + ")");
	}

	private void stopServer() {
		server.stop();
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
		statusLabel.setText("중지됨 (포트 " + GameServer.CONTROL_PORT + " / " + GameServer.GAME_PORT + ")");
	}

	// 소켓 스레드에서 불리기 때문에 화면 변경은 invokeLater 안에서
	public void addLog(String text) {
		SwingUtilities.invokeLater(() -> {
			logArea.append(text + "\n");
			logArea.setCaretPosition(logArea.getDocument().getLength());
		});
	}

	public void showPlayers(List<Object[]> rows) {
		SwingUtilities.invokeLater(() -> {
			tableModel.setRowCount(0);
			for (Object[] row : rows) {
				tableModel.addRow(row);
			}
			countLabel.setText("접속자 " + rows.size() + " / " + GameServer.MAX_PLAYERS);
		});
	}
}

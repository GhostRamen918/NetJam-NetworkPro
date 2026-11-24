package dynamic_beat_16;

import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

import dynamic_beat_16.NoteCharts.Instrument;
import dynamic_beat_16.NoteCharts.Lane;

public class Note extends Thread {
	private static final int TRAVEL_DISTANCE = 1000 / Main.SLEEP_TIME * Main.NOTE_SPEED * Main.REACH_TIME;
	private Image noteBasicImage = new ImageIcon(Main.class.getResource("images/noteBasic.png")).getImage();
	private Lane lane;
	private Instrument instrument;
	private int y;
	private boolean proceeded = true;

	public Note(String noteType, Instrument instrument) {
		this.lane = instrument.laneFor(noteType);
		this.instrument = instrument;
		this.y = instrument.judgementY - instrument.direction.step * TRAVEL_DISTANCE;
	}

	public String getNoteType() {
		return lane.noteName;
	}

	public boolean isProceeded() {
		return proceeded;
	}

	public int getY() {
		return y;
	}

	public void close() {
		proceeded = false;
	}

	public void screenDraw(Graphics2D g) {
		for (int tile = 0; tile < lane.widthInTiles; tile++) {
			g.drawImage(noteBasicImage, lane.x + tile * Lane.TILE_WIDTH, y, null);
		}
	}

	public void drop() {
		y += instrument.direction.step * Main.NOTE_SPEED;
		if (hasMissed()) {
			System.out.println("Miss");
			close();
		}
	}

	public boolean hasMissed() {
		return distancePastJudgementLine() > 40;
	}

	private int distancePastJudgementLine() {
		return (y - instrument.judgementY) * instrument.direction.step;
	}

	@Override
	public void run() {
		try {
			while (proceeded) {
				drop();
				if (proceeded) {
					Thread.sleep(Main.SLEEP_TIME);
				}
			}
		} catch (InterruptedException e) {
			close();
			Thread.currentThread().interrupt();
		}
	}

	public String judge() {
		int distance = distancePastJudgementLine();
		String result;
		if (distance < -45 || distance > 40) {
			return "None";
		} else if (distance >= 33) {
			result = "Late";
		} else if (distance >= 20) {
			result = "Good";
		} else if (distance >= 7) {
			result = "Great";
		} else if (distance >= -7) {
			result = "Perfect";
		} else if (distance >= -15) {
			result = "Great";
		} else if (distance >= -30) {
			result = "Good";
		} else {
			result = "Early";
		}
		close();
		System.out.println(result);
		return result;
	}
}

package dynamic_beat_16;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class NoteCharts {

	public enum Instrument {
		DEFAULT(Direction.DOWN, 580, List.of(
			new Lane("S", KeyEvent.VK_S, 228, 1, "drumSmall1.mp3"),
			new Lane("D", KeyEvent.VK_D, 332, 1, "drumSmall1.mp3"),
			new Lane("F", KeyEvent.VK_F, 436, 1, "drumSmall1.mp3"),
			new Lane("Space", KeyEvent.VK_SPACE, 540, 2, "drumBig1.mp3"),
			new Lane("J", KeyEvent.VK_J, 744, 1, "drumSmall1.mp3"),
			new Lane("K", KeyEvent.VK_K, 848, 1, "drumSmall1.mp3"),
			new Lane("L", KeyEvent.VK_L, 952, 1, "drumSmall1.mp3"))),
		DRUM,
		BASS(Direction.UP, 100, List.of(
			new Lane("D", KeyEvent.VK_D, 434, 1, null),
			new Lane("F", KeyEvent.VK_F, 538, 1, null),
			new Lane("J", KeyEvent.VK_J, 642, 1, null),
			new Lane("K", KeyEvent.VK_K, 746, 1, null))),
		KEYBOARD,
		GUITAR;

		final Direction direction;
		final int judgementY;
		final List<Lane> lanes;

		Instrument() {
			this(Direction.DOWN, 580, List.of());
		}

		Instrument(Direction direction, int judgementY, List<Lane> lanes) {
			this.direction = direction;
			this.judgementY = judgementY;
			this.lanes = lanes;
		}

		Lane laneFor(String noteName) {
			for (Lane lane : lanes) {
				if (lane.noteName().equals(noteName)) {
					return lane;
				}
			}
			throw new IllegalArgumentException("Unsupported note for " + this + ": " + noteName);
		}
	}

	enum Direction {
		DOWN(1), UP(-1);

		final int step;

		Direction(int step) {
			this.step = step;
		}
	}

	record Lane(String noteName, int keyCode, int x, int widthInTiles, String soundFile) {
		static final int TILE_WIDTH = 100;
	}

	private record ChartKey(String musicFile, String difficulty, Instrument instrument) {}

	// 같은 곡이라도 난이도와 악기 조합마다 별도의 악보를 등록할 수 있다.
	private static final Map<ChartKey, Supplier<Beat[]>> CHARTS = Map.of(
		new ChartKey("Bensound - Energy.mp3", "Easy", Instrument.DEFAULT), NoteCharts::singleNote,
		new ChartKey("Bensound - Energy.mp3", "Hard", Instrument.DEFAULT), NoteCharts::singleNote,
		new ChartKey("Bensound - Energy.mp3", "Easy", Instrument.BASS), NoteCharts::energyBassEasy,
		new ChartKey("Joakim Karud - Wild Flower.mp3", "Easy", Instrument.DEFAULT), NoteCharts::singleNote,
		new ChartKey("Joakim Karud - Wild Flower.mp3", "Hard", Instrument.DEFAULT), NoteCharts::singleNote,
		new ChartKey("Joakim Karud - Mighty Love.mp3", "Easy", Instrument.DEFAULT), NoteCharts::mightyLoveEasy,
		new ChartKey("Joakim Karud - Mighty Love.mp3", "Hard", Instrument.DEFAULT), NoteCharts::singleNote
	);

	private NoteCharts() {}

	public static boolean hasChart(String musicFile, String difficulty, Instrument instrument) {
		return CHARTS.containsKey(new ChartKey(musicFile, difficulty, instrument));
	}

	public static Beat[] getBeats(String musicFile, String difficulty, Instrument instrument) {
		Supplier<Beat[]> chart = CHARTS.get(new ChartKey(musicFile, difficulty, instrument));
		if (chart == null) {
			throw new IllegalArgumentException("Missing chart for " + musicFile + " / " + difficulty + " / " + instrument);
		}
		return chart.get();
	}

	private static Beat[] singleNote() {
		int startTime = 1000 - Main.REACH_TIME * 1000;
		return new Beat[] { new Beat(startTime, "Space") };
	}

	private static Beat[] energyBassEasy() {
		int start = 3000 - Main.REACH_TIME * 1000;
		int beat = 600;
		return new Beat[] {
			new Beat(start, "D"),
			new Beat(start + beat, "F"),
			new Beat(start + beat * 2, "J"),
			new Beat(start + beat * 3, "K"),
			new Beat(start + beat * 4, "K"),
			new Beat(start + beat * 5, "J"),
			new Beat(start + beat * 6, "F"),
			new Beat(start + beat * 7, "D"),
			new Beat(start + beat * 8, "D"),
			new Beat(start + beat * 8, "J"),
			new Beat(start + beat * 9, "F"),
			new Beat(start + beat * 10, "K"),
			new Beat(start + beat * 11, "F"),
			new Beat(start + beat * 12, "D"),
			new Beat(start + beat * 12, "K")
		};
	}

	private static Beat[] mightyLoveEasy() {
		// 판정선에 도달해야 하는 시각에서 노트가 내려오는 시간을 뺀다.
		int startTime = 4460 - Main.REACH_TIME * 1000;
		int gap = 125;
		return new Beat[] {
			new Beat(startTime + gap * 1, "S"),
			new Beat(startTime + gap * 3, "D"),
			new Beat(startTime + gap * 5, "S"),
			new Beat(startTime + gap * 7, "D"),
			new Beat(startTime + gap * 9, "S"),
			new Beat(startTime + gap * 11, "D"),
			new Beat(startTime + gap * 13, "S"),
			new Beat(startTime + gap * 15, "D"),
			new Beat(startTime + gap * 18, "J"),
			new Beat(startTime + gap * 20, "K"),
			new Beat(startTime + gap * 22, "J"),
			new Beat(startTime + gap * 24, "K"),
			new Beat(startTime + gap * 26, "J"),
			new Beat(startTime + gap * 28, "K"),
			new Beat(startTime + gap * 30, "J"),
			new Beat(startTime + gap * 32, "K"),
			new Beat(startTime + gap * 35, "S"),
			new Beat(startTime + gap * 37, "D"),
			new Beat(startTime + gap * 39, "S"),
			new Beat(startTime + gap * 41, "D"),
			new Beat(startTime + gap * 43, "S"),
			new Beat(startTime + gap * 45, "D"),
			new Beat(startTime + gap * 48, "J"),
			new Beat(startTime + gap * 49, "K"),
			new Beat(startTime + gap * 50, "L"),
			new Beat(startTime + gap * 52, "F"),
			new Beat(startTime + gap * 52, "Space"),
			new Beat(startTime + gap * 52, "J"),
			new Beat(startTime + gap * 54, "S"),
			new Beat(startTime + gap * 56, "D"),
			new Beat(startTime + gap * 59, "F"),
			new Beat(startTime + gap * 59, "Space"),
			new Beat(startTime + gap * 59, "J"),
			new Beat(startTime + gap * 61, "L"),
			new Beat(startTime + gap * 63, "K"),
			new Beat(startTime + gap * 65, "F"),
			new Beat(startTime + gap * 65, "Space"),
			new Beat(startTime + gap * 65, "J"),
			new Beat(startTime + gap * 70, "S"),
			new Beat(startTime + gap * 72, "S"),
			new Beat(startTime + gap * 74, "S"),
			new Beat(startTime + gap * 78, "J"),
			new Beat(startTime + gap * 79, "K"),
			new Beat(startTime + gap * 80, "L"),
			new Beat(startTime + gap * 83, "Space"),
			new Beat(startTime + gap * 85, "S"),
			new Beat(startTime + gap * 87, "D"),
			new Beat(startTime + gap * 89, "S"),
			new Beat(startTime + gap * 91, "D"),
			new Beat(startTime + gap * 93, "F"),
			new Beat(startTime + gap * 96, "Space"),
			new Beat(startTime + gap * 98, "L"),
			new Beat(startTime + gap * 100, "Space"),
			new Beat(startTime + gap * 102, "S"),
			new Beat(startTime + gap * 104, "D"),
			new Beat(startTime + gap * 106, "Space"),
			new Beat(startTime + gap * 109, "Space"),
			new Beat(startTime + gap * 112, "Space"),
			new Beat(startTime + gap * 118, "S"),
			new Beat(startTime + gap * 119, "D"),
			new Beat(startTime + gap * 120, "F"),
			new Beat(startTime + gap * 123, "S"),
			new Beat(startTime + gap * 124, "D"),
			new Beat(startTime + gap * 125, "F"),
			new Beat(startTime + gap * 126, "J"),
			new Beat(startTime + gap * 127, "K"),
			new Beat(startTime + gap * 130, "J"),
			new Beat(startTime + gap * 133, "K"),
			new Beat(startTime + gap * 136, "L"),
			new Beat(startTime + gap * 138, "S"),
			new Beat(startTime + gap * 140, "Space"),
			new Beat(startTime + gap * 142, "S"),
			new Beat(startTime + gap * 144, "Space"),
			new Beat(startTime + gap * 146, "Space"),
			new Beat(startTime + gap * 150, "Space"),
			new Beat(startTime + gap * 152, "Space"),
			new Beat(startTime + gap * 157, "J"),
			new Beat(startTime + gap * 161, "K"),
			new Beat(startTime + gap * 165, "L"),
			new Beat(startTime + gap * 167, "S"),
			new Beat(startTime + gap * 169, "D"),
			new Beat(startTime + gap * 171, "F"),
			new Beat(startTime + gap * 174, "S"),
			new Beat(startTime + gap * 176, "D"),
			new Beat(startTime + gap * 178, "F"),
			new Beat(startTime + gap * 180, "Space"),
			new Beat(startTime + gap * 181, "L"),
			new Beat(startTime + gap * 184, "Space"),
			new Beat(startTime + gap * 187, "L"),
			new Beat(startTime + gap * 188, "K"),
			new Beat(startTime + gap * 189, "J"),
			new Beat(startTime + gap * 192, "S"),
			new Beat(startTime + gap * 192, "Space"),
			new Beat(startTime + gap * 196, "D"),
			new Beat(startTime + gap * 196, "Space"),
			new Beat(startTime + gap * 200, "S"),
			new Beat(startTime + gap * 200, "Space"),
			new Beat(startTime + gap * 207, "J"),
			new Beat(startTime + gap * 207, "Space"),
			new Beat(startTime + gap * 211, "K"),
			new Beat(startTime + gap * 211, "Space"),
			new Beat(startTime + gap * 216, "L"),
			new Beat(startTime + gap * 216, "Space"),
			new Beat(startTime + gap * 218, "Space"),
			new Beat(startTime + gap * 221, "J"),
			new Beat(startTime + gap * 223, "K"),
			new Beat(startTime + gap * 225, "L"),
			new Beat(startTime + gap * 227, "S"),
			new Beat(startTime + gap * 227, "Space"),
			new Beat(startTime + gap * 231, "D"),
			new Beat(startTime + gap * 231, "Space"),
			new Beat(startTime + gap * 235, "S"),
			new Beat(startTime + gap * 235, "Space"),
			new Beat(startTime + gap * 242, "S"),
			new Beat(startTime + gap * 242, "Space"),
			new Beat(startTime + gap * 242, "L"),
			new Beat(startTime + gap * 246, "D"),
			new Beat(startTime + gap * 246, "Space"),
			new Beat(startTime + gap * 246, "K"),
			new Beat(startTime + gap * 250, "F"),
			new Beat(startTime + gap * 250, "Space"),
			new Beat(startTime + gap * 250, "J"),
			new Beat(startTime + gap * 255, "J"),
			new Beat(startTime + gap * 257, "K"),
			new Beat(startTime + gap * 259, "K"),
			new Beat(startTime + gap * 262, "S"),
			new Beat(startTime + gap * 262, "Space"),
			new Beat(startTime + gap * 266, "D"),
			new Beat(startTime + gap * 266, "Space"),
			new Beat(startTime + gap * 270, "S"),
			new Beat(startTime + gap * 270, "Space"),
			new Beat(startTime + gap * 275, "J"),
			new Beat(startTime + gap * 277, "K"),
			new Beat(startTime + gap * 279, "L"),
			new Beat(startTime + gap * 282, "J"),
			new Beat(startTime + gap * 284, "K"),
			new Beat(startTime + gap * 286, "L"),
			new Beat(startTime + gap * 289, "J"),
			new Beat(startTime + gap * 291, "K"),
			new Beat(startTime + gap * 293, "L"),
			new Beat(startTime + gap * 295, "J"),
			new Beat(startTime + gap * 297, "F"),
			new Beat(startTime + gap * 299, "D"),
			new Beat(startTime + gap * 301, "S"),
			new Beat(startTime + gap * 304, "F"),
			new Beat(startTime + gap * 306, "D"),
			new Beat(startTime + gap * 308, "S"),
			new Beat(startTime + gap * 310, "F"),
			new Beat(startTime + gap * 312, "D"),
			new Beat(startTime + gap * 314, "S"),
			new Beat(startTime + gap * 317, "F"),
			new Beat(startTime + gap * 319, "D"),
			new Beat(startTime + gap * 321, "S"),
			new Beat(startTime + gap * 323, "F"),
			new Beat(startTime + gap * 325, "D"),
			new Beat(startTime + gap * 327, "S"),
			new Beat(startTime + gap * 330, "F"),
			new Beat(startTime + gap * 332, "S"),
			new Beat(startTime + gap * 332, "Space"),
			new Beat(startTime + gap * 336, "D"),
			new Beat(startTime + gap * 336, "Space"),
			new Beat(startTime + gap * 340, "S"),
			new Beat(startTime + gap * 340, "Space")
		};
	}
}

package dynamic_beat_16;

public final class Score {
	public static final int PERFECT_POINTS = 40;
	public static final int GREAT_POINTS = 30;

	public final int total;
	public final int maxCombo;
	public final String rank;

	public Score(int total, int maxCombo, int noteCount) {
		this.total = total;
		this.maxCombo = maxCombo;
		this.rank = calculateRank(total, noteCount);
	}

	static int pointsFor(String judgement) {
		switch (judgement) {
			case "Perfect": return PERFECT_POINTS;
			case "Great": return GREAT_POINTS;
			case "Good": return 20;
			case "Early":
			case "Late": return 10;
			default: return 0;
		}
	}

	private static String calculateRank(int total, int noteCount) {
		if (noteCount == 0) {
			return "D";
		}
		if (total >= noteCount * 36) {
			return "S";
		}
		if (total >= noteCount * GREAT_POINTS) {
			return "A";
		}
		if (total >= noteCount * 24) {
			return "B";
		}
		if (total >= noteCount * 16) {
			return "C";
		}
		return "D";
	}
}

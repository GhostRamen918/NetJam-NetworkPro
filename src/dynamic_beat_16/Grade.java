package dynamic_beat_16;

public final class Grade {
	public final int total;
	public final int maxCombo;
	public final String rank;

	public Grade(int total, int maxCombo, int noteCount) {
		this.total = total;
		this.maxCombo = maxCombo;
		this.rank = calculateRank(total, noteCount);
	}

	static int pointsFor(String judgement) {
		switch (judgement) {
			case "Perfect": return 40;
			case "Great": return 30;
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
		if (total >= noteCount * 40) {
			return "S";
		}
		if (total >= noteCount * 30) {
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

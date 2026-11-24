package reivajh06.kata3.model;

public class SeismicRange {

	public static SeismicRange of(double min, double max) {
		return new SeismicRange(min, max);
	}

	public static SeismicRange openLeft(double max) {
		return new SeismicRange(Double.NEGATIVE_INFINITY, max);
	}

	public static SeismicRange openRight(double min) {
		return new SeismicRange(min, Double.POSITIVE_INFINITY);
	}

	private final double min;
	private final double max;

	private SeismicRange(double min, double max) {
		this.min = min;
		this.max = max;
	}

	public boolean inRange(double value) {
		return value >= min && value < max;
	}

	public double min() {
		return min;
	}

	public double max() {
		return max;
	}
}

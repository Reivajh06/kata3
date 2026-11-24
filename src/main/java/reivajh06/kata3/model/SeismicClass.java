package reivajh06.kata3.model;

public enum SeismicClass {

	MICRO(SeismicRange.openLeft(3.0)),
	MINOR(SeismicRange.of(3.0, 4.0)),
	LIGHT(SeismicRange.of(4.0, 5.0)),
	MODERATE(SeismicRange.of(5.0, 6.0)),
	STRONG(SeismicRange.of(6.0, 7.0)),
	MAJOR(SeismicRange.of(7.0, 8.0)),
	GREAT(SeismicRange.openRight(8.0));

	final SeismicRange range;

	SeismicClass(SeismicRange range) {
		this.range = range;
	}
}

package reivajh06.kata3.model;

import java.time.Instant;

public class SeismBuilder {

	private String id;
	private double magnitude;
	private SeismicType type;
	private SeismicClass seismicClass;
	private Place place;
	private Instant time;
	private Instant updated;

	public SeismBuilder id(String id) {
		this.id = id;
		return this;
	}

	public SeismBuilder magnitude(double magnitude) {
		this.magnitude = magnitude;
		return this;
	}

	public SeismBuilder type(SeismicType type) {
		this.type = type;
		return this;
	}

	public SeismBuilder seismicClass(SeismicClass seismicClass) {
		this.seismicClass = seismicClass;
		return this;
	}

	public SeismBuilder place(Place place) {
		this.place = place;
		return this;
	}

	public SeismBuilder time(Instant time) {
		this.time = time;
		return this;
	}

	public SeismBuilder updated(Instant updated) {
		this.updated = updated;
		return this;
	}

	public Seism build() {
		return new Seism(
			id,
			magnitude,
			type,
			seismicClass,
			place,
			time,
			updated
		);
	}
}

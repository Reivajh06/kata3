package reivajh06.kata3.model;

public enum SeismicType {

	EARTHQUAKE,
	QUARRY_BLAST,
	NUCLEAR_EXPLOSION,
	METEOR,
	MINING_EXPLOSION,
	VOLCANIC_ERUPTION,
	VOLCANIC_EXPLOSION,
	LANDSLIDE,
	OTHER;

	public static SeismicType parse(String typename) {
		try {
			return SeismicType.valueOf(typename.strip().replace(" ", "_").toUpperCase());

		} catch(IllegalArgumentException e) {
			return OTHER;
		}
	}
}

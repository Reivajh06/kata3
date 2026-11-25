package reivajh06.kata3;

import reivajh06.kata3.model.Seism;
import reivajh06.kata3.sources.SeismSource;

import java.util.List;
import java.util.Optional;

public class SeismsProvider {

	private final SeismSource source;
	private final SeismParser parser;

	public SeismsProvider(SeismSource source) {
		this.source = source;
		this.parser = new SeismParser();
	}

	public List<Seism> getSeisms() {
		return parser.parseAll(source.requestSeisms()).stream()
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();
	}
}

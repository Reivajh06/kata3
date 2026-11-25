package reivajh06.kata3;

import reivajh06.kata3.model.Seism;
import reivajh06.kata3.sources.SeismSource;

import java.util.List;
import java.util.Optional;

public class SeismsProvider {

	private final SeismSource source;
	private final SeismParser parser;
	private int skipped;

	public SeismsProvider(SeismSource source) {
		this.source = source;
		this.parser = new SeismParser();
	}

	public int skipped() {
		return skipped;
	}

	public List<Seism> getSeisms() {
		skipped = 0;

		return parser.parseAll(source.requestSeisms()).stream()
				.filter(o -> {
					if(o.isPresent()) return true;

					skipped++;
					return false;
				})
				.map(Optional::get)
				.toList();
	}
}

package reivajh06.kata3;

import reivajh06.kata3.sources.SeismSource;
import reivajh06.kata3.sources.USGSSource;

public class Main {

	public static void main(String[] args) {
		SeismSource source = USGSSource.create();
		var result = source.requestSeisms();
		SeismParser parser = new SeismParser();
		System.out.println(parser.parseAll(result));
	}
}

package reivajh06.kata3;

public class Main {

	public static void main(String[] args) {
		USGSSource source = USGSSource.create();
		var result = source.requestSeisms();
		SeismParser parser = new SeismParser();
		System.out.println(parser.parseAll(result));
	}
}

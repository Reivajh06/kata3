package reivajh06.kata3;

import reivajh06.kata3.sources.USGSSource;

public class Main {

	public static void main(String[] args) {
		SeismsProvider provider = new SeismsProvider(USGSSource.create());
		System.out.println(provider.getSeisms());
	}
}

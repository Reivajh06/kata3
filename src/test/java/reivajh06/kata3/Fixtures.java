package reivajh06.kata3;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public final class Fixtures {

	private Fixtures() {}

	public static String read(String resource) {
		try (InputStream in = Fixtures.class.getResourceAsStream("/" + resource)) {
			if (in == null) throw new IllegalArgumentException("Missing test resource: " + resource);
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}

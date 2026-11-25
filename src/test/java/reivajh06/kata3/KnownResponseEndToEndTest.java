package reivajh06.kata3;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import reivajh06.kata3.model.Seism;
import reivajh06.kata3.sources.USGSSource;
import reivajh06.kata3.writers.FileSeismWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Kata verification: run the whole flow with a known response and compare the generated JSON with the expected one. */
class KnownResponseEndToEndTest {

	@Test
	void generatedJsonMatchesExpectedAnalysis(@TempDir Path dir) throws IOException {
		byte[] body = Fixtures.read("usgs/sample.geojson").getBytes(StandardCharsets.UTF_8);
		HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		server.createContext("/", exchange -> {
			exchange.getResponseHeaders().set("Content-Type", "application/json");
			exchange.sendResponseHeaders(200, body.length);
			try (OutputStream out = exchange.getResponseBody()) {
				out.write(body);
			}
		});
		server.start();

		Path output = dir.resolve("results.json");
		try {
			SeismsProvider provider = new SeismsProvider(USGSSource.ofPath("http://127.0.0.1:" + server.getAddress().getPort()));
			List<Seism> seisms = provider.getSeisms();
			FileSeismWriter.to(output.toString()).write(new SeismAnalyzer().analyze(provider.skipped(), seisms));
		} finally {
			server.stop(0);
		}

		JsonObject actual = JsonParser.parseString(Files.readString(output, StandardCharsets.UTF_8)).getAsJsonObject();
		actual.remove("generatedAt"); // depends on the clock
		JsonObject expected = JsonParser.parseString(Fixtures.read("usgs/expected-analysis.json")).getAsJsonObject();

		assertEquals(expected, actual);
	}
}

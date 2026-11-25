package reivajh06.kata3.sources;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import reivajh06.kata3.Fixtures;
import reivajh06.kata3.dtos.SeismDto;
import reivajh06.kata3.exceptions.SeismSourceConnectionException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Runs USGSSource against a local JDK HttpServer so every response is controlled. */
class USGSSourceTest {

	private HttpServer server;

	private USGSSource sourceServing(int status, String contentType, String body, long delayMillis) throws IOException {
		server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		server.createContext("/", exchange -> {
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().set("Content-Type", contentType);
			exchange.sendResponseHeaders(status, bytes.length);
			try (OutputStream out = exchange.getResponseBody()) {
				out.write(bytes);
			}
		});
		server.start();
		return USGSSource.ofPath("http://127.0.0.1:" + server.getAddress().getPort());
	}

	@AfterEach
	void stopServer() {
		if (server != null) server.stop(0);
	}

	@Test
	void deserializesEveryFeatureOfAKnownResponse() throws IOException {
		USGSSource source = sourceServing(200, "application/json", Fixtures.read("usgs/sample.geojson"), 0);

		List<SeismDto> dtos = source.requestSeisms();

		assertEquals(5, dtos.size());
		assertEquals("ci1", dtos.get(0).id());
		assertEquals("5 km S of Guánica, Puerto Rico", dtos.get(2).properties().place());
		assertNull(dtos.get(4).properties().mag());
	}

	@Test
	void httpErrorIsReportedWithItsStatusBeforeParsingTheBody() throws IOException {
		USGSSource source = sourceServing(500, "text/plain", "Internal Server Error", 0);

		RuntimeException e = assertThrows(RuntimeException.class, source::requestSeisms);

		assertTrue(e.getMessage() != null && e.getMessage().contains("500"),
				"an HTTP error should mention the status code, got: " + e);
	}

	@Test
	void plainTextBodyWithStatus200IsReportedAsInvalidResponse() throws IOException {
		// USGS answers an unknown feed name with 200 OK and a text/plain body.
		USGSSource source = sourceServing(200, "text/plain", "Not Found", 0);

		RuntimeException e = assertThrows(RuntimeException.class, source::requestSeisms);

		assertFalse(e instanceof SeismSourceConnectionException, "this is not a connection problem: " + e);
	}

	@Test
	void malformedJsonIsReportedAsInvalidResponse() throws IOException {
		USGSSource source = sourceServing(200, "application/json", "{\"type\": \"FeatureCollection\", \"features\": [", 0);

		RuntimeException e = assertThrows(RuntimeException.class, source::requestSeisms);

		assertFalse(e instanceof SeismSourceConnectionException, "this is not a connection problem: " + e);
	}

	@Test
	void jsonWithoutFeaturesIsReportedAsInvalidResponse() throws IOException {
		USGSSource source = sourceServing(200, "application/json", "{\"type\": \"FeatureCollection\"}", 0);

		RuntimeException e = assertThrows(RuntimeException.class, source::requestSeisms);

		assertFalse(e instanceof NullPointerException, "a missing 'features' array should be a reported error, not an NPE");
	}

	@Test
	void slowServerTriggersTimeout() throws IOException {
		USGSSource source = sourceServing(200, "application/json", Fixtures.read("usgs/sample.geojson"), 2_000)
				.setRequestTimeout(Duration.ofMillis(300));

		assertThrows(SeismSourceConnectionException.class, source::requestSeisms);
	}

	@Test
	void refusedConnectionIsReportedAsConnectionFailure() throws IOException {
		USGSSource source = sourceServing(200, "application/json", "{}", 0);
		server.stop(0); // nothing listens on that port anymore

		assertThrows(SeismSourceConnectionException.class, source::requestSeisms);
	}
}

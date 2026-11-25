package reivajh06.kata3.sources;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import reivajh06.kata3.JsonHandler;
import reivajh06.kata3.dtos.SeismDto;
import reivajh06.kata3.exceptions.SeismSourceConnectionException;
import reivajh06.kata3.exceptions.SeismSourceException;
import reivajh06.kata3.exceptions.SeismSourceInterruptedException;
import reivajh06.kata3.writers.WriterException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class USGSSource implements SeismSource {

	private static final int MIN_SUCCESSFUL_CODE = 200;
	private static final int MAX_SUCCESSFUL_CODE = 299;
	private static final String REMOTE_USGS_PATH = "https://earthquake.usgs.gov/";
	private static final String SUMMARY_FEEDS_ENDPOINT = "earthquakes/feed/v1.0/summary/all_day.geojson";

	public static USGSSource create() {
		return USGSSource.ofPath(REMOTE_USGS_PATH);
	}

	public static USGSSource ofPath(String providerBasePath) {
		return new USGSSource(providerBasePath);
	}

	private final String providerBasePath;
	private Duration requestTimeout;
	private final JsonHandler jsonHandler;

	private USGSSource() {
		this(REMOTE_USGS_PATH);
	}

	private USGSSource(String providerBasePath) {
		this.providerBasePath = providerBasePath.endsWith("/") ? providerBasePath : providerBasePath + "/";
		requestTimeout = Duration.of(3, ChronoUnit.MINUTES);
		jsonHandler = new JsonHandler();
	}

	public String providerBasePath() {
		return providerBasePath;
	}

	public USGSSource setRequestTimeout(Duration requestTimeout) {
		this.requestTimeout = requestTimeout;
		return this;
	}

	@Override
	public List<SeismDto> requestSeisms() {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(providerBasePath + SUMMARY_FEEDS_ENDPOINT))
				.GET()
				.timeout(requestTimeout)
				.build();

		List<SeismDto> seisms;
		HttpResponse<String> response;

		try(HttpClient client = HttpClient.newHttpClient()) {
			 response = client.send(request, HttpResponse.BodyHandlers.ofString());

		} catch(HttpTimeoutException | ConnectException e) {
			throw new SeismSourceConnectionException("Ongoing request for seisms fetching has exceeded maximum amount of time allowed".formatted(providerBasePath, SUMMARY_FEEDS_ENDPOINT), e);

		} catch(IOException e) {
			throw new SeismSourceException("Unexpected exception has occurred while fetching seisms from %s".formatted(providerBasePath + SUMMARY_FEEDS_ENDPOINT), e);

		} catch(InterruptedException e) {
			throw new SeismSourceInterruptedException("Ongoing request for seisms fetching to %s has been interrupted".formatted(providerBasePath + SUMMARY_FEEDS_ENDPOINT), e);
		}

		if(response.statusCode() < MIN_SUCCESSFUL_CODE || response.statusCode() > MAX_SUCCESSFUL_CODE) {
			throw new SeismSourceException("Server responded with %s code while fetching seisms from %s. Please check server availability".formatted(response.statusCode(), providerBasePath + SUMMARY_FEEDS_ENDPOINT));
		}

		try {
			JsonElement features = jsonHandler.deserialize(response.body(), JsonObject.class)
					.asMap()
					.get("features");

			seisms = features
					.getAsJsonArray()
					.asList()
					.stream()
					.map(j -> jsonHandler.deserialize(j.toString(), SeismDto.class))
					.toList();

		} catch(Exception e) {
			throw new SeismSourceException("Obtained invalid response from %s. Please check if source returns valid responses for seisms fetching".formatted(providerBasePath + SUMMARY_FEEDS_ENDPOINT));
		}

		return seisms;
	}
}

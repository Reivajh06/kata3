package reivajh06.kata3;

import reivajh06.kata3.dtos.SeismDto;
import reivajh06.kata3.exceptions.SeismSourceConnectionException;
import reivajh06.kata3.exceptions.SeismSourceException;
import reivajh06.kata3.exceptions.SeismSourceInterruptedException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class USGSSource implements SeismSource {

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

	private USGSSource() {
		this(REMOTE_USGS_PATH);
	}

	private USGSSource(String providerBasePath) {
		this.providerBasePath = providerBasePath.endsWith("/") ? providerBasePath : providerBasePath + "/";
		requestTimeout = Duration.of(3, ChronoUnit.MINUTES);
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

		List<SeismDto> seisms = new ArrayList<>();

		try(HttpClient client = HttpClient.newHttpClient()) {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		} catch(HttpTimeoutException e) {
			throw new SeismSourceConnectionException("Ongoing request for seisms fetching has exceeded maximum amount of time allowed".formatted(providerBasePath, SUMMARY_FEEDS_ENDPOINT), e);

		} catch(IOException e) {
			throw new SeismSourceException("Unexpected exception has occurred while fetching seisms from %s".formatted(providerBasePath + SUMMARY_FEEDS_ENDPOINT), e);

		} catch(InterruptedException e) {
			throw new SeismSourceInterruptedException("Ongoing request for seisms fetching to %s has been interrupted".formatted(providerBasePath + SUMMARY_FEEDS_ENDPOINT), e);
		}

		return seisms;
	}
}

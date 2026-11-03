package reivajh06.adapters;

import reivajh06.dtos.CelestialBodyDto;
import reivajh06.SparqlClientException;
import reivajh06.ports.CelestialBodyClient;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SparqlClient implements CelestialBodyClient {

	private static final String HEADER_NAME = "User-Agent";
	private static final String HEADER_VALUE = "kata3-astronomer/1.0 unused@kata.com";

	private final HttpClient client;

	public SparqlClient() {
		this.client = HttpClient.newBuilder().build();
	}

	@Override
	public CelestialBodyDto requestCelestialBody(String name) {
		try {
			HttpRequest request = HttpRequest.newBuilder().header(HEADER_NAME, HEADER_VALUE).build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());



		} catch(Exception e) {
			throw new SparqlClientException("Error while requesting information of %s".formatted(celestialBody), e);
		}
	}

	@Override
	public void close() throws Exception {
		client.close();
	}


}

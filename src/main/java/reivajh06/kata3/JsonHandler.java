package reivajh06.kata3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.time.Instant;

public class JsonHandler {

	private final Gson gson;
	private final Gson gsonFormatted;

	public JsonHandler() {
		gson = new GsonBuilder()
				.serializeNulls()
				.registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (i, t, j) -> new JsonPrimitive(i.toString()))
				.create();

		gsonFormatted = new GsonBuilder()
				.serializeNulls()
				.setPrettyPrinting()
				.registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (i, t, j) -> new JsonPrimitive(i.toString()))
				.create();;
	}

	public String serialize(Object object, boolean formatted) {
		return formatted ?
				gsonFormatted.toJson(object)
				: gson.toJson(object);
	}

	public<T> T deserialize(String json, Class<T> type) {
		return gson.fromJson(json, type);
	}
}

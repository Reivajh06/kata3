package reivajh06.kata3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonHandler {

	private final Gson gson;
	private final Gson gsonFormatted;

	public JsonHandler() {
		gson = new GsonBuilder()
				.serializeNulls()
				.create();

		gsonFormatted = new GsonBuilder()
				.serializeNulls()
				.setPrettyPrinting()
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

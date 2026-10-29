package reivajh06;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Json {

	private static final Gson GSON_PRETTY = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
	private static final Gson GSON = new GsonBuilder().serializeNulls().create();

	public String toJsonPretty(Object object) {
		return GSON_PRETTY.toJson(object);
	}

	public <R> R fromJsonPretty(String json, Class<R> type) {
		return GSON_PRETTY.fromJson(json, type);
	}

	public String toJson(Object object) {
		return GSON.toJson(object);
	}

	public <R> R fromJson(String json, Class<R> type) {
		return GSON.fromJson(json, type);
	}
}

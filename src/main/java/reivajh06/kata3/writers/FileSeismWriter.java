package reivajh06.kata3.writers;

import reivajh06.kata3.JsonHandler;
import reivajh06.kata3.model.Seism;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileSeismWriter implements SeismWriter {

	public static FileSeismWriter to(String filepath) {
		return new FileSeismWriter(filepath);
	}

	private final File file;
	private final JsonHandler jsonHandler;

	private FileSeismWriter(String filepath) {
		file = new File(filepath);
		jsonHandler = new JsonHandler();
	}

	@Override
	public void write(Object object) {
		String content = jsonHandler.serialize(object, true);

		try {
			file.getAbsoluteFile().getParentFile().mkdirs();

			Files.write(file.toPath(), content.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

		} catch (IOException e) {
			throw new WriterException("An error has occurred while writing seisms to %s".formatted(file.toPath().toString()), e);
		}
	}
}

package reivajh06.kata3.writers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileSeismWriterTest {

	@Test
	void writesUtf8JsonAndCreatesMissingFolders(@TempDir Path dir) throws IOException {
		Path file = dir.resolve("nested/out/results.json");

		FileSeismWriter.to(file.toString()).write(Map.of("place", "5 km S of Guánica, Puerto Rico"));

		byte[] bytes = Files.readAllBytes(file);
		assertTrue(new String(bytes, StandardCharsets.UTF_8).contains("Guánica"), "file must be UTF-8 encoded");
	}

	@Test
	void writesInstantsAsIsoStrings(@TempDir Path dir) throws IOException {
		Path file = dir.resolve("results.json");

		FileSeismWriter.to(file.toString()).write(Map.of("time", Instant.parse("2026-11-25T10:18:45.642Z")));

		assertTrue(Files.readString(file, StandardCharsets.UTF_8).contains("\"2026-11-25T10:18:45.642Z\""));
	}

	@Test
	void overwritesPreviousResults(@TempDir Path dir) throws IOException {
		Path file = dir.resolve("results.json");
		Files.writeString(file, "x".repeat(1_000));

		FileSeismWriter.to(file.toString()).write(Map.of("total", 1));

		assertFalse(Files.readString(file).contains("x"), "old content must be truncated");
	}
}

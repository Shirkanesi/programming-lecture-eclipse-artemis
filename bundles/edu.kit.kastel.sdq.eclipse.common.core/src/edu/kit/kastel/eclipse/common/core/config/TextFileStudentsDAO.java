package edu.kit.kastel.eclipse.common.core.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class TextFileStudentsDAO implements StudentsDAO {

	private List<String> ownStudentsNames;

	private final File configFile;

	public TextFileStudentsDAO(File configFile) {
		this.configFile = configFile;
	}

	private void parseIfNotAlreadyParsed() throws IOException {
		if (this.ownStudentsNames != null) {
			return;
		}
		
		this.ownStudentsNames = Files.lines(this.configFile.toPath())
				.filter(line -> !line.isBlank())
				.map(String::strip)
				.map(String::toLowerCase)
				.distinct()
				.toList();
	}
	
	@Override
	public List<String> getOwnStudentsNames() throws IOException {
		this.parseIfNotAlreadyParsed();
		return Collections.unmodifiableList(this.ownStudentsNames);
	}
	
}

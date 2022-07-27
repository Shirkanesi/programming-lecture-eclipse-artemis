package edu.kit.kastel.eclipse.common.core.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TextFileStudentsDAO extends StudentsDAO {

	private final File configFile;

	public TextFileStudentsDAO(File configFile) {
		this.configFile = configFile;
	}

	@Override
	protected void parseIfNotAlreadyParsed() throws IOException {
		if (this.ownStudentsNames != null) {
			return;
		}
		
		this.setOwnStudentsNames(Files.lines(this.configFile.toPath()).toList());
	}
	
}

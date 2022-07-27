package edu.kit.kastel.eclipse.common.core.config;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

public class JsonFileStudentsDAO extends StudentsDAO {

	private List<String> ownStudentsNames;

	private final File configFile;
	private final ObjectMapper oom = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public JsonFileStudentsDAO(File configFile) {
		this.configFile = configFile;
	}

	@Override
	protected void parseIfNotAlreadyParsed() throws IOException {
		if (this.ownStudentsNames != null) {
			return;
		}

		CollectionType type = oom.getTypeFactory().constructCollectionType(List.class, String.class);
		List<String> ownStudents = oom.readValue(this.configFile, type);
	
		this.setOwnStudentsNames(ownStudents);
	}

}

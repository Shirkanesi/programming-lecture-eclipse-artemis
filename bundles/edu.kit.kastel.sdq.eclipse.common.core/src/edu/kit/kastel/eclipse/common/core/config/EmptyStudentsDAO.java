package edu.kit.kastel.eclipse.common.core.config;

import java.io.IOException;
import java.util.List;

public class EmptyStudentsDAO implements StudentsDAO {

	@Override
	public List<String> getOwnStudentsNames() throws IOException {
		return List.of();
	}

}

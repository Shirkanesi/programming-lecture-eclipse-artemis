package edu.kit.kastel.eclipse.common.core.config;

import java.io.IOException;
import java.util.Set;

public class EmptyStudentsDAO extends StudentsDAO {

	@Override
	protected void parseIfNotAlreadyParsed() throws IOException {
		this.setOwnStudentsNames(Set.of());
	}

}

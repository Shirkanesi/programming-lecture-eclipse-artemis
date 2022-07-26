package edu.kit.kastel.eclipse.common.core.config;

import java.io.IOException;
import java.util.List;

public interface StudentsDAO {
	
	List<String> getOwnStudentsNames() throws IOException;

}

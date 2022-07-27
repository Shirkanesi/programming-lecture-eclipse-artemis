package edu.kit.kastel.eclipse.common.core.config;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class StudentsDAO {
	
	protected Set<String> ownStudentsNames;
	
	protected abstract void parseIfNotAlreadyParsed() throws IOException;
	
	public Set<String> getOwnStudentsNames() throws IOException {
		this.parseIfNotAlreadyParsed();
		return Collections.unmodifiableSet(this.ownStudentsNames);
	};
	
	protected void setOwnStudentsNames(Collection<String> names) {
		this.ownStudentsNames = names.stream()
				.filter(line -> !line.isBlank())
				.map(s -> s.split("@")[0])	// get name from e-mail
				.map(String::strip)
				.map(String::toLowerCase)
				.distinct()
				.collect(Collectors.toSet());
	}

}

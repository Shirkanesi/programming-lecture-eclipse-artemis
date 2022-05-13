package edu.kit.kastel.sdq.eclipse.common.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.common.api.model.IDisplayGroup;

public class DisplayGroup implements IDisplayGroup {

	private final String displayName;
	
	private final String shortName;
	
	public DisplayGroup(@JsonProperty("displayName") String displayName, @JsonProperty("shortName") String shortName) {
		this.displayName = displayName;
		this.shortName = shortName;
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public String getShortName() {
		return shortName;
	}

}

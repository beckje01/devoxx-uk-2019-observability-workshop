package ratpack.hub

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

class Hub {

	String id
	String name
	String hardwareType

	@JsonIgnore
	String getName() {
		return name
	}

	@JsonProperty
	void setName(String name) {
		this.name = name
	}

	@JsonIgnore
	String getHardwareType() {
		return hardwareType
	}

	@JsonProperty
	void setHardwareType(String hardwareType) {
		this.hardwareType = hardwareType
	}
}

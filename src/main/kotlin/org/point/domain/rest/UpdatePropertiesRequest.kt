package org.point.domain.rest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdatePropertiesRequest(
    @JsonProperty("values")
    val values: Map<String, String?>? = null
)

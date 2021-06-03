package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@JsonIgnoreProperties
@AllArgsConstructor
@Data
@NoArgsConstructor
public class UpdateProviderRequest {
    @NonNull
    @JsonProperty("oldPhone")
    private String oldPhone;
    @NonNull
    @JsonProperty("newPhone")
    private String newPhone;
    @NonNull
    @JsonProperty("firstName")
    private String firstName;
    @NonNull
    @JsonProperty("lastName")
    private String secondName;
    @NonNull
    @JsonProperty("email")
    private String email;
}

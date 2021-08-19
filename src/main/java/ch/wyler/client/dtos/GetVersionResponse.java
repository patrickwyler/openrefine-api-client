package ch.wyler.client.dtos;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class GetVersionResponse {
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("full_version")
    private String fullVersion;
    private String version;
    private String revision;
}

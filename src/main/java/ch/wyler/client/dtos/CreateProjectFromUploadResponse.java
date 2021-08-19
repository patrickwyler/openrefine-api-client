package ch.wyler.client.dtos;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class CreateProjectFromUploadResponse {
    private final URL location;

    public String getProjectId() {
        return StringUtils.substringAfterLast(location.getQuery(), "=");
    }
}

package ch.wyler.client.dtos;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class GetProjectMetadataResponse {
    private String name;
    private long rowCount;
}

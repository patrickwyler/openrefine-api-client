package ch.wyler.client.dtos;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class GetCsrfTokenResponse {
    private String token;
}

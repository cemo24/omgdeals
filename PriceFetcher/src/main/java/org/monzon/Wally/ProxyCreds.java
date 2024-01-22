package org.monzon.Wally;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ProxyCreds {
    @Getter private final String ip;
    @Getter private final String username;
    @Getter private final String password;
    @Getter private final int port;
}
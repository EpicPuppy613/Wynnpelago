package dev.epicpuppy.wynnpelago.client.services.content;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public class Region {
    private final String name;
    private final int level;
    private final boolean defaultUnlock;
    private final List<Region> connections = new ArrayList<>();

    @Setter
    private boolean enabled = false;

    @Setter
    private boolean accessible = false;

    @Setter
    private boolean unlocked = false;
}

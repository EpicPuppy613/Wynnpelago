package dev.epicpuppy.wynnpelago.client.services.content;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LevelEntry {
    @CsvBindByName(column = "Level", required = true)
    private int level;

    @CsvBindAndSplitByName(column = "Regions", elementType = String.class, splitOn = ", +")
    private Set<String> regions;
}

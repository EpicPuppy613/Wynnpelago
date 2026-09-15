package dev.epicpuppy.wynnpelago.client.services.content;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LevelRuleEntry {
    @CsvBindByName(column = "Level", required = true)
    private int level;

    @CsvCustomBindByName(column = "Type", required = true, converter = LevelTypeConverter.class)
    private LevelRuleType type;

    @CsvBindAndSplitByName(column = "Regions", elementType = String.class, splitOn = ", +")
    private Set<String> regions;

    @CsvBindAndSplitByName(column = "Prerequisites", elementType = String.class, splitOn = ", +")
    private Set<String> prereqs;
}

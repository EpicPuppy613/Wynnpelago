package dev.epicpuppy.wynnpelago.client.services.content;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessRuleEntry {
    @CsvBindByName(column = "Region", required = true)
    private String region;

    @CsvBindByName(column = "Level", required = true)
    private int level;

    @CsvBindAndSplitByName(column = "Prerequisites", elementType = String.class, splitOn = ", +")
    private Set<String> prereqs;
}

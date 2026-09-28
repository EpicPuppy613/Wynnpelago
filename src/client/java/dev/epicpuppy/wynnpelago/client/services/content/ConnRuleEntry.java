package dev.epicpuppy.wynnpelago.client.services.content;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnRuleEntry {
    @CsvBindByName(column = "From", required = true)
    private String from;

    @CsvBindByName(column = "To", required = true)
    private String to;

    @CsvBindByName(column = "Level", required = true)
    private int level;

    @CsvBindAndSplitByName(column = "Prerequisites", elementType = String.class, splitOn = ", +")
    private Set<String> prereqs;
}

package dev.epicpuppy.wynnpelago.client.services.content;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataEntry {
    @CsvBindByName(column = "Content", required = true)
    private String name;

    @CsvBindByName(column = "Ready", required = true)
    private boolean ready;

    @CsvBindByName(column = "Level")
    private int level;

    @CsvCustomBindByName(column = "Type", required = true, converter = TypeConverter.class)
    private DataType type;

    @CsvBindByName(column = "AP", required = true)
    private APType apType;

    @CsvCustomBindByName(column = "ID (Hex)", converter = IDConverter.class)
    private Long id;

    @CsvBindAndSplitByName(column = "Region/Connections", elementType = String.class, splitOn = ", +")
    private Set<String> regions;

    @CsvBindAndSplitByName(column = "Prerequisites", elementType = String.class, splitOn = ", +")
    private Set<String> prereqs;

    @CsvBindAndSplitByName(column = "Gear Req", elementType = String.class, splitOn = ", +")
    private Set<String> gearreqs;
}

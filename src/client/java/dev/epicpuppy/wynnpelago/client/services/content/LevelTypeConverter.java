package dev.epicpuppy.wynnpelago.client.services.content;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class LevelTypeConverter extends AbstractBeanField<LevelRuleType, String> {
    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        return LevelRuleType.fromSerializedName(value);
    }
}

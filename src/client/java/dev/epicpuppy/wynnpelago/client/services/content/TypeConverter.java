package dev.epicpuppy.wynnpelago.client.services.content;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class TypeConverter extends AbstractBeanField<DataType, String> {
    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        return DataType.fromSerializedName(value);
    }
}

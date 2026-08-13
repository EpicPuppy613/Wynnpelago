package dev.epicpuppy.wynnpelago.client.services.content;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class IDConverter extends AbstractBeanField<Long, String> {
    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (value.isBlank()) {
            return null;
        }
        return Long.parseLong(value.replace(" ", ""), 16);
    }
}

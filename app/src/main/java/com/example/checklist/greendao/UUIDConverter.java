package com.example.checklist.greendao;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.UUID;

public class UUIDConverter implements PropertyConverter<UUID, String> {
    @Override
    public UUID convertToEntityProperty(String databaseValue) {
        return (databaseValue == null) ? null : UUID.fromString(databaseValue);

    }

    @Override
    public String convertToDatabaseValue(UUID entityProperty) {
        return (entityProperty == null) ? null : entityProperty.toString();
    }
}


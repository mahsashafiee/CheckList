package com.example.checklist.greendao;

import com.example.checklist.model.State;

import org.greenrobot.greendao.converter.PropertyConverter;

public class StateConverter implements PropertyConverter<State, Integer> {
    @Override
    public State convertToEntityProperty(Integer databaseValue) {
        return (databaseValue == null) ? null : State.fromInt(databaseValue);

    }

    @Override
    public Integer convertToDatabaseValue(State entityProperty) {
        return (entityProperty == null) ? null : entityProperty.getValue();
    }
}

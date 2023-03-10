package com.example.beside.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PasswordConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if(attribute==null) return null;
        try {
            return Aes256Utils.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            return Aes256Utils.decrypt(dbData);
        } catch (Exception e) {
            return dbData;
        }
    }
}

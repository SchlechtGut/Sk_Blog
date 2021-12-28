package com.example.sk_blog.model.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class ModerationStatusConverter implements AttributeConverter<ModerationStatus, String> {
    @Override
    public String convertToDatabaseColumn(ModerationStatus moderationStatus) {
        if (moderationStatus == null) {
            return null;
        }
        return moderationStatus.toString();
    }

    @Override
    public ModerationStatus convertToEntityAttribute(String moderationStatusValue) {
        if (moderationStatusValue == null) {
            return null;
        }

        return Stream.of(ModerationStatus.values())
                .filter(c -> c.toString().equals(moderationStatusValue))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}

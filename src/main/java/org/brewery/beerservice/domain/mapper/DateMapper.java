package org.brewery.beerservice.domain.mapper;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class DateMapper {

    public OffsetDateTime timestampToOffsetDateTime(Timestamp timestamp) {
        if (timestamp == null)
            return null;

        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        return OffsetDateTime.of(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth(),
                localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond(), localDateTime.getNano(),
                ZoneOffset.UTC);
    }

    public Timestamp offsetDateTimeToTimestamp(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null)
            return null;

        return Timestamp.valueOf(offsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
    }
}

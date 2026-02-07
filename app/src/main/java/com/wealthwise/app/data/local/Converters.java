package com.wealthwise.app.data.local;

import androidx.room.TypeConverter;

import com.wealthwise.app.data.model.RecurrenceInterval;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.data.remote.SyncStatus;

import java.util.Date;

public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String fromTransactionType(TransactionType type) {
        return type == null ? null : type.name();
    }

    @TypeConverter
    public static TransactionType toTransactionType(String value) {
        return value == null ? null : TransactionType.valueOf(value);
    }

    @TypeConverter
    public static String fromSyncStatus(SyncStatus status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static SyncStatus toSyncStatus(String value) {
        return value == null ? null : SyncStatus.valueOf(value);
    }

    @TypeConverter
    public static String fromRecurrenceInterval(RecurrenceInterval interval) {
        return interval == null ? null : interval.name();
    }

    @TypeConverter
    public static RecurrenceInterval toRecurrenceInterval(String value) {
        return value == null ? null : RecurrenceInterval.valueOf(value);
    }
}

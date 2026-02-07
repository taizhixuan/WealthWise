# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }

# Models & DTOs
-keep class com.wealthwise.app.data.model.** { *; }
-keep class com.wealthwise.app.data.remote.dto.** { *; }
-keep class com.wealthwise.app.data.local.entity.** { *; }

# Enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

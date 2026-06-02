# Pearl Millet Care - Production ProGuard Rules

# 1. Source Information
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes EnclosingMethod

# 2. TensorFlow Lite Rules
-keep class org.tensorflow.lite.** { *; }
-keep class com.google.android.gms.tflite.** { *; }

# 3. Room Database Rules
-keep class * extends androidx.room.RoomDatabase
-keep class androidx.room.Entity
-keep class androidx.room.Dao
-keep class androidx.room.Query
-keep class androidx.room.Insert
-keep class androidx.room.Delete
-keep class androidx.room.Update
-keep class com.pearlmillet.app.data.database.** { *; }

# 4. Jetpack Compose Rules
-keep class androidx.compose.runtime.** { *; }
-keepclassmembers class ** {
    @androidx.compose.runtime.Composable <methods>;
}

# 5. Application Models & Entities (Preserve for Reflection/JSON)
-keep class com.pearlmillet.app.engine.** { *; }
-keep class com.pearlmillet.app.data.** { *; }

# 6. Optimization Guards
-dontwarn org.tensorflow.lite.**
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
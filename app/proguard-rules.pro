# Gson serialized model + network DTOs - field names must survive minification
-keep class com.makeforge.forgefit.network.** { *; }
-keep class com.makeforge.forgefit.domain.model.** { *; }

-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Gson generic type tokens
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Retrofit
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keep,allowobfuscation interface retrofit2.Call
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**

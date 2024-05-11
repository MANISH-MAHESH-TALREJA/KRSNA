# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\android-sdks/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontwarn com.squareup.okhttp.**
-dontwarn org.apache.http.**
-dontwarn com.google.android.gms.**
-dontwarn com.google.firebase.**
-keepattributes *Annotation,Signature
-keepattributes *Annotation*
-dontwarn okhttp3.internal.platform.*
-dontwarn org.conscrypt.*

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes Annotation

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.* { ; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

##---------------End: proguard configuration for Gson  ----------

-keep class com.startapp.** {
      *;
}
-keep class com.truenet.** {
      *;
}
-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile,
LineNumberTable, *Annotation*, EnclosingMethod
-dontwarn android.webkit.JavascriptInterface
-dontwarn com.startapp.**
-dontwarn org.jetbrains.annotations.**


#--------------Onesignal-------------
-dontwarn com.onesignal.**

# These 2 methods are called with reflection.
-keep class com.google.android.gms.common.api.GoogleApiClient {
    void connect();
    void disconnect();
}

-keep class com.onesignal.ActivityLifecycleListenerCompat** {*;}

-dontwarn com.amazon.**
-dontwarn com.huawei.**
-keep class ** implements com.onesignal.notifications.INotificationServiceExtension{
   void onNotificationReceived(com.onesignal.notifications.INotificationReceivedEvent);
}

-keep class com.onesignal.JobIntentService$* {*;}

-keep class com.google.android.gms.common.api.GoogleApiClient {
    void connect();
    void disconnect();
}

-keep class * implements com.onesignal.notifications.IPermissionObserver{
    void onNotificationPermissionChange(java.lang.Boolean);
}

-keep class * implements com.onesignal.user.subscriptions.IPushSubscriptionObserver {
    void onPushSubscriptionChange(com.onesignal.user.subscriptions.PushSubscriptionChangedState);
}

-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.AdwHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.ApexHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.AsusHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.DefaultBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.EverythingMeHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.HuaweiHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.LGHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.NewHtcHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.NovaHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.OPPOHomeBader { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.SamsungHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.SonyHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.VivoHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.XiaomiHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.ZukHomeBadger { <init>(...); }

-dontwarn com.amazon.**

# Proguard ends up removing this class even if it is used in AndroidManifest.xml so force keeping it.
-keep public class com.onesignal.notifications.services.ADMMessageHandler {*;}

-keep class com.onesignal.JobIntentService$* {*;}
-keep class com.onesignal.user.subscriptions.** { *; }
-keep class com.onesignal.common.modeling.** { *; }
-keep class org.json.**

-dontwarn org.xmlpull.v1.**
-dontwarn org.kxml2.io.**
-dontwarn android.content.res.**

-keep class org.xmlpull.** { *; }
-keepclassmembers class org.xmlpull.** { *; }

-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

-keep class com.onesignal.common.services.**
-keep class com.onesignal.common.modeling.**

-keep class com.adcolony.**

-keep class androidx.startup.**
-keep class androidx.startup.AppInitializer
-keep class * extends androidx.startup.Initializer
-keepnames class * extends androidx.startup.Initializer
# These Proguard rules ensures that ComponentInitializers are are neither shrunk nor obfuscated,
# and are a part of the primary dex file. This is because they are discovered and instantiated
# during application startup.
-keep class * extends androidx.startup.Initializer {
    # Keep the public no-argument constructor while allowing other methods to be optimized.
    <init>();
}
-assumenosideeffects class androidx.startup.StartupLogger { public static <methods>; }
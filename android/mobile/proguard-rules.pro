# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\jance\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
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

-optimizationpasses 20

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

#To repackage classes on a single package
#-repackageclasses ''

#Uncomment if using annotations to keep them.
-keepattributes *Annotation*

-keepattributes EnclosingMethod

#Keep classes that are referenced on the AndroidManifest
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService


#To remove debug logs:
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

#To avoid changing names of methods invoked on layout's onClick.
# Uncomment and add specific method names if using onClick on layouts
#-keepclassmembers class * {
# public void onClickButton(android.view.View);
#}

#Maintain java native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

#To maintain custom components names that are used on layouts XML.
#Uncomment if having any problem with the approach below
#-keep public class custom.components.package.and.name.**

#To maintain custom components names that are used on layouts XML:
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#Maintain enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#To keep parcelable classes (to serialize - deserialize objects to sent through Intents)
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#Keep the R
-keepclassmembers class **.R$* {
    public static <fields>;
}

###### ADDITIONAL OPTIONS NOT USED NORMALLY

#To keep callback calls. Uncomment if using any
#http://proguard.sourceforge.net/index.html#/manual/examples.html#callback
#-keep class mypackage.MyCallbackClass {
#   void myCallbackMethod(java.lang.String);
#}

#Uncomment if using Serializable
-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


###### NOTES
-dontnote com.google.android.gms.**
-dontnote okhttp3.internal.platform.**
-dontnote com.google.firebase.database.connection.idl.IPersistentConnectionImpl
-dontnote android.databinding.ViewDataBinding**
-dontnote kotlin.internal.PlatformImplementationsKt
-dontnote com.squareup.okhttp.internal.Platform
-dontnote com.google.firebase.FirebaseApp
-dontnote uk.co.senab.photoview.PhotoView
-dontnote androidx.work.impl.Schedulers
-dontnote com.google.gson.internal.UnsafeAllocator
-dontnote androidx.work.impl.Schedulers
-dontnote kotlin.jvm.internal.Reflection
-dontnote androidx.work.impl.Schedulers
-dontnote androidx.work.impl.Schedulers
-dontnote kotlin.coroutines.experimental.AbstractCoroutineContextElement
-dontnote ru.rambler.libs.swipe_layout.SwipeLayout
-dontnote android.widget.Space
-dontnote kotlinx.coroutines.experimental.android.AndroidExceptionPreHandler


###### LIBRARIES

# Alfonz
-dontwarn org.alfonz.adapter.databinding.BindingVariablesBinding
-dontwarn org.alfonz.arch.databinding.BindingVariablesBinding

# RxFirebase
-dontwarn com.androidhuman.rxfirebase2.database.**

# Retrofit 2
-dontwarn okio.**
# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>
# With R8 full mode generic signatures are stripped for classes that are not kept.
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Guava:
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

# RxJava:
-dontwarn org.mockito.**
-dontwarn org.junit.**
-dontwarn org.robolectric.**

# Gson:
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
#-keep class com.example.project.models.json.** { *; }

# Databinding
-dontwarn android.databinding.tool.expr.**
-dontwarn android.databinding.tool.ext.**
-dontwarn android.databinding.tool.writer.**
-keep class android.databinding.** { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl

# ViewPagerIndicator
-dontwarn com.viewpagerindicator.**

# Kotlin
-dontwarn kotlin.reflect.jvm.internal.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

# Firebase
-keep public class com.google.android.gms.* { public *; }
-keep public class com.google.firebase.* {public *;}
-dontwarn com.google.android.gms.**
-dontwarn com.google.firebase.**
-keepclassmembers class ** {
    @com.google.firebase.database.PropertyName public *;
}

# Crashlytics
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

# Room
-dontwarn android.arch.util.paging.CountedDataSource
-dontwarn android.arch.persistence.room.paging.LimitOffsetDataSource

# Agora
-keep class io.agora.**{*;}

# Main navigation bar
-keep public class com.google.android.material.bottomnavigation.BottomNavigationView { *; }
-keep public class com.google.android.material.bottomnavigation.BottomNavigationMenuView { *; }
-keep public class com.google.android.material.bottomnavigation.BottomNavigationPresenter { *; }
-keep public class com.google.android.material.bottomnavigation.BottomNavigationItemView { *; }

# OkHttp3
-dontwarn okhttp3.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase # A resource is loaded with a relative path so the package of this class must be preserved.

# Android Arch worker
-dontwarn androidx.work.impl.background.systemjob.SystemJobService

# Transcend
-keep class io.transcend.webview.** { *; }

###### PROJECT SPECIFIC
# TODO(tls): eventually remove the global keep so proguard can actually delete unused code
-keep,allowobfuscation class com.theathletic.**

# Needed for AnalyticsSchema fields to not be overwritten
# https://medium.com/androiddevelopers/practical-proguard-rules-examples-5640a3907dc9
-keep class com.theathletic.analytics.newarch.schemas.** { *; }
-dontwarn com.theathletic.activity.**
-dontwarn com.theathletic.adapter.settings.ManageTeamsAdapter
-dontwarn com.theathletic.databinding.**

# These are already being kept, but we want to disable obfuscation
# and eventually we will remove the global keep so these directives should stay
-keep class com.theathletic.entity.** { *; }
-keep class com.theathletic.**.data.** { *; }
-keep class com.theathletic.analytics** { <fields>; }
-keep class com.theathletic.compass** { <fields>; }
-keep class com.theathletic.debugtools** { <fields>; }
-keep class com.theathletic.data.local** { <fields>; }
-keep class com.theathletic.gifts** { <fields>; }
-keep class com.theathletic.injection** { <fields>; }
-keep class com.theathletic.network** { <fields>; }
-keep class com.theathletic.onboarding** { <fields>; }
-keep class com.theathletic.repository.twitter** { <fields>; }
-keep class com.theathletic.utility.Preferences** { <fields>; }

-dontwarn com.theathletic.fragment.**
-dontwarn com.theathletic.utility.**
-dontwarn com.theathletic.viewmodel.**
-dontwarn com.theathletic.network.rest.**
-dontwarn com.theathletic.widget.**
-keepclassmembers class name.cpr.VideoEnabledWebView$JavascriptInterface { public *; }
-keepclassmembers enum com.theathletic.** { *; } #https://stackoverflow.com/questions/23155794/app-crashing-when-i-run-proguard-on-gson-which-using-enum

-keep class com.comscore.** { *; }
-dontwarn com.comscore.**
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\ProgrammeWorld\AndroidSDK\SDK/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-keep class com.cgw.mq.mode.entity.** { *; }
-keep class com.cgw.mq.utils.** {*;}


# eventBus
#保护给定的可选属性
-keepattributes *Annotation*
#保护指定的类的成员的名称（如果他们不会压缩步骤中删除）
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}


#okhttp3
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-keep class okhttp3.** { *;}
-keep class okio.** { *;}
-dontwarn sun.security.**
-keep class sun.security.** { *;}
-dontwarn okio.**
-dontwarn okhttp3.**




#okhttputils
-dontwarn com.zhy.http.**
-keep class com.zhy.http.**{*;}



#retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-dontwarn org.robovm.**
-keep class org.robovm.** { *; }


#rxjava
-dontwarn rx.**
-keep class rx.** { *; }

-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}


# fastJson
-keep class com.alibaba.fastjson.**{*;}
-dontwarn com.alibaba.fastjson.**

# lambda规则
-dontwarn java.lang.invoke.**
-keep class java.lang.invoke.**{*;}


#动态权限库
-keep class pub.devrel.** { *; }
-dontwarn pub.devrel.**



#消息mq
-keep class org.slf4j.** { *; }
-dontwarn org.slf4j.**

-keep class com.alibaba.rocketmq.client.** { *; }
-dontwarn com.alibaba.rocketmq.client.**

-keep class com.alibaba.rocketmq.common.** { *; }
-dontwarn com.alibaba.rocketmq.common.**

-keep class com.alibaba.rocketmq.remoting.** { *; }
-dontwarn com.alibaba.rocketmq.remoting.**

#gson包
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**
-keep class java.lang.** { *; }
-dontwarn java.lang.**




#---------------------------------基本指令区----------------------------------
-optimizationpasses 5 # 代码混淆压缩比，在0~7之间，默认为5
-dontskipnonpubliclibraryclassmembers # 指定不去忽略非公共库的类
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/* # 混淆时所采用的算法
-keepattributes *Annotation*,InnerClasses #保留Annotation不混淆
-keepattributes Signature #避免混淆泛型
-keepattributes SourceFile,LineNumberTable #抛出异常时保留代码行号
#-ignorewarnings

-keepattributes EnclosingMethod
-dontpreverify # 不做预校验,加快混淆速度
#----------------------------------------------------------------------------



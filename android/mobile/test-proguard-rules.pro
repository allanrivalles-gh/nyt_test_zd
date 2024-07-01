# These rules only apply to the secondary test apk that is generated (secondary apk contains code for
# testing the target apk).
-dontobfuscate
-dontoptimize
-dontshrink
-dontusemixedcaseclassnames

-keep public class ** { *; }
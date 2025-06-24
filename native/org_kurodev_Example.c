#include <jni.h>
#include <stdio.h>
#include "org_kurodev_Example.h"

JNIEXPORT jint JNICALL Java_org_kurodev_Example_doStuff(JNIEnv *env, jobject thisObj, jint someInt) {
    // Get the class
    jclass cls = (*env)->GetObjectClass(env, thisObj);

    // Get the field ID of the "text" field
    jfieldID fid = (*env)->GetFieldID(env, cls, "text", "Ljava/lang/String;");
    if (fid == NULL) {
        return -1; // field not found
    }

    // Get the Java string
    jstring jstr = (jstring)(*env)->GetObjectField(env, thisObj, fid);

    // Convert to C string
    const char *cstr = (*env)->GetStringUTFChars(env, jstr, NULL);
    if (cstr == NULL) {
        return -2; // Out of memory
    }

    // Do something with it
    printf("From C: text = %s, int = %d\n", cstr, someInt);

    // Release the string
    (*env)->ReleaseStringUTFChars(env, jstr, cstr);
    return someInt * 2;
}
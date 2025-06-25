
#include <jni.h>
#include <stdio.h>
#include "olcPixelGameEngine.h"
#include "org_kurodev_jpixelgameengine_PixelGameEngineNativeImpl.h"

static jobject singletonListener = nullptr;

struct Global_context
{
    JavaVM *jvm;
    JNIEnv *env;
    jobject listener;
};

static Global_context gContext;

jmethodID getMethod(std::string methodName, std::string methodSignature)
{
    jclass myClass = gContext.env->GetObjectClass(gContext.listener);
    jmethodID method = gContext.env->GetMethodID(myClass, methodName.c_str(), methodSignature.c_str());
    return method;
}

class PixelGameEngineWrapper : public olc::PixelGameEngine
{
private:
    jmethodID onUserUpdateMethodID;
    jmethodID onUserDestroyMethodID;

public:
    PixelGameEngineWrapper() {
    };
    bool OnUserCreate()
    {
        std::cout << "OLC PixelGameEngine created" << std::endl;
        gContext.jvm->AttachCurrentThread((void **)&gContext.env, nullptr);
        jmethodID method = getMethod("onUserCreate", "()Z");
        onUserUpdateMethodID = getMethod("onUserUpdate", "(F)Z");
        onUserDestroyMethodID = getMethod("onUserDestroy", "()Z");
        jboolean result = gContext.env->CallBooleanMethod(gContext.listener, method);
        return result == JNI_TRUE;
        ;
    }

    bool OnUserUpdate(float fElapsedTime)
    {
        jmethodID method = getMethod("onUserUpdate", "(F)Z");
        jboolean result = gContext.env->CallBooleanMethod(gContext.listener, onUserUpdateMethodID, fElapsedTime);
        return result == JNI_TRUE;
    }
    bool OnUserDestroy()
    {
        std::cout << "destroy" << std::endl;
        jboolean result = gContext.env->CallBooleanMethod(gContext.listener, onUserDestroyMethodID);
        if (result == JNI_TRUE)
        {
            gContext.jvm->DetachCurrentThread();
            return true;
        }
        return false;
    }
};

static PixelGameEngineWrapper *engineInstance = new PixelGameEngineWrapper();

JNIEXPORT jboolean JNICALL
Java_org_kurodev_jpixelgameengine_PixelGameEngineNativeImpl_construct(
    JNIEnv *env,
    jclass clazz,
    jint screen_w,
    jint screen_h,
    jint pixel_w,
    jint pixel_h,
    jboolean full_screen,
    jboolean vsync,
    jboolean cohesion,
    jboolean realwindow,
    jobject listener)
{

    env->GetJavaVM(&gContext.jvm);
    gContext.jvm->AttachCurrentThread((void **)&gContext.env, nullptr);
    gContext.listener = gContext.env->NewGlobalRef(listener);

    int32_t width = static_cast<int32_t>(screen_w);
    int32_t height = static_cast<int32_t>(screen_h);

    std::cout << "Creating Pixelgame instance with " << width << "x" << height << "px" << std::endl;
    olc::rcode result = engineInstance->Construct(
        width,
        height,
        static_cast<int32_t>(pixel_w),
        static_cast<int32_t>(pixel_h),
        full_screen,
        vsync,
        cohesion,
        realwindow);

    return result == olc::OK ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_org_kurodev_jpixelgameengine_PixelGameEngineNativeImpl_start(
    JNIEnv *env, jclass clazz)
{
    auto result = olc::FAIL;
    if (engineInstance)
    {
        std::cout << "Starting pixelgame engine" << std::endl;
        result = engineInstance->Start();
    }
    return result == olc::OK ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_org_kurodev_jpixelgameengine_PixelGameEngineNativeImpl_draw(JNIEnv *env, jclass c, jint x, jint y, jint rgba)
{
    return engineInstance->Draw(x, y, olc::Pixel(rgba)) ? JNI_TRUE : JNI_FALSE;
}

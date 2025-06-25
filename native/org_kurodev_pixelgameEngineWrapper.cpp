
#include <jni.h>
#include <stdio.h>
#include "olcPixelGameEngine.h"
#include "org_kurodev_jpixelgameengine_impl_PixelGameEngineNativeImpl.h"

static jobject singletonListener = nullptr;

struct Global_context
{
    JavaVM *jvm;
    JNIEnv *env;
    jobject listener;
};

static Global_context gContext;

jmethodID getMethod(std::string methodName, std::string methodSignature, jclass type = gContext.env->GetObjectClass(gContext.listener))
{
    jmethodID method = gContext.env->GetMethodID(type, methodName.c_str(), methodSignature.c_str());
    return method;
}

jmethodID getConstructor(jclass type, std::string methodSignature)
{
    jclass myClass = type;
    jmethodID method = gContext.env->GetMethodID(myClass, "<init>", methodSignature.c_str());
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
        auto mouse = GetMouse(olc::Mouse::LEFT);
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

static PixelGameEngineWrapper *gui = new PixelGameEngineWrapper();

JNIEXPORT jboolean JNICALL
Java_org_kurodev_jpixelgameengine_impl_PixelGameEngineNativeImpl_construct(
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
    olc::rcode result = gui->Construct(
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

JNIEXPORT jboolean JNICALL Java_org_kurodev_jpixelgameengine_impl_PixelGameEngineNativeImpl_start(
    JNIEnv *env, jclass clazz)
{
    auto result = olc::FAIL;
    if (gui)
    {
        std::cout << "Starting pixelgame engine" << std::endl;
        result = gui->Start();
    }
    return result == olc::OK ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_org_kurodev_jpixelgameengine_impl_PixelGameEngineNativeImpl_draw(JNIEnv *env, jclass c, jint x, jint y, jint rgba)
{
    return gui->Draw(x, y, olc::Pixel(rgba)) ? JNI_TRUE : JNI_FALSE;
}

/*
 * Class:     org_kurodev_jpixelgameengine_PixelGameEngineNativeImpl
 * Method:    isFocused
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_kurodev_jpixelgameengine_impl_PixelGameEngineNativeImpl_isFocused(JNIEnv *env, jclass clazz)
{
    return gui->IsFocused() ? JNI_TRUE : JNI_FALSE;
}

jboolean toJbool(bool val)
{
    return val ? JNI_TRUE : JNI_FALSE;
}

/*
 * Class:     org_kurodev_jpixelgameengine_PixelGameEngineNativeImpl
 * Method:    getKey
 * Signature: (I)L org/kurodev/jpixelgameengine/input/HWButton;
 */
JNIEXPORT jobject JNICALL Java_org_kurodev_jpixelgameengine_impl_PixelGameEngineNativeImpl_getKey(JNIEnv *env, jclass clazz, jint k)
{
    jclass targetClass = env->FindClass("org/kurodev/jpixelgameengine/input/HWButton");
    if (targetClass == nullptr)
    {
        return nullptr;
    }
    jmethodID constructor = getConstructor(targetClass, "(ZZZ)V");
    if (constructor == nullptr)
    {
        return nullptr;
    }
    auto result = gui->GetKey(olc::Key(k));
    jobject obj = env->NewObject(targetClass, constructor, toJbool(result.bPressed), toJbool(result.bReleased), toJbool(result.bHeld));
    return obj;
}

/*
 * Class:     org_kurodev_jpixelgameengine_PixelGameEngineNativeImpl
 * Method:    getMouse
 * Signature: (I)L org/kurodev/jpixelgameengine/input/HWButton;
 */
JNIEXPORT jobject JNICALL Java_org_kurodev_jpixelgameengine_impl_PixelGameEngineNativeImpl_getMouse(JNIEnv *env, jclass clazz, jint k)
{
    jclass targetClass = env->FindClass("org/kurodev/jpixelgameengine/input/HWButton");
    if (targetClass == nullptr)
    {
        return nullptr;
    }
    jmethodID constructor = getConstructor(targetClass, "(ZZZ)V");
    if (constructor == nullptr)
    {
        return nullptr;
    }

    auto result = gui->GetMouse(k);

    jobject obj = env->NewObject(targetClass, constructor, toJbool(result.bPressed), toJbool(result.bReleased), toJbool(result.bHeld));

    return obj;
}

/*
 * Class:     org_kurodev_jpixelgameengine_PixelGameEngineNativeImpl
 * Method:    getMouseX
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_kurodev_jpixelgameengine_impl_PixelGameEngineNativeImpl_getMouseX(JNIEnv *env, jclass _c)
{
    return gui->GetMouseX();
}

/*
 * Class:     org_kurodev_jpixelgameengine_PixelGameEngineNativeImpl
 * Method:    getMouseY
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_kurodev_jpixelgameengine_impl_PixelGameEngineNativeImpl_getMouseY(JNIEnv *env, jclass _c)
{
    return gui->GetMouseY();
}

/*
 * Class:     org_kurodev_jpixelgameengine_PixelGameEngineNativeImpl
 * Method:    getMouseWheel
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_kurodev_jpixelgameengine_impl_PixelGameEngineNativeImpl_getMouseWheel(JNIEnv *env, jclass _c)
{
    return gui->GetMouseWheel();
}

/*
 * Class:     org_kurodev_jpixelgameengine_PixelGameEngineNativeImpl
 * Method:    getWindowMouse
 * Signature: ()L org/kurodev/jpixelgameengine/pos/IntegerVector2D;
 */
JNIEXPORT jobject JNICALL Java_org_kurodev_jpixelgameengine_impl_PixelGameEngineNativeImpl_getWindowMouse(JNIEnv *env, jclass _c)
{
    jclass targetClass = env->FindClass("org/kurodev/jpixelgameengine/pos/IntegerVector2D");
    jmethodID constructor = getConstructor(targetClass, "(II)V");
    auto result = gui->GetWindowMouse();

    return env->NewObject(targetClass, constructor, result.x, result.y);
}

/*
 * Class:     org_kurodev_jpixelgameengine_PixelGameEngineNativeImpl
 * Method:    getMousePos
 * Signature: ()L org/kurodev/jpixelgameengine/pos/IntegerVector2D;
 */
JNIEXPORT jobject JNICALL Java_org_kurodev_jpixelgameengine_impl_PixelGameEngineNativeImpl_getMousePos(JNIEnv *env, jclass _c)
{
    jclass targetClass = env->FindClass("org/kurodev/jpixelgameengine/pos/IntegerVector2D");
    jmethodID constructor = getConstructor(targetClass, "(II)V");
    auto result = gui->GetMousePos();

    return env->NewObject(targetClass, constructor, result.x, result.y);
}
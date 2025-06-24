
#include <jni.h>
#include <stdio.h>
#include "olcPixelGameEngine.h"
#include "org_kurodev_jpixelgameengine_PixelGameEngineNativeImpl.h"

class org_kurodev_pixelgameEnginewrapper : public olc::PixelGameEngine
{
private:
    /* data */
public:
    org_kurodev_pixelgameEnginewrapper(/* args */);
    ~org_kurodev_pixelgameEnginewrapper();

    bool OnUserCreate() override
    {
        return true;
    }
    bool OnUserUpdate(float fElapsedTime) override
    {
        return true;
    }
};

class JavaMethodCache
{
private:
    JNIEnv *env;
    jobject javaSingleton;
    const std::string methodName;
    const std::string signature;
    jmethodID methodID = nullptr;

public:
    JavaMethodCache(JNIEnv *env, jobject listener,
                    const char *methodName,
                    const char *signature)
        : env(env), javaSingleton(listener),
          methodName(methodName), signature(signature) {}

    template <typename ReturnType, typename... Args>
    ReturnType call(Args... args)
    {
        if (!methodID)
        {
            jclass clazz = env->GetObjectClass(javaSingleton);
            methodID = env->GetMethodID(clazz, methodName.c_str(), signature.c_str());
            env->DeleteLocalRef(clazz);

            if (!methodID)
            {
                if constexpr (std::is_same_v<ReturnType, bool>)
                    return false;
                if constexpr (std::is_same_v<ReturnType, float>)
                    return 0.0f;
                if constexpr (std::is_same_v<ReturnType, void>)
                    return;
                // Remove this line as it causes the error:
                // return nullptr;
                // Instead throw an exception or return a default-constructed object
                if constexpr (std::is_default_constructible_v<ReturnType>)
                {
                    return ReturnType{};
                }
                else
                {
                    throw std::runtime_error("Method not found and no default return type");
                }
            }
        }

        if constexpr (std::is_same_v<ReturnType, bool>)
        {
            return env->CallBooleanMethod(javaSingleton, methodID, args...);
        }
        else if constexpr (std::is_same_v<ReturnType, float>)
        {
            return env->CallFloatMethod(javaSingleton, methodID, args...);
        }
        else if constexpr (std::is_same_v<ReturnType, void>)
        {
            env->CallVoidMethod(javaSingleton, methodID, args...);
        }
        else
        {
            return static_cast<ReturnType>(env->CallObjectMethod(javaSingleton, methodID, args...));
        }
    }
};

class NativeEngineWrapper : public olc::PixelGameEngine
{
private:
    JavaMethodCache onUserCreate;
    JavaMethodCache onUserUpdate;
    JavaMethodCache onUserDestroy;

public:
    NativeEngineWrapper(JNIEnv *env, jobject listener)
        : onUserCreate(env, listener, "onUserCreate", "()Z"),
          onUserUpdate(env, listener, "onUserUpdate", "(F)Z"),
          onUserDestroy(env, listener, "onUserDestroy", "()Z") {}

    bool OnUserCreate() override
    {
        std::cout << "Created" << std::endl;
        return onUserCreate.call<bool>();
    }

    bool OnUserUpdate(float fElapsedTime) override
    {
        std::cout << "Updated" << std::endl;
        return onUserUpdate.call<bool>(fElapsedTime);
    }

    bool OnUserDestroy() override
    {
        std::cout << "Destroyed" << std::endl;
        return onUserDestroy.call<bool>();
    }
};

static NativeEngineWrapper *engineInstance = nullptr;

JNIEXPORT jboolean JNICALL Java_org_kurodev_jpixelgameengine_PixelGameEngineNativeImpl_construct(
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
    if (engineInstance)
    {
        return JNI_TRUE; // Already initialized
    }

    engineInstance = new NativeEngineWrapper(env, listener);
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

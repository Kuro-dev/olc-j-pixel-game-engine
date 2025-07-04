#include <cstdint>
#include <iostream>
#include <memory>
#include <functional>
#include "olcPixelGameEngine.h"

#ifdef __cplusplus
extern "C"
{
#endif

    // Callback types
    typedef bool (*UserCreateCallback)(void);
    typedef bool (*UserUpdateCallback)(float);
    typedef bool (*UserDestroyCallback)(void);
    typedef bool (*OnConsoleCommandCallback)(const char *);
    typedef void (*OnTextEntryCompleteCallback)(const char *);

    class GameEngine;

    static std::unique_ptr<GameEngine> instance;

    class GameEngine : public olc::PixelGameEngine
    {
    private:
        UserCreateCallback m_onCreate;
        UserUpdateCallback m_onUpdate;
        UserDestroyCallback m_onDestroy;
        OnConsoleCommandCallback onConsoleCommand;
        OnTextEntryCompleteCallback onTextEntryComplete;

    public:
        GameEngine(UserCreateCallback onCreate,
                   UserUpdateCallback onUpdate,
                   UserDestroyCallback onDestroy,
                   OnConsoleCommandCallback onConsoleCommand,
                   OnTextEntryCompleteCallback onTextEntryComplete)
            : m_onCreate(onCreate),
              m_onUpdate(onUpdate),
              m_onDestroy(onDestroy),
              onConsoleCommand(onConsoleCommand),
              onTextEntryComplete(onTextEntryComplete) {}

        bool OnUserCreate() override
        {
            if (!m_onCreate)
            {
                std::cerr << "No OnUserCreate callback provided" << std::endl;
                return false;
            }
            return m_onCreate();
        }

        bool OnUserUpdate(float fElapsedTime) override
        {
            if (!m_onUpdate)
            {
                std::cerr << "No OnUserUpdate callback provided" << std::endl;
                return false;
            }
            return m_onUpdate(fElapsedTime);
        }

        bool OnUserDestroy() override
        {
            bool result = true;
            if (m_onDestroy)
            {
                result = m_onDestroy();
            }
            instance.release();
            instance = nullptr;
            return result;
        }
        // Called when a text entry is confirmed with "enter" key
        void OnTextEntryComplete(const std::string &sText)
        {
            onTextEntryComplete(sText.c_str());
        }
        // Called when a console command is executed
        bool OnConsoleCommand(const std::string &sCommand)
        {
            ConsoleOut() << "> " + sCommand << std::endl;
            return onConsoleCommand(sCommand.c_str());
        }
    };

    enum NativeStatusCode
    {
        SUCCESS = 0,
        FAIL = 1,
        INSTANCE_ALREADY_EXISTS = 2
    };

    int createGameEngineInstance(int32_t width,
                                 int32_t height,
                                 UserCreateCallback onCreate,
                                 UserUpdateCallback onUpdate,
                                 UserDestroyCallback onDestroy,
                                 OnConsoleCommandCallback onConsoleCommand,
                                 OnTextEntryCompleteCallback onTextEntryComplete)
    {
        if (instance && instance->olc_IsRunning())
        {
            return INSTANCE_ALREADY_EXISTS;
        }

        std::cout << "Instantiating new Game Engine instance" << std::endl;

        instance = std::make_unique<GameEngine>(onCreate, onUpdate, onDestroy, onConsoleCommand, onTextEntryComplete);
        instance->Construct(width, height, 1, 1, false, false, false, true);
        // Verify callbacks were stored
        if (!instance)
        {
            std::cerr << "Failed to create instance" << std::endl;
            return FAIL;
        }

        return SUCCESS;
    }

    int start()
    {
        if (!instance)
        {
            std::cerr << "No instance created" << std::endl;
            return FAIL;
        }

        std::cout << "Starting pixelgame engine" << std::endl;
        return instance->Start();
    }

    bool draw(int x, int y, int rgba)
    {
        return instance->Draw(olc::vd2d(x, y), olc::Pixel(rgba));
    }


    void drawString(int32_t x, int32_t y, const char *str, int32_t color, uint32_t scale)
    {
        instance->DrawString(x, y, str, olc::Pixel(color), scale);
    }

    void drawCircle(int32_t x, int32_t y, int32_t radius, int32_t color, int32_t mask)
    {
        instance->DrawCircle(x, y, radius, olc::Pixel(color), mask & 0xFF);
    }

    void fillCircle(int32_t x, int32_t y, int32_t radius, int32_t color)
    {
        instance->FillCircle(x, y, radius, olc::Pixel(color));
    }

    bool isFocused()
    {
        return instance->IsFocused();
    }

    olc::HWButton getKey(olc::Key k)
    {
        return instance->GetKey(k);
    }

    olc::HWButton getMouse(int32_t k)
    {
        return instance->GetMouse(k);
    }

    olc::vi2d getMousePos()
    {
        return instance->GetMousePos();
    }

    olc::vi2d getWindowMouse()
    {
        olc::vi2d vec = instance->GetWindowMouse();
        return vec;
    }

    int32_t getMouseWheel()
    {
        return instance->GetMouseWheel();
    }

    void setScreenSize(int w, int h)
    {
        instance->SetScreenSize(w, h);
    }
    void consoleShow(olc::Key closeKey, bool suspendTime)
    {
        instance->ConsoleShow(closeKey, suspendTime);
    }

    void consoleClear()
    {
        instance->ConsoleClear();
    }

    bool isConsoleShowing()
    {
        return instance->IsConsoleShowing();
    }

    void test(bool enable, char *initialString)
    {
        instance->TextEntryEnable(enable, std::string(initialString));
    }

    char* textEntryGetString(){
       return instance->TextEntryGetString().c_str();
    }

    int32_t textEntryGetCursor(){
        return instance->TextEntryGetCursor();
    }

    bool isTextEntryEnabled(){
        return instance->IsTextEntryEnabled();
    }


#ifdef __cplusplus
}
#endif

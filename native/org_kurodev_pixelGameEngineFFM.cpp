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

    void drawLine(int32_t x1, int32_t y1, int32_t x2, int32_t y2, int32_t color, uint32_t pattern)
    {
        instance->DrawLine(x1, y1, x2, y2, olc::Pixel(color), pattern);
    }

    void drawString(int32_t x, int32_t y, const char *str, int32_t color, uint32_t scale)
    {
        instance->DrawString(x, y, str, olc::Pixel(color), scale);
    }

    void drawRect(int32_t x, int32_t y, int32_t w, int32_t h, int32_t color)
    {
        instance->DrawRect(x, y, w, h, olc::Pixel(color));
    }

    void fillRect(int32_t x, int32_t y, int32_t w, int32_t h, int32_t color)
    {
        instance->FillRect(x, y, w, h, olc::Pixel(color));
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

    void textEntryEnable(bool enable, char *initialString)
    {
        instance->TextEntryEnable(enable, std::string(initialString));
    }

    const char *textEntryGetString()
    {
        return instance->TextEntryGetString().c_str();
    }

    int32_t textEntryGetCursor()
    {
        return instance->TextEntryGetCursor();
    }

    bool isTextEntryEnabled()
    {
        return instance->IsTextEntryEnabled();
    }

    olc::vi2d getScreenPixelSize()
    {
        return instance->GetScreenPixelSize();
    }

    olc::vi2d getScreenSize()
    {
        return instance->GetScreenSize();
    }

    void drawSprite(int32_t x, int32_t y, olc::Sprite *sprite, uint32_t scale, uint8_t flip)
    {
        instance->DrawSprite(x, y, sprite, scale, olc::Sprite::Flip(flip));
    }

    void drawPartialSprite(int32_t x, int32_t y, olc::Sprite *sprite, int32_t ox, int32_t oy, int32_t w, int32_t h, uint32_t scale, uint8_t flip)
    {
        instance->DrawPartialSprite(x, y, sprite, ox, oy, w, h, scale, olc::Sprite::Flip(flip));
    }

    void setPixelMode(int32_t mode)
    {
        instance->SetPixelMode(olc::Pixel::Mode(mode));
    }

    void destroy_sprite(olc::Sprite *sprite) { delete sprite; }

    void destroy_decal(olc::Decal *decal) { delete decal; }

    int32_t sprite_width(olc::Sprite *s) { return s->width; }
    int32_t sprite_height(olc::Sprite *s) { return s->height; }

    olc::Sprite *create_sprite(const char *path)
    {
        return new olc::Sprite(std::string(path));
    }

    olc::Decal *create_decal(olc::Sprite *sprite)
    {
        return new olc::Decal(sprite);
    }

    void setDecalMode(int32_t mode)
    {
        instance->SetDecalMode(olc::DecalMode(mode));
    }

    void setDecalStructure(int32_t structure)
    {
        instance->SetDecalStructure(olc::DecalStructure(structure));
    }

    olc::vi2d decal_vUVScale(olc::Decal *d)
    {
        return d->vUVScale;
    }

      void printToConsole(char *text)
    {
        instance->ConsoleOut() << text;
    }

    // Draws a whole decal, with optional scale and tinting
    void drawDecal(olc::vf2d pos, olc::Decal *decal, olc::vf2d scale, olc::Pixel tint)
    {
        instance->DrawDecal(pos, decal, scale, olc::Pixel(tint));
    }

    // Draws a region of a decal, with optional scale and tinting
    void drawPartialDecal(const olc::vf2d pos, olc::Decal *decal, const olc::vf2d source_pos, const olc::vf2d source_size, const olc::vf2d scale, const olc::Pixel tint)
    {
        instance->DrawPartialDecal(pos, decal, source_pos, source_size, scale, tint);
    };
// Draws fully user controlled 4 vertices, pos(pixels), uv(pixels), colours
void DrawExplicitDecal(olc::Decal *decal, const olc::vf2d *pos, const olc::vf2d *uv, const olc::Pixel *col, uint32_t elements)
{
    instance->DrawExplicitDecal(decal, pos, uv, col, elements);
}

// Draws a decal with 4 arbitrary points, warping the texture to look "correct"
// Note: C++ overloads for array, pointer, and std::array will be mapped to a single C function here.
// The caller is responsible for ensuring the 'pos' pointer points to an array of 4 olc::vf2d.
void DrawWarpedDecal(olc::Decal *decal, const olc::vf2d *pos, const olc::Pixel tint)
{
    instance->DrawWarpedDecal(decal, pos, tint);
}

// As above, but you can specify a region of a decal source sprite
// Similar to DrawWarpedDecal, handling array/pointer versions through a single C function.
void DrawPartialWarpedDecal(olc::Decal *decal, const olc::vf2d *pos, const olc::vf2d source_pos, const olc::vf2d source_size, const olc::Pixel tint)
{
    instance->DrawPartialWarpedDecal(decal, pos, source_pos, source_size, tint);
}

// Draws a decal rotated to specified angle, with point of rotation offset
void DrawRotatedDecal(const olc::vf2d pos, olc::Decal *decal, const float fAngle, const olc::vf2d center, const olc::vf2d scale, const olc::Pixel tint)
{
    instance->DrawRotatedDecal(pos, decal, fAngle, center, scale, tint);
}

void DrawPartialRotatedDecal(const olc::vf2d pos, olc::Decal *decal, const float fAngle, const olc::vf2d center, const olc::vf2d source_pos, const olc::vf2d source_size, const olc::vf2d scale, const olc::Pixel tint)
{
    instance->DrawPartialRotatedDecal(pos, decal, fAngle, center, source_pos, source_size, scale, tint);
}

// Draws a multiline string as a decal, with tinting and scaling
// Note: std::string in C++ will likely be passed as a const char* in C for FFM.
void DrawStringDecal(const olc::vf2d pos, const char *sText, const olc::Pixel col, const olc::vf2d scale)
{
    instance->DrawStringDecal(pos, sText, col, scale);
}

void DrawStringPropDecal(const olc::vf2d pos, const char *sText, const olc::Pixel col, const olc::vf2d scale)
{
    instance->DrawStringPropDecal(pos, sText, col, scale);
}

// Draws a single shaded filled rectangle as a decal
void DrawRectDecal(const olc::vf2d pos, const olc::vf2d size, const olc::Pixel col)
{
    instance->DrawRectDecal(pos, size, col);
}

void FillRectDecal(const olc::vf2d pos, const olc::vf2d size, const olc::Pixel col)
{
    instance->FillRectDecal(pos, size, col);
}

// Draws a corner shaded rectangle as a decal
void GradientFillRectDecal(const olc::vf2d pos, const olc::vf2d size, const olc::Pixel colTL, const olc::Pixel colBL, const olc::Pixel colBR, const olc::Pixel colTR)
{
    instance->GradientFillRectDecal(pos, size, colTL, colBL, colBR, colTR);
}

// Draws a single shaded filled triangle as a decal
void FillTriangleDecal(const olc::vf2d p0, const olc::vf2d p1, const olc::vf2d p2, const olc::Pixel col)
{
    instance->FillTriangleDecal(p0, p1, p2, col);
}

// Draws a corner shaded triangle as a decal
void GradientTriangleDecal(const olc::vf2d p0, const olc::vf2d p1, const olc::vf2d p2, const olc::Pixel c0, const olc::Pixel c1, const olc::Pixel c2)
{
    instance->GradientTriangleDecal(p0, p1, p2, c0, c1, c2);
}

// Draws an arbitrary convex textured polygon using GPU
// Note: std::vector in C++ will typically be passed as a pointer to the underlying data and a size/count.
// For simplicity and direct mapping, we'll assume a pointer to the first element and let the C++ side handle the vector.
// In a real FFM scenario, you might pass a pointer to the array data and the size.
void DrawPolygonDecal(olc::Decal *decal, const olc::vf2d *pos, const olc::vf2d *uv, const olc::Pixel tint)
{
    instance->DrawPolygonDecal(decal, std::vector<olc::vf2d>(pos, pos + 4), std::vector<olc::vf2d>(uv, uv + 4), tint);
}

void DrawPolygonDecalWithDepth(olc::Decal *decal, const olc::vf2d *pos, const float *depth, const olc::vf2d *uv, const olc::Pixel tint)
{
    instance->DrawPolygonDecal(decal, std::vector<olc::vf2d>(pos, pos + 4), std::vector<float>(depth, depth + 4), std::vector<olc::vf2d>(uv, uv + 4), tint);
}

void DrawPolygonDecalWithColors(olc::Decal *decal, const olc::vf2d *pos, const olc::vf2d *uv, const olc::Pixel *tintColors)
{
    instance->DrawPolygonDecal(decal, std::vector<olc::vf2d>(pos, pos + 4), std::vector<olc::vf2d>(uv, uv + 4), std::vector<olc::Pixel>(tintColors, tintColors + 4));
}

void DrawPolygonDecalWithColorsAndTint(olc::Decal *decal, const olc::vf2d *pos, const olc::vf2d *uv, const olc::Pixel *colours, const olc::Pixel tint)
{
    instance->DrawPolygonDecal(decal, std::vector<olc::vf2d>(pos, pos + 4), std::vector<olc::vf2d>(uv, uv + 4), std::vector<olc::Pixel>(colours, colours + 4), tint);
}

void DrawPolygonDecalWithDepthAndColorsAndTint(olc::Decal *decal, const olc::vf2d *pos, const float *depth, const olc::vf2d *uv, const olc::Pixel *colours, const olc::Pixel tint)
{
    instance->DrawPolygonDecal(decal, std::vector<olc::vf2d>(pos, pos + 4), std::vector<float>(depth, depth + 4), std::vector<olc::vf2d>(uv, uv + 4), std::vector<olc::Pixel>(colours, colours + 4), tint);
}


// Draws a line in Decal Space
void DrawLineDecal(const olc::vf2d pos1, const olc::vf2d pos2, olc::Pixel p)
{
    instance->DrawLineDecal(pos1, pos2, p);
}

void DrawRotatedStringDecal(const olc::vf2d pos, const char *sText, const float fAngle, const olc::vf2d center, const olc::Pixel col, const olc::vf2d scale)
{
    instance->DrawRotatedStringDecal(pos, sText, fAngle, center, col, scale);
}

void DrawRotatedStringPropDecal(const olc::vf2d pos, const char *sText, const float fAngle, const olc::vf2d center, const olc::Pixel col, const olc::vf2d scale)
{
    instance->DrawRotatedStringPropDecal(pos, sText, fAngle, center, col, scale);
}

// Clears entire draw target to Pixel
void Clear(olc::Pixel p)
{
    instance->Clear(p);
}
#ifdef __cplusplus
}
#endif

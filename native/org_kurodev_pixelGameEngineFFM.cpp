#include <cstdint>
#include <iostream>
#include <memory>
#include <functional>

#define OLC_PGE_APPLICATION
#include "olcPixelGameEngine.h"

#ifdef __cplusplus
extern "C"
{
#endif
#ifndef VERSION
#define VERSION "unknown"
#endif

#ifdef _WIN32
#define EXPORT __declspec(dllexport)
#else
#define EXPORT __attribute__((visibility("default")))
#endif

    // Callback types
    typedef bool (*UserCreateCallback)(void);
    typedef bool (*UserUpdateCallback)(float);
    typedef bool (*UserDestroyCallback)(void);
    typedef bool (*OnConsoleCommandCallback)(const char *);
    typedef void (*OnTextEntryCompleteCallback)(const char *);

    class GameEngine;

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

    const char *get_library_version()
    {
        return VERSION;
    }

    void gameEngine_destroy(GameEngine *instance)
    {
        delete instance;
    }

    GameEngine *createGameEngineInstance(UserCreateCallback onCreate,
                                         UserUpdateCallback onUpdate,
                                         UserDestroyCallback onDestroy,
                                         OnConsoleCommandCallback onConsoleCommand,
                                         OnTextEntryCompleteCallback onTextEntryComplete)
    {
        GameEngine *instance = new GameEngine(onCreate, onUpdate, onDestroy, onConsoleCommand, onTextEntryComplete);
        return instance;
    }

    olc::rcode engine_construct(GameEngine *instance, int32_t screen_w, int32_t screen_h, int32_t pixel_w, int32_t pixel_h, bool full_screen, bool vsync, bool cohesion, bool realwindow)
    {
        return instance->Construct(screen_w, screen_h, pixel_w, pixel_h, full_screen, vsync, cohesion, realwindow);
    }

    int start(GameEngine *instance)
    {
        return instance->Start();
    }

    bool draw(GameEngine *instance, int x, int y, int rgba)
    {
        return instance->Draw(olc::vd2d(x, y), olc::Pixel(rgba));
    }

    void drawLine(GameEngine *instance, int32_t x1, int32_t y1, int32_t x2, int32_t y2, int32_t color, uint32_t pattern)
    {
        instance->DrawLine(x1, y1, x2, y2, olc::Pixel(color), pattern);
    }

    void drawString(GameEngine *instance, int32_t x, int32_t y, const char *str, int32_t color, uint32_t scale)
    {
        instance->DrawString(x, y, str, olc::Pixel(color), scale);
    }

    void drawRect(GameEngine *instance, int32_t x, int32_t y, int32_t w, int32_t h, int32_t color)
    {
        instance->DrawRect(x, y, w, h, olc::Pixel(color));
    }

    void fillRect(GameEngine *instance, int32_t x, int32_t y, int32_t w, int32_t h, int32_t color)
    {
        instance->FillRect(x, y, w, h, olc::Pixel(color));
    }

    void drawCircle(GameEngine *instance, int32_t x, int32_t y, int32_t radius, int32_t color, int32_t mask)
    {
        instance->DrawCircle(x, y, radius, olc::Pixel(color), mask & 0xFF);
    }

    void fillCircle(GameEngine *instance, int32_t x, int32_t y, int32_t radius, int32_t color)
    {
        instance->FillCircle(x, y, radius, olc::Pixel(color));
    }

    bool isFocused(GameEngine *instance)
    {
        return instance->IsFocused();
    }

    olc::HWButton getKey(GameEngine *instance, olc::Key k)
    {
        return instance->GetKey(k);
    }

    olc::HWButton getMouse(GameEngine *instance, int32_t k)
    {
        return instance->GetMouse(k);
    }

    olc::vi2d getMousePos(GameEngine *instance)
    {
        return instance->GetMousePos();
    }

    olc::vi2d getWindowMouse(GameEngine *instance)
    {
        olc::vi2d vec = instance->GetWindowMouse();
        return vec;
    }

    int32_t getMouseWheel(GameEngine *instance)
    {
        return instance->GetMouseWheel();
    }

    void setScreenSize(GameEngine *instance, int w, int h)
    {
        instance->SetScreenSize(w, h);
    }

    void consoleShow(GameEngine *instance, olc::Key closeKey, bool suspendTime)
    {
        instance->ConsoleShow(closeKey, suspendTime);
    }

    void consoleClear(GameEngine *instance)
    {
        instance->ConsoleClear();
    }

    bool isConsoleShowing(GameEngine *instance)
    {
        return instance->IsConsoleShowing();
    }

    void textEntryEnable(GameEngine *instance, bool enable, char *initialString)
    {
        instance->TextEntryEnable(enable, std::string(initialString));
    }

    int32_t textEntryGetString(char *buffer, int32_t bufferSize, GameEngine *instance)
    {
        std::string str = instance->TextEntryGetString();
        if (buffer == nullptr)
        {
            // Return required size (including null terminator)
            return str.length() + 1;
        }
        int32_t copyLen = str.length();
        std::memcpy(buffer, str.c_str(), copyLen);
        buffer[copyLen] = '\0'; // Always null-terminate
        return copyLen + 1;     // Return total size written (or needed)
    }

    int32_t textEntryGetCursor(GameEngine *instance)
    {
        return instance->TextEntryGetCursor();
    }

    bool isTextEntryEnabled(GameEngine *instance)
    {
        return instance->IsTextEntryEnabled();
    }

    olc::vi2d getScreenPixelSize(GameEngine *instance)
    {
        return instance->GetScreenPixelSize();
    }

    olc::vi2d getScreenSize(GameEngine *instance)
    {
        return instance->GetScreenSize();
    }

    void drawSprite(GameEngine *instance, int32_t x, int32_t y, olc::Sprite *sprite, uint32_t scale, uint8_t flip)
    {
        instance->DrawSprite(x, y, sprite, scale, olc::Sprite::Flip(flip));
    }

    void drawPartialSprite(GameEngine *instance, int32_t x, int32_t y, olc::Sprite *sprite, int32_t ox, int32_t oy, int32_t w, int32_t h, uint32_t scale, uint8_t flip)
    {
        instance->DrawPartialSprite(x, y, sprite, ox, oy, w, h, scale, olc::Sprite::Flip(flip));
    }

    void setPixelMode(GameEngine *instance, int32_t mode)
    {
        instance->SetPixelMode(olc::Pixel::Mode(mode));
    }

    void sprite_destroy(olc::Sprite *sprite) { delete sprite; }

    void decal_destroy(olc::Decal *decal) { delete decal; }

    int32_t sprite_width(olc::Sprite *s) { return s->width; }
    int32_t sprite_height(olc::Sprite *s) { return s->height; }
    bool sprite_setPixel(olc::Sprite *s, int32_t x, int32_t y, olc::Pixel p) { return s->SetPixel(x, y, p); }
    void sprite_bulk_setPixel(olc::Sprite *s, const int32_t *pRgba)
    {
        const int32_t w = s->width;
        const int32_t h = s->height;
        for (int32_t y = 0; y < h; y++)
        {
            for (int32_t x = 0; x < w; x++)
            {
                s->SetPixel(x, y, olc::Pixel(pRgba[y * w + x]));
            }
        }
    }

    olc::Pixel sprite_getPixel(olc::Sprite *s, olc::vi2d a) { return s->GetPixel(a); }

    /**
     * Creates a sprite from a file
     */
    olc::Sprite *sprite_createPath(const char *path)
    {
        return new olc::Sprite(std::string(path));
    }
    /**
     * Creates an empty sprite of the given size
     */
    olc::Sprite *sprite_createWidthHeight(int32_t w, int32_t h)
    {
        return new olc::Sprite(w, h);
    }

    olc::Decal *decal_create(olc::Sprite *sprite)
    {
        return new olc::Decal(sprite);
    }

    void setDecalMode(GameEngine *instance, int32_t mode)
    {
        instance->SetDecalMode(olc::DecalMode(mode));
    }

    void setDecalStructure(GameEngine *instance, int32_t structure)
    {
        instance->SetDecalStructure(olc::DecalStructure(structure));
    }

    olc::vi2d decal_vUVScale(olc::Decal *d)
    {
        return d->vUVScale;
    }

    void decal_Update(olc::Decal *d)
    {
        d->Update();
    }

    void decal_UpdateSprite(olc::Decal *d)
    {
        d->UpdateSprite();
    }

    void printToConsole(GameEngine *instance, char *text)
    {
        instance->ConsoleOut() << text;
    }

    // Draws a whole decal, with optional scale and tinting
    void drawDecal(GameEngine *instance, olc::vf2d pos, olc::Decal *decal, olc::vf2d scale, olc::Pixel tint)
    {
        instance->DrawDecal(pos, decal, scale, olc::Pixel(tint));
    }

    // Draws a region of a decal, with optional scale and tinting
    void drawPartialDecal(GameEngine *instance, const olc::vf2d pos, olc::Decal *decal, const olc::vf2d source_pos, const olc::vf2d source_size, const olc::vf2d scale, const olc::Pixel tint)
    {
        instance->DrawPartialDecal(pos, decal, source_pos, source_size, scale, tint);
    };
    // Draws fully user controlled 4 vertices, pos(pixels), uv(pixels), colours
    void DrawExplicitDecal(GameEngine *instance, olc::Decal *decal, const olc::vf2d *pos, const olc::vf2d *uv, const olc::Pixel *col, uint32_t elements)
    {
        instance->DrawExplicitDecal(decal, pos, uv, col, elements);
    }

    // Draws a decal with 4 arbitrary points, warping the texture to look "correct"
    // Note: C++ overloads for array, pointer, and std::array will be mapped to a single C function here.
    // The caller is responsible for ensuring the 'pos' pointer points to an array of 4 olc::vf2d.
    void DrawWarpedDecal(GameEngine *instance, olc::Decal *decal, const olc::vf2d *pos, const olc::Pixel tint)
    {
        instance->DrawWarpedDecal(decal, pos, tint);
    }

    // As above, but you can specify a region of a decal source sprite
    // Similar to DrawWarpedDecal, handling array/pointer versions through a single C function.
    void DrawPartialWarpedDecal(GameEngine *instance, olc::Decal *decal, const olc::vf2d *pos, const olc::vf2d source_pos, const olc::vf2d source_size, const olc::Pixel tint)
    {
        instance->DrawPartialWarpedDecal(decal, pos, source_pos, source_size, tint);
    }

    // Draws a decal rotated to specified angle, with point of rotation offset
    void DrawRotatedDecal(GameEngine *instance, const olc::vf2d pos, olc::Decal *decal, const float fAngle, const olc::vf2d center, const olc::vf2d scale, const olc::Pixel tint)
    {
        instance->DrawRotatedDecal(pos, decal, fAngle, center, scale, tint);
    }

    void DrawPartialRotatedDecal(GameEngine *instance, const olc::vf2d pos, olc::Decal *decal, const float fAngle, const olc::vf2d center, const olc::vf2d source_pos, const olc::vf2d source_size, const olc::vf2d scale, const olc::Pixel tint)
    {
        instance->DrawPartialRotatedDecal(pos, decal, fAngle, center, source_pos, source_size, scale, tint);
    }

    // Draws a multiline string as a decal, with tinting and scaling
    // Note: std::string in C++ will likely be passed as a const char* in C for FFM.
    void DrawStringDecal(GameEngine *instance, const olc::vf2d pos, const char *sText, const olc::Pixel col, const olc::vf2d scale)
    {
        instance->DrawStringDecal(pos, sText, col, scale);
    }

    void DrawStringPropDecal(GameEngine *instance, const olc::vf2d pos, const char *sText, const olc::Pixel col, const olc::vf2d scale)
    {
        instance->DrawStringPropDecal(pos, sText, col, scale);
    }

    // Draws a single shaded filled rectangle as a decal
    void DrawRectDecal(GameEngine *instance, const olc::vf2d pos, const olc::vf2d size, const olc::Pixel col)
    {
        instance->DrawRectDecal(pos, size, col);
    }

    void FillRectDecal(GameEngine *instance, const olc::vf2d pos, const olc::vf2d size, const olc::Pixel col)
    {
        instance->FillRectDecal(pos, size, col);
    }

    // Draws a single shaded filled triangle as a decal
    void FillTriangleDecal(GameEngine *instance, const olc::vf2d p0, const olc::vf2d p1, const olc::vf2d p2, const olc::Pixel col)
    {
        instance->FillTriangleDecal(p0, p1, p2, col);
    }

    // Draws a corner shaded rectangle as a decal
    void GradientFillRectDecal(GameEngine *instance, const olc::vf2d pos, const olc::vf2d size, const olc::Pixel colTL, const olc::Pixel colBL, const olc::Pixel colBR, const olc::Pixel colTR)
    {
        instance->GradientFillRectDecal(pos, size, colTL, colBL, colBR, colTR);
    }

    // Draws a corner shaded triangle as a decal
    void GradientTriangleDecal(GameEngine *instance, const olc::vf2d p0, const olc::vf2d p1, const olc::vf2d p2, const olc::Pixel c0, const olc::Pixel c1, const olc::Pixel c2)
    {
        instance->GradientTriangleDecal(p0, p1, p2, c0, c1, c2);
    }

    void GradientLineDecal(olc::PixelGameEngine *instance,
                           const olc::vf2d start, const olc::vf2d end,
                           const olc::Pixel colStart, const olc::Pixel colEnd,
                           int thickness)
    {
        olc::vf2d delta = end - start;
        float length = delta.mag();

        if (length <= 1.0f)
        {
            instance->FillCircle(start, thickness / 2.0f, colStart);
            return;
        }

        // Calculate perpendicular vector for thickness
        olc::vf2d dir = delta.norm();
        olc::vf2d perp = {-dir.y, dir.x};

        float halfThickness = thickness * 0.5f;

        // Calculate the four corners
        olc::vf2d corners[4] = {
            start + perp * halfThickness, // Top-left
            end + perp * halfThickness,   // Top-right
            end - perp * halfThickness,   // Bottom-right
            start - perp * halfThickness  // Bottom-left
        };

        // Split into two triangles and draw with gradient
        instance->GradientTriangleDecal(corners[0], corners[1], corners[3],
                                        colStart, colEnd, colStart);
        instance->GradientTriangleDecal(corners[1], corners[2], corners[3],
                                        colEnd, colEnd, colStart);
    }

    // Draws an arbitrary convex textured polygon using GPU
    // Note: std::vector in C++ will typically be passed as a pointer to the underlying data and a size/count.
    // For simplicity and direct mapping, we'll assume a pointer to the first element and let the C++ side handle the vector.
    // In a real FFM scenario, you might pass a pointer to the array data and the size.
    void DrawPolygonDecal(GameEngine *instance, olc::Decal *decal, const olc::vf2d *pos, const olc::vf2d *uv, const olc::Pixel tint)
    {
        instance->DrawPolygonDecal(decal, std::vector<olc::vf2d>(pos, pos + 4), std::vector<olc::vf2d>(uv, uv + 4), tint);
    }

    void DrawPolygonDecalWithDepth(GameEngine *instance, olc::Decal *decal, const olc::vf2d *pos, const float *depth, const olc::vf2d *uv, const olc::Pixel tint)
    {
        instance->DrawPolygonDecal(decal, std::vector<olc::vf2d>(pos, pos + 4), std::vector<float>(depth, depth + 4), std::vector<olc::vf2d>(uv, uv + 4), tint);
    }

    void DrawPolygonDecalWithColors(GameEngine *instance, olc::Decal *decal, const olc::vf2d *pos, const olc::vf2d *uv, const olc::Pixel *tintColors)
    {
        instance->DrawPolygonDecal(decal, std::vector<olc::vf2d>(pos, pos + 4), std::vector<olc::vf2d>(uv, uv + 4), std::vector<olc::Pixel>(tintColors, tintColors + 4));
    }

    void DrawPolygonDecalWithColorsAndTint(GameEngine *instance, olc::Decal *decal, const olc::vf2d *pos, const olc::vf2d *uv, const olc::Pixel *colours, const olc::Pixel tint)
    {
        instance->DrawPolygonDecal(decal, std::vector<olc::vf2d>(pos, pos + 4), std::vector<olc::vf2d>(uv, uv + 4), std::vector<olc::Pixel>(colours, colours + 4), tint);
    }

    void DrawPolygonDecalWithDepthAndColorsAndTint(GameEngine *instance, olc::Decal *decal, const olc::vf2d *pos, const float *depth, const olc::vf2d *uv, const olc::Pixel *colours, const olc::Pixel tint)
    {
        instance->DrawPolygonDecal(decal, std::vector<olc::vf2d>(pos, pos + 4), std::vector<float>(depth, depth + 4), std::vector<olc::vf2d>(uv, uv + 4), std::vector<olc::Pixel>(colours, colours + 4), tint);
    }

    // Draws a line in Decal Space
    void DrawLineDecal(GameEngine *instance, const olc::vf2d pos1, const olc::vf2d pos2, olc::Pixel p)
    {
        instance->DrawLineDecal(pos1, pos2, p);
    }

    void DrawRotatedStringDecal(GameEngine *instance, const olc::vf2d pos, const char *sText, const float fAngle, const olc::vf2d center, const olc::Pixel col, const olc::vf2d scale)
    {
        instance->DrawRotatedStringDecal(pos, sText, fAngle, center, col, scale);
    }

    void DrawRotatedStringPropDecal(GameEngine *instance, const olc::vf2d pos, const char *sText, const float fAngle, const olc::vf2d center, const olc::Pixel col, const olc::vf2d scale)
    {
        instance->DrawRotatedStringPropDecal(pos, sText, fAngle, center, col, scale);
    }

    // Clears entire draw target to Pixel
    void Clear(GameEngine *instance, olc::Pixel p)
    {
        instance->Clear(p);
    }

    void resize(GameEngine *instance, const olc::vi2d vPos, const olc::vi2d vSize)
    {
        instance->SetWindowSize(vPos, vSize);
    }

    olc::vi2d getWindowSize(GameEngine *instance)
    {
        return instance->GetWindowSize();
    }
    // Gets Actual Window position
    olc::vi2d getWindowPos(GameEngine *instance)
    {
        return instance->GetWindowPos();
    };

    int32_t getFps(GameEngine *instance)
    {
        return instance->GetFPS();
    }

    void setWindowTitle(GameEngine *instance, const char *title)
    {
        instance->sAppName = std::string(title);
    }

    /**
     * Creates a layer and returns it's index.
     */
    int32_t createLayer(GameEngine *instance)
    {
        return instance->CreateLayer();
    }

    void enableLayer(GameEngine *instance, uint8_t layer, bool b)
    {
        return instance->EnableLayer(layer, b);
    }

    typedef void (*customRenderFn)();

    void SetLayerCustomRenderFunction(GameEngine *instance, uint8_t layer, customRenderFn f)
    {
        return instance->SetLayerCustomRenderFunction(layer, f);
    }

    void SetLayerOffset(GameEngine *instance, uint8_t layer, float x, float y)
    {
        return instance->SetLayerOffset(layer, x, y);
    }

    void SetLayerScale(GameEngine *instance, uint8_t layer, float x, float y)
    {
        return instance->SetLayerScale(layer, x, y);
    }

    void SetLayerTint(GameEngine *instance, uint8_t layer, olc::Pixel &tint)
    {
        return instance->SetLayerTint(layer, tint);
    }

#ifdef __cplusplus
}
#endif

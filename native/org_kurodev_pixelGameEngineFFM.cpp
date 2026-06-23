#include <cstdint>
#include <iostream>
#include <memory>
#include <functional>
#include <algorithm>
#include <array>
#include <cstring>
#include <iterator>
#include <vector>

#define OLC_PGE_APPLICATION
#include "olcPixelGameEngine.h"

#ifndef VERSION
#define VERSION "unknown"
#endif

#ifdef _WIN32
#define EXPORT __declspec(dllexport)
#else
#define EXPORT __attribute__((visibility("default")))
#endif

static int32_t copyStringToBuffer(const std::string &str, char *buffer, int32_t bufferSize) {
    const int32_t required = static_cast<int32_t>(str.size()) + 1;
    if (buffer == nullptr || bufferSize <= 0) {
        return required;
    }

    const int32_t copyLen = std::min<int32_t>(required - 1, bufferSize - 1);
    std::memcpy(buffer, str.c_str(), copyLen);
    buffer[copyLen] = '\0';
    return required;
}

template<typename T>
static std::vector<T> copyArray(const T *data, int32_t count) {
    if (data == nullptr || count <= 0) {
        return {};
    }
    return std::vector<T>(data, data + count);
}

static std::array<float, 16> copyMatrix16(const float *data) {
    std::array<float, 16> result{};
    if (data != nullptr) {
        std::copy(data, data + result.size(), result.begin());
    }
    return result;
}

static std::array<float, 4> copyArray4(const float *data) {
    std::array<float, 4> result{};
    if (data != nullptr) {
        std::copy(data, data + result.size(), result.begin());
    }
    return result;
}

struct DrawCommand {
    int32_t op;
    int32_t args[12];
    const void *payload;
};

static_assert(sizeof(DrawCommand) == 64, "DrawCommand layout must stay stable");

static float intBitsToFloat(int32_t bits) {
    float value;
    std::memcpy(&value, &bits, sizeof(value));
    return value;
}

#ifdef __cplusplus
extern "C" {
#endif

// Callback types
typedef bool (*UserCreateCallback)(void);

typedef bool (*UserUpdateCallback)(float);

typedef bool (*UserDestroyCallback)(void);

typedef bool (*OnConsoleCommandCallback)(const char *);

typedef void (*OnTextEntryCompleteCallback)(const char *);

class GameEngine;

class GameEngine : public olc::PixelGameEngine {
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
          onTextEntryComplete(onTextEntryComplete) {
    }

    bool OnUserCreate() override {
        if (!m_onCreate) {
            std::cerr << "No OnUserCreate callback provided" << std::endl;
            return false;
        }
        return m_onCreate();
    }

    bool OnUserUpdate(float fElapsedTime) override {
        if (!m_onUpdate) {
            std::cerr << "No OnUserUpdate callback provided" << std::endl;
            return false;
        }
        return m_onUpdate(fElapsedTime);
    }

    bool OnUserDestroy() override {
        bool result = true;
        if (m_onDestroy) {
            result = m_onDestroy();
        }
        return result;
    }

    // Called when a text entry is confirmed with "enter" key
    void OnTextEntryComplete(const std::string &sText) {
        onTextEntryComplete(sText.c_str());
    }

    // Called when a console command is executed
    bool OnConsoleCommand(const std::string &sCommand) {
        return onConsoleCommand(sCommand.c_str());
    }
};

const char *get_library_version() {
    return VERSION;
}

void gameEngine_destroy(GameEngine *instance) {
    delete instance;
}

GameEngine *createGameEngineInstance(UserCreateCallback onCreate,
                                     UserUpdateCallback onUpdate,
                                     UserDestroyCallback onDestroy,
                                     OnConsoleCommandCallback onConsoleCommand,
                                     OnTextEntryCompleteCallback onTextEntryComplete) {
    GameEngine *instance = new GameEngine(onCreate, onUpdate, onDestroy, onConsoleCommand, onTextEntryComplete);
    return instance;
}

olc::ResourcePack *resourcePack_create() {
    return new olc::ResourcePack();
}

void resourcePack_destroy(olc::ResourcePack *pack) {
    delete pack;
}

bool resourcePack_addFile(olc::ResourcePack *pack, const char *path) {
    return pack->AddFile(std::string(path));
}

bool resourcePack_loadPack(olc::ResourcePack *pack, const char *path, const char *key) {
    return pack->LoadPack(std::string(path), std::string(key));
}

bool resourcePack_savePack(olc::ResourcePack *pack, const char *path, const char *key) {
    return pack->SavePack(std::string(path), std::string(key));
}

bool resourcePack_loaded(olc::ResourcePack *pack) {
    return pack->Loaded();
}

int32_t resourcePack_getFileBufferSize(olc::ResourcePack *pack, const char *path) {
    olc::ResourceBuffer buffer = pack->GetFileBuffer(std::string(path));
    return static_cast<int32_t>(buffer.vMemory.size());
}

int32_t resourcePack_getFileBuffer(olc::ResourcePack *pack, const char *path, char *buffer, int32_t bufferSize) {
    olc::ResourceBuffer resource = pack->GetFileBuffer(std::string(path));
    const int32_t required = static_cast<int32_t>(resource.vMemory.size());
    if (buffer != nullptr && bufferSize > 0 && required > 0) {
        const int32_t copyLen = std::min(required, bufferSize);
        std::memcpy(buffer, resource.vMemory.data(), copyLen);
    }
    return required;
}

olc::rcode engine_construct(GameEngine *instance, int32_t screen_w, int32_t screen_h, int32_t pixel_w, int32_t pixel_h,
                            bool full_screen, bool vsync, bool cohesion, bool realwindow) {
    return instance->Construct(screen_w, screen_h, pixel_w, pixel_h, full_screen, vsync, cohesion, realwindow);
}

int start(GameEngine *instance) {
    return instance->Start();
}

bool draw(GameEngine *instance, int x, int y, int rgba) {
    return instance->Draw(olc::vd2d(x, y), olc::Pixel(rgba));
}

void drawLine(GameEngine *instance, int32_t x1, int32_t y1, int32_t x2, int32_t y2, int32_t color, uint32_t pattern) {
    instance->DrawLine(x1, y1, x2, y2, olc::Pixel(color), pattern);
}

void drawString(GameEngine *instance, int32_t x, int32_t y, const char *str, int32_t color, uint32_t scale) {
    instance->DrawString(x, y, str, olc::Pixel(color), scale);
}

void drawStringProp(GameEngine *instance, int32_t x, int32_t y, const char *str, int32_t color, uint32_t scale) {
    instance->DrawStringProp(x, y, str, olc::Pixel(color), scale);
}

olc::vi2d getTextSize(GameEngine *instance, const char *str) {
    return instance->GetTextSize(std::string(str));
}

olc::vi2d getTextSizeProp(GameEngine *instance, const char *str) {
    return instance->GetTextSizeProp(std::string(str));
}

void drawRect(GameEngine *instance, int32_t x, int32_t y, int32_t w, int32_t h, int32_t color) {
    instance->DrawRect(x, y, w, h, olc::Pixel(color));
}

void fillRect(GameEngine *instance, int32_t x, int32_t y, int32_t w, int32_t h, int32_t color) {
    instance->FillRect(x, y, w, h, olc::Pixel(color));
}

void drawTriangle(GameEngine *instance, int32_t x1, int32_t y1, int32_t x2, int32_t y2, int32_t x3, int32_t y3,
                  int32_t color) {
    instance->DrawTriangle(x1, y1, x2, y2, x3, y3, olc::Pixel(color));
}

void fillTriangle(GameEngine *instance, int32_t x1, int32_t y1, int32_t x2, int32_t y2, int32_t x3, int32_t y3,
                  int32_t color) {
    instance->FillTriangle(x1, y1, x2, y2, x3, y3, olc::Pixel(color));
}

void fillTexturedTriangle(GameEngine *instance, const olc::vf2d *points, const olc::vf2d *texture,
                          const olc::Pixel *colors, int32_t elements, olc::Sprite *sprite) {
    instance->FillTexturedTriangle(copyArray(points, elements), copyArray(texture, elements),
                                   copyArray(colors, elements), sprite);
}

void fillTexturedPolygon(GameEngine *instance, const olc::vf2d *points, const olc::vf2d *texture,
                         const olc::Pixel *colors, int32_t elements, olc::Sprite *sprite, int32_t structure) {
    instance->FillTexturedPolygon(copyArray(points, elements), copyArray(texture, elements),
                                  copyArray(colors, elements), sprite, olc::DecalStructure(structure));
}

void drawCircle(GameEngine *instance, int32_t x, int32_t y, int32_t radius, int32_t color, int32_t mask) {
    instance->DrawCircle(x, y, radius, olc::Pixel(color), mask & 0xFF);
}

void fillCircle(GameEngine *instance, int32_t x, int32_t y, int32_t radius, int32_t color) {
    instance->FillCircle(x, y, radius, olc::Pixel(color));
}

bool isFocused(GameEngine *instance) {
    return instance->IsFocused();
}

olc::HWButton getKey(GameEngine *instance, olc::Key k) {
    return instance->GetKey(k);
}

olc::HWButton getMouse(GameEngine *instance, int32_t k) {
    return instance->GetMouse(k);
}

olc::vi2d getMousePos(GameEngine *instance) {
    return instance->GetMousePos();
}

olc::vi2d getWindowMouse(GameEngine *instance) {
    olc::vi2d vec = instance->GetWindowMouse();
    return vec;
}

int32_t getMouseWheel(GameEngine *instance) {
    return instance->GetMouseWheel();
}

int32_t getMouseX(GameEngine *instance) {
    return instance->GetMouseX();
}

int32_t getMouseY(GameEngine *instance) {
    return instance->GetMouseY();
}

int32_t getKeyMapCount(GameEngine *instance) {
    return static_cast<int32_t>(instance->GetKeyMap().size());
}

int64_t getKeyMapNativeKeyAt(GameEngine *instance, int32_t index) {
    const auto &map = instance->GetKeyMap();
    if (index < 0 || static_cast<size_t>(index) >= map.size()) {
        return -1;
    }

    auto it = map.begin();
    std::advance(it, index);
    return static_cast<int64_t>(it->first);
}

int32_t getKeyMapEngineKeyAt(GameEngine *instance, int32_t index) {
    const auto &map = instance->GetKeyMap();
    if (index < 0 || static_cast<size_t>(index) >= map.size()) {
        return static_cast<int32_t>(olc::Key::NONE);
    }

    auto it = map.begin();
    std::advance(it, index);
    return static_cast<int32_t>(it->second);
}

void setScreenSize(GameEngine *instance, int w, int h) {
    instance->SetScreenSize(w, h);
}

int32_t screenWidth(GameEngine *instance) {
    return instance->ScreenWidth();
}

int32_t screenHeight(GameEngine *instance) {
    return instance->ScreenHeight();
}

int32_t getDrawTargetWidth(GameEngine *instance) {
    return instance->GetDrawTargetWidth();
}

int32_t getDrawTargetHeight(GameEngine *instance) {
    return instance->GetDrawTargetHeight();
}

olc::Sprite *getDrawTarget(GameEngine *instance) {
    return instance->GetDrawTarget();
}

void setDrawTargetSprite(GameEngine *instance, olc::Sprite *sprite) {
    instance->SetDrawTarget(sprite);
}

void setDrawTargetLayer(GameEngine *instance, uint8_t layer, bool dirty) {
    instance->SetDrawTarget(layer, dirty);
}

float getElapsedTime(GameEngine *instance) {
    return instance->GetElapsedTime();
}

olc::vi2d getPixelSize(GameEngine *instance) {
    return instance->GetPixelSize();
}

int32_t getDroppedFilesCount(GameEngine *instance) {
    return static_cast<int32_t>(instance->GetDroppedFiles().size());
}

int32_t getDroppedFile(char *buffer, int32_t bufferSize, GameEngine *instance, int32_t index) {
    const auto &files = instance->GetDroppedFiles();
    if (index < 0 || static_cast<size_t>(index) >= files.size()) {
        return copyStringToBuffer("", buffer, bufferSize);
    }
    return copyStringToBuffer(files[index], buffer, bufferSize);
}

olc::vi2d getDroppedFilesPoint(GameEngine *instance) {
    return instance->GetDroppedFilesPoint();
}

int32_t showWindowFrame(GameEngine *instance, bool showFrame) {
    return instance->ShowWindowFrame(showFrame);
}

void consoleShow(GameEngine *instance, olc::Key closeKey, bool suspendTime) {
    instance->ConsoleShow(closeKey, suspendTime);
}

void consoleClear(GameEngine *instance) {
    instance->ConsoleClear();
}

void consoleCaptureStdOut(GameEngine *instance, bool capture) {
    instance->ConsoleCaptureStdOut(capture);
}

bool isConsoleShowing(GameEngine *instance) {
    return instance->IsConsoleShowing();
}

void textEntryEnable(GameEngine *instance, bool enable, char *initialString) {
    instance->TextEntryEnable(enable, std::string(initialString));
}

int32_t textEntryGetString(char *buffer, int32_t bufferSize, GameEngine *instance) {
    return copyStringToBuffer(instance->TextEntryGetString(), buffer, bufferSize);
}

int32_t textEntryGetCursor(GameEngine *instance) {
    return instance->TextEntryGetCursor();
}

bool isTextEntryEnabled(GameEngine *instance) {
    return instance->IsTextEntryEnabled();
}

olc::vi2d getScreenPixelSize(GameEngine *instance) {
    return instance->GetScreenPixelSize();
}

olc::vi2d getScreenSize(GameEngine *instance) {
    return instance->GetScreenSize();
}

void drawSprite(GameEngine *instance, int32_t x, int32_t y, olc::Sprite *sprite, uint32_t scale, uint8_t flip) {
    instance->DrawSprite(x, y, sprite, scale, olc::Sprite::Flip(flip));
}

void drawPartialSprite(GameEngine *instance, int32_t x, int32_t y, olc::Sprite *sprite, int32_t ox, int32_t oy,
                       int32_t w, int32_t h, uint32_t scale, uint8_t flip) {
    instance->DrawPartialSprite(x, y, sprite, ox, oy, w, h, scale, olc::Sprite::Flip(flip));
}

void setPixelMode(GameEngine *instance, int32_t mode) {
    instance->SetPixelMode(olc::Pixel::Mode(mode));
}

int32_t getPixelMode(GameEngine *instance) {
    return static_cast<int32_t>(instance->GetPixelMode());
}

typedef uint32_t (*customPixelModeFn)(int32_t, int32_t, uint32_t, uint32_t);

void setPixelModeCustom(GameEngine *instance, customPixelModeFn fn) {
    if (fn == nullptr) {
        instance->SetPixelMode(olc::Pixel::NORMAL);
        return;
    }

    instance->SetPixelMode([fn](const int x, const int y, const olc::Pixel &source, const olc::Pixel &dest) {
        return olc::Pixel(fn(x, y, source.n, dest.n));
    });
}

void setPixelBlend(GameEngine *instance, float blend) {
    instance->SetPixelBlend(blend);
}

void flushDrawQueue(GameEngine *instance, const DrawCommand *commands, int32_t count) {
    if (instance == nullptr || commands == nullptr || count <= 0) {
        return;
    }

    for (int32_t i = 0; i < count; i++) {
        const DrawCommand &command = commands[i];
        switch (command.op) {
            case 1:
                instance->Draw(olc::vi2d(command.args[0], command.args[1]), olc::Pixel(command.args[2]));
                break;
            case 2:
                instance->DrawLine(command.args[0], command.args[1], command.args[2], command.args[3],
                                   olc::Pixel(command.args[4]), static_cast<uint32_t>(command.args[5]));
                break;
            case 3:
                instance->DrawRect(command.args[0], command.args[1], command.args[2], command.args[3],
                                   olc::Pixel(command.args[4]));
                break;
            case 4:
                instance->FillRect(command.args[0], command.args[1], command.args[2], command.args[3],
                                   olc::Pixel(command.args[4]));
                break;
            case 5:
                instance->DrawTriangle(command.args[0], command.args[1], command.args[2], command.args[3],
                                       command.args[4], command.args[5], olc::Pixel(command.args[6]));
                break;
            case 6:
                instance->FillTriangle(command.args[0], command.args[1], command.args[2], command.args[3],
                                       command.args[4], command.args[5], olc::Pixel(command.args[6]));
                break;
            case 7:
                instance->DrawCircle(command.args[0], command.args[1], command.args[2], olc::Pixel(command.args[3]),
                                     command.args[4]);
                break;
            case 8:
                instance->FillCircle(command.args[0], command.args[1], command.args[2], olc::Pixel(command.args[3]));
                break;
            case 9:
                instance->Clear(olc::Pixel(command.args[0]));
                break;
            case 10:
                instance->ClearBuffer(olc::Pixel(command.args[0]), command.args[1] != 0);
                break;
            case 11:
                instance->DrawString(command.args[0], command.args[1], static_cast<const char *>(command.payload),
                                     olc::Pixel(command.args[2]), static_cast<uint32_t>(command.args[3]));
                break;
            case 12:
                instance->DrawStringProp(command.args[0], command.args[1], static_cast<const char *>(command.payload),
                                        olc::Pixel(command.args[2]), static_cast<uint32_t>(command.args[3]));
                break;
            case 20:
                instance->SetScreenSize(command.args[0], command.args[1]);
                break;
            case 21:
                instance->SetDrawTarget(static_cast<olc::Sprite *>(const_cast<void *>(command.payload)));
                break;
            case 22:
                instance->SetDrawTarget(static_cast<uint8_t>(command.args[0]), command.args[1] != 0);
                break;
            case 23:
                instance->SetPixelMode(olc::Pixel::Mode(command.args[0]));
                break;
            case 24:
                setPixelModeCustom(instance, reinterpret_cast<customPixelModeFn>(const_cast<void *>(command.payload)));
                break;
            case 25:
                instance->SetPixelBlend(intBitsToFloat(command.args[0]));
                break;
            case 26:
                instance->EnablePixelTransfer(command.args[0] != 0);
                break;
            case 27:
                instance->SetDecalMode(olc::DecalMode(command.args[0]));
                break;
            case 28:
                instance->SetDecalStructure(olc::DecalStructure(command.args[0]));
                break;
            default:
                std::cerr << "Unknown draw command opcode: " << command.op << std::endl;
                break;
        }
    }
}

olc::Sprite *sprite_create() {
    return new olc::Sprite();
}

void sprite_destroy(olc::Sprite *sprite) { delete sprite; }

void decal_destroy(olc::Decal *decal) { delete decal; }

int32_t sprite_width(olc::Sprite *s) { return s->width; }
int32_t sprite_height(olc::Sprite *s) { return s->height; }

int32_t sprite_loadFromFile(olc::Sprite *s, const char *path, olc::ResourcePack *pack) {
    return s->LoadFromFile(std::string(path), pack);
}

void sprite_setSampleMode(olc::Sprite *s, int32_t mode) {
    s->SetSampleMode(olc::Sprite::Mode(mode));
}

int32_t sprite_getSampleMode(olc::Sprite *s) {
    return static_cast<int32_t>(s->modeSample);
}

bool sprite_setPixel(olc::Sprite *s, int32_t x, int32_t y, olc::Pixel p) { return s->SetPixel(x, y, p); }

void sprite_bulk_setPixel(olc::Sprite *s, const int32_t *pRgba) {
    const int32_t w = s->width;
    const int32_t h = s->height;
    for (int32_t y = 0; y < h; y++) {
        for (int32_t x = 0; x < w; x++) {
            s->SetPixel(x, y, olc::Pixel(pRgba[y * w + x]));
        }
    }
}

olc::Pixel sprite_getPixel(olc::Sprite *s, olc::vi2d a) { return s->GetPixel(a); }
olc::Pixel sprite_getPixelXY(olc::Sprite *s, int32_t x, int32_t y) { return s->GetPixel(x, y); }
olc::Pixel sprite_sample(olc::Sprite *s, float x, float y) { return s->Sample(x, y); }
olc::Pixel sprite_sampleBL(olc::Sprite *s, float u, float v) { return s->SampleBL(u, v); }
int32_t sprite_dataLength(olc::Sprite *s) { return s->width * s->height; }

olc::Pixel sprite_getDataPixel(olc::Sprite *s, int32_t index) {
    if (index < 0 || index >= s->width * s->height) {
        return olc::Pixel(0, 0, 0, 0);
    }
    return s->GetData()[index];
}

olc::Sprite *sprite_duplicate(olc::Sprite *s) {
    return s->Duplicate();
}

olc::Sprite *sprite_duplicateRegion(olc::Sprite *s, olc::vi2d pos, olc::vi2d size) {
    return s->Duplicate(pos, size);
}

olc::vi2d sprite_size(olc::Sprite *s) {
    return s->Size();
}

void sprite_setSize(olc::Sprite *s, int32_t w, int32_t h) {
    s->SetSize(w, h);
}

olc::SpritePatch sprite_patch(olc::Sprite *s, olc::vi2d pos, olc::vi2d size) {
    return s->Patch(pos, size);
}

olc::SpritePatch sprite_patchUv(olc::Sprite *s, olc::vf2d pBL, olc::vf2d pTL, olc::vf2d pTR, olc::vf2d pBR) {
    return s->Patch(pBL, pTL, pTR, pBR);
}

/**
 * Creates a sprite from a file
 */
olc::Sprite *sprite_createPath(const char *path) {
    return new olc::Sprite(std::string(path));
}

/**
 * Creates an empty sprite of the given size
 */
olc::Sprite *sprite_createWidthHeight(int32_t w, int32_t h) {
    return new olc::Sprite(w, h);
}

olc::Decal *decal_create(olc::Sprite *sprite) {
    return new olc::Decal(sprite);
}

olc::Decal *decal_createOptions(olc::Sprite *sprite, bool filter, bool clamp) {
    return new olc::Decal(sprite, filter, clamp);
}

olc::Decal *decal_createExisting(uint32_t existingTextureResource, olc::Sprite *sprite) {
    return new olc::Decal(existingTextureResource, sprite);
}

int32_t decal_id(olc::Decal *d) {
    return d->id;
}

int32_t decal_width(olc::Decal *d) {
    return d->width;
}

int32_t decal_height(olc::Decal *d) {
    return d->height;
}

olc::Sprite *decal_sprite(olc::Decal *d) {
    return d->sprite;
}

void setDecalMode(GameEngine *instance, int32_t mode) {
    instance->SetDecalMode(olc::DecalMode(mode));
}

void setDecalStructure(GameEngine *instance, int32_t structure) {
    instance->SetDecalStructure(olc::DecalStructure(structure));
}

olc::vf2d decal_vUVScale(olc::Decal *d) {
    return d->vUVScale;
}

void decal_Update(olc::Decal *d) {
    d->Update();
}

void decal_UpdateSprite(olc::Decal *d) {
    d->UpdateSprite();
}

olc::DecalPatch decal_patch(olc::Decal *d, olc::vi2d pos, olc::vi2d size) {
    return d->Patch(pos, size);
}

olc::DecalPatch decal_patchUv(olc::Decal *d, olc::vf2d pBL, olc::vf2d pTL, olc::vf2d pTR, olc::vf2d pBR) {
    return d->Patch(pBL, pTL, pTR, pBR);
}

olc::Renderable *renderable_create() {
    return new olc::Renderable();
}

void renderable_destroy(olc::Renderable *renderable) {
    delete renderable;
}

int32_t renderable_load(olc::Renderable *renderable, const char *path, olc::ResourcePack *pack, bool filter,
                        bool clamp) {
    return renderable->Load(std::string(path), pack, filter, clamp);
}

void renderable_createTarget(olc::Renderable *renderable, uint32_t width, uint32_t height, bool filter, bool clamp) {
    renderable->Create(width, height, filter, clamp);
}

olc::Decal *renderable_decal(olc::Renderable *renderable) {
    return renderable->Decal();
}

olc::Sprite *renderable_sprite(olc::Renderable *renderable) {
    return renderable->Sprite();
}

void printToConsole(GameEngine *instance, char *text) {
    instance->ConsoleOut() << text;
}

// Draws a whole decal, with optional scale and tinting
void drawDecal(GameEngine *instance, olc::vf2d pos, olc::Decal *decal, olc::vf2d scale, olc::Pixel tint) {
    instance->DrawDecal(pos, decal, scale, olc::Pixel(tint));
}

// Draws a region of a decal, with optional scale and tinting
void drawPartialDecal(GameEngine *instance, const olc::vf2d pos, olc::Decal *decal, const olc::vf2d source_pos,
                      const olc::vf2d source_size, const olc::vf2d scale, const olc::Pixel tint) {
    instance->DrawPartialDecal(pos, decal, source_pos, source_size, scale, tint);
};

void drawPartialDecalSized(GameEngine *instance, const olc::vf2d pos, const olc::vf2d size, olc::Decal *decal,
                           const olc::vf2d source_pos, const olc::vf2d source_size, const olc::Pixel tint) {
    instance->DrawPartialDecal(pos, size, decal, source_pos, source_size, tint);
}

// Draws fully user controlled 4 vertices, pos(pixels), uv(pixels), colours
void DrawExplicitDecal(GameEngine *instance, olc::Decal *decal, const olc::vf2d *pos, const olc::vf2d *uv,
                       const olc::Pixel *col, uint32_t elements) {
    instance->DrawExplicitDecal(decal, pos, uv, col, elements);
}

// Draws a decal with 4 arbitrary points, warping the texture to look "correct"
// Note: C++ overloads for array, pointer, and std::array will be mapped to a single C function here.
// The caller is responsible for ensuring the 'pos' pointer points to an array of 4 olc::vf2d.
void DrawWarpedDecal(GameEngine *instance, olc::Decal *decal, const olc::vf2d *pos, const olc::Pixel tint) {
    instance->DrawWarpedDecal(decal, pos, tint);
}

// As above, but you can specify a region of a decal source sprite
// Similar to DrawWarpedDecal, handling array/pointer versions through a single C function.
void DrawPartialWarpedDecal(GameEngine *instance, olc::Decal *decal, const olc::vf2d *pos, const olc::vf2d source_pos,
                            const olc::vf2d source_size, const olc::Pixel tint) {
    instance->DrawPartialWarpedDecal(decal, pos, source_pos, source_size, tint);
}

// Draws a decal rotated to specified angle, with point of rotation offset
void DrawRotatedDecal(GameEngine *instance, const olc::vf2d pos, olc::Decal *decal, const float fAngle,
                      const olc::vf2d center, const olc::vf2d scale, const olc::Pixel tint) {
    instance->DrawRotatedDecal(pos, decal, fAngle, center, scale, tint);
}

void DrawPartialRotatedDecal(GameEngine *instance, const olc::vf2d pos, olc::Decal *decal, const float fAngle,
                             const olc::vf2d center, const olc::vf2d source_pos, const olc::vf2d source_size,
                             const olc::vf2d scale, const olc::Pixel tint) {
    instance->DrawPartialRotatedDecal(pos, decal, fAngle, center, source_pos, source_size, scale, tint);
}

// Draws a multiline string as a decal, with tinting and scaling
// Note: std::string in C++ will likely be passed as a const char* in C for FFM.
void DrawStringDecal(GameEngine *instance, const olc::vf2d pos, const char *sText, const olc::Pixel col,
                     const olc::vf2d scale) {
    instance->DrawStringDecal(pos, sText, col, scale);
}

void DrawStringPropDecal(GameEngine *instance, const olc::vf2d pos, const char *sText, const olc::Pixel col,
                         const olc::vf2d scale) {
    instance->DrawStringPropDecal(pos, sText, col, scale);
}

// Draws a single shaded filled rectangle as a decal
void DrawRectDecal(GameEngine *instance, const olc::vf2d pos, const olc::vf2d size, const olc::Pixel col) {
    instance->DrawRectDecal(pos, size, col);
}

void FillRectDecal(GameEngine *instance, const olc::vf2d pos, const olc::vf2d size, const olc::Pixel col) {
    instance->FillRectDecal(pos, size, col);
}

// Draws a single shaded filled triangle as a decal
void FillTriangleDecal(GameEngine *instance, const olc::vf2d p0, const olc::vf2d p1, const olc::vf2d p2,
                       const olc::Pixel col) {
    instance->FillTriangleDecal(p0, p1, p2, col);
}

// Draws a corner shaded rectangle as a decal
void GradientFillRectDecal(GameEngine *instance, const olc::vf2d pos, const olc::vf2d size, const olc::Pixel colTL,
                           const olc::Pixel colBL, const olc::Pixel colBR, const olc::Pixel colTR) {
    instance->GradientFillRectDecal(pos, size, colTL, colBL, colBR, colTR);
}

// Draws a corner shaded triangle as a decal
void GradientTriangleDecal(GameEngine *instance, const olc::vf2d p0, const olc::vf2d p1, const olc::vf2d p2,
                           const olc::Pixel c0, const olc::Pixel c1, const olc::Pixel c2) {
    instance->GradientTriangleDecal(p0, p1, p2, c0, c1, c2);
}

void GradientLineDecal(olc::PixelGameEngine *instance,
                       const olc::vf2d start, const olc::vf2d end,
                       const olc::Pixel colStart, const olc::Pixel colEnd,
                       int thickness) {
    olc::vf2d delta = end - start;
    float length = delta.mag();

    if (length <= 1.0f) {
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
        end + perp * halfThickness, // Top-right
        end - perp * halfThickness, // Bottom-right
        start - perp * halfThickness // Bottom-left
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
void DrawPolygonDecal(GameEngine *instance, olc::Decal *decal, const olc::vf2d *pos, const olc::vf2d *uv,
                      int32_t elements, const olc::Pixel tint) {
    instance->DrawPolygonDecal(decal, copyArray(pos, elements), copyArray(uv, elements), tint);
}

void DrawPolygonDecalWithDepth(GameEngine *instance, olc::Decal *decal, const olc::vf2d *pos, const float *depth,
                               const olc::vf2d *uv, int32_t elements, const olc::Pixel tint) {
    instance->DrawPolygonDecal(decal, copyArray(pos, elements), copyArray(depth, elements), copyArray(uv, elements),
                               tint);
}

void DrawPolygonDecalWithColors(GameEngine *instance, olc::Decal *decal, const olc::vf2d *pos, const olc::vf2d *uv,
                                const olc::Pixel *tintColors, int32_t elements) {
    instance->DrawPolygonDecal(decal, copyArray(pos, elements), copyArray(uv, elements),
                               copyArray(tintColors, elements));
}

void DrawPolygonDecalWithColorsAndTint(GameEngine *instance, olc::Decal *decal, const olc::vf2d *pos,
                                       const olc::vf2d *uv, const olc::Pixel *colours, int32_t elements,
                                       const olc::Pixel tint) {
    instance->DrawPolygonDecal(decal, copyArray(pos, elements), copyArray(uv, elements), copyArray(colours, elements),
                               tint);
}

void DrawPolygonDecalWithDepthAndColorsAndTint(GameEngine *instance, olc::Decal *decal, const olc::vf2d *pos,
                                               const float *depth, const olc::vf2d *uv, const olc::Pixel *colours,
                                               int32_t elements, const olc::Pixel tint) {
    instance->DrawPolygonDecal(decal, copyArray(pos, elements), copyArray(depth, elements), copyArray(uv, elements),
                               copyArray(colours, elements), tint);
}

// Draws a line in Decal Space
void DrawLineDecal(GameEngine *instance, const olc::vf2d pos1, const olc::vf2d pos2, olc::Pixel p) {
    instance->DrawLineDecal(pos1, pos2, p);
}

void DrawRotatedStringDecal(GameEngine *instance, const olc::vf2d pos, const char *sText, const float fAngle,
                            const olc::vf2d center, const olc::Pixel col, const olc::vf2d scale) {
    instance->DrawRotatedStringDecal(pos, sText, fAngle, center, col, scale);
}

void DrawRotatedStringPropDecal(GameEngine *instance, const olc::vf2d pos, const char *sText, const float fAngle,
                                const olc::vf2d center, const olc::Pixel col, const olc::vf2d scale) {
    instance->DrawRotatedStringPropDecal(pos, sText, fAngle, center, col, scale);
}

// Clears entire draw target to Pixel
void Clear(GameEngine *instance, olc::Pixel p) {
    instance->Clear(p);
}

void ClearBuffer(GameEngine *instance, olc::Pixel p, bool depth) {
    instance->ClearBuffer(p, depth);
}

olc::Sprite *GetFontSprite(GameEngine *instance) {
    return instance->GetFontSprite();
}

bool ClipLineToDrawTarget(GameEngine *instance, olc::vi2d *p1, olc::vi2d *p2) {
    if (p1 == nullptr || p2 == nullptr) {
        return false;
    }
    return instance->ClipLineToDrawTarget(*p1, *p2);
}

void DrawSpritePatch(GameEngine *instance, const olc::vf2d pos, const olc::SpritePatch patch, const olc::vf2d scale) {
    instance->DrawSprite(pos, patch, scale);
}

void DrawDecalPatch(GameEngine *instance, const olc::vf2d pos, const olc::DecalPatch patch, const olc::vf2d scale) {
    instance->DrawDecal(pos, patch, scale);
}

void EnablePixelTransfer(GameEngine *instance, bool enable) {
    instance->EnablePixelTransfer(enable);
}

void resize(GameEngine *instance, const olc::vi2d vPos, const olc::vi2d vSize) {
    instance->SetWindowSize(vPos, vSize);
}

olc::vi2d getWindowSize(GameEngine *instance) {
    return instance->GetWindowSize();
}

// Gets Actual Window position
olc::vi2d getWindowPos(GameEngine *instance) {
    return instance->GetWindowPos();
};

int32_t getFps(GameEngine *instance) {
    return instance->GetFPS();
}

int32_t getKeyPressCacheCount(GameEngine *instance) {
    return static_cast<int32_t>(instance->GetKeyPressCache().size());
}

int32_t getKeyPressCacheAt(GameEngine *instance, int32_t index) {
    const auto &cache = instance->GetKeyPressCache();
    if (index < 0 || static_cast<size_t>(index) >= cache.size()) {
        return 0;
    }
    return cache[index];
}

int32_t convertKeycode(GameEngine *instance, int32_t keycode) {
    return static_cast<int32_t>(instance->ConvertKeycode(keycode));
}

int32_t getKeySymbol(char *buffer, int32_t bufferSize, GameEngine *instance, int32_t key, bool shift, bool ctrl,
                     bool alt) {
    return copyStringToBuffer(instance->GetKeySymbol(olc::Key(key), shift, ctrl, alt), buffer, bufferSize);
}

void setWindowTitle(GameEngine *instance, const char *title) {
    instance->sAppName = std::string(title);
}

/**
 * Creates a layer and returns it's index.
 */
int32_t createLayer(GameEngine *instance) {
    return instance->CreateLayer();
}

int32_t getLayerCount(GameEngine *instance) {
    return static_cast<int32_t>(instance->GetLayers().size());
}

void enableLayer(GameEngine *instance, uint8_t layer, bool b) {
    return instance->EnableLayer(layer, b);
}

bool isLayerEnabled(GameEngine *instance, uint8_t layer) {
    const auto &layers = instance->GetLayers();
    return layer < layers.size() && layers[layer].bShow;
}

olc::vf2d getLayerOffset(GameEngine *instance, uint8_t layer) {
    const auto &layers = instance->GetLayers();
    if (layer >= layers.size()) {
        return {0.0f, 0.0f};
    }
    return layers[layer].vOffset;
}

olc::vf2d getLayerScale(GameEngine *instance, uint8_t layer) {
    const auto &layers = instance->GetLayers();
    if (layer >= layers.size()) {
        return {1.0f, 1.0f};
    }
    return layers[layer].vScale;
}

olc::Pixel getLayerTint(GameEngine *instance, uint8_t layer) {
    const auto &layers = instance->GetLayers();
    if (layer >= layers.size()) {
        return olc::WHITE;
    }
    return layers[layer].tint;
}

typedef void (*customRenderFn)();

void SetLayerCustomRenderFunction(GameEngine *instance, uint8_t layer, customRenderFn f) {
    return instance->SetLayerCustomRenderFunction(layer, f);
}

void SetLayerOffset(GameEngine *instance, uint8_t layer, float x, float y) {
    return instance->SetLayerOffset(layer, x, y);
}

void SetLayerScale(GameEngine *instance, uint8_t layer, float x, float y) {
    return instance->SetLayerScale(layer, x, y);
}

void SetLayerTint(GameEngine *instance, uint8_t layer, olc::Pixel tint) {
    return instance->SetLayerTint(layer, tint);
}

void adv_ManualRenderEnable(GameEngine *instance, bool enable) {
    instance->adv_ManualRenderEnable(enable);
}

void adv_HardwareClip(GameEngine *instance, bool scale, olc::vi2d viewPos, olc::vi2d viewSize, bool clear) {
    instance->adv_HardwareClip(scale, viewPos, viewSize, clear);
}

void adv_FlushLayer(GameEngine *instance, int32_t layer) {
    instance->adv_FlushLayer(static_cast<size_t>(layer));
}

void adv_FlushLayerDecals(GameEngine *instance, int32_t layer) {
    instance->adv_FlushLayerDecals(static_cast<size_t>(layer));
}

void adv_FlushLayerGPUTasks(GameEngine *instance, int32_t layer) {
    instance->adv_FlushLayerGPUTasks(static_cast<size_t>(layer));
}

void HW3D_Projection(GameEngine *instance, const float *matrix) {
    instance->HW3D_Projection(copyMatrix16(matrix));
}

void HW3D_EnableDepthTest(GameEngine *instance, bool enableDepth) {
    instance->HW3D_EnableDepthTest(enableDepth);
}

void HW3D_SetCullMode(GameEngine *instance, int32_t mode) {
    instance->HW3D_SetCullMode(olc::CullMode(mode));
}

void HW3D_DrawObject(GameEngine *instance, const float *matModelView, olc::Decal *decal, int32_t layout,
                     const float *positions, const float *uvs, const olc::Pixel *colors, int32_t elements,
                     olc::Pixel tint) {
    std::vector<std::array<float, 4> > pos;
    std::vector<std::array<float, 2> > uv;
    std::vector<olc::Pixel> col = copyArray(colors, elements);
    pos.reserve(std::max(0, elements));
    uv.reserve(std::max(0, elements));

    for (int32_t i = 0; i < elements; i++) {
        pos.push_back({positions[i * 4], positions[i * 4 + 1], positions[i * 4 + 2], positions[i * 4 + 3]});
        uv.push_back({uvs[i * 2], uvs[i * 2 + 1]});
    }

    instance->HW3D_DrawObject(copyMatrix16(matModelView), decal, olc::DecalStructure(layout), pos, uv, col, tint);
}

void HW3D_DrawLine(GameEngine *instance, const float *matModelView, const float *pos1, const float *pos2,
                   olc::Pixel color) {
    instance->HW3D_DrawLine(copyMatrix16(matModelView), copyArray4(pos1), copyArray4(pos2), color);
}

void HW3D_DrawLineBox(GameEngine *instance, const float *matModelView, const float *pos, const float *size,
                      olc::Pixel color) {
    instance->HW3D_DrawLineBox(copyMatrix16(matModelView), copyArray4(pos), copyArray4(size), color);
}

#ifdef __cplusplus
}
#endif

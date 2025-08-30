

#define OLC_PGE_APPLICATION
#include "olcPixelGameEngine.h"
class MyGame : public olc::PixelGameEngine
{
private:
    olc::Sprite *test = new olc::Sprite(50, 50);
    olc::Decal *d;

public:
    MyGame() { sAppName = "Test"; }
    bool OnUserCreate() override
    {
        
        d = new olc::Decal(test);
        for (int32_t y = 0; y < 50; y++)
        {
            for (int32_t x = 0; x < 50; x++)
            {
                test->SetPixel({x, y}, olc::GREEN);
            }
        }
        d->Update();
        return true;
    }
    bool OnUserUpdate(float fElapsedTime) override
    {
        Clear(olc::BLACK);
        DrawString({60, 20}, "Hello world");
        DrawDecal({10, 10}, d);
        return true;
    }
};

int main()
{
    MyGame game;
    if (game.Construct(256, 240, 2, 2))
        game.Start();
    return 0;
}
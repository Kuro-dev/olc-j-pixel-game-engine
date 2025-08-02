# OLC - J - PixelGameEngine

Simply out of love for game development, java and the OLC channel.

Uses the new FFM API introduced in Java 22. Rough testing shows no significant performance impact,
at least the example program (random pixels all around) gives the same amount of FPS in Java as it does in C.

Feel free to reach out, especially for alternative OS support!

Supported features so far:

- [x] Creating a window
- [x] Set application name
- [x] Basic drawing
- [ ] Some more advanced drawing methods
- [x] Support Windows platforms
- [x] Support Linux platforms (Tested on ubuntu)
- [ ] Support macOS (I do not own one, and don't know how to compile for it. Help is welcome)
- [x] Creating Sprites
- [x] Creating Decals from sprites
- [x] Drawing Sprites including partial sprites
- [x] Drawing Decals including partial Decals and Polygons or other shapes.
- [x] Console and Textinput modes
- [x] Set windows Size during runtime
- [x] Get FPS and other information
- [ ] Layer Support
- [ ] HW3D Rendering support
- [ ] Possibly other features I don't know about

Simple implementation example:

The engine will attempt to download library files (.dll / .so) if they are not present in `./lib`, so you likely won't
have to worry about it. Just make sure to delete your library files or replace them with the latest release when
Upgrading versions. Possibly in the future I will add a version check to do this automatically.

```java
import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.input.HWButton;
import org.kurodev.jpixelgameengine.pos.Vector2D;

public class PixelGameEngineImpl extends PixelGameEngine {
    public PixelGameEngineImpl(int width, int height) {
        super(width, height, 1, 1);
    }

    public static void main(String[] args) {
        PixelGameEngine wrapper = new PixelGameEngineImpl(500, 500);
        wrapper.start();
    }

    @Override
    public boolean onUserCreate() {
        return true;
    }

    @Override
    public boolean onUserUpdate(float delta) {
        if (!isFocussed()) {
            return true;
        }
        int size = 50;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                draw(x, y, new Pixel((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
            }
        }

        HWButton mouse = getKey(MouseKey.LEFT);
        if (mouse.isHeld()) {
            Vector2D<Integer> pos = getWindowMousePos();
            draw(pos, Pixel.WHITE);
        }
        drawString(50, 50, "Hello World", Pixel.WHITE, 4);
        drawCircle(150, 150, 50, Pixel.WHITE);
        fillCircle(250, 250, 50, Pixel.WHITE);
        return true;
    }
}
```

# Getting this to Run

I personally use IntelliJ, but other IDEs should have the same features.

1. In the runtime configuration, if VM options aren't already present click on "Modify Options" `alt + m`

   ![IJ-run-config.png](/readme-res/intelliJ-run-config.png)

2. click "Add VM Options" `alt + v`

   ![IJ-modify-options.png](/readme-res/intelliJ-modify-options.png)
3. Then there should be a new textinput in the Run config for added VM options.
   There you add `--enable-native-access=ALL-UNNAMED` to enable FFM, which is necessary to execute native code from
   Java.

![IJ--set-flag.png](/readme-res/intelliJ-set-flag.png)

# Release steps:

1. update version in pom.xml
2. run `docker build -o . .`
3. commit and push
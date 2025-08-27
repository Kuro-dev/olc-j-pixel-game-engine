import org.junit.jupiter.api.Test;
import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.gfx.decal.Decal;
import org.kurodev.jpixelgameengine.gfx.sprite.Sprite;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.pos.Vector2D;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssetTests extends PixelGameEngine {
    /**
     * Create engine instance for Renderer to be initialised
     */
    public AssetTests() {
        super(50, 50, 1, 1);
    }

    @Test
    public void testSpriteAndDecalCreation() {
        Sprite s = new Sprite(50, 50, "Test_sprite");
        var pos = Vector2D.ofInt(20);
        s.setPixel(pos, Pixel.BLUE);
        assertEquals(Pixel.BLUE, s.getPixel(pos));
        Decal d = new Decal(s);
        d.update();
        d.updateSprite();
    }

    @Test
    public void testSpriteAndDecalCreationFromFile() {
        Sprite s = new Sprite(Path.of("./test/resources/sprites/ship193.png"));
        Decal d = new Decal(s);
        d.update();
        d.updateSprite();
    }

    @Override
    public boolean onUserCreate() {
        return false;
    }

    @Override
    public boolean onUserUpdate(float delta) {
        return false;
    }
}

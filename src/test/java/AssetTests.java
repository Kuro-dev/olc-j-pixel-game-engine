import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.gfx.decal.Decal;
import org.kurodev.jpixelgameengine.gfx.sprite.Sprite;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.pos.Vector2D;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

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

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    void spriteCleanupTest() throws InterruptedException {
        ReferenceQueue<Sprite> queue = new ReferenceQueue<>();
        PhantomReference<Sprite> ref = createSprite(queue);
        while (ref.isEnqueued() == false) {
            System.gc();
            Thread.sleep(100);
        }
    }

    private PhantomReference<Sprite> createSprite(ReferenceQueue<Sprite> queue) {
        Sprite sprite = new Sprite(64, 64, "test");
        PhantomReference<Sprite> ref = new PhantomReference<>(sprite, queue);
        sprite = null; // drop strong reference
        return ref;
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

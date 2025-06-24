package org.kurodev.jpixelgameengine;

public class PixelGameEngineNativeImpl {

    /**
     * Constructs the PixelGameEngine.
     *
     * @param screen_w    The width of the screen in "pixels".
     * @param screen_h    The height of the screen in "pixels".
     * @param pixel_w     The width of each pixel.
     * @param pixel_h     The height of each pixel.
     * @param full_screen Whether the application should run in full screen mode.
     * @param vsync       Whether to enable vertical synchronization.
     * @param cohesion    Whether to disallow arbitrary window scaling.
     * @param realwindow  Whether to use 1:1 "Real Window" mode.
     * @return True if successful, false otherwise.
     */
    static native boolean construct(int screen_w,
                                    int screen_h,
                                    int pixel_w,
                                    int pixel_h,
                                    boolean full_screen,
                                    boolean vsync,
                                    boolean cohesion,
                                    boolean realwindow,
                                    PixelGameEngineWrapper wrapper);

    /**
     * Starts the PixelGameEngine application loop.
     *
     * @return True if successful, false otherwise.
     */
    public static native boolean start();

//    /**
//     * Returns true if window is currently in focus.
//     *
//     * @return True if window is currently in focus.
//     */
//    public native boolean isFocused();
//
//    /**
//     * Get the state of a specific keyboard button.
//     *
//     * @param k The key code.
//     * @return The state of the hardware button.
//     */
//    public native HWButton getKey(int k); // olc::Key maps to int
//
//    /**
//     * Get the state of a specific mouse button.
//     *
//     * @param b The mouse button code (e.g., olc::Mouse::LEFT).
//     * @return The state of the hardware button.
//     */
//    public native HWButton getMouse(int b);
//
//    /**
//     * Get Mouse X coordinate in "pixel" space.
//     *
//     * @return Mouse X coordinate.
//     */
//    public native int getMouseX();
//
//    /**
//     * Get Mouse Y coordinate in "pixel" space.
//     *
//     * @return Mouse Y coordinate.
//     */
//    public native int getMouseY();
//
//    /**
//     * Get Mouse Wheel Delta.
//     *
//     * @return Mouse wheel delta.
//     */
//    public native int getMouseWheel();
//
//    /**
//     * Get the mouse in window space.
//     *
//     * @return The mouse position in window space as a Vector2D.
//     */
//    public native Vector2D getWindowMouse();
//
//    /**
//     * Gets the mouse as a vector.
//     *
//     * @return The mouse position as a Vector2D.
//     */
//    public native Vector2D getMousePos();
//
//    /**
//     * Sets the window size and position.
//     *
//     * @param vPos  The new window position.
//     * @param vSize The new window size.
//     * @return True if successful, false otherwise.
//     */
//    public native boolean setWindowSize(Vector2D vPos, Vector2D vSize);
//
//    /**
//     * Shows or hides the window frame.
//     *
//     * @param bShowFrame True to show the frame, false to hide.
//     * @return True if successful, false otherwise.
//     */
//    public native boolean showWindowFrame(boolean bShowFrame);
//
//    /**
//     * Returns the width of the screen in "pixels".
//     *
//     * @return Screen width.
//     */
//    public native int screenWidth();
//
//    /**
//     * Returns the height of the screen in "pixels".
//     *
//     * @return Screen height.
//     */
//    public native int screenHeight();
//
//    /**
//     * Returns the width of the currently selected drawing target in "pixels".
//     *
//     * @return Drawing target width.
//     */
//    public native int getDrawTargetWidth();
//
//    /**
//     * Returns the height of the currently selected drawing target in "pixels".
//     *
//     * @return Drawing target height.
//     */
//    public native int getDrawTargetHeight();
//
//    /**
//     * Returns the currently active draw target.
//     *
//     * @return The current drawing target sprite.
//     */
//    public native Sprite getDrawTarget();
//
//    /**
//     * Resize the primary screen sprite.
//     *
//     * @param w New width.
//     * @param h New height.
//     */
//    public native void setScreenSize(int w, int h);
//
//    /**
//     * Specify which Sprite should be the target of drawing functions, use null
//     * to specify the primary screen.
//     *
//     * @param target The target sprite, or null for the primary screen.
//     */
//    public native void setDrawTarget(Sprite target);
//
//    /**
//     * Gets the current Frames Per Second.
//     *
//     * @return Current FPS.
//     */
//    public native int getFPS();
//
//    /**
//     * Gets last update of elapsed time.
//     *
//     * @return Elapsed time in seconds.
//     */
//    public native float getElapsedTime();
//
//    /**
//     * Gets Actual Window size.
//     *
//     * @return Actual window size as a Vector2D.
//     */
//    public native Vector2D getWindowSize();
//
//    /**
//     * Gets Actual Window position.
//     *
//     * @return Actual window position as a Vector2D.
//     */
//    public native Vector2D getWindowPos();
//
//    /**
//     * Gets pixel scale.
//     *
//     * @return Pixel scale as a Vector2D.
//     */
//    public native Vector2D getPixelSize();
//
//    /**
//     * Gets actual pixel scale.
//     *
//     * @return Actual pixel scale as a Vector2D.
//     */
//    public native Vector2D getScreenPixelSize();
//
//    /**
//     * Gets "screen" size.
//     *
//     * @return Screen size as a Vector2D.
//     */
//    public native Vector2D getScreenSize();
//
//    /**
//     * Gets any files dropped this frame.
//     *
//     * @return An array of strings representing dropped file paths.
//     */
//    public native String[] getDroppedFiles();
//
//    /**
//     * Gets location of dropped files.
//     *
//     * @return The point where files were dropped as a Vector2D.
//     */
//    public native Vector2D getDroppedFilesPoint();
//
//    // CONFIGURATION ROUTINES
//
//    /**
//     * Layer targeting functions.
//     *
//     * @param layer  The layer ID.
//     * @param bDirty Whether the layer is dirty (needs update).
//     */
//    public native void setDrawTarget(byte layer, boolean bDirty); // uint8_t to byte
//
//    /**
//     * Enables or disables a layer.
//     *
//     * @param layer The layer ID.
//     * @param b     True to enable, false to disable.
//     */
//    public native void enableLayer(byte layer, boolean b);
//
//    /**
//     * Sets the offset for a specific layer.
//     *
//     * @param layer  The layer ID.
//     * @param offset The offset as a Vector2D.
//     */
//    public native void setLayerOffset(byte layer, Vector2D offset);
//
//    /**
//     * Sets the offset for a specific layer.
//     *
//     * @param layer The layer ID.
//     * @param x     X-offset.
//     * @param y     Y-offset.
//     */
//    public native void setLayerOffset(byte layer, float x, float y);
//
//    /**
//     * Sets the scale for a specific layer.
//     *
//     * @param layer The layer ID.
//     * @param scale The scale as a Vector2D.
//     */
//    public native void setLayerScale(byte layer, Vector2D scale);
//
//    /**
//     * Sets the scale for a specific layer.
//     *
//     * @param layer The layer ID.
//     * @param x     X-scale.
//     * @param y     Y-scale.
//     */
//    public native void setLayerScale(byte layer, float x, float y);
//
//    /**
//     * Sets the tint for a specific layer.
//     *
//     * @param layer The layer ID.
//     * @param tint  The tint color.
//     */
//    public native void setLayerTint(byte layer, Pixel tint);
//
//    /**
//     * Creates a new drawing layer.
//     *
//     * @return The ID of the newly created layer.
//     */
//    public native int createLayer();
//
//    /**
//     * Change the pixel mode for different optimisations.
//     * olc::Pixel::NORMAL = No transparency
//     * olc::Pixel::MASK   = Transparent if alpha is < 255
//     * olc::Pixel::ALPHA  = Full transparency
//     *
//     * @param m The pixel mode (e.g., Pixel.Mode.NORMAL).
//     */
//    public native void setPixelMode(int m); // olc::Pixel::Mode to int
//
//    /**
//     * Gets the current pixel mode.
//     *
//     * @return The current pixel mode.
//     */
//    public native int getPixelMode();
//
//    /**
//     * Change the blend factor from between 0.0f to 1.0f.
//     *
//     * @param fBlend The blend factor.
//     */
//    public native void setPixelBlend(float fBlend);
//
//    // DRAWING ROUTINES
//
//    /**
//     * Draws a single Pixel.
//     *
//     * @param x X-coordinate.
//     * @param y Y-coordinate.
//     * @param p The pixel color. Defaults to olc::WHITE.
//     * @return True if successful, false otherwise.
//     */
//    public native boolean draw(int x, int y, Pixel p);
//
//    /**
//     * Draws a single Pixel.
//     *
//     * @param pos The position of the pixel as a Vector2D.
//     * @param p   The pixel color. Defaults to olc::WHITE.
//     * @return True if successful, false otherwise.
//     */
//    public native boolean draw(Vector2D pos, Pixel p);
//
//    /**
//     * Draws a line from (x1,y1) to (x2,y2).
//     *
//     * @param x1      X-coordinate of the first point.
//     * @param y1      Y-coordinate of the first point.
//     * @param x2      X-coordinate of the second point.
//     * @param y2      Y-coordinate of the second point.
//     * @param p       The line color. Defaults to olc::WHITE.
//     * @param pattern The line pattern. Defaults to 0xFFFFFFFF.
//     */
//    public native void drawLine(int x1, int y1, int x2, int y2, Pixel p, int pattern);
//
//    /**
//     * Draws a line.
//     *
//     * @param pos1    The first point as a Vector2D.
//     * @param pos2    The second point as a Vector2D.
//     * @param p       The line color. Defaults to olc::WHITE.
//     * @param pattern The line pattern. Defaults to 0xFFFFFFFF.
//     */
//    public native void drawLine(Vector2D pos1, Vector2D pos2, Pixel p, int pattern);
//
//    /**
//     * Draws a circle located at (x,y) with radius.
//     *
//     * @param x      X-coordinate of the center.
//     * @param y      Y-coordinate of the center.
//     * @param radius The radius of the circle.
//     * @param p      The circle color. Defaults to olc::WHITE.
//     * @param mask   The mask for drawing the circle. Defaults to 0xFF.
//     */
//    public native void drawCircle(int x, int y, int radius, Pixel p, byte mask);
//
//    /**
//     * Draws a circle.
//     *
//     * @param pos    The center of the circle as a Vector2D.
//     * @param radius The radius of the circle.
//     * @param p      The circle color. Defaults to olc::WHITE.
//     * @param mask   The mask for drawing the circle. Defaults to 0xFF.
//     */
//    public native void drawCircle(Vector2D pos, int radius, Pixel p, byte mask);
//
//    /**
//     * Fills a circle located at (x,y) with radius.
//     *
//     * @param x      X-coordinate of the center.
//     * @param y      Y-coordinate of the center.
//     * @param radius The radius of the circle.
//     * @param p      The fill color. Defaults to olc::WHITE.
//     */
//    public native void fillCircle(int x, int y, int radius, Pixel p);
//
//    /**
//     * Fills a circle.
//     *
//     * @param pos    The center of the circle as a Vector2D.
//     * @param radius The radius of the circle.
//     * @param p      The fill color. Defaults to olc::WHITE.
//     */
//    public native void fillCircle(Vector2D pos, int radius, Pixel p);
//
//    /**
//     * Draws a rectangle at (x,y) to (x+w,y+h).
//     *
//     * @param x X-coordinate of the top-left corner.
//     * @param y Y-coordinate of the top-left corner.
//     * @param w The width of the rectangle.
//     * @param h The height of the rectangle.
//     * @param p The rectangle color. Defaults to olc::WHITE.
//     */
//    public native void drawRect(int x, int y, int w, int h, Pixel p);
//
//    /**
//     * Draws a rectangle.
//     *
//     * @param pos  The top-left corner of the rectangle as a Vector2D.
//     * @param size The size of the rectangle as a Vector2D.
//     * @param p    The rectangle color. Defaults to olc::WHITE.
//     */
//    public native void drawRect(Vector2D pos, Vector2D size, Pixel p);
//
//    /**
//     * Fills a rectangle at (x,y) to (x+w,y+h).
//     *
//     * @param x X-coordinate of the top-left corner.
//     * @param y Y-coordinate of the top-left corner.
//     * @param w The width of the rectangle.
//     * @param h The height of the rectangle.
//     * @param p The fill color. Defaults to olc::WHITE.
//     */
//    public native void fillRect(int x, int y, int w, int h, Pixel p);
//
//    /**
//     * Fills a rectangle.
//     *
//     * @param pos  The top-left corner of the rectangle as a Vector2D.
//     * @param size The size of the rectangle as a Vector2D.
//     * @param p    The fill color. Defaults to olc::WHITE.
//     */
//    public native void fillRect(Vector2D pos, Vector2D size, Pixel p);
//
//    /**
//     * Draws a triangle between points (x1,y1), (x2,y2) and (x3,y3).
//     *
//     * @param x1 X-coordinate of the first point.
//     * @param y1 Y-coordinate of the first point.
//     * @param x2 X-coordinate of the second point.
//     * @param y2 Y-coordinate of the second point.
//     * @param x3 X-coordinate of the third point.
//     * @param y3 Y-coordinate of the third point.
//     * @param p  The triangle color. Defaults to olc::WHITE.
//     */
//    public native void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Pixel p);
//
//    /**
//     * Draws a triangle.
//     *
//     * @param pos1 The first point as a Vector2D.
//     * @param pos2 The second point as a Vector2D.
//     * @param pos3 The third point as a Vector2D.
//     * @param p    The triangle color. Defaults to olc::WHITE.
//     */
//    public native void drawTriangle(Vector2D pos1, Vector2D pos2, Vector2D pos3, Pixel p);
//
//    /**
//     * Flat fills a triangle between points (x1,y1), (x2,y2) and (x3,y3).
//     *
//     * @param x1 X-coordinate of the first point.
//     * @param y1 Y-coordinate of the first point.
//     * @param x2 X-coordinate of the second point.
//     * @param y2 Y-coordinate of the second point.
//     * @param x3 X-coordinate of the third point.
//     * @param y3 Y-coordinate of the third point.
//     * @param p  The fill color. Defaults to olc::WHITE.
//     */
//    public native void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Pixel p);
//
//    /**
//     * Flat fills a triangle.
//     *
//     * @param pos1 The first point as a Vector2D.
//     * @param pos2 The second point as a Vector2D.
//     * @param pos3 The third point as a Vector2D.
//     * @param p    The fill color. Defaults to olc::WHITE.
//     */
//    public native void fillTriangle(Vector2D pos1, Vector2D pos2, Vector2D pos3, Pixel p);
//
//    /**
//     * Fill a textured and coloured triangle.
//     *
//     * @param vPoints An array of Vector2D representing the vertices.
//     * @param vTex    An array of Vector2D representing the texture coordinates.
//     * @param vColour An array of Pixel representing the vertex colors.
//     * @param sprTex  The texture sprite.
//     */
//    public native void fillTexturedTriangle(Vector2D[] vPoints, Vector2D[] vTex, Pixel[] vColour, Sprite sprTex);
//
//    /**
//     * Fill a textured and coloured polygon.
//     *
//     * @param vPoints   An array of Vector2D representing the vertices.
//     * @param vTex      An array of Vector2D representing the texture coordinates.
//     * @param vColour   An array of Pixel representing the vertex colors.
//     * @param sprTex    The texture sprite.
//     * @param structure The decal structure. Defaults to olc::DecalStructure::LIST.
//     */
//    public native void fillTexturedPolygon(Vector2D[] vPoints, Vector2D[] vTex, Pixel[] vColour, Sprite sprTex, int structure); // olc::DecalStructure to int
//
//    /**
//     * Draws an entire sprite at location (x,y).
//     *
//     * @param x      X-coordinate.
//     * @param y      Y-coordinate.
//     * @param sprite The sprite to draw.
//     * @param scale  The scaling factor. Defaults to 1.
//     * @param flip   The flip mode (e.g., olc::Sprite::NONE). Defaults to olc::Sprite::NONE.
//     */
//    public native void drawSprite(int x, int y, Sprite sprite, int scale, byte flip);
//
//    /**
//     * Draws an entire sprite.
//     *
//     * @param pos    The position as a Vector2D.
//     * @param sprite The sprite to draw.
//     * @param scale  The scaling factor. Defaults to 1.
//     * @param flip   The flip mode (e.g., olc::Sprite::NONE). Defaults to olc::Sprite::NONE.
//     */
//    public native void drawSprite(Vector2D pos, Sprite sprite, int scale, byte flip);
//
//    /**
//     * Draws an area of a sprite at location (x,y), where the
//     * selected area is (ox,oy) to (ox+w,oy+h).
//     *
//     * @param x      X-coordinate.
//     * @param y      Y-coordinate.
//     * @param sprite The sprite to draw from.
//     * @param ox     X-coordinate of the source area.
//     * @param oy     Y-coordinate of the source area.
//     * @param w      Width of the source area.
//     * @param h      Height of the source area.
//     * @param scale  The scaling factor. Defaults to 1.
//     * @param flip   The flip mode (e.g., olc::Sprite::NONE). Defaults to olc::Sprite::NONE.
//     */
//    public native void drawPartialSprite(int x, int y, Sprite sprite, int ox, int oy, int w, int h, int scale, byte flip);
//
//    /**
//     * Draws an area of a sprite.
//     *
//     * @param pos       The position as a Vector2D.
//     * @param sprite    The sprite to draw from.
//     * @param sourcepos The top-left corner of the source area as a Vector2D.
//     * @param size      The size of the source area as a Vector2D.
//     * @param scale     The scaling factor. Defaults to 1.
//     * @param flip      The flip mode (e.g., olc::Sprite::NONE). Defaults to olc::Sprite::NONE.
//     */
//    public native void drawPartialSprite(Vector2D pos, Sprite sprite, Vector2D sourcepos, Vector2D size, int scale, byte flip);
//
//    /**
//     * Draws a single line of text - traditional monospaced.
//     *
//     * @param x     X-coordinate.
//     * @param y     Y-coordinate.
//     * @param sText The text string.
//     * @param col   The text color. Defaults to olc::WHITE.
//     * @param scale The scaling factor. Defaults to 1.
//     */
//    public native void drawString(int x, int y, String sText, Pixel col, int scale);
//
//    /**
//     * Draws a single line of text - traditional monospaced.
//     *
//     * @param pos   The position as a Vector2D.
//     * @param sText The text string.
//     * @param col   The text color. Defaults to olc::WHITE.
//     * @param scale The scaling factor. Defaults to 1.
//     */
//    public native void drawString(Vector2D pos, String sText, Pixel col, int scale);
//
//    /**
//     * Gets the size of a monospaced text string.
//     *
//     * @param s The text string.
//     * @return The size of the text as a Vector2D.
//     */
//    public native Vector2D getTextSize(String s);
//
//    /**
//     * Draws a single line of text - non-monospaced.
//     *
//     * @param x     X-coordinate.
//     * @param y     Y-coordinate.
//     * @param sText The text string.
//     * @param col   The text color. Defaults to olc::WHITE.
//     * @param scale The scaling factor. Defaults to 1.
//     */
//    public native void drawStringProp(int x, int y, String sText, Pixel col, int scale);
//
//    /**
//     * Draws a single line of text - non-monospaced.
//     *
//     * @param pos   The position as a Vector2D.
//     * @param sText The text string.
//     * @param col   The text color. Defaults to olc::WHITE.
//     * @param scale The scaling factor. Defaults to 1.
//     */
//    public native void drawStringProp(Vector2D pos, String sText, Pixel col, int scale);
//
//    /**
//     * Gets the size of a non-monospaced text string.
//     *
//     * @param s The text string.
//     * @return The size of the text as a Vector2D.
//     */
//    public native Vector2D getTextSizeProp(String s);
//
//    // Decal Quad functions
//
//    /**
//     * Sets the decal mode.
//     *
//     * @param mode The decal mode (e.g., olc::DecalMode::NORMAL).
//     */
//    public native void setDecalMode(int mode); // olc::DecalMode to int
//
//    /**
//     * Sets the decal structure.
//     *
//     * @param structure The decal structure (e.g., olc::DecalStructure::FAN).
//     */
//    public native void setDecalStructure(int structure); // olc::DecalStructure to int
//
//    /**
//     * Draws a whole decal, with optional scale and tinting.
//     *
//     * @param pos   The position as a Vector2D.
//     * @param decal The decal to draw.
//     * @param scale The scaling factor as a Vector2D. Defaults to {1.0f, 1.0f}.
//     * @param tint  The tint color. Defaults to olc::WHITE.
//     */
//    public native void drawDecal(Vector2D pos, Decal decal, Vector2D scale, Pixel tint);
//
//    /**
//     * Draws a region of a decal, with optional scale and tinting.
//     *
//     * @param pos         The position as a Vector2D.
//     * @param decal       The decal to draw from.
//     * @param source_pos  The top-left corner of the source area as a Vector2D.
//     * @param source_size The size of the source area as a Vector2D.
//     * @param scale       The scaling factor as a Vector2D. Defaults to {1.0f, 1.0f}.
//     * @param tint        The tint color. Defaults to olc::WHITE.
//     */
//    public native void drawPartialDecal(Vector2D pos, Decal decal, Vector2D source_pos, Vector2D source_size, Vector2D scale, Pixel tint);
//
//    /**
//     * Draws a region of a decal.
//     *
//     * @param pos         The position as a Vector2D.
//     * @param size        The size of the decal as a Vector2D.
//     * @param decal       The decal to draw from.
//     * @param source_pos  The top-left corner of the source area as a Vector2D.
//     * @param source_size The size of the source area as a Vector2D.
//     * @param tint        The tint color. Defaults to olc::WHITE.
//     */
//    public native void drawPartialDecal(Vector2D pos, Vector2D size, Decal decal, Vector2D source_pos, Vector2D source_size, Pixel tint);
//
//    /**
//     * Draws fully user controlled 4 vertices, pos(pixels), uv(pixels), colours.
//     *
//     * @param decal    The decal.
//     * @param pos      An array of Vector2D representing the positions.
//     * @param uv       An array of Vector2D representing the UV coordinates.
//     * @param col      An array of Pixel representing the colors.
//     * @param elements The number of elements (vertices) to draw. Defaults to 4.
//     */
//    public native void drawExplicitDecal(Decal decal, Vector2D[] pos, Vector2D[] uv, Pixel[] col, int elements);
//
//    /**
//     * Draws a decal with 4 arbitrary points, warping the texture to look "correct".
//     *
//     * @param decal The decal.
//     * @param pos   An array of 4 Vector2D representing the positions.
//     * @param tint  The tint color. Defaults to olc::WHITE.
//     */
//    public native void drawWarpedDecal(Decal decal, Vector2D[] pos, Pixel tint);
//
//    /**
//     * As above, but you can specify a region of a decal source sprite.
//     *
//     * @param decal       The decal.
//     * @param pos         An array of 4 Vector2D representing the positions.
//     * @param source_pos  The top-left corner of the source area as a Vector2D.
//     * @param source_size The size of the source area as a Vector2D.
//     * @param tint        The tint color. Defaults to olc::WHITE.
//     */
//    public native void drawPartialWarpedDecal(Decal decal, Vector2D[] pos, Vector2D source_pos, Vector2D source_size, Pixel tint);
//
//    /**
//     * Draws a decal rotated to specified angle, with point of rotation offset.
//     *
//     * @param pos    The position as a Vector2D.
//     * @param decal  The decal.
//     * @param fAngle The rotation angle in radians.
//     * @param center The center of rotation as a Vector2D. Defaults to {0.0f, 0.0f}.
//     * @param scale  The scaling factor as a Vector2D. Defaults to {1.0f, 1.0f}.
//     * @param tint   The tint color. Defaults to olc::WHITE.
//     */
//    public native void drawRotatedDecal(Vector2D pos, Decal decal, float fAngle, Vector2D center, Vector2D scale, Pixel tint);
//
//    /**
//     * Draws a partial decal rotated to specified angle, with point of rotation offset.
//     *
//     * @param pos         The position as a Vector2D.
//     * @param decal       The decal.
//     * @param fAngle      The rotation angle in radians.
//     * @param center      The center of rotation as a Vector2D.
//     * @param source_pos  The top-left corner of the source area as a Vector2D.
//     * @param source_size The size of the source area as a Vector2D.
//     * @param scale       The scaling factor as a Vector2D. Defaults to {1.0f, 1.0f}.
//     * @param tint        The tint color. Defaults to olc::WHITE.
//     */
//    public native void drawPartialRotatedDecal(Vector2D pos, Decal decal, float fAngle, Vector2D center, Vector2D source_pos, Vector2D source_size, Vector2D scale, Pixel tint);
//
//    /**
//     * Draws a multiline string as a decal, with tinting and scaling.
//     *
//     * @param pos   The position as a Vector2D.
//     * @param sText The text string.
//     * @param col   The text color. Defaults to olc::WHITE.
//     * @param scale The scaling factor as a Vector2D. Defaults to {1.0f, 1.0f}.
//     */
//    public native void drawStringDecal(Vector2D pos, String sText, Pixel col, Vector2D scale);
//
//    /**
//     * Draws a multiline string (proportional font) as a decal, with tinting and scaling.
//     *
//     * @param pos   The position as a Vector2D.
//     * @param sText The text string.
//     * @param col   The text color. Defaults to olc::WHITE.
//     * @param scale The scaling factor as a Vector2D. Defaults to {1.0f, 1.0f}.
//     */
//    public native void drawStringPropDecal(Vector2D pos, String sText, Pixel col, Vector2D scale);
//
//    /**
//     * Draws a single shaded filled rectangle as a decal.
//     *
//     * @param pos  The position as a Vector2D.
//     * @param size The size as a Vector2D.
//     * @param col  The color. Defaults to olc::WHITE.
//     */
//    public native void drawRectDecal(Vector2D pos, Vector2D size, Pixel col);
//
//    /**
//     * Fills a single shaded rectangle as a decal.
//     *
//     * @param pos  The position as a Vector2D.
//     * @param size The size as a Vector2D.
//     * @param col  The color. Defaults to olc::WHITE.
//     */
//    public native void fillRectDecal(Vector2D pos, Vector2D size, Pixel col);
//
//    /**
//     * Draws a corner shaded rectangle as a decal.
//     *
//     * @param pos   The position as a Vector2D.
//     * @param size  The size as a Vector2D.
//     * @param colTL The top-left corner color.
//     * @param colBL The bottom-left corner color.
//     * @param colBR The bottom-right corner color.
//     * @param colTR The top-right corner color.
//     */
//    public native void gradientFillRectDecal(Vector2D pos, Vector2D size, Pixel colTL, Pixel colBL, Pixel colBR, Pixel colTR);
}

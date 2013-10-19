package com.innerlogic.quadtreedemo;

/**
 * Created with IntelliJ IDEA.
 * User: Taylor
 * Date: 10/8/13
 * Time: 10:21 PM
 * To change this template use File | Settings | File Templates.
 */

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.innerlogic.quadtreedemo.screens.LoadingScreen;

public class QuadtreeDemo extends Game
{
    // Easier ways to reference file paths
    // TODO May be better in Enum or other data structure
    public final static String TEXTURE_BLOCK_64 = "textures/block_64x64.png";
    public final static String TEXTURE_BLOCK_32 = "textures/block_32x32.png";
    public final static String TEXTURE_BLOCK_16 = "textures/block_16x16.png";
    public final static String TEXTURE_BLOCK_8 = "textures/block_8x8.png";
    public final static String TEXTURE_BLOCK_4 = "textures/block_4x4.png";
    public final static String TEXTURE_BLOCK_2 = "textures/block_2x2.png";
    public final static String TEXTURE_BLOCK_1 = "textures/block_1x1.png";

    public final static String TEXTURE_BLOCK = TEXTURE_BLOCK_8;

    // Our AssetManager! Very important!
    public AssetManager assetManager;

    // Our sprite batch, used to optimize 2D rendering
    public SpriteBatch batch;

    // Our shape renderer, used to render shapes and perform debug drawing
    public ShapeRenderer shapeRenderer;

    // The Arial Bitmap font to use for rendering
    public BitmapFont font;

    // Camera used to render the main menu
    public OrthographicCamera camera;

    public void create()
    {
        // Set up the asset manager
        assetManager = new AssetManager();

        // Set up other important object
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();

        // Create and set up the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // The camera's dimensions mirror viewport's

        // Set the initial screen of our game to an instance the LoadingScreen
        this.setScreen(new LoadingScreen(this));
    }

    @Override
    public void dispose()
    {
        super.dispose();

        // Dispose the asset manager and all its managed assets
        assetManager.dispose();

        // Dispose of assets and objects outside of the AssetManager's jurisdiction
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
}

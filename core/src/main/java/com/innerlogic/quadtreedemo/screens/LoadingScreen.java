package com.innerlogic.quadtreedemo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.innerlogic.quadtreedemo.QuadtreeDemo;

/**
 * Created with IntelliJ IDEA.
 * User: Taylor
 * Date: 10/8/13
 * Time: 10:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoadingScreen implements Screen
{
    // Text for loading
    private final static String LOADING_TEXT = "Loading... %d%% Complete";

    // Reference to main game object
    private final QuadtreeDemo _game;

    public LoadingScreen(final QuadtreeDemo game)
    {
        _game = game;

        // Tell the asset manager what needs to be loaded
        _game.assetManager.load(QuadtreeDemo.TEXTURE_BLOCK, Texture.class);
    }

    @Override
    public void render(float delta)
    {
        // Ensure the camera is updated
        _game.camera.update();

        // Continuously call update until all assets are loaded, then switch to the Main Menu
        if(_game.assetManager.update())
        {
            _game.setScreen(new MainMenuScreen(_game));

            // Be sure to dispose of anything needed with the screen.
            dispose();
        }

        // Clear the backbuffer (Black)
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // Set the sprite batch to use the camera's combined projection/view matrix
        _game.batch.setProjectionMatrix(_game.camera.combined);

        _game.batch.begin();

        // Render loading progress
        _game.font.draw(_game.batch, String.format(LOADING_TEXT, Float.valueOf(_game.assetManager.getProgress()).intValue()), 10, 25);

        _game.batch.end();
    }

    @Override
    public void resize(int width, int height)
    {
        // Do nothing (For now)
    }

    @Override
    public void show()
    {
        // Do nothing (For now)
    }

    @Override
    public void hide()
    {
        // Do nothing (For now)
    }

    @Override
    public void pause()
    {
        // On Android, this is called when home button is pressed or context is otherwise switched (Incoming  call, etc)
        // On Desktop, this is called just before dispose() when exiting the application.
        //
        // It is typically a good place to save the game state.

        // Do nothing (For now)
    }

    @Override
    public void resume()
    {
        // Only called on Android, when the application resumes from the paused state.

        // Do nothing (For now)
    }

    @Override
    public void dispose()
    {
        // Do nothing (For now)
    }
}


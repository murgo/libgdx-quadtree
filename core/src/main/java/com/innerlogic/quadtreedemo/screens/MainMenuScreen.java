package com.innerlogic.quadtreedemo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.innerlogic.quadtreedemo.QuadtreeDemo;

/**
 * Created with IntelliJ IDEA.
 * User: Taylor
 * Date: 10/8/13
 * Time: 10:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainMenuScreen implements Screen
{
    // Text for the main menu
    private final static String WELCOME_TEXT = "Welcome to my Quadtree demo!";
    private final static String CLICK_TO_BEGIN_TEXT = "Click anywhere to begin!";

    // Reference to main game object
    private final QuadtreeDemo _game;

    public MainMenuScreen(final QuadtreeDemo game)
    {
        _game = game;
    }

    @Override
    public void render(float delta)
    {
        // Ensure the camera is updated
        _game.camera.update();

        // When touched or clicked, switch to the main game screen
        if(Gdx.input.isTouched())
        {
            _game.setScreen(new GameScreen(_game));

            // Be sure to dispose of anything needed with the screen.
            dispose();
        }

        // Clear the backbuffer (Dark blue-green)
        Gdx.gl.glClearColor(0, 0.15f, 0.2f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // Set the sprite batch to use the camera's combined projection/view matrix
        _game.batch.setProjectionMatrix(_game.camera.combined);

        _game.batch.begin();

        _game.font.draw(_game.batch, WELCOME_TEXT, 10, 50);
        _game.font.draw(_game.batch, CLICK_TO_BEGIN_TEXT, 10, 25);

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
        // On Android, this is called when home button is pressed or context is otherwised switch (Incoming  call, etc)
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

package com.innerlogic.quadtreedemo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.innerlogic.quadtreedemo.QuadTreeDemo;

/**
 * Created with IntelliJ IDEA.
 * User: Taylor
 * Date: 10/8/13
 * Time: 10:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainMenuScreen extends ScreenAdapter
{
    // Text for the main menu
    private final static String WELCOME_TEXT = "Welcome to the quadtree demo!";
    private final static String CLICK_TO_BEGIN_TEXT = "Click anywhere to begin!";

    // Reference to main game object
    private final QuadTreeDemo _game;

    public MainMenuScreen(final QuadTreeDemo game)
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
        Gdx.gl20.glClearColor(0, 0.15f, 0.2f, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Set the sprite batch to use the camera's combined projection/view matrix
        _game.batch.setProjectionMatrix(_game.camera.combined);

        _game.batch.begin();

        _game.font.draw(_game.batch, WELCOME_TEXT, 10, 50);
        _game.font.draw(_game.batch, CLICK_TO_BEGIN_TEXT, 10, 25);

        _game.batch.end();
    }
}

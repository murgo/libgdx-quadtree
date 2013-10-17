package com.innerlogic.quadtreedemo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.innerlogic.quadtreedemo.QuadtreeDemo;
import com.innerlogic.quadtreedemo.collision.QuadTreeNode;
import com.innerlogic.quadtreedemo.entities.SpriteEntity;
import com.innerlogic.quadtreedemo.logging.LoggingAction;
import com.innerlogic.quadtreedemo.logging.PeriodicLogger;

/**
 * Created with IntelliJ IDEA.
 * User: Taylor
 * Date: 10/8/13
 * Time: 10:23 PM
 */
public class GameScreen implements Screen
{
    // Reference to main game object
    private final QuadtreeDemo _game;

    // --------------------
    // Game Entities
    // --------------------
    private Array<SpriteEntity> blockArray;
    private final int NUM_BLOCKS = 75;

    // --------------------
    // Sounds / Music
    // --------------------
    private Music bgMusic; // TODO Consider looking at AssetManager (https://github.com/libgdx/libgdx/wiki/Managing-your-assets)

    // --------------------
    // Scratch variables
    // --------------------
    private Vector3 clickPos = new Vector3();
    private int screenWidth; // TODO Consider moving elsewhere
    private int screenHeight; // TODO Consider moving elsewhere

    // TODO Consider how to abstract these for easier use by Periodic Logger
    private int numCollisionChecksThisFrame;
    private int minNumCollisionChecks = Integer.MAX_VALUE;
    private int maxNumCollisionChecks;
    private int numCollisionPerSecond;
    private int updatesPerSecond;

    // ------------------------
    // Periodic logger
    // ------------------------
    private PeriodicLogger periodicLogger;

    // -----------------------------------
    // Quadtree
    // -----------------------------------
    private QuadTreeNode quadTree;
    private Array<SpriteEntity> entitiesToCheck;

    // ------------------------------------
    // Shader related
    // Adapted from https://github.com/mattdesl/lwjgl-basics/wiki/2D-Pixel-Perfect-Shadows
    // ------------------------------------

    // The number of rays emitted for a "light", as well as their length. This is used in a 360 degree fashion, so the higher, the higher the precision
    // but higher fill rate as well. Needs to be power of 2
    private int lightSize = 512;
    private float upScale = 1f;

    private FrameBuffer occludersFBO;
    private FrameBuffer shadowMapFBO;

    private TextureRegion shadowMap1D; //1 dimensional shadow map
    private TextureRegion occluders;   //occluder map

    ShaderProgram shadowMapShader, shadowRenderShader;

    boolean additive = true;
    boolean softShadows = true;

    /**
     * Compiles a new instance of the default shader for this batch and returns it. If compilation
     * was unsuccessful, GdxRuntimeException will be thrown.
     *
     * @return the default shader
     */
    public static ShaderProgram createShader(String vert, String frag)
    {
        ShaderProgram prog = new ShaderProgram(vert, frag);
        if(!prog.isCompiled())
        {
            throw new GdxRuntimeException("could not compile shader: " + prog.getLog());
        }
        if(prog.getLog().length() != 0)
        {
            Gdx.app.log("GpuShadows", prog.getLog());
        }
        return prog;
    }

    public GameScreen(final QuadtreeDemo game)
    {
        _game = game;

        bgMusic = _game.assetManager.get(QuadtreeDemo.MUSIC_RAIN, Music.class);
        bgMusic.setLooping(true);

        // -------------------------------------
        // Set up scratch variables
        // -------------------------------------
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        // -------------------------------------
        // Set up the game entities
        // -------------------------------------
        blockArray = new Array<SpriteEntity>(true, NUM_BLOCKS);
        for(int i = 0; i < NUM_BLOCKS; i++)
        {
            blockArray.add(generateValidBlock());
        }

        // Set up the periodic logger and its logging actions
        periodicLogger = new PeriodicLogger();
        periodicLogger.addLoggingAction(new LoggingAction()
        {
            @Override
            public void doAction()
            {
                Gdx.app.log("FPSLogger", "fps: " + Gdx.graphics.getFramesPerSecond());
            }
        });
        periodicLogger.addLoggingAction(new LoggingAction()
        {
            @Override
            public void doAction()
            {
                Gdx.app.log("CollisionLogger", "Min Collision Checks: " + minNumCollisionChecks);
            }
        });
        periodicLogger.addLoggingAction(new LoggingAction()
        {
            @Override
            public void doAction()
            {
                Gdx.app.log("CollisionLogger", "Max Collision Checks: " + maxNumCollisionChecks);
            }
        });
        periodicLogger.addLoggingAction(new LoggingAction()
        {
            @Override
            public void doAction()
            {
                Gdx.app.log("CollisionLogger", "Average Collision Checks Per Frame over past second: " + (numCollisionPerSecond / updatesPerSecond));
                numCollisionPerSecond = 0;
                updatesPerSecond = 0;
            }
        });

        // Set up the quad tree
        quadTree = new QuadTreeNode(0, new Rectangle(0, 0, screenWidth, screenWidth));
        entitiesToCheck = new Array<SpriteEntity>(true, QuadTreeNode.MAX_ENTITIES);

        // Set up shader related things
        occludersFBO = new FrameBuffer(Pixmap.Format.RGBA8888, lightSize, lightSize, false);
        occluders = new TextureRegion(occludersFBO.getColorBufferTexture());
        occluders.flip(false, true);

        shadowMapFBO = new FrameBuffer(Pixmap.Format.RGBA8888, lightSize, 1, false);
        Texture shadowMapTex = shadowMapFBO.getColorBufferTexture();

        // Use linear filtering and repeat wrap mode when sampling
        shadowMapTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        shadowMapTex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        shadowMap1D = new TextureRegion(shadowMapTex);
        shadowMap1D.flip(false, true);

        // TODO See if shaders can be loaded with the asset manager
        ShaderProgram.pedantic = false;

        //read vertex pass-through shader
        final String VERT_SRC = Gdx.files.internal("shaders/pass.vert").readString();

        // renders occluders to 1D shadow map
        shadowMapShader = createShader(VERT_SRC, Gdx.files.internal("shaders/shadowMap.frag").readString());
        // samples 1D shadow map to create the blurred soft shadow
        shadowRenderShader = createShader(VERT_SRC, Gdx.files.internal("shaders/shadowRender.frag").readString());
    }

    // TODO Probably can be added elsewhere
    private SpriteEntity generateValidBlock()
    {
        SpriteEntity block = new SpriteEntity(_game.assetManager.get(QuadtreeDemo.TEXTURE_BLOCK, Texture.class));
        block.setPosition(MathUtils.random(0, screenWidth - block.getWidth()), MathUtils.random(0, screenHeight - block.getHeight()));
        block.setVelocity(MathUtils.random(-1.0f, 1.0f), MathUtils.random(-1.0f, 1.0f));
        block.setSpeed(MathUtils.random(20, 200));

        return block;
    }

    @Override
    public void render(float delta)
    {
        // TODO Flesh out game loop as needed

        // Outline of game loop:
        //
        // 1) Update camera
        // 2) Handle input
        // 3) Update entities
        // 4) Process collision / physics
        // 5) Render

        _game.camera.update();

        // Clear the backbuffer (Dark blue-green)
        // TODO Watch for clearing!!
        //Gdx.gl.glClearColor(0, 0.15f, 0.2f, 1);
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        updateEntities(delta);

        renderLightsAndShadows();

        _game.batch.begin();

        _game.batch.setShader(null);

        // Uncomment to render rectangle sprite entities

        /**
        for(SpriteEntity currBlock : blockArray)
        {
            // Render our game entities
            _game.batch.setColor(currBlock.getColor());
            _game.batch.draw(currBlock.getTexture(), currBlock.getX(), currBlock.getY());

            // Set the curr block back to white
            currBlock.setColor(Color.WHITE);
            _game.batch.setColor(currBlock.getColor());
        }
        */

        _game.batch.end();

        // Uncomment to display Quadtree debug drawing

        /**
        // Now, render the debug lines for the quad tree
        _game.shapeRenderer.setProjectionMatrix(_game.camera.combined);

        _game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        quadTree.render(_game.shapeRenderer);
        _game.shapeRenderer.end();

        // Lastly, attempt the periodic log
        periodicLogger.log();
        */
    }

    private void renderLightsAndShadows()
    {
        // TODO See if we can have the light where the mouse is
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Set additive if needed
        if(additive)
        {
            _game.batch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
        }

        // STEP 1: Render the occluders to FBO

        // Bind the occluders FBO
        occludersFBO.begin();

        // Clear the FBO fully
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // Set the camera to the size of our FBO
        _game.camera.setToOrtho(false, occludersFBO.getWidth(), occludersFBO.getHeight());

        // Translate camera so that light is in the center
        _game.camera.translate(mx - lightSize / 2f, my - lightSize / 2f);

        // Make sure the camera is up to date
        _game.camera.update();

        // Set up our batch for the occluder pass
        _game.batch.setProjectionMatrix(_game.camera.combined);
        _game.batch.setShader(null); //use default shader

        _game.batch.begin();

        // Draw all the blocks for determining the occlusion map
        for(SpriteEntity currBlock : blockArray)
        {
            // Render our game entities
            _game.batch.draw(currBlock.getTexture(), currBlock.getX(), currBlock.getY());
        }

        //end the batch before unbinding the FBO
        _game.batch.end();

        //unbind the FBO
        occludersFBO.end();

        // STEP 2: Build a 1D shadow map from occluders FBO

        // Bind the shadow map
        shadowMapFBO.begin();

        // Clear it
        Gdx.gl.glClearColor(0f,0f,0f,0f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // Set our shadow map shader
        _game.batch.setShader(shadowMapShader);
        _game.batch.begin();
        shadowMapShader.setUniformf("lightCastLength", lightSize);
        shadowMapShader.setUniformf("upScale", upScale);

        // Reset our camera to the FBO size
        _game.camera.setToOrtho(false, shadowMapFBO.getWidth(), shadowMapFBO.getHeight());
        _game.batch.setProjectionMatrix(_game.camera.combined);

        // Draw the occluders texture to our 1D shadow map FBO
        _game.batch.draw(occluders.getTexture(), 0, 0, lightSize, shadowMapFBO.getHeight());

        // Flush batch
        _game.batch.end();

        // Unbind shadow map FBO
        shadowMapFBO.end();

        // STEP 3: Render the blurred shadows

        // Reset projection matrix to screen
        _game.camera.setToOrtho(false);
        _game.batch.setProjectionMatrix(_game.camera.combined);

        //set the shader which actually draws the light/shadow
        _game.batch.setShader(shadowRenderShader);
        _game.batch.begin();

        shadowRenderShader.setUniformf("lightCastLength", lightSize);
        shadowRenderShader.setUniformf("softShadows", softShadows ? 1f : 0f);

        // Set the color of the light
        _game.batch.setColor(Color.WHITE);

        float finalSize = lightSize * upScale;

        //draw centered on light position
        _game.batch.draw(shadowMap1D.getTexture(), mx-finalSize/2f, my-finalSize/2f, finalSize, finalSize);

        // Flush the batch before swapping shaders
        _game.batch.end();

        // Reset color
        _game.batch.setColor(Color.WHITE);

        // Lastly, restore the prior blending mode if additive blending was used
        if(additive)
        {
            _game.batch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    @Override
    public void resize(int width, int height)
    {
        // Do nothing (For now)
    }

    @Override
    public void show()
    {
        // Start playing the music when we show the screen
        bgMusic.play();
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
        // Disposal happens within the AssetManager, so nothing to here
    }

    // ------------------------
    // Private methods
    // ------------------------

    private void updateEntities(float delta)
    {
        // Reset the number of collision checks for this frame
        numCollisionChecksThisFrame = 0;

        // Clear out the quadtree
        quadTree.clear();

        for(int i = 0; i < blockArray.size; i++)
        {
            // Fetch the current block
            SpriteEntity currBlock = blockArray.get(i);

            // Update the velocity
            float updatedX = currBlock.getX() + (currBlock.getVelocity().x * currBlock.getSpeed() * delta);
            float updatedY = currBlock.getY() + (currBlock.getVelocity().y * currBlock.getSpeed() * delta);
            currBlock.setPosition(updatedX, updatedY);

            // Keep the entity within screen space
            if(currBlock.getX() < 0)
            {
                currBlock.setX(0);

                // Negate x component of velocity
                currBlock.negateVelocityX();
            }
            else if(currBlock.getX() > screenWidth - currBlock.getWidth())
            {
                currBlock.setX(screenWidth - currBlock.getWidth());

                // Negate x component of velocity
                currBlock.negateVelocityX();
            }

            if(currBlock.getY() < 0)
            {
                currBlock.setY(0);

                // Negate y component of velocity
                currBlock.negateVelocityY();
            }
            else if(currBlock.getY() > screenHeight - currBlock.getHeight())
            {
                currBlock.setY(screenHeight - currBlock.getHeight());

                // Negate y component of velocity
                currBlock.negateVelocityY();
            }

            // Now that we've updated all the objects based on screen bounds, insert them into the quad tree
            quadTree.insert(currBlock);
        }

        for(int i = 0; i < blockArray.size; i++)
        {
            // Fetch the current block
            SpriteEntity currBlock = blockArray.get(i);

            // Clear out the entities to check
            entitiesToCheck.clear();

            // Retrieve the entities we might be able to compare against
            quadTree.retrieve(entitiesToCheck, currBlock);

            // Check the blocks
            for(SpriteEntity blockToTest : entitiesToCheck)
            {
                // SPECIAL CASE: No need to check against our self
                if(currBlock == blockToTest)
                {
                    continue;
                }

                // If the blocks are colliding (Overlapping), tint them as red
                if(currBlock.getBoundingRectangle().overlaps(blockToTest.getBoundingRectangle()))
                {
                    currBlock.setColor(Color.RED);
                    blockToTest.setColor(Color.RED);
                }

                // Increment the number of collision checks
                numCollisionChecksThisFrame++;
            }
        }

        // Update the minimum and maximum number of collisions detected
        if(numCollisionChecksThisFrame < minNumCollisionChecks)
        {
            minNumCollisionChecks = numCollisionChecksThisFrame;
        }

        if(numCollisionChecksThisFrame > maxNumCollisionChecks)
        {
            maxNumCollisionChecks = numCollisionChecksThisFrame;
        }

        // Lastly, tick the number of collisions per second and updates
        numCollisionPerSecond += numCollisionChecksThisFrame;
        updatesPerSecond++;
    }
}



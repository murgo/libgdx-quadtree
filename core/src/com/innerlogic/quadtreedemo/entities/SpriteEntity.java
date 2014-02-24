package com.innerlogic.quadtreedemo.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

/**
 * Created with IntelliJ IDEA.
 * User: Taylor
 * Date: 10/8/13
 * Time: 11:17 PM
 */
public class SpriteEntity extends Sprite
{
    private Vector3 velocity;
    private float speed;

    public SpriteEntity(Texture texture)
    {
        // Construct the underlying sprite
        super(texture);

        // Set up the rest
        velocity = new Vector3();
        speed = 0f;
    }

    public void negateVelocityX()
    {
        velocity.x *= -1;
    }

    public void negateVelocityY()
    {
        velocity.y *= -1;
    }

    public Vector3 getVelocity()
    {
        return velocity;
    }

    public void setVelocity(Vector3 velocity)
    {
        this.velocity = velocity;

        // Normalize the given velocity to be sure
        this.velocity.nor();
    }

    public void setVelocity(float x, float y, float z)
    {
        velocity.set(x, y, z);

        // Normalize the given velocity to be sure
        velocity.nor();
    }

    public void setVelocity(float x, float y)
    {
        setVelocity(x, y, 0f);
    }

    public float getSpeed()
    {
        return speed;
    }

    public void setSpeed(float speed)
    {
        this.speed = speed;
    }
}

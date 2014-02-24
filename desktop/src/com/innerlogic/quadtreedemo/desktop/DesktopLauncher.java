package com.innerlogic.quadtreedemo.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.innerlogic.quadtreedemo.QuadTreeDemo;

public class DesktopLauncher
{
    public static void main(String[] arg)
    {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.title = "quadtree-demo";
        config.useGL20 = true;
        config.width = 720;
        config.height = 720;

        new LwjglApplication(new QuadTreeDemo(), config);
    }
}

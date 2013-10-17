// Incoming attributes to be aware of on the Vertex object
attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

// The combined projection view matrix
// Set by LibGDX's SpriteBatch
uniform mat4 u_projTrans;

// Outgoing attributes that will be leverage during the next phase (fragment shader)
varying vec2 vTexCoord0;
varying vec4 vColor;

void main()
{
    vColor = a_color;
    vTexCoord0 = a_texCoord0;
    gl_Position = u_projTrans * a_position;
}
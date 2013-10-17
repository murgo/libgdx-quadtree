#define PI 3.14

// Incoming attributes to be aware of on the Fragment object
// NOTE: These have been interpolated over the verticies
varying vec2 vTexCoord0;
varying vec4 vColor;

// Bound texture that contains the occulders this light source should test against
uniform sampler2D u_texture;

// The lightCastLength (Length) of the light cast
uniform float lightCastLength;

// For debugging, use a constant value in final release
uniform float upScale;

// Alpha threshold for our occlusion map
const float THRESHOLD = 0.75;

void main(void)
{
    float distance = 1.0;

    // The output texture is a 1D texture, so we will essentially sample the column of pixes at the x of the Texture
    // coordinates based on the length of the light to cast
    for (float y = 0.0; y < lightCastLength; y += 1.0)
    {
        // Convert calculated texture coordinates into [-1, 1] space
        //
        // NOTE: We only care about the x of the texture coordinates because the output is to a 1D texture
        vec2 normalizedTexCoords = vec2(vTexCoord0.x, y / lightCastLength) * 2.0 - 1.0;

        // Polar coordinates are represented as (radial, theta), which is the length out from an origin and how much to rotate from that point. Tersely, (r, theta)

        // Calculate a theta from PI/2 (90 deg), increasing clockwise until back at PI/2 (90 degree)
        //
        // 3PI/2 + -1PI = 3PI/2 - PI = PI/2
        // 3PI/2 + 1PI = PI/2
        float theta = (PI * 1.5) + (normalizedTexCoords.x * PI);

        // Calculate a radial between 0 and 1
        float r = (1.0 + normalizedTexCoords.y) * 0.5;

        // Convert from Polar coordinates to Rectangular (Cartesian) coordinates in order to
        // sample from the occlusion texture.
        //
        // Look at: http://en.wikipedia.org/wiki/Polar_coordinate_system
        //
        // x = r * cos(theta)  [cos(0)=1 cos(90)=0 cos(180)=-1 cos(270)=0]
        // y = r * sin(theta)  [sin(0)=0 sin(90)=1 sin(180)=0 sin(270)=-1]
        vec2 polarToRectCoords = vec2(-r * sin(theta), -r * cos(theta))/2.0 + 0.5;

        // Sample the fragment at the calculated rect coordinates from the occlusion texture
        vec4 sampledFragment = texture2D(u_texture, polarToRectCoords);

        // The current distance is how far from the top we've come
        float dst = (y / lightCastLength) / upScale;

        // If we've hit an opaque fragment/pixel (occluder), then get new distance
        // If the new distance is below the current, then we'll use that for our ray
        float caster = sampledFragment.a;
        if (caster > THRESHOLD)
        {
            distance = min(distance, dst);
        }
    }

    gl_FragColor = vec4(vec3(distance), 1.0);
}
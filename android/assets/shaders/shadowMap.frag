#define PI 3.14

// Incoming attributes to be aware of on the Fragment object
// NOTE: These have been interpolated over the verticies
varying vec2 vTexCoord0;
varying vec4 vColor;

// Texture that is bound during this program
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

    for (float y = 0.0; y < lightCastLength; y += 1.0)
    {
        // Convert calculated UV coordinates in to [-1, 1] space
        vec2 normalizedTexCoords = vec2(vTexCoord0.x, y / lightCastLength) * 2.0 - 1.0;

        // Polar coordinates are represented as (radial, theta). Tersely, (r, theta)

        // Calculate a theta from PI/2 (90 deg), increasing clockwise until back to PI/2 (90 degree)
        //
        // 3PI/2 + -1PI = 3PI/2 - PI = PI/2
        // 3PI/2 + 1PI = PI/2
        float theta = PI*1.5 + normalizedTexCoords.x * PI;

        // Calculate a radial between 0 and 1
        float r = (1.0 + normalizedTexCoords.y) * 0.5;

        // Convert from Polar coordinates to Rectangular (Cartesian) coordinates in order to
        // sample from the occlusion texture.
        //
        // Look at: http://en.wikipedia.org/wiki/Polar_coordinate_system
        //
        // x = r * cos(theta)
        // y = r * sin(theta)
        vec2 polarToRectCoords = vec2(-r * sin(theta), -r * cos(theta))/2.0 + 0.5;

        // Sample the fragment at the calculated rect coordinates from the occlusion texture
        vec4 sampledFragment = texture2D(u_texture, polarToRectCoords);

        // The current distance is how far from the top we've come
        float dst = (y / lightCastLength) / upScale;

        // If we've hit an opaque fragment/pixel (occluder), then get new distance
        // If the new distance is below the current, then we'll use that for our ray
        float caster = data.a;
        if (caster > THRESHOLD)
        {
            distance = min(distance, dst);
        }
    }

    gl_FragColor = vec4(vec3(distance), 1.0);
}
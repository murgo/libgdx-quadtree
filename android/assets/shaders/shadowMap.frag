#define PI 3.14159
#define THREEPI_DIV_2 4.71239

// Incoming attributes to be aware of on the Fragment object
// NOTE: These have been interpolated over the verticies
varying vec2 vTexCoord0;
varying vec4 vColor;

// Bound texture that contains the occulders this light source should test against
// Provided by LibGDX
uniform sampler2D u_texture;

// The lightCastLength (Length) of the light cast
uniform float lightCastLength;

// For debugging, use a constant value in final release
const float UPSCALE = 1;

// Alpha threshold for our occlusion map
const float THRESHOLD = 0.75;

void main(void)
{
    float distance = 1.0;
    float currPercentOfLightLength = 0.0;

    // The output texture is a 1D texture, so we will essentially sample the column of pixes at the x of the Texture
    // coordinates based on the length of the light to cast
    for (float y = 0.0; y < lightCastLength; y += 1.0)
    {
        // Calculate the current percent of the light cast length
        currPercentOfLightLength = y / lightCastLength;

        // Convert calculated texture coordinates into [-1, 1] space
        //
        // NOTE: We only care about the x of the texture coordinates because the output is to a 1D texture
        vec2 normalizedTexCoords = vec2(vTexCoord0.x, currPercentOfLightLength) * 2.0 - 1.0;

        // Polar coordinates are represented as (radial, theta), which is the length out from an origin and how much to rotate from that point. Tersely, (r, theta)

        // Calculate a theta from PI/2 (90 deg), increasing clockwise until back at PI/2 (90 degree).
        // NOTE: We use the X because we want to rotate a bit for every X interval and iterating outward based on the Y
        //
        // 3PI/2 + -1PI = 3PI/2 - PI = PI/2
        // 3PI/2 + 1PI = PI/2
        float theta = (THREEPI_DIV_2) + (normalizedTexCoords.x * PI);

        // Calculate a radial between 0 and 1
        // NOTE: We use the Y to go outward because we rotate for every X interval of the texture coordinates
        float r = (1.0 + normalizedTexCoords.y) * 0.5;

        // Convert from Polar coordinates to Rectangular (Cartesian) coordinates in order to
        // sample from the occlusion texture. Also, put back into [0,1] space
        //
        // Look at: http://en.wikipedia.org/wiki/Polar_coordinate_system
        //
        // NOTE: The calculation results in being -PI radians rotated, moving clockwise back to PI radians.
        // This is useful later when rendering the shadows so that we can index the 1D array from 0-1 on the TexCoord.x axis.
        vec2 polarToRectCoords = vec2(-r * sin(theta), -r * cos(theta))/2.0 + 0.5;

        // Sample the fragment at the calculated rect coordinates from the occlusion texture
        vec4 sampledFragment = texture2D(u_texture, polarToRectCoords);

        // The current distance is how far from the center we've come
        float dst = currPercentOfLightLength / UPSCALE;

        // If we come across fragment/pixel with a greater opacity value than our THRESHOLD, attempt to store the distance.
        // If the new distance is below the current, then we'll use that for our ray
        float fragmentOpacity = sampledFragment.a;
        if (fragmentOpacity > THRESHOLD)
        {
            distance = min(distance, dst);
        }
    }

    gl_FragColor = vec4(vec3(distance), 1.0);
}
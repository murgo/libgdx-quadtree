#define PI 3.14159
#define TWOPI 6.28319

// Incoming attributes to be aware of on the Fragment object
// NOTE: These have been interpolated over the verticies
varying vec2 vTexCoord0;
varying vec4 vColor;

// Bound texture that contains the 1D shadow/distance map
// Provided by LibGDX
uniform sampler2D u_texture;

// The lightCastLength (Length) of the light cast
uniform float lightCastLength;

// Flag to indicate whether or not to compute soft shadows
uniform float softShadows;

// Sample from the shadow/distance map using the standard step function
// http://en.wikipedia.org/wiki/Step_function
float getVisibilityFromLookup(vec2 coord, float r)
{
    // Return 0.0 if the red value of the sampled fragment is less than r
    // Return 1.0 if the red value of the sampled fragment is greater than or equal to r
    return step(r, texture2D(u_texture, coord).r);
}

void main(void)
{
    // Convert texture coordinates into [-1, 1] space
	vec2 normalizedTexCoords = vTexCoord0.xy * 2.0 - 1.0;
	
	// Convert from Rectangular (Cartesian) coordinates to Polar coordinates in order to sample from the 1D shadow/distance map
    // occlusion texture.
    //
	// Theta can range from -PI radians, moving clockwise to PI radians. This was first made possible when creating the
	// 1D Texture Map.
    // This is useful later when rendering the shadows so that we can index the 1D array from 0-1 on the TexCoord.x axis.
	float theta = atan(normalizedTexCoords.y, normalizedTexCoords.x); // This is CRITICAL because this is used to look up in the shadow/distance map
	float r = length(normalizedTexCoords);

	// Calculate the texture coords to use to sample from the 1D shadow map/distance texture
	// NOTE: Make Y coord 0.0
	vec2 lookupCoords = vec2(((theta + PI) / TWOPI), 0.0);

	// Determine the visibility of the the current fragment
	// 1.0 is visible, 0.0 is not
	float visibility = getVisibilityFromLookup(lookupCoords, r);

	// Multiply the blur amount by our distance from center (r, the calculated radial)
	// This results in more blurriness as the shadow "fades away".
	float blur = (1.0 / lightCastLength) * smoothstep(0.0, 1.0, r);

    // Perform a simple guassian, sampling from up to 8 different "casts" besides the original "cast"
	float sum = getVisibilityFromLookup(vec2(lookupCoords.x - 4.0 * blur, lookupCoords.y), r) * 0.0162162162;
	sum += getVisibilityFromLookup(vec2(lookupCoords.x - 3.0 * blur, lookupCoords.y), r) * 0.0540540541;
	sum += getVisibilityFromLookup(vec2(lookupCoords.x - 2.0 * blur, lookupCoords.y), r) * 0.12;
	sum += getVisibilityFromLookup(vec2(lookupCoords.x - 1.0 * blur, lookupCoords.y), r) * 0.1945945946;

	sum += visibility * 0.2270270270;

	sum += getVisibilityFromLookup(vec2(lookupCoords.x + 1.0 * blur, lookupCoords.y), r) * 0.1945945946;
	sum += getVisibilityFromLookup(vec2(lookupCoords.x + 2.0 * blur, lookupCoords.y), r) * 0.1216216216;;
	sum += getVisibilityFromLookup(vec2(lookupCoords.x + 3.0 * blur, lookupCoords.y), r) * 0.0540540541;
	sum += getVisibilityFromLookup(vec2(lookupCoords.x + 4.0 * blur, lookupCoords.y), r) * 0.0162162162;

	// Use the summed value if soft shadows was flagged, otherwise, use visibility value for hard shadows.
 	float chosenOpacity = mix(visibility, sum, softShadows);

 	// Scale by our distance, which gives us a radial falloff, then multiply by vertex-interpolated color e.g. light color
 	gl_FragColor = vColor * vec4(vec3(1.0), chosenOpacity * smoothstep(1.0, 0.0, r));
}
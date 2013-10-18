#define PI 3.14

// Incoming attributes to be aware of on the Fragment object
// NOTE: These have been interpolated over the verticies
varying vec2 vTexCoord0;
varying vec4 vColor;

// Bound texture that contains the 1D shadow/distance map
uniform sampler2D u_texture;

// The lightCastLength (Length) of the light cast
uniform float lightCastLength;

uniform float softShadows;

//sample from the distance map
float sample(vec2 coord, float r)
{
    return step(r, texture2D(u_texture, coord).r);
}

void main(void)
{
    // Rectangular to Polar Coordinates
	vec2 norm = vTexCoord0.st * 2.0 - 1.0;
	// Theta can range from -PI radians, moving clockwise to PI radians. This was first made possible when creating the
	// 1D Texture Map.
    // This is useful later when rendering the shadows so that we can index the 1D array from 0-1 on the TexCoord.x axis.
	float theta = atan(norm.y, norm.x);
	float r = length(norm);
	float coord = (theta + PI) / (2.0*PI);  // Theta can be -PI to PI, resulting in 0/2PI to 2PI/2PI or 0 to 1

	//the tex coord to sample our 1D lookup texture
	//always 0.0 on y axis
	vec2 tc = vec2(coord, 0.0);

	//the center tex coord, which gives us hard shadows
	float center = sample(vec2(tc.x, tc.y), r);

	//we multiply the blur amount by our distance from center
	//this leads to more blurriness as the shadow "fades away"
	float blur = (1. / lightCastLength)  * smoothstep(0., 1., r);

	//now we use a simple gaussian blur
	float sum = 0.0;

	sum += sample(vec2(tc.x - 4.0*blur, tc.y), r) * 0.05;
	sum += sample(vec2(tc.x - 3.0*blur, tc.y), r) * 0.09;
	sum += sample(vec2(tc.x - 2.0*blur, tc.y), r) * 0.12;
	sum += sample(vec2(tc.x - 1.0*blur, tc.y), r) * 0.15;

	sum += center * 0.16;

	sum += sample(vec2(tc.x + 1.0*blur, tc.y), r) * 0.15;
	sum += sample(vec2(tc.x + 2.0*blur, tc.y), r) * 0.12;
	sum += sample(vec2(tc.x + 3.0*blur, tc.y), r) * 0.09;
	sum += sample(vec2(tc.x + 4.0*blur, tc.y), r) * 0.05;

	//1.0 -> in light, 0.0 -> in shadow
 	float lit = mix(center, sum, softShadows);

 	//multiply the summed amount by our distance, which gives us a radial falloff
 	//then multiply by vertex (light) color
 	gl_FragColor = vColor * vec4(vec3(1.0), lit * smoothstep(1.0, 0.0, r));
}
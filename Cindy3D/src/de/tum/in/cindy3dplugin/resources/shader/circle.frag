// Circle center in view space
uniform vec3 circleCenter;
// Circle radius
uniform float circleRadiusSq;

// View space position
varying vec3 viewSpacePosition;
// Normal at the view space position
varying vec3 normal;

// Include shading methods
#pragma include _shading.frag

// ----------------------------------------------------------------------------
// Fragment shader for circle rendering
// ----------------------------------------------------------------------------
void main() {
	// Compute distance from view space position to circle center
  vec3 diff = viewSpacePosition - circleCenter;
  
  // If position is too far away from center and therefore outside the circle
  if (dot(diff, diff) > circleRadiusSq) {
    discard;
  } else {
    shade(viewSpacePosition, normalize(normal));
  }
}

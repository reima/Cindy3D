#version 110

// Position in view space
varying vec3 viewSpacePosition;
// Normal at the view space position
varying vec3 normal;

// Include shading methods
#pragma include _shading.frag

// ----------------------------------------------------------------------------
// Fragment shader for polygon rendering
// ----------------------------------------------------------------------------
void main() {
  shade(viewSpacePosition, normalize(normal));
}

#version 110

// View space position
varying vec3 viewSpacePosition;
// Normal at the view space position
varying vec3 normal;

// ----------------------------------------------------------------------------
// Vertex shader for polygon rendering
// ----------------------------------------------------------------------------
void main() {
  // Transform position into view space
  viewSpacePosition = vec3(gl_ModelViewMatrix * gl_Vertex);
  // Transform position into screen space
  gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
  // Compute normal in view space
	normal = normalize(gl_NormalMatrix * gl_Normal);
}

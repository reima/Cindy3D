// Surface position in view space
varying vec3 viewSpacePosition;

// ----------------------------------------------------------------------------
// Vertex shader for cylinder rendering
// ----------------------------------------------------------------------------
void main() {
  // Transform position into view space
  viewSpacePosition = vec3(gl_ModelViewMatrix * gl_Vertex);
  // Transform position into screen space
  gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}

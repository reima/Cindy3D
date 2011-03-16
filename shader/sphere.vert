uniform vec3 center;
uniform float radius;

varying vec3 pos;

void main() {
  vec3 right =
    normalize(vec3(gl_ModelViewMatrix[0][0],
                   gl_ModelViewMatrix[1][0],
                   gl_ModelViewMatrix[2][0]));
  vec3 up =
    normalize(vec3(gl_ModelViewMatrix[0][1],
                   gl_ModelViewMatrix[1][1],
                   gl_ModelViewMatrix[2][1]));

  vec3 alignedVertex = center + radius*(right * gl_Vertex.x + up * gl_Vertex.y);
  pos = vec3(gl_ModelViewMatrix * vec4(alignedVertex, 1));
  gl_Position = gl_ModelViewProjectionMatrix * vec4(alignedVertex, 1);
}

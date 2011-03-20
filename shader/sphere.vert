uniform vec3 sphereCenter;
uniform float sphereRadius;

varying vec3 pos;

void main() {
/*  vec3 right =
    normalize(vec3(gl_ModelViewMatrix[0][0],
                   gl_ModelViewMatrix[1][0],
                   gl_ModelViewMatrix[2][0]));
  vec3 up =
    normalize(vec3(gl_ModelViewMatrix[0][1],
                   gl_ModelViewMatrix[1][1],
                   gl_ModelViewMatrix[2][1]));*/
                   
  vec3 camSpaceCenter = vec3(gl_ModelViewMatrix * vec4(sphereCenter, 1));
  vec3 dir = normalize(-camSpaceCenter);
  vec3 right = cross(dir, vec3(0, 1, 0));
  vec3 up = cross(right, dir);

  pos = camSpaceCenter + sphereRadius*(right * gl_Vertex.x + up * gl_Vertex.y + dir);
  gl_Position = gl_ProjectionMatrix * vec4(pos, 1);
}

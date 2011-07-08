uniform vec3 circleCenter;
uniform float circleRadiusSq;
uniform vec3 circleNormal;
uniform vec4 circleColor;

varying vec3 pos;

#pragma include _shading.frag

void main() {
  vec3 diff = pos - circleCenter;
  if (dot(diff, diff) > circleRadiusSq) {
    discard;
  } else {
    shade(gl_NormalMatrix * circleNormal,
          vec3(gl_ModelViewMatrix * vec4(pos, 1)),
          circleColor);
  }
}

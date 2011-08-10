uniform vec3 circleCenter;
uniform float circleRadiusSq;
uniform vec3 circleNormal;

varying vec3 pos;
varying vec3 normal;

#pragma include _shading.frag

void main() {

	gl_FragColor = vec4(pos,1);
	//return;
	
  vec3 diff = pos - circleCenter;
  if (dot(diff, diff) > circleRadiusSq) {
    discard;
  } else {
    shade(normalize(normal),pos);
  }
}

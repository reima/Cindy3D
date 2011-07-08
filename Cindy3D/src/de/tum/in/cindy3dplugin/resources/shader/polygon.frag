uniform vec4 polygonColor;

varying vec3 pos;
varying vec3 normal;

#pragma include _shading.frag

void main() {
	shade(normalize(normal),
	      vec3(gl_ModelViewMatrix * vec4(pos, 1)),
	      polygonColor);
}

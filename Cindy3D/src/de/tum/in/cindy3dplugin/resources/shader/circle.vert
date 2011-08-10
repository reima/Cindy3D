varying vec3 pos;
varying vec3 normal;

void main() {
	pos = vec3(gl_ModelViewMatrix * gl_Vertex);

	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	normal = normalize(gl_NormalMatrix * gl_Normal);
}

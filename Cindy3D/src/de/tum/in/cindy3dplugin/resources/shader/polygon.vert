varying vec3 pos;
varying vec3 normal;

void main() {
	pos = vec3(gl_Vertex);
	normal = normalize(gl_NormalMatrix * gl_Normal);
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}

varying vec3 pos;

void main() {
	pos = vec3(gl_ModelViewMatrix * gl_Vertex);
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
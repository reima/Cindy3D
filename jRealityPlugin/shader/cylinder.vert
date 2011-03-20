uniform mat4 cylinderTransform;

varying vec3 pos;

void main() {
	vec4 transformed = cylinderTransform * gl_Vertex;
	pos = vec3(gl_ModelViewMatrix * transformed);
	gl_Position = gl_ModelViewProjectionMatrix * transformed;
}

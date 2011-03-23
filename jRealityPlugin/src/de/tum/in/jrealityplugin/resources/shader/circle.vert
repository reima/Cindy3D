uniform mat4 circleTransform;

varying vec3 pos;

void main() {
	vec4 transformed = circleTransform * gl_Vertex;
	pos = vec3(transformed);
	gl_Position = gl_ModelViewProjectionMatrix * transformed;
}

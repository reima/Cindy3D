uniform vec3 cylinderPoint;
uniform vec3 cylinderDirection;
uniform float cylinderRadius;
uniform vec4 cylinderColor;
uniform float cylinderLength;

varying vec3 pos;

#pragma include _shading.frag

void main() {
	vec3 dir = normalize(pos);
	
	vec3 AOxAB = cross(-cylinderPoint, cylinderDirection);
	vec3 VxAB = cross(dir, cylinderDirection);
	
	float a = dot(VxAB, VxAB);
	float b = 2.0 * dot(VxAB, AOxAB);
	float c = dot(AOxAB, AOxAB) - cylinderRadius*cylinderRadius;
	
	float lambda = -1.0;
	float d = b*b-4.0*a*c;
	float hit = 0.0;
	if (d > 0.0) {
		float sqrtD = sqrt(d);
		lambda = (-b - sqrtD) / (2.0*a);
		if (lambda > 0.0) {
			hit = 1.0;
		} else {
			lambda = (-b + sqrtD) / (2.0*a);
			if (lambda > 0.0) {
				hit = 1.0;
			}
		}
	}
	
	if (hit == 1.0) {
		vec3 pointOnCylinder = lambda*dir;
		float dist = dot(cylinderDirection, pointOnCylinder-cylinderPoint);
		if (cylinderLength >= 0.0) {
			if (dist < 0.0 || (cylinderLength > 0.0 && dist > cylinderLength)) {
				hit = 0.0;
			}
		}
	}
	
	if (hit == 0.0) {
		discard;
	}

	vec3 normal = normalize(cross(cross(cylinderDirection,
						pointOnCylinder-cylinderPoint), cylinderDirection));

	shade(normal, pointOnCylinder, cylinderColor);
  
	vec4 projPoint = gl_ProjectionMatrix * vec4(pointOnCylinder, 1);
	gl_FragDepth = (projPoint.z / projPoint.w + 1.0) / 2.0;
}

// Cylinder start position in view space
uniform vec3 cylinderPoint;
// Cylinder direction in view space
uniform vec3 cylinderDirection;
// Cylinder radius
uniform float cylinderRadius;
// Cylinder length indicator
uniform float cylinderLength;

// Surface position in view space
varying vec3 viewSpacePosition;

// Include shading methods
#pragma include _shading.frag

// ----------------------------------------------------------------------------
// Fragment shader for cylinder rendering
// ----------------------------------------------------------------------------
void main() {
  // Vector from eye point to surface position
	vec3 dir = normalize(viewSpacePosition);
	
  // Compute intersection with infinitly long cylinder
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
	
  // If infinitly long cylinder is hit, check if the possibly finite
  // cylinder is hit too
	vec3 pointOnCylinder;
	if (hit == 1.0) {
		pointOnCylinder = lambda*dir;
		float dist = dot(cylinderDirection, pointOnCylinder-cylinderPoint);
		if (cylinderLength >= 0.0) {
			if (dist < 0.0 || (cylinderLength > 0.0 && dist > cylinderLength)) {
				hit = 0.0;
			}
		}
	}
	
  // If ray does not hit cylinder discard
	if (hit == 0.0) {
		discard;
	}

  // Compute normal
	vec3 normal = normalize(cross(cross(cylinderDirection,
						pointOnCylinder-cylinderPoint), cylinderDirection));

  // Shade surface position
	shade(pointOnCylinder, normal);

  // Adjust depth value as the depth value of the bounding box differs
  // from the actual depth value
	vec4 projPoint = gl_ProjectionMatrix * vec4(pointOnCylinder, 1);
	gl_FragDepth = (projPoint.z / projPoint.w + 1.0) / 2.0;
}

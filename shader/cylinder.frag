uniform vec3 cylinderPoint1;
uniform vec3 cylinderPoint2;
uniform float cylinderRadius;

varying vec3 pos;

void main() {
	vec3 camSpaceBase = vec3(gl_ModelViewMatrix * vec4(cylinderPoint1, 1));
	
	vec3 camSpaceDir = vec3(gl_ModelViewMatrix * vec4(cylinderPoint2,1))-camSpaceBase;
	float cylinderLength = length(camSpaceDir);
	camSpaceDir = camSpaceDir / length;

	vec3 dir = normalize(pos);
	
	vec3 AOxAB = cross(-camSpaceBase, camSpaceDir);
	vec3 VxAB = cross(dir, camSpaceDir);
	
	float a = dot(VxAB, VxAB);
	float b = 2.0 * dot(VxAB, AOxAB);
	float c = dot(AOxAB, AOxAB) - cylinderRadius*cylinderRadius;
	
	float lambda = -1.0;
	float d = b*b-4.0*a*c;
	if (d < 0.0)
		discard;
	else {
		float sqrtD = sqrt(d);
		lambda = (-b - sqrtD) / (2.0*a);
		if (lambda < 0.0) {
			lambda = (-b + sqrtD) / (2.0*a);
			if (lambda < 0.0)
				discard;
		}
	}
		
	vec3 pointOnCylinder = lambda*dir;
  
  	float dist = dot(camSpaceDir, pointOnCylinder-camSpaceBase);
  	if (dist < 0.0 || dist > cylinderLength)
  		discard;
  
	gl_FragColor = vec4(pointOnCylinder, 1.0);
	vec3 normal = normalize(cross(cross(camSpaceDir, pointOnCylinder-camSpaceBase), camSpaceDir));

	vec3 lightPos = vec3(gl_ModelViewMatrix * vec4(10, 10, 20, 1));
	vec3 lightDir = normalize(lightPos - pointOnCylinder);
  
	float diffuse = max(dot(lightDir, normal), 0.0);
  
	vec3 reflectVec = reflect(-lightDir, normal);
	float spec = max(dot(reflectVec, -dir), 0.0);
	spec = pow(spec, 16.0)*0.5;

	gl_FragColor = vec4(diffuse, 0.0, 0.0, 1.0) + spec*vec4(1.0, 1.0, 1.0, 0.0);
  
	vec4 projPoint = gl_ProjectionMatrix * vec4(pointOnCylinder, 1);
	gl_FragDepth = (projPoint.z / projPoint.w + 1.0) / 2.0;
}
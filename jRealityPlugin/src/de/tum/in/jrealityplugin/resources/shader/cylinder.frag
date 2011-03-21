uniform vec3 cylinderPoint1;
uniform vec3 cylinderPoint2;
uniform float cylinderRadius;
/*uniform*/ vec4 cylinderColor = vec4(1,0,0,1);

varying vec3 pos;

vec4 Ambient;
vec4 Diffuse;
vec4 Specular;

void pointLight(in int i, in vec3 normal, in vec3 eye, in vec3 ecPosition3)
{
   float nDotVP;       // normal . light direction
   float nDotHV;       // normal . light half vector
   float pf;           // power factor
   float attenuation;  // computed attenuation factor
   float d;            // distance from surface to light source
   vec3  VP;           // direction from surface to light position
   vec3  halfVector;   // direction of maximum highlights

   // Compute vector from surface to light position
   VP = vec3 (gl_LightSource[i].position) - ecPosition3;

   // Compute distance between surface and light position
   d = length(VP);

   // Normalize the vector from surface to light position
   VP = normalize(VP);

   // Compute attenuation
   attenuation = 1.0 / (gl_LightSource[i].constantAttenuation +
       gl_LightSource[i].linearAttenuation * d +
       gl_LightSource[i].quadraticAttenuation * d * d);

   halfVector = normalize(VP + eye);

   nDotVP = max(0.0, dot(normal, VP));
   nDotHV = max(0.0, dot(normal, halfVector));

   if (nDotVP == 0.0)
   {
       pf = 0.0;
   }
   else
   {
       pf = pow(nDotHV, gl_FrontMaterial.shininess);

   }
   Ambient  += gl_LightSource[i].ambient * attenuation;
   Diffuse  += gl_LightSource[i].diffuse * nDotVP * attenuation;
   Specular += gl_LightSource[i].specular * pf * attenuation;
}

void directionalLight(in int i, in vec3 normal)
{
   float nDotVP;         // normal . light direction
   float nDotHV;         // normal . light half vector
   float pf;             // power factor

   nDotVP = max(0.0, dot(normal, normalize(vec3 (gl_LightSource[i].position))));
   nDotHV = max(0.0, dot(normal, vec3 (gl_LightSource[i].halfVector)));

   if (nDotVP == 0.0)
   {
       pf = 0.0;
   }
   else
   {
       pf = pow(nDotHV, gl_FrontMaterial.shininess);

   }
   Ambient  += gl_LightSource[i].ambient;
   Diffuse  += gl_LightSource[i].diffuse * nDotVP;
   Specular += gl_LightSource[i].specular * pf;
}

void shade(in vec3 normal, in vec3 ecPoint) {
  Ambient = vec4(0.0);
  Diffuse = vec4(0.0);
  Specular = vec4(0.0);
  
  pointLight(0, normal, -ecPoint, ecPoint);
  pointLight(1, normal, -ecPoint, ecPoint);
  directionalLight(2, normal);
 
  vec4 color = gl_FrontLightModelProduct.sceneColor +
    Ambient  * gl_FrontMaterial.ambient +
    Diffuse  * cylinderColor;
  color += Specular * gl_FrontMaterial.specular;
  color = clamp( color, 0.0, 1.0 );
  gl_FragColor = vec4(color.rgb, 1.0);
}


void main() {
	//vec3 camSpaceBase = vec3(gl_ModelViewMatrix * vec4(cylinderPoint1, 1));
	vec3 camSpaceBase = cylinderPoint1;
	
	//vec3 camSpaceDir = vec3(gl_ModelViewMatrix * vec4(cylinderPoint2,1))-camSpaceBase;
	vec3 camSpaceDir = cylinderPoint2-camSpaceBase;
	float cylinderLength = length(camSpaceDir);
	camSpaceDir = camSpaceDir / cylinderLength;

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
  
	vec3 normal = normalize(cross(cross(camSpaceDir, pointOnCylinder-camSpaceBase), camSpaceDir));

	shade(normal, pointOnCylinder);
  
	vec4 projPoint = gl_ProjectionMatrix * vec4(pointOnCylinder, 1);
	gl_FragDepth = (projPoint.z / projPoint.w + 1.0) / 2.0;
}

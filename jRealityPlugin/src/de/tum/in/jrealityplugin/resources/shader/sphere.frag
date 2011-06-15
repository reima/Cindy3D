uniform vec3 sphereCenter;
uniform float sphereRadius;
uniform vec4 sphereColor;
uniform float sphereMode;

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
  //directionalLight(2, normal);
 
  vec4 color = gl_FrontLightModelProduct.sceneColor +
    Ambient  * gl_FrontMaterial.ambient +
    Diffuse  * vec4(sphereColor.rgb,1.0);
  color += Specular * gl_FrontMaterial.specular;
  color = clamp( color, 0.0, 1.0 );
  gl_FragColor = vec4(color.rgb, sphereColor.a);
}

void main() {
  vec3 camSpaceCenter = vec3(gl_ModelViewMatrix * vec4(sphereCenter, 1));

  vec3 dir = normalize(pos);

  float b = dot(camSpaceCenter, dir);
  float c = dot(camSpaceCenter, camSpaceCenter) - sphereRadius*sphereRadius;
  float d = b*b - c;
  float lambda = 0.0;
  if (d < 0.0) {
    discard;
  } else {
    float sqrtD = sqrt(d);
    if (sphereMode == 0.0)
    	lambda = b + sqrtD;
    else
    	lambda = b - sqrtD;
    	
    if (lambda < 0.0)
    	discard;
    
    
//    if (lambda < 0.0) {
//      lambda = b + sqrtD;
//      if (lambda < 0.0) {
//        discard;
//      }
//    }
  }

  vec3 pointOnSphere = lambda*dir;
  vec3 normal = normalize(pointOnSphere - camSpaceCenter);
  
  //gl_FragColor = sphereColor;
  
  shade(normal, pointOnSphere);
   
  vec4 projPoint = gl_ProjectionMatrix * vec4(pointOnSphere, 1);
  gl_FragDepth = (projPoint.z / projPoint.w + 1.0) / 2.0;
}

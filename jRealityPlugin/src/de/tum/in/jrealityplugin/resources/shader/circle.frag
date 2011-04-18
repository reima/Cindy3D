uniform vec3 circleCenter;
uniform float circleRadiusSq;
uniform vec3 circleNormal;
uniform vec4 circleColor;

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

   nDotVP = abs(dot(normal, VP));
   nDotHV = abs(dot(normal, halfVector));

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

   nDotVP = abs(dot(normal, normalize(vec3 (gl_LightSource[i].position))));
   nDotHV = abs(dot(normal, vec3 (gl_LightSource[i].halfVector)));

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
    Diffuse  * vec4(circleColor.rgb, 1.0);
  color += Specular * gl_FrontMaterial.specular;
  color = clamp( color, 0.0, 1.0 );
  gl_FragColor = vec4(color.rgb, circleColor.a);
}

void main() {
  vec3 diff = pos - circleCenter;
  if (dot(diff, diff) > circleRadiusSq) {
    discard;
  } else {
    shade(gl_NormalMatrix * circleNormal, vec3(gl_ModelViewMatrix * vec4(pos, 1)));
  }
}

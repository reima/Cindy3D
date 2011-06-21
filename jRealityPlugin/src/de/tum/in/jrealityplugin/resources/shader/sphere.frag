uniform vec3 sphereCenter;
uniform float sphereRadius;
uniform vec4 sphereColor;
uniform float sphereMode;

varying vec3 pos;

#pragma include _shading.frag

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
  
  shade(normal, pointOnSphere, sphereColor);
   
  vec4 projPoint = gl_ProjectionMatrix * vec4(pointOnSphere, 1);
  gl_FragDepth = (projPoint.z / projPoint.w + 1.0) / 2.0;
}

uniform vec3 sphereCenter;
uniform float sphereRadius;

varying vec3 pos;

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
    lambda = b - sqrtD;
    if (lambda < 0.0) {
      lambda = b + sqrtD;
      if (lambda < 0.0) {
        discard;
      }
    }
  }

  vec3 pointOnSphere = lambda*dir;
  vec3 normal = normalize(pointOnSphere - camSpaceCenter);

  vec3 lightPos = vec3(gl_ModelViewMatrix * vec4(10, 10, 20, 1));
  vec3 lightDir = normalize(lightPos - pointOnSphere);
  
  float diffuse = max(dot(lightDir, normal), 0.0);
  
  vec3 reflectVec = reflect(-lightDir, normal);
  float spec = max(dot(reflectVec, -dir), 0.0);
  spec = pow(spec, 16.0)*0.5;

  gl_FragColor = vec4(diffuse, 0.0, 0.0, 1.0) + spec*vec4(1.0, 1.0, 1.0, 0.0);
  
  vec4 projPoint = gl_ProjectionMatrix * vec4(pointOnSphere, 1);
  gl_FragDepth = (projPoint.z / projPoint.w + 1.0) / 2.0;
}

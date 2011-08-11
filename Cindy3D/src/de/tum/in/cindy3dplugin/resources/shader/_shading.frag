// Global ambient color for a surface point
vec4 Ambient;
// Global diffuse color for a surface point
vec4 Diffuse;
// Global specular color for a surface point
vec4 Specular;

// ----------------------------------------------------------------------------
// Point light shading for a particular surface position
// ----------------------------------------------------------------------------
// position     Surface position to be shaded
// normal       Normal vector at the given surface position
// eye          Normalized vector pointing from surface position to eye point
// lightIdx     Light index needed to retrieve light properties
// ----------------------------------------------------------------------------
void pointLight(in vec3 position, in vec3 normal, in vec3 eye, in int lightIdx) {
  vec3 lightDir;      // Direction from surface to light position
  float distance;     // Distance from surface to light source
  float attenuation;  // Attenuation factor
  vec3 halfVector;    // Direction of maximum highlights
  float diffuseDot;   // Dot(normal, light direction)
  float specularDot;  // Dot(normal, light half vector)
  float specFactor;   // specular factor

  // Invert normal for double sided lighting
  if (dot(normal, eye) < 0.0) {
    normal *= -1.0;
  }

  // Compute vector from surface to light position
  lightDir = vec3(gl_LightSource[lightIdx].position) - position;
  // Compute distance between surface and light position
  distance = length(lightDir);
  // Normalize the vector from surface to light position
  lightDir = normalize(lightDir);

  // Compute attenuation
  attenuation = 1.0 / (gl_LightSource[lightIdx].constantAttenuation +
    gl_LightSource[lightIdx].linearAttenuation * distance +
    gl_LightSource[lightIdx].quadraticAttenuation * distance * distance);

  // Compute reflection half vector
  halfVector = normalize(lightDir + eye);

  // Compute diffuse factor
  diffuseDot = max(0.0, dot(normal, lightDir));
  // Compute specular factor
  specularDot = max(0.0, dot(normal, halfVector));

  // If point is not lit
  if (diffuseDot == 0.0) {
    specFactor = 0.0;
  } else {
    specFactor = pow(specularDot, gl_FrontMaterial.shininess);
  }

  // Add light received from this light source to global colors
  Ambient  += attenuation * gl_LightSource[lightIdx].ambient;
  Diffuse  += attenuation * gl_LightSource[lightIdx].diffuse * diffuseDot;
  Specular += attenuation * gl_LightSource[lightIdx].specular * specFactor;
}

// ----------------------------------------------------------------------------
// Directional light shading for a particular surface position
// ----------------------------------------------------------------------------
// normal       Normal vector at the given surface position
// eye          Normalized vector pointing from surface position to eye point
// lightIdx     Light index needed to retrieve light properties
// ----------------------------------------------------------------------------
void directionalLight(in vec3 normal, in vec3 eye, in int lightIdx) {
  vec3 lightDir;      // Direction from surface to light position
  vec3 halfVector;    // Direction of maximum highlights
  float diffuseDot;   // Dot(normal, light direction)
  float specularDot;  // Dot(normal, light half vector)
  float specFactor;   // specular factor

  // Invert normal for double sided lighting
  if (dot(normal, eye) < 0.0) {
    normal *= -1.0;
  }

  // Compute vector from surface to light position
  lightDir = normalize(vec3(gl_LightSource[lightIdx].position));
  
  // Compute reflection half vector 
  halfVector = normalize(lightDir + eye);

  // Compute diffuse factor
  diffuseDot = max(0.0, dot(normal, lightDir));
  // Compute specular factor
  specularDot = max(0.0, dot(normal, halfVector));

  // If intersection is not lit
  if (diffuseDot == 0.0) {
    specFactor = 0.0;
  } else {
    specFactor = pow(specularDot, gl_FrontMaterial.shininess);
  }
  
  // Add light received from this light source to global colors
  Ambient  += gl_LightSource[lightIdx].ambient;
  Diffuse  += gl_LightSource[lightIdx].diffuse * diffuseDot;
  Specular += gl_LightSource[lightIdx].specular * specFactor;
}

// ----------------------------------------------------------------------------
// Spot light shading for a particular surface position
// ----------------------------------------------------------------------------
// position     Surface position to be shaded
// normal       Normal vector at the given surface position
// eye          Normalized vector pointing from surface position to eye point
// lightIdx     Light index needed to retrieve light properties
// ----------------------------------------------------------------------------
void spotLight(in vec3 position, in vec3 normal, in vec3 eye, in int lightIdx) {
  float diffuseDot;      // normal . light direction
  float specularDot;     // normal . light half vector
  float specFactor;      // power factor
  float spotCosAngle;    // cosine of angle between spotlight
  float spotAttenuation; // spotlight attenuation factor
  float attenuation;     // computed attenuation factor
  float distance;        // distance from surface to light source
  vec3  lightDir;        // direction from surface to light position
  vec3  halfVector;      // direction of maximum highlights
   
     // Invert normal for double sided lighting
  if (dot(normal, eye) < 0.0) {
    normal *= -1.0;
  }

  // Compute vector from surface to light position
  lightDir = vec3(gl_LightSource[lightIdx].position) - position;
  // Compute distance between surface and light position
  distance = length(lightDir);
  // Normalize the vector from surface to light position
  lightDir = normalize(lightDir);

  // Compute attenuation
  attenuation = 1.0 / (gl_LightSource[lightIdx].constantAttenuation +
    gl_LightSource[lightIdx].linearAttenuation * distance +
    gl_LightSource[lightIdx].quadraticAttenuation * distance * distance);

  // See if point on surface is inside cone of illumination
  spotCosAngle = dot(-lightDir, normalize(gl_LightSource[lightIdx].spotDirection));

  if (spotCosAngle < gl_LightSource[lightIdx].spotCosCutoff) {
    // Light adds no contribution
    spotAttenuation = 0.0;
  } else {
    spotAttenuation = pow(spotCosAngle, gl_LightSource[lightIdx].spotExponent);  
  }

  // Combine the spotlight and distance attenuation.
  attenuation *= spotAttenuation;

  halfVector = normalize(lightDir + eye);

  diffuseDot = max(0.0, dot(normal, lightDir));
  specularDot = max(0.0, dot(normal, halfVector));

  // If point is not lit
  if (diffuseDot == 0.0) {
    specFactor = 0.0;
  } else {
    specFactor = pow(specularDot, gl_FrontMaterial.shininess);
  }
  
  // Add light received from this light source to global colors
  Ambient  += attenuation * gl_LightSource[lightIdx].ambient;
  Diffuse  += attenuation * gl_LightSource[lightIdx].diffuse * diffuseDot;
  Specular += attenuation * gl_LightSource[lightIdx].specular * specFactor;
}

// ----------------------------------------------------------------------------
// Shades a particular surface point
// ----------------------------------------------------------------------------
// position     position vector to be shaded
// normal       normal vector at the given position
// ----------------------------------------------------------------------------
void shade(in vec3 position, in vec3 normal) {
  // Reset global colors to black as in an unlit scene
  Ambient = vec4(0.0);
  Diffuse = vec4(0.0);
  Specular = vec4(0.0);

  // Appropriate code is inserted during shader compiling according
  // to current light settings
#pragma lights
 
  // Modulate pure lighting color at intersection point with
  // intersection point material
  vec4 color = Ambient  * gl_FrontMaterial.ambient +
               Diffuse  * gl_FrontMaterial.diffuse +
               Specular * gl_FrontMaterial.specular;

  color = clamp(color, 0.0, 1.0);
  gl_FragColor = vec4(color.rgb, gl_FrontMaterial.diffuse.a);
}
// ----------------------------------------------------------------------------

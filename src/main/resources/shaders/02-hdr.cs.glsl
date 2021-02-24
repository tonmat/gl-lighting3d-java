#version 430
#extension GL_ARB_compute_variable_group_size : enable

struct Light {
    float position[3];
    float ambient[3];
    float diffuse[3];
    float specular[3];
    float attenuation[3];
};

layout (local_size_variable) in;
layout (rgba32f, binding = 0) uniform readonly image2D in_position;
layout (rgba32f, binding = 1) uniform readonly image2D in_normal;
layout (rgba32f, binding = 2) uniform readonly image2D in_albedo_shininess;
layout (rgba32f, binding = 3) uniform writeonly image2D out_color;

layout (std430, binding = 0) buffer Lights {
    uint lights_count;
    Light lights[];
};

void main() {
    ivec2 pixcoords = ivec2(gl_GlobalInvocationID.xy);
    vec3 position = imageLoad(in_position, pixcoords).rgb;
    vec3 normal = imageLoad(in_normal, pixcoords).rgb;
    vec4 albedo_shininess = imageLoad(in_albedo_shininess, pixcoords);
    vec3 albedo = albedo_shininess.rgb;
    float shininess = albedo_shininess.a;

    vec3 color = vec3(0.0);

    for (uint i = 0; i < lights_count; i++) {
        Light light = lights[i];
        vec3 light_position = vec3(light.position[0], light.position[1], light.position[2]);
        vec3 light_ambient = vec3(light.ambient[0], light.ambient[1], light.ambient[2]);
        vec3 light_diffuse = vec3(light.diffuse[0], light.diffuse[1], light.diffuse[2]);
        vec3 light_specular = vec3(light.specular[0], light.specular[1], light.specular[2]);

        vec3 ambient = light_ambient * albedo;

        vec3 light_dir = light_position - position;
        float distance = length(light_dir);
        light_dir = normalize(light_dir);

        vec3 diffuse = vec3(0.0);
        vec3 specular = vec3(0.0);
        float lambert = max(dot(light_dir, normal), 0.0);
        if (lambert > 0.0) {
            diffuse = lambert * light_diffuse * albedo;
            vec3 view_dir = normalize(-position);
            vec3 h = normalize(view_dir + light_dir);
            float specularAngle = max(dot(h, normal), 0.0);
            if (specularAngle > 0.0) {
                specular = light_specular * albedo * pow(specularAngle, shininess);
            }
        }

        float attenuation = 1.0f / (light.attenuation[0] + light.attenuation[1] * distance + light.attenuation[2] * distance * distance);

        color += (ambient + diffuse + specular) * attenuation;
    }

    imageStore(out_color, pixcoords, vec4(color, 1.0));
}
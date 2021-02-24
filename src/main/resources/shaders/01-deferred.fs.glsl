#version 430

struct Material {
    vec4 albedo_shininess;
};

in vec3 v_position;
in vec3 v_normal;
in flat uint v_material;

layout (std430, binding = 1) buffer Materials {
    Material materials[];
};

layout (location = 0) out vec4 f_position;
layout (location = 1) out vec4 f_normal;
layout (location = 2) out vec4 f_albedo_shininess;

void main() {
    Material material = materials[v_material];
    f_position = vec4(v_position, 1.0);
    f_normal = vec4(normalize(v_normal), 1.0);
    f_albedo_shininess = material.albedo_shininess;
}
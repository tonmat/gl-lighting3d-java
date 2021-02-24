#version 430

layout (location = 0) in vec3 a_position;
layout (location = 1) in uint a_face;
layout (location = 2) in uint a_material;

const vec3 normals[6] = {
vec3(0.0, 0.0, -1.0),
vec3(0.0, 0.0, 1.0),
vec3(1.0, 0.0, 0.0),
vec3(-1.0, 0.0, 0.0),
vec3(0.0, 1.0, 0.0),
vec3(0.0, -1.0, 0.0)
};

uniform mat4 u_view;
uniform mat4 u_vp;
uniform mat3 u_normal;

out vec3 v_position;
out vec3 v_normal;
out flat uint v_material;

void main() {
    gl_Position = u_vp * vec4(a_position, 1.0);
    v_position = (u_view * vec4(a_position, 1.0)).xyz;
    v_normal = (u_normal * normals[a_face]);
    v_material = a_material;
}
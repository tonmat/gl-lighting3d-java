#version 430
#extension GL_ARB_compute_variable_group_size : enable

layout (local_size_variable) in;
layout (rgba32f, binding = 0) uniform readonly image2D in_texture;
layout (rgba32f, binding = 1) uniform writeonly image2D out_texture;

uniform float u_exposure;
uniform float u_gamma;

void main() {
    ivec2 pixcoords = ivec2(gl_GlobalInvocationID.xy);
    vec3 color = imageLoad(in_texture, pixcoords).rgb;
    color = vec3(1.0) - exp(-color * u_exposure);
    color = pow(color, vec3(1.0 / u_gamma));
    imageStore(out_texture, pixcoords, vec4(color, 1.0));
}
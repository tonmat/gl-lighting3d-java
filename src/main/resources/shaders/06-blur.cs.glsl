#version 430
#extension GL_ARB_compute_variable_group_size : enable

layout (local_size_variable) in;
layout (rgba32f, binding = 0) uniform readonly image2D in_texture;
layout (rgba32f, binding = 1) uniform writeonly image2D out_texture;

uniform bool u_horizontal;

const float weights[] = { 0.38774, 0.24477, 0.06136 };

vec4 get_color(ivec2 pixcoords, ivec2 dims) {
    if (pixcoords.x < 0 || pixcoords.y < 0 || pixcoords.x >= dims.x || pixcoords.y >= dims.y) {
        return vec4(0.0);
    }
    return imageLoad(in_texture, pixcoords);
}

void main() {
    ivec2 pixcoords = ivec2(gl_GlobalInvocationID.xy);
    ivec2 dims = imageSize(in_texture);

    vec4 color = imageLoad(in_texture, pixcoords) * weights[0];
    if (u_horizontal) {
        for (int i = 1; i < 3; i++) {
            color += get_color(ivec2(pixcoords.x - i, pixcoords.y), dims) * weights[i];
            color += get_color(ivec2(pixcoords.x + i, pixcoords.y), dims) * weights[i];
        }
    } else {
        for (int i = 1; i < 3; i++) {
            color += get_color(ivec2(pixcoords.x, pixcoords.y - i), dims) * weights[i];
            color += get_color(ivec2(pixcoords.x, pixcoords.y + i), dims) * weights[i];
        }
    }
    imageStore(out_texture, pixcoords, color);
}
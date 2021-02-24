#version 430
#extension GL_ARB_compute_variable_group_size : enable

layout (local_size_variable) in;
layout (rgba32f, binding = 0) uniform readonly image2D in_texture_a;
layout (rgba32f, binding = 1) uniform readonly image2D in_texture_b;
layout (binding = 2) uniform sampler2D in_depth_a;
layout (binding = 3) uniform sampler2D in_depth_b;
layout (rgba32f, binding = 4) uniform writeonly image2D out_texture;

void main() {
    ivec2 pixcoords = ivec2(gl_GlobalInvocationID.xy);
    ivec2 size = imageSize(in_texture_a);
    vec2 texcoords = vec2(pixcoords) / vec2(size);
    vec4 color_a = imageLoad(in_texture_a, pixcoords);
    vec4 color_b = imageLoad(in_texture_b, pixcoords);
    vec4 depth_a = texture(in_depth_a, texcoords);
    vec4 depth_b = texture(in_depth_b, texcoords);

    float da = depth_a.g / depth_a.w;
    float db = depth_b.g / depth_b.w;

    vec4 color;
    if (da < db) {
        color = color_a;
    } else if (da > db) {
        color = color_b;
    } else {
        color = vec4(color_a.rgb * color_a.a + color_b.rgb * color_b.a, max(color_a.a, color_b.a));
    }

    imageStore(out_texture, pixcoords, color);
}
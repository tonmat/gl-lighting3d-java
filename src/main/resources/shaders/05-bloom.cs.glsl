#version 430
#extension GL_ARB_compute_variable_group_size : enable

layout (local_size_variable) in;
layout (rgba32f, binding = 0) uniform readonly image2D in_texture;
layout (rgba32f, binding = 1) uniform writeonly image2D out_brightness;
layout (rgba32f, binding = 2) uniform writeonly image2D out_bloom;

uniform float u_brightness;

void main() {
    ivec2 pixcoords = ivec2(gl_GlobalInvocationID.xy);
    ivec2 dims = imageSize(in_texture);
    vec2 uv = vec2(2 * pixcoords - dims) / vec2(dims);
    float d2 = dot(uv, uv);

    vec4 color = imageLoad(in_texture, pixcoords);
    vec4 brightness = vec4(0.0);
    vec4 bloom = vec4(0.0);

    float luminance = 0.2126 * color.r + 0.7152 * color.g + 0.0722 * color.b;

    if (luminance > 0.0) {
        if (d2 < 0.01) {
            brightness = vec4(vec3(luminance), 1.0);
        } else if (d2 < 0.11) {
            float i = (d2 - 0.01) / 0.1;
            brightness = vec4(vec3(luminance * (1 - i)) + vec3(u_brightness * i), 1.0);
        } else {
            brightness = vec4(vec3(u_brightness), 1.0);
        }

        if (luminance > 1.0)  {
            bloom = vec4(color.xyz, luminance - 1.0);
        }
    } else {
        if (d2 < 0.01) {
        } else if (d2 < 0.11) {
            float i = (d2 - 0.01) / 0.1;
            brightness = vec4(vec3(u_brightness * i), 1.0);
        } else {
            brightness = vec4(vec3(u_brightness), 1.0);
        }
    }

    imageStore(out_brightness, pixcoords, brightness);
    imageStore(out_bloom, pixcoords, bloom);
}
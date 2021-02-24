package com.tonmatsu.raypathing.gl;

import com.tonmatsu.raypathing.utils.*;
import org.joml.*;
import org.lwjgl.opengl.*;

import java.util.*;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.system.MemoryStack.*;

public class ShaderProgram {
    private static ShaderProgram binded;
    private final int program;
    private final ArrayList<Integer> shaders;
    private final HashMap<String, Integer> uniformsLocations;

    public ShaderProgram() {
        program = glCreateProgram();
        shaders = new ArrayList<>();
        uniformsLocations = new HashMap<>();
    }

    public void dispose() {
        glDeleteProgram(program);
    }

    public void bind() {
        if (binded == this)
            return;
        glUseProgram(program);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glUseProgram(GL_NONE);
        binded = null;
    }

    public void attachShader(int type, String asset) {
        final var source = AssetUtils.getString(asset);
        final var shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        final var status = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (status == GL_FALSE) {
            System.err.println("could not compile shader: " + asset);
            System.err.println(glGetShaderInfoLog(shader));
            glDeleteShader(shader);
            return;
        }
        glAttachShader(program, shader);
        shaders.add(shader);
    }

    public void link() {
        glLinkProgram(program);
        shaders.forEach(shader -> {
            glDetachShader(program, shader);
            glDeleteShader(shader);
        });
        final var status = glGetProgrami(program, GL_LINK_STATUS);
        if (status == GL_FALSE) {
            System.err.println("could not link program");
            System.err.println(glGetProgramInfoLog(program));
        }
    }

    private int getUniformLocation(String name) {
        var location = uniformsLocations.get(name);
        if (location == null) {
            location = glGetUniformLocation(program, name);
            uniformsLocations.put(name, location);
            if (location == -1) {
                System.err.println("could not find uniform location: " + name);
            }
        }
        return location;
    }

    public void setUniform1b(String name, boolean b) {
        glProgramUniform1i(program, getUniformLocation(name), b ? GL_TRUE : GL_FALSE);
    }

    public void setUniform1i(String name, int i) {
        glProgramUniform1i(program, getUniformLocation(name), i);
    }

    public void setUniform2i(String name, Vector2i ivec2) {
        glProgramUniform2i(program, getUniformLocation(name), ivec2.x, ivec2.y);
    }

    public void setUniform3i(String name, Vector3i ivec3) {
        glProgramUniform3i(program, getUniformLocation(name), ivec3.x, ivec3.y, ivec3.z);
    }

    public void setUniform4i(String name, Vector4i ivec4) {
        glProgramUniform4i(program, getUniformLocation(name), ivec4.x, ivec4.y, ivec4.z, ivec4.w);
    }

    public void setUniform1f(String name, float f) {
        glProgramUniform1f(program, getUniformLocation(name), f);
    }

    public void setUniform2f(String name, Vector2f vec2) {
        glProgramUniform2f(program, getUniformLocation(name), vec2.x, vec2.y);
    }

    public void setUniform3f(String name, Vector3f vec3) {
        glProgramUniform3f(program, getUniformLocation(name), vec3.x, vec3.y, vec3.z);
    }

    public void setUniform4f(String name, Vector4f vec4) {
        glProgramUniform4f(program, getUniformLocation(name), vec4.x, vec4.y, vec4.z, vec4.w);
    }

    public void setUniformMatrix3f(String name, Matrix3f mat3) {
        try (final var stack = stackPush()) {
            glProgramUniformMatrix3fv(program, getUniformLocation(name), false, mat3.get(stack.mallocFloat(9)));
        }
    }

    public void setUniformMatrix4f(String name, Matrix4f mat4) {
        try (final var stack = stackPush()) {
            glProgramUniformMatrix4fv(program, getUniformLocation(name), false, mat4.get(stack.mallocFloat(16)));
        }
    }
}

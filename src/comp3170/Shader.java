package comp3170;

/**
 * Version 2022.1
 * 
 * 2022.1: Factored into Shader, GLBuffers, and GLTypes to allow shaders to share buffers
 * 
 * @author Malcolm Ryan
 */

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_UNSIGNED_INT;
import static com.jogamp.opengl.GL4.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

public class Shader {

	private static boolean SHADER_DEBUG_MODE = false;
	
	private int program;
	private int vao;
	private Map<String, Integer> attributes;
	private Map<String, Integer> attributeTypes;
	private Map<String, Integer> uniforms;
	private Map<String, Integer> uniformTypes;

	private Map<String, Boolean> trackedAttributeErrors;
	private Map<String, Boolean> trackedUniformErrors;
	
	private FloatBuffer matrix2Buffer = Buffers.newDirectFloatBuffer(4);
	private FloatBuffer matrix3Buffer = Buffers.newDirectFloatBuffer(9);
	private FloatBuffer matrix4Buffer = Buffers.newDirectFloatBuffer(16);

	private FloatBuffer vector2Buffer = Buffers.newDirectFloatBuffer(2);
	private FloatBuffer vector3Buffer = Buffers.newDirectFloatBuffer(3);
	private FloatBuffer vector4Buffer = Buffers.newDirectFloatBuffer(4);

	/**
	 * Compile and link a vertex and fragment shader
	 * 
	 * @param vertexShaderFile	The vertex shader source file
	 * @param fragmentShaderFile	The fragment shader source file
	 * @throws IOException
	 * @throws GLException
	 */

	public Shader(File vertexShaderFile, File fragmentShaderFile) throws IOException, GLException {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		// compile the shaders

		int vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderFile);
		int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderFile);

		// link the shaders

		this.program = gl.glCreateProgram();
		gl.glAttachShader(this.program, vertexShader);
		gl.glAttachShader(this.program, fragmentShader);
		gl.glLinkProgram(program);
		GLException.checkGLErrors();
		
		// delete the shaders after linking
		
		gl.glDetachShader(this.program, vertexShader);
		gl.glDetachShader(this.program, fragmentShader);
		gl.glDeleteShader(vertexShader);
		gl.glDeleteShader(fragmentShader);

		// check for linker errors

		int[] linked = new int[1];
		gl.glGetProgramiv(this.program, GL_LINK_STATUS, linked, 0);
		if (linked[0] != 1) {
			int[] maxlen = new int[1];
			int[] len = new int[1];
			byte[] log = null;
			String logString = "";

			// determine length of the program compilation log
			gl.glGetProgramiv(this.program, GL_INFO_LOG_LENGTH, maxlen, 0);

			if (maxlen[0] > 0) {
				log = new byte[maxlen[0]];

				gl.glGetProgramInfoLog(this.program, maxlen[0], len, 0, log, 0);
				logString = new String(log);
			}

			String message = String.format("Link failed: %s\n", logString);
			throw new GLException(message);
		}

		// create VAO

		int buffer[] = new int[1];
		gl.glGenVertexArrays(1, buffer, 0);
		this.vao = buffer[0];

		// record attribute and uniforms

		recordAttributes();
		recordUniforms();
	}

	/**
	 * Check if the shader has a particular attribute
	 * 
	 * @param name	The name of the attribute
	 * @return true if the shader has an attribute with the name provided
	 */

	public boolean hasAttribute(String name) {
		return this.attributes.containsKey(name);
	}

	/**
	 * Check if the shader has a particular uniform
	 * 
	 * @param name	The name of the uniform
	 * @return true if the shader has a uniform with the name provided
	 */

	public boolean hasUniform(String name) {
		return this.uniforms.containsKey(name);
	}

	/**
	 * Get the handle for an attribute
	 * 
	 * @param name	The name of the attribute
	 * @return	The OpenGL handle for the attribute
	 */

	public int getAttribute(String name) {
		if (!this.attributes.containsKey(name)) {
			String message = String.format("Unknown attribute: '%s'", name);
			if(!SHADER_DEBUG_MODE) {
				throw new IllegalArgumentException(String.format("%s\nTo prevent crashes, call 'Shader.setDebugMode(true);' in your program.", message));
			} else if(!this.trackedAttributeErrors.containsKey(name)) {
				System.err.println(message);
				this.trackedAttributeErrors.put(name, true);
			}
			return -1;
		}

		return this.attributes.get(name);
	}

	/**
	 * Get the handle for a uniform
	 * 
	 * @param name	The name of the uniform
	 * @return	The OpenGL handle for the uniform 
	 */

	public int getUniform(String name) {
		if (!this.uniforms.containsKey(name)) {
			String message = String.format("Unknown uniform: '%s'", name);
			if(!SHADER_DEBUG_MODE) {
				throw new IllegalArgumentException(String.format("%s\nTo prevent crashes, call 'Shader.setDebugMode(true);' in your program.", message));
			} else if(!this.trackedUniformErrors.containsKey(name)) {
				System.err.println(message);
				this.trackedUniformErrors.put(name, true);
			}
			return -1;
		}

		return this.uniforms.get(name);
	}

	/**
	 * Enable the shader
	 */

	public void enable() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glUseProgram(this.program);
		gl.glBindVertexArray(this.vao);
	}


	/**
	 * Create a render texture with the specified dimensions
	 * 
	 * @param width	Texture width in pixels
	 * @param height	Texture height in pixels
	 * @return	The OpenGL handle to the render texture.
	 */

	public static int createRenderTexture(int width, int height) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] renderTexture = new int[1];
		gl.glGenTextures(1, renderTexture, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, renderTexture[0]);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, width, height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, null);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		
		return renderTexture[0];
	}

	/**
	 * Create a render texture to act as a depth buffer
	 * 
	 * @param width	Texture width in pixels
	 * @param height	Texture height in pixels
	 * @return	The OpenGL handle to the render texture.
	 */

	public static int createDepthTexture(int width, int height) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] renderTexture = new int[1];
		gl.glGenTextures(1, renderTexture, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, renderTexture[0]);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL4.GL_DEPTH_COMPONENT, width, height, 0, GL4.GL_DEPTH_COMPONENT, GL.GL_UNSIGNED_BYTE, null);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		
		return renderTexture[0];
	}

	/**
	 * Create a framebuffer that writes colours to the renderTexture given.
	 * 
	 * @param renderTexture	A render texture in which to store the colour buffer
	 * @return	The OpenGL handle to the frame buffer
	 * @throws GLException
	 */
	
	public static int createFrameBuffer(int renderTexture) throws GLException {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		int[] width = new int[1];
		int[] height = new int[1];
		gl.glGetTexLevelParameteriv(GL.GL_TEXTURE_2D, 0, GL4.GL_TEXTURE_WIDTH, width, 0);
		gl.glGetTexLevelParameteriv(GL.GL_TEXTURE_2D, 0, GL4.GL_TEXTURE_HEIGHT, height, 0);
		
		int[] framebufferName = new int[1];
		gl.glGenFramebuffers(1, framebufferName, 0);
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, framebufferName[0]);		
		
		int[] depthrenderbuffer = new int[1];
		gl.glGenRenderbuffers(1, depthrenderbuffer, 0);
		gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, depthrenderbuffer[0]);
		gl.glRenderbufferStorage(GL.GL_RENDERBUFFER, GL4.GL_DEPTH_COMPONENT, width[0], height[0]);
		gl.glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER, GL.GL_DEPTH_ATTACHMENT, GL.GL_RENDERBUFFER, depthrenderbuffer[0]);

		gl.glFramebufferTexture(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, renderTexture, 0);

		int[] drawBuffers = new int[] { GL4.GL_COLOR_ATTACHMENT0 };
		gl.glDrawBuffers(drawBuffers.length, drawBuffers, 0);
		
		if (gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER) != GL.GL_FRAMEBUFFER_COMPLETE) {
			GLException.checkGLErrors();
			throw new GLException("Failed to create framebuffer");
		}

		return framebufferName[0];
	}
	
	public static int createFrameBuffer(Integer colourTexture, Integer depthTexture) throws GLException {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		int[] width = new int[1];
		int[] height = new int[1];
		gl.glGetTexLevelParameteriv(GL.GL_TEXTURE_2D, 0, GL4.GL_TEXTURE_WIDTH, width, 0);
		gl.glGetTexLevelParameteriv(GL.GL_TEXTURE_2D, 0, GL4.GL_TEXTURE_HEIGHT, height, 0);
		
		int[] framebufferName = new int[1];
		gl.glGenFramebuffers(1, framebufferName, 0);
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, framebufferName[0]);		

		int[] drawBuffers = new int[] { GL4.GL_COLOR_ATTACHMENT0 };
		gl.glDrawBuffers(drawBuffers.length, drawBuffers, 0);

		if (colourTexture == null ) {
			// no colour buffer
			gl.glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, GL.GL_RENDERBUFFER, 0);						
		}
		else {
			gl.glFramebufferTexture(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, colourTexture, 0);
		}

		if (depthTexture == null) {
			// default depth buffer
			int[] depthrenderbuffer = new int[1];
			gl.glGenRenderbuffers(1, depthrenderbuffer, 0);
			gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, depthrenderbuffer[0]);
			gl.glRenderbufferStorage(GL.GL_RENDERBUFFER, GL4.GL_DEPTH_COMPONENT, width[0], height[0]);
			gl.glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER, GL.GL_DEPTH_ATTACHMENT, GL.GL_RENDERBUFFER, depthrenderbuffer[0]);
		}
		else {
			gl.glFramebufferTexture(GL.GL_FRAMEBUFFER, GL.GL_DEPTH_ATTACHMENT, depthTexture, 0);
		}

		
		if (gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER) != GL.GL_FRAMEBUFFER_COMPLETE) {
			GLException.checkGLErrors();
			throw new GLException("Failed to create framebuffer");
		}

		return framebufferName[0];
	}

	
	/**
	 * Connect a buffer to a shader attribute
	 * 
	 * @param attributeName The name of the shader attribute
	 * @param buffer        The buffer
	 */
	public void setAttribute(String attributeName, int buffer) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int attribute = getAttribute(attributeName);
		if(attribute < 0) return;
		
		int type = attributeTypes.get(attributeName);
		GLBuffers.checkType(buffer, type);

		int size = GLTypes.typeSize(type);
		int elementType = GLTypes.elementType(type);

		gl.glBindBuffer(GL_ARRAY_BUFFER, buffer);
		gl.glVertexAttribPointer(attribute, size, elementType, false, 0, 0);
		gl.glEnableVertexAttribArray(attribute);
	}

	/**
	 * Set the value of a uniform to a boolean
	 * 
	 * @param uniformName The GLSL uniform
	 * @param value       The int value
	 */
	public void setUniform(String uniformName, boolean value) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int uniform = getUniform(uniformName);
		if(uniform < 0) return;
		
		int type = uniformTypes.get(uniformName);

		switch (type) {
		case GL_BOOL:
			gl.glUniform1ui(uniform, value ? 1 : 0);
			break;
		default:
			throw new IllegalArgumentException(String.format("Expected %s got boolean", GLTypes.typeName(type)));			
		}	
	}

	/**
	 * Set the value of a uniform to an int
	 * 
	 * @param uniformName The GLSL uniform
	 * @param value       The int value
	 */
	public void setUniform(String uniformName, int value) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int uniform = getUniform(uniformName);
		if(uniform < 0) return;
		
		int type = uniformTypes.get(uniformName);

		switch (type) {
		case GL_UNSIGNED_INT:
			gl.glUniform1ui(uniform, value);
			break;
		case GL_INT:
		case GL_SAMPLER_2D:
			gl.glUniform1i(uniform, value);
			break;			
		default:
			throw new IllegalArgumentException(String.format("Expected %s got int", GLTypes.typeName(type)));			
		}	
	}

	/**
	 * Set the value of a uniform to a float
	 * 
	 * @param uniformName The GLSL uniform
	 * @param value       The float value
	 */
	public void setUniform(String uniformName, float value) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int uniform = getUniform(uniformName);
		if(uniform < 0) return;
		
		int type = uniformTypes.get(uniformName);

		if (type != GL_FLOAT) {
			throw new IllegalArgumentException(String.format("Expected %s got float", GLTypes.typeName(type)));
		}

		gl.glUniform1f(uniform, value);
	}

	/**
	 * Set the value of a uniform to an array of int
	 * 
	 * This works for GLSL types float, vec2, vec3, vec4, mat2, mat3 and mat4.
	 * 
	 * Note that for matrix types, the elements should be specified in column order
	 * 
	 * @param uniformName
	 * @param value
	 */
	public void setUniform(String uniformName, int[] value) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int uniform = getUniform(uniformName);
		if(uniform < 0) return;
		
		int type = uniformTypes.get(uniformName);
		int expectedArgs = GLTypes.typeSize(type);

		if (value.length != expectedArgs) {
			throw new IllegalArgumentException(
					String.format("Expected %s got int[%d]", GLTypes.typeName(type), value.length));
		}

		switch (type) {
		case GL_INT:
			gl.glUniform1i(uniform, value[0]);
			break;
		case GL_INT_VEC2:
			gl.glUniform2i(uniform, value[0], value[1]);
			break;
		case GL_INT_VEC3:
			gl.glUniform3i(uniform, value[0], value[1], value[2]);
			break;
		case GL_INT_VEC4:
			gl.glUniform4i(uniform, value[0], value[1], value[2], value[4]);
			break;
		case GL_UNSIGNED_INT:
			gl.glUniform1ui(uniform, value[0]);
			break;
		case GL_UNSIGNED_INT_VEC2:
			gl.glUniform2ui(uniform, value[0], value[1]);
			break;
		case GL_UNSIGNED_INT_VEC3:
			gl.glUniform3ui(uniform, value[0], value[1], value[2]);
			break;
		case GL_UNSIGNED_INT_VEC4:
			gl.glUniform4ui(uniform, value[0], value[1], value[2], value[4]);
			break;
		default:
			throw new IllegalArgumentException(
					String.format("Cannot convert int array to %s", GLTypes.typeName(type)));
		}
	}

	/**
	 * Set the value of a uniform to an array of floats
	 * 
	 * This works for GLSL types float, vec2, vec3, vec4, mat2, mat3 and mat4.
	 * 
	 * Note that for matrix types, the elements should be specified in column order
	 * 
	 * @param uniformName
	 * @param value
	 */
	public void setUniform(String uniformName, float[] value) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int uniform = getUniform(uniformName);
		if(uniform < 0) return;
		
		int type = uniformTypes.get(uniformName);
		int expectedArgs = GLTypes.typeSize(type);

		if (value.length != expectedArgs) {
			throw new IllegalArgumentException(
					String.format("Expected %s got float[%d]", GLTypes.typeName(type), value.length));
		}

		switch (type) {
		case GL_FLOAT:
			gl.glUniform1f(uniform, value[0]);
			break;
		case GL_FLOAT_VEC2:
			gl.glUniform2f(uniform, value[0], value[1]);
			break;
		case GL_FLOAT_VEC3:
			gl.glUniform3f(uniform, value[0], value[1], value[2]);
			break;
		case GL_FLOAT_VEC4:
			gl.glUniform4f(uniform, value[0], value[1], value[2], value[3]);
			break;
		case GL_FLOAT_MAT2:
			gl.glUniformMatrix2fv(uniform, 1, false, value, 0);
			break;
		case GL_FLOAT_MAT3:
			gl.glUniformMatrix3fv(uniform, 1, false, value, 0);
			break;
		case GL_FLOAT_MAT4:
			gl.glUniformMatrix4fv(uniform, 1, false, value, 0);
			break;
		default:
			throw new IllegalArgumentException(
					String.format("Cannot convert float array to %s", GLTypes.typeName(type)));
			
		}

	}

	/**
	 * Set a uniform of type vec2 to a Vector2f value
	 * 
	 * @param uniformName the uniform to set
	 * @param vector      the vector value to send
	 */

	public void setUniform(String uniformName, Vector2f vector) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int uniform = getUniform(uniformName);
		if(uniform < 0) return;
		
		int type = uniformTypes.get(uniformName);

		if (type != GL_FLOAT_VEC2) {
			throw new IllegalArgumentException(String.format("Expected %s got Vector2f", GLTypes.typeName(type)));
		}

		gl.glUniform2fv(uniform, 1, vector.get(vector2Buffer));
	}

	/**
	 * Set a uniform of type vec3 to a Vector3f value
	 * 
	 * @param uniformName the uniform to set
	 * @param vector      the vector value to send
	 */

	public void setUniform(String uniformName, Vector3f vector) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int uniform = getUniform(uniformName);
		if(uniform < 0) return;
		
		int type = uniformTypes.get(uniformName);

		if (type != GL_FLOAT_VEC3) {
			throw new IllegalArgumentException(String.format("Expected %s got Vector3f", GLTypes.typeName(type)));
		}

		gl.glUniform3fv(uniform, 1, vector.get(vector3Buffer));
	}

	/**
	 * Set a uniform of type vec4 to a Vector4f value
	 * 
	 * @param uniformName the uniform to set
	 * @param vector      the vector value to send
	 */

	public void setUniform(String uniformName, Vector4f vector) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int uniform = getUniform(uniformName);
		if(uniform < 0) return;
		
		int type = uniformTypes.get(uniformName);

		if (type != GL_FLOAT_VEC4) {
			throw new IllegalArgumentException(String.format("Expected %s got Vector4f", GLTypes.typeName(type)));
		}

		gl.glUniform4fv(uniform, 1, vector.get(vector4Buffer));
	}

	/**
	 * Set a uniform of type mat2 to a Matrix2f value
	 * 
	 * @param uniformName the uniform to set
	 * @param matrix      the matrix value to send
	 */

	public void setUniform(String uniformName, Matrix2f matrix) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int uniform = getUniform(uniformName);
		if(uniform < 0) return;
		
		int type = uniformTypes.get(uniformName);

		if (type != GL_FLOAT_MAT2) {
			throw new IllegalArgumentException(String.format("Expected %s got Matrix2f", GLTypes.typeName(type)));
		}

		gl.glUniformMatrix2fv(uniform, 1, false, matrix.get(matrix2Buffer));
	}

	/**
	 * Set a uniform of type mat3 to a Matrix3f value
	 * 
	 * @param uniformName the uniform to set
	 * @param matrix      the matrix value to send
	 */

	public void setUniform(String uniformName, Matrix3f matrix) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int uniform = getUniform(uniformName);
		if(uniform < 0) return;
		
		int type = uniformTypes.get(uniformName);

		if (type != GL_FLOAT_MAT3) {
			throw new IllegalArgumentException(String.format("Expected %s got Matrix3f", GLTypes.typeName(type)));
		}

		gl.glUniformMatrix3fv(uniform, 1, false, matrix.get(matrix3Buffer));
	}

	/**
	 * Set a uniform of type mat4 to a Matrix4 value
	 * 
	 * @param uniformName the uniform to set
	 * @param matrix      the matrix value to send
	 */

	public void setUniform(String uniformName, Matrix4f matrix) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int uniform = getUniform(uniformName);
		if(uniform < 0) return;
		
		int type = uniformTypes.get(uniformName);

		if (type != GL_FLOAT_MAT4) {
			throw new IllegalArgumentException(String.format("Expected %s got Matrix4f", GLTypes.typeName(type)));
		}

		gl.glUniformMatrix4fv(uniform, 1, false, matrix.get(matrix4Buffer));
	}

	// ===================
	// PRIVATE METHODS
	// ===================
	

	/**
	 * Establish the mapping from attribute names to IDs
	 */

	private void recordAttributes() {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		this.attributes = new HashMap<String, Integer>();
		this.attributeTypes = new HashMap<String, Integer>();
		this.trackedAttributeErrors = new HashMap<String, Boolean>();

		int[] iBuff = new int[1];
		gl.glGetProgramiv(this.program, GL_ACTIVE_ATTRIBUTES, iBuff, 0);
		int activeAttributes = iBuff[0];

		gl.glGetProgramiv(this.program, GL_ACTIVE_ATTRIBUTE_MAX_LENGTH, iBuff, 0);
		int maxNameSize = iBuff[0];

		byte[] nameBuffer = new byte[maxNameSize];

		int[] sizeBuffer = new int[1];
		int[] typeBuffer = new int[1];
		int[] nameLenBuffer = new int[1];
		for (int i = 0; i < activeAttributes; ++i) {
			gl.glGetActiveAttrib(this.program, i, maxNameSize, nameLenBuffer, 0, sizeBuffer, 0, typeBuffer, 0,
					nameBuffer, 0);
			String name = new String(nameBuffer, 0, nameLenBuffer[0]);

			this.attributes.put(name, gl.glGetAttribLocation(this.program, name));
			this.attributeTypes.put(name, typeBuffer[0]);
		}
	}

	/**
	 * Establish the mapping from uniform names to IDs
	 */

	private void recordUniforms() {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		this.uniforms = new HashMap<String, Integer>();
		this.uniformTypes = new HashMap<String, Integer>();
		this.trackedUniformErrors = new HashMap<String, Boolean>();

		int[] iBuff = new int[1];
		gl.glGetProgramiv(this.program, GL_ACTIVE_UNIFORMS, iBuff, 0);
		int activeUniforms = iBuff[0];

		gl.glGetProgramiv(this.program, GL_ACTIVE_UNIFORM_MAX_LENGTH, iBuff, 0);
		int maxNameSize = iBuff[0];

		byte[] nameBuffer = new byte[maxNameSize];

		int[] sizeBuffer = new int[1];
		int[] typeBuffer = new int[1];
		int[] nameLenBuffer = new int[1];
		for (int i = 0; i < activeUniforms; ++i) {
			gl.glGetActiveUniform(this.program, i, maxNameSize, nameLenBuffer, 0, sizeBuffer, 0, typeBuffer, 0,
					nameBuffer, 0);
			String name = new String(nameBuffer, 0, nameLenBuffer[0]);

			this.uniforms.put(name, gl.glGetUniformLocation(this.program, name));
			this.uniformTypes.put(name, typeBuffer[0]);
		}
	}

	/**
	 * Read source code from a shader file.
	 * 
	 * @param shaderFile
	 * @return
	 * @throws IOException
	 */
	private static String[] readSource(File shaderFile) throws IOException {
		ArrayList<String> source = new ArrayList<String>();
		BufferedReader in = null;

		try {
			in = new BufferedReader(new FileReader(shaderFile));

			for (String line = in.readLine(); line != null; line = in.readLine()) {
				source.add(line + "\n");
			}

		} catch (IOException e) {
			throw e;
		} finally {
			if (in != null) {
				in.close();
			}
		}

		String[] lines = new String[source.size()];
		return source.toArray(lines);
	}

	/**
	 * Compile a shader
	 * 
	 * @param type
	 * @param sourceFile
	 * @return
	 * @throws GLException
	 * @throws IOException
	 */

	private static int compileShader(int type, File sourceFile) throws GLException, IOException {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		String[] source = readSource(sourceFile);

		int shader = gl.glCreateShader(type);
		gl.glShaderSource(shader, source.length, source, null, 0);
		gl.glCompileShader(shader);
		GLException.checkGLErrors();

		// check compilation

		int[] compiled = new int[1];
		gl.glGetShaderiv(shader, GL_COMPILE_STATUS, compiled, 0);
		String logString = "";

		if (compiled[0] != 1) {

			int[] maxlen = new int[1];
			gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, maxlen, 0);

			if (maxlen[0] > 0) {
				int[] len = new int[1];
				byte[] log = null;

				log = new byte[maxlen[0]];
				gl.glGetShaderInfoLog(shader, maxlen[0], len, 0, log, 0);
				logString = new String(log);
			}

			// delete the shader if the compilation failed
			gl.glDeleteShader(shader);
			
			String message = String.format("%s: %s compilation error\n%s", sourceFile.getName(), shaderType(type), logString);
			throw new GLException(message);
		}

		return shader;
	}

	/**
	 * Enables/disables debug mode which will determine if missing uniform/attributes
	 * will throw an error or just print a message.
	 * @param enabled Whether debug mode is enabled or disabled
	 */
	public static void setDebugMode(boolean enabled) {
		SHADER_DEBUG_MODE = enabled;
	}
	
	/**
	 * Turn a shader type constant into a descriptive string.
	 * 
	 * @param type
	 * @return
	 */
	private static String shaderType(int type) {
		switch (type) {
		case GL_VERTEX_SHADER:
			return "Vertex shader";
		case GL_FRAGMENT_SHADER:
			return "Fragment shader";
		// adding these for completeness, not because they're used
		case GL_GEOMETRY_SHADER:
			return "Geometry shader";
		case GL_COMPUTE_SHADER:
			return "Compute shader";
		case GL_TESS_CONTROL_SHADER:
			return "Tessellation control shader";
		case GL_TESS_EVALUATION_SHADER:
			return "Tessellation evaluation shader";
		}
		return "Unknown shader";
	}
	
}

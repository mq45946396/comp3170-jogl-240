package comp3170;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL2ES2.GL_FLOAT_VEC2;
import static com.jogamp.opengl.GL2ES2.GL_FLOAT_VEC3;
import static com.jogamp.opengl.GL2ES2.GL_FLOAT_VEC4;
import static com.jogamp.opengl.GL2ES2.GL_INT;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

/**
 * Version 2022.1
 * 
 * 2022.1: Factored buffer code out of Shader to allow shaders to share buffers
 * 
 * @author Malcolm Ryan
 */
public class GLBuffers {
	static private Map<Integer, Integer> bufferTypes = new HashMap<Integer,Integer>();

	/**
	 * Get the GL type of a buffer.
	 * @param buffer	An allocated buffer
	 * @return the type value
	 * @throws IllegalArgumentException if the buffer has not been allocated.
	 */
	
	static public int getType(int buffer) {
		if (bufferTypes.containsKey(buffer)) {
			return bufferTypes.get(buffer);
		}
		
		throw new IllegalArgumentException(String.format("Buffer %d has not been allocated.", buffer));
	}
	
	/**
	 * Check the type of a buffer matches the expected type
	 * @param buffer	An allocated buffer
	 * @param type		The expected type
	 * @return true if the buffer type matches the expected type
	 * @throws IllegalArgumentException if the types do not match.
	 */
	static public boolean checkType(int buffer, int type) {
		if (GLBuffers.getType(buffer) != type) {
			throw new IllegalArgumentException(String.format("Expected buffer of type %s, got %s.", 
					GLTypes.typeName(type),
					GLTypes.typeName(GLBuffers.getType(buffer))));
		}

		return true;
	}

	/**
	 * Create a new VBO (vertex buffer object) in graphics memory and copy data into
	 * it
	 * 
	 * @param data The data as an array of floats
	 * @param type The type of data in this buffer
	 * @return	The OpenGL handle to the VBO
	 */
	
	static public int createBuffer(float[] data, int type) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] buffers = new int[1];
		gl.glGenBuffers(buffers.length, buffers, 0);

		FloatBuffer buffer = Buffers.newDirectFloatBuffer(data);
		gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
		gl.glBufferData(GL_ARRAY_BUFFER, data.length * Buffers.SIZEOF_FLOAT, buffer, GL_STATIC_DRAW);

		bufferTypes.put(buffers[0], type);
		if (data.length % GLTypes.typeSize(type) != 0) {
			System.err.println(
					String.format("Warning: buffer of type %s has length which is not a mutliple of %d.",
					GLTypes.typeName(type), GLTypes.typeSize(type)));
		}

		return buffers[0];
	}

	/**
	 * Create a new VBO (vertex buffer object) in graphics memory and copy data into
	 * it from a FloatBuffer
	 * 
	 * @param buffer A FloatBuffer containing the data
	 * @param type The type of data in this buffer
	 * @return	The OpenGL handle to the VBO
	 */
	static public int createBuffer(FloatBuffer buffer, int type) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] buffers = new int[1];
		gl.glGenBuffers(buffers.length, buffers, 0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
		gl.glBufferData(GL_ARRAY_BUFFER, buffer.limit() * Buffers.SIZEOF_FLOAT, buffer, GL_STATIC_DRAW);

		bufferTypes.put(buffers[0], type);
		if (buffer.limit() % GLTypes.typeSize(type) != 0) {
			System.err.println(
					String.format("Warning: buffer of type %s has length which is not a mutliple of %d.",
							GLTypes.typeName(type), GLTypes.typeSize(type)));
		}

		return buffers[0];
	}

	/**
	 * Create a new VBO (vertex buffer object) in graphics memory and copy data into
	 * it
	 * 
	 * @param data The data as an array of Vector2f
	 * @return	The OpenGL handle to the VBO
	 */
	static public int createBuffer(Vector2f[] data) {
		// this is a hack, but I can't get it to work otherwise
		float[] array = new float[2 * data.length];
		int j = 0;
		for (int i = 0; i < data.length; i++) {
			array[j++] = data[i].x;
			array[j++] = data[i].y;
		}

		return createBuffer(array, GL_FLOAT_VEC2);
	}

	/**
	 * Create a new VBO (vertex buffer object) in graphics memory and copy data into
	 * it
	 * 
	 * @param data The data as an array of Vector3f
	 * @return	The OpenGL handle to the VBO
	 */
	static public int createBuffer(Vector3f[] data) {
		// this is a hack, but I can't get it to work otherwise
		float[] array = new float[3 * data.length];
		int j = 0;
		for (int i = 0; i < data.length; i++) {
			array[j++] = data[i].x;
			array[j++] = data[i].y;
			array[j++] = data[i].z;
		}

		return createBuffer(array, GL_FLOAT_VEC3);
	}

	/**
	 * Create a new VBO (vertex buffer object) in graphics memory and copy data into
	 * it. 
	 * 
	 * @param data The data as an array of Vector4f
	 * @return	The OpenGL handle to the VBO
	 */
	static public int createBuffer(Vector4f[] data) {
		// this is a hack, but I can't get it to work otherwise
		float[] array = new float[4 * data.length];
		int j = 0;
		for (int i = 0; i < data.length; i++) {
			array[j++] = data[i].x;
			array[j++] = data[i].y;
			array[j++] = data[i].z;
			array[j++] = data[i].w;
		}

		return createBuffer(array, GL_FLOAT_VEC4);
	}

	/**
	 * Create a new index buffer and initialise it
	 * 
	 * @param indices The indices as an array of ints
	 * @return	The OpenGL handle to the index buffer
	 */
	static public int createIndexBuffer(int[] indices) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] buffers = new int[1];
		gl.glGenBuffers(buffers.length, buffers, 0);

		IntBuffer buffer = Buffers.newDirectIntBuffer(indices);
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[0]);
		gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.length * Buffers.SIZEOF_INT, buffer, GL_STATIC_DRAW);

		bufferTypes.put(buffers[0], GL_INT);

		return buffers[0];
	}
	
}

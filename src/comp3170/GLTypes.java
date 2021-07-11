package comp3170;

import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_UNSIGNED_INT;
import static com.jogamp.opengl.GL2ES2.GL_BOOL;
import static com.jogamp.opengl.GL2ES2.GL_BOOL_VEC2;
import static com.jogamp.opengl.GL2ES2.GL_BOOL_VEC3;
import static com.jogamp.opengl.GL2ES2.GL_BOOL_VEC4;
import static com.jogamp.opengl.GL2ES2.GL_FLOAT_MAT2;
import static com.jogamp.opengl.GL2ES2.GL_FLOAT_MAT3;
import static com.jogamp.opengl.GL2ES2.GL_FLOAT_MAT4;
import static com.jogamp.opengl.GL2ES2.GL_FLOAT_VEC2;
import static com.jogamp.opengl.GL2ES2.GL_FLOAT_VEC3;
import static com.jogamp.opengl.GL2ES2.GL_FLOAT_VEC4;
import static com.jogamp.opengl.GL2ES2.GL_INT;
import static com.jogamp.opengl.GL2ES2.GL_INT_SAMPLER_2D_MULTISAMPLE;
import static com.jogamp.opengl.GL2ES2.GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY;
import static com.jogamp.opengl.GL2ES2.GL_INT_VEC2;
import static com.jogamp.opengl.GL2ES2.GL_INT_VEC3;
import static com.jogamp.opengl.GL2ES2.GL_INT_VEC4;
import static com.jogamp.opengl.GL2ES2.GL_SAMPLER_2D;
import static com.jogamp.opengl.GL2ES2.GL_SAMPLER_2D_MULTISAMPLE;
import static com.jogamp.opengl.GL2ES2.GL_SAMPLER_2D_MULTISAMPLE_ARRAY;
import static com.jogamp.opengl.GL2ES2.GL_SAMPLER_2D_SHADOW;
import static com.jogamp.opengl.GL2ES2.GL_SAMPLER_3D;
import static com.jogamp.opengl.GL2ES2.GL_SAMPLER_CUBE;
import static com.jogamp.opengl.GL2ES2.GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE;
import static com.jogamp.opengl.GL2ES2.GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY;
import static com.jogamp.opengl.GL2ES3.GL_FLOAT_MAT2x3;
import static com.jogamp.opengl.GL2ES3.GL_FLOAT_MAT2x4;
import static com.jogamp.opengl.GL2ES3.GL_FLOAT_MAT3x2;
import static com.jogamp.opengl.GL2ES3.GL_FLOAT_MAT3x4;
import static com.jogamp.opengl.GL2ES3.GL_FLOAT_MAT4x2;
import static com.jogamp.opengl.GL2ES3.GL_FLOAT_MAT4x3;
import static com.jogamp.opengl.GL2ES3.GL_IMAGE_2D;
import static com.jogamp.opengl.GL2ES3.GL_IMAGE_2D_ARRAY;
import static com.jogamp.opengl.GL2ES3.GL_IMAGE_3D;
import static com.jogamp.opengl.GL2ES3.GL_IMAGE_BUFFER;
import static com.jogamp.opengl.GL2ES3.GL_IMAGE_CUBE;
import static com.jogamp.opengl.GL2ES3.GL_INT_IMAGE_2D;
import static com.jogamp.opengl.GL2ES3.GL_INT_IMAGE_2D_ARRAY;
import static com.jogamp.opengl.GL2ES3.GL_INT_IMAGE_3D;
import static com.jogamp.opengl.GL2ES3.GL_INT_IMAGE_BUFFER;
import static com.jogamp.opengl.GL2ES3.GL_INT_IMAGE_CUBE;
import static com.jogamp.opengl.GL2ES3.GL_INT_SAMPLER_2D;
import static com.jogamp.opengl.GL2ES3.GL_INT_SAMPLER_2D_ARRAY;
import static com.jogamp.opengl.GL2ES3.GL_INT_SAMPLER_3D;
import static com.jogamp.opengl.GL2ES3.GL_INT_SAMPLER_BUFFER;
import static com.jogamp.opengl.GL2ES3.GL_INT_SAMPLER_CUBE;
import static com.jogamp.opengl.GL2ES3.GL_SAMPLER_2D_ARRAY;
import static com.jogamp.opengl.GL2ES3.GL_SAMPLER_2D_ARRAY_SHADOW;
import static com.jogamp.opengl.GL2ES3.GL_SAMPLER_BUFFER;
import static com.jogamp.opengl.GL2ES3.GL_SAMPLER_CUBE_SHADOW;
import static com.jogamp.opengl.GL2ES3.GL_UNSIGNED_INT_ATOMIC_COUNTER;
import static com.jogamp.opengl.GL2ES3.GL_UNSIGNED_INT_IMAGE_2D;
import static com.jogamp.opengl.GL2ES3.GL_UNSIGNED_INT_IMAGE_2D_ARRAY;
import static com.jogamp.opengl.GL2ES3.GL_UNSIGNED_INT_IMAGE_3D;
import static com.jogamp.opengl.GL2ES3.GL_UNSIGNED_INT_IMAGE_BUFFER;
import static com.jogamp.opengl.GL2ES3.GL_UNSIGNED_INT_IMAGE_CUBE;
import static com.jogamp.opengl.GL2ES3.GL_UNSIGNED_INT_SAMPLER_2D;
import static com.jogamp.opengl.GL2ES3.GL_UNSIGNED_INT_SAMPLER_2D_ARRAY;
import static com.jogamp.opengl.GL2ES3.GL_UNSIGNED_INT_SAMPLER_3D;
import static com.jogamp.opengl.GL2ES3.GL_UNSIGNED_INT_SAMPLER_BUFFER;
import static com.jogamp.opengl.GL2ES3.GL_UNSIGNED_INT_SAMPLER_CUBE;
import static com.jogamp.opengl.GL2ES3.GL_UNSIGNED_INT_VEC2;
import static com.jogamp.opengl.GL2ES3.GL_UNSIGNED_INT_VEC3;
import static com.jogamp.opengl.GL2ES3.GL_UNSIGNED_INT_VEC4;
import static com.jogamp.opengl.GL2GL3.GL_DOUBLE;
import static com.jogamp.opengl.GL2GL3.GL_INT_SAMPLER_1D;
import static com.jogamp.opengl.GL2GL3.GL_INT_SAMPLER_1D_ARRAY;
import static com.jogamp.opengl.GL2GL3.GL_INT_SAMPLER_2D_RECT;
import static com.jogamp.opengl.GL2GL3.GL_SAMPLER_1D;
import static com.jogamp.opengl.GL2GL3.GL_SAMPLER_1D_ARRAY;
import static com.jogamp.opengl.GL2GL3.GL_SAMPLER_1D_ARRAY_SHADOW;
import static com.jogamp.opengl.GL2GL3.GL_SAMPLER_1D_SHADOW;
import static com.jogamp.opengl.GL2GL3.GL_SAMPLER_2D_RECT;
import static com.jogamp.opengl.GL2GL3.GL_SAMPLER_2D_RECT_SHADOW;
import static com.jogamp.opengl.GL2GL3.GL_UNSIGNED_INT_SAMPLER_1D;
import static com.jogamp.opengl.GL2GL3.GL_UNSIGNED_INT_SAMPLER_1D_ARRAY;
import static com.jogamp.opengl.GL2GL3.GL_UNSIGNED_INT_SAMPLER_2D_RECT;
import static com.jogamp.opengl.GL3.GL_DOUBLE_MAT2;
import static com.jogamp.opengl.GL3.GL_DOUBLE_MAT2x3;
import static com.jogamp.opengl.GL3.GL_DOUBLE_MAT2x4;
import static com.jogamp.opengl.GL3.GL_DOUBLE_MAT3;
import static com.jogamp.opengl.GL3.GL_DOUBLE_MAT3x2;
import static com.jogamp.opengl.GL3.GL_DOUBLE_MAT3x4;
import static com.jogamp.opengl.GL3.GL_DOUBLE_MAT4;
import static com.jogamp.opengl.GL3.GL_DOUBLE_MAT4x2;
import static com.jogamp.opengl.GL3.GL_DOUBLE_MAT4x3;
import static com.jogamp.opengl.GL3.GL_DOUBLE_VEC2;
import static com.jogamp.opengl.GL3.GL_DOUBLE_VEC3;
import static com.jogamp.opengl.GL3.GL_DOUBLE_VEC4;
import static com.jogamp.opengl.GL3.GL_IMAGE_1D;
import static com.jogamp.opengl.GL3.GL_IMAGE_1D_ARRAY;
import static com.jogamp.opengl.GL3.GL_IMAGE_2D_MULTISAMPLE;
import static com.jogamp.opengl.GL3.GL_IMAGE_2D_MULTISAMPLE_ARRAY;
import static com.jogamp.opengl.GL3.GL_IMAGE_2D_RECT;
import static com.jogamp.opengl.GL3.GL_INT_IMAGE_1D;
import static com.jogamp.opengl.GL3.GL_INT_IMAGE_1D_ARRAY;
import static com.jogamp.opengl.GL3.GL_INT_IMAGE_2D_MULTISAMPLE;
import static com.jogamp.opengl.GL3.GL_INT_IMAGE_2D_MULTISAMPLE_ARRAY;
import static com.jogamp.opengl.GL3.GL_INT_IMAGE_2D_RECT;
import static com.jogamp.opengl.GL3.GL_UNSIGNED_INT_IMAGE_1D;
import static com.jogamp.opengl.GL3.GL_UNSIGNED_INT_IMAGE_1D_ARRAY;
import static com.jogamp.opengl.GL3.GL_UNSIGNED_INT_IMAGE_2D_MULTISAMPLE;
import static com.jogamp.opengl.GL3.GL_UNSIGNED_INT_IMAGE_2D_MULTISAMPLE_ARRAY;
import static com.jogamp.opengl.GL3.GL_UNSIGNED_INT_IMAGE_2D_RECT;

/**
 * Version 2022.1
 * 
 * 2022.1: Factored out of Shader class
 * 
 * @author Malcolm Ryan
 */
public class GLTypes {

	/**
	 * Get the number of elements in a type. 
	 * Returns 1 for all base types.
	 * For vector types, returns the number of elements in the vector.
	 * For matrix types, returns the number of elements in the matrix.
	 * 
	 * @param type	The GL Type
	 * @return the number of elements
	 */
	static public int typeSize(int type) {
		switch (type) {
		case GL_FLOAT:
			return 1;
		case GL_FLOAT_VEC2:
			return 2;
		case GL_FLOAT_VEC3:
			return 3;
		case GL_FLOAT_VEC4:
			return 4;
		case GL_FLOAT_MAT2:
			return 4;
		case GL_FLOAT_MAT3:
			return 9;
		case GL_FLOAT_MAT4:
			return 16;
		case GL_INT:
			return 1;
		case GL_INT_VEC2:
			return 2;
		case GL_INT_VEC3:
			return 3;
		case GL_INT_VEC4:
			return 4;
		case GL_UNSIGNED_INT:
			return 1;
		case GL_UNSIGNED_INT_VEC2:
			return 2;
		case GL_UNSIGNED_INT_VEC3:
			return 3;
		case GL_UNSIGNED_INT_VEC4:
			return 4;
		default:
			throw new UnsupportedOperationException(
					String.format("Unsupported GLSL attribute type: %s", typeName(type)));
		}

	}
	
	/**
	 * Get the base type of a compound type. 
	 */

	static public int elementType(int type) {
		switch (type) {
		case GL_INT:
		case GL_INT_VEC2:
		case GL_INT_VEC3:
		case GL_INT_VEC4:
			return GL_INT;
		case GL_UNSIGNED_INT:
		case GL_UNSIGNED_INT_VEC2:
		case GL_UNSIGNED_INT_VEC3:
		case GL_UNSIGNED_INT_VEC4:
			return GL_UNSIGNED_INT;
		case GL_FLOAT:
		case GL_FLOAT_VEC2:
		case GL_FLOAT_VEC3:
		case GL_FLOAT_VEC4:
		case GL_FLOAT_MAT2:
		case GL_FLOAT_MAT3:
		case GL_FLOAT_MAT4:
			return GL_FLOAT;
		default:
			throw new UnsupportedOperationException(
					String.format("Unsupported GLSL attribute type: %s", typeName(type)));
		}

	}

	/**
	 * Get a the GLSL type name for a given type
	 * @param type
	 * @return
	 */
	static public String typeName(int type) {
		switch (type) {
		case GL_FLOAT:
			return "float";

		case GL_FLOAT_VEC2:
			return "vec2";

		case GL_FLOAT_VEC3:
			return "vec3";

		case GL_FLOAT_VEC4:
			return "vec4";

		case GL_DOUBLE:
			return "double";

		case GL_DOUBLE_VEC2:
			return "dvec2";

		case GL_DOUBLE_VEC3:
			return "dvec3";

		case GL_DOUBLE_VEC4:
			return "dvec4";

		case GL_INT:
			return "int";

		case GL_INT_VEC2:
			return "ivec2";

		case GL_INT_VEC3:
			return "ivec3";

		case GL_INT_VEC4:
			return "ivec4";

		case GL_UNSIGNED_INT:
			return "unsigned int";

		case GL_UNSIGNED_INT_VEC2:
			return "uvec2";

		case GL_UNSIGNED_INT_VEC3:
			return "uvec3";

		case GL_UNSIGNED_INT_VEC4:
			return "uvec4";

		case GL_BOOL:
			return "bool";

		case GL_BOOL_VEC2:
			return "bvec2";

		case GL_BOOL_VEC3:
			return "bvec3";

		case GL_BOOL_VEC4:
			return "bvec4";

		case GL_FLOAT_MAT2:
			return "mat2";

		case GL_FLOAT_MAT3:
			return "mat3";

		case GL_FLOAT_MAT4:
			return "mat4";

		case GL_FLOAT_MAT2x3:
			return "mat2x3";

		case GL_FLOAT_MAT2x4:
			return "mat2x4";

		case GL_FLOAT_MAT3x2:
			return "mat3x2";

		case GL_FLOAT_MAT3x4:
			return "mat3x4";

		case GL_FLOAT_MAT4x2:
			return "mat4x2";

		case GL_FLOAT_MAT4x3:
			return "mat4x3";

		case GL_DOUBLE_MAT2:
			return "dmat2";

		case GL_DOUBLE_MAT3:
			return "dmat3";

		case GL_DOUBLE_MAT4:
			return "dmat4";

		case GL_DOUBLE_MAT2x3:
			return "dmat2x3";

		case GL_DOUBLE_MAT2x4:
			return "dmat2x4";

		case GL_DOUBLE_MAT3x2:
			return "dmat3x2";

		case GL_DOUBLE_MAT3x4:
			return "dmat3x4";

		case GL_DOUBLE_MAT4x2:
			return "dmat4x2";

		case GL_DOUBLE_MAT4x3:
			return "dmat4x3";

		case GL_SAMPLER_1D:
			return "sampler1D";

		case GL_SAMPLER_2D:
			return "sampler2D";

		case GL_SAMPLER_3D:
			return "sampler3D";

		case GL_SAMPLER_CUBE:
			return "samplerCube";

		case GL_SAMPLER_1D_SHADOW:
			return "sampler1DShadow";

		case GL_SAMPLER_2D_SHADOW:
			return "sampler2DShadow";

		case GL_SAMPLER_1D_ARRAY:
			return "sampler1DArray";

		case GL_SAMPLER_2D_ARRAY:
			return "sampler2DArray";

		case GL_SAMPLER_1D_ARRAY_SHADOW:
			return "sampler1DArrayShadow";

		case GL_SAMPLER_2D_ARRAY_SHADOW:
			return "sampler2DArrayShadow";

		case GL_SAMPLER_2D_MULTISAMPLE:
			return "sampler2DMS";

		case GL_SAMPLER_2D_MULTISAMPLE_ARRAY:
			return "sampler2DMSArray";

		case GL_SAMPLER_CUBE_SHADOW:
			return "samplerCubeShadow";

		case GL_SAMPLER_BUFFER:
			return "samplerBuffer";

		case GL_SAMPLER_2D_RECT:
			return "sampler2DRect";

		case GL_SAMPLER_2D_RECT_SHADOW:
			return "sampler2DRectShadow";

		case GL_INT_SAMPLER_1D:
			return "isampler1D";

		case GL_INT_SAMPLER_2D:
			return "isampler2D";

		case GL_INT_SAMPLER_3D:
			return "isampler3D";

		case GL_INT_SAMPLER_CUBE:
			return "isamplerCube";

		case GL_INT_SAMPLER_1D_ARRAY:
			return "isampler1DArray";

		case GL_INT_SAMPLER_2D_ARRAY:
			return "isampler2DArray";

		case GL_INT_SAMPLER_2D_MULTISAMPLE:
			return "isampler2DMS";

		case GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY:
			return "isampler2DMSArray";

		case GL_INT_SAMPLER_BUFFER:
			return "isamplerBuffer";

		case GL_INT_SAMPLER_2D_RECT:
			return "isampler2DRect";

		case GL_UNSIGNED_INT_SAMPLER_1D:
			return "usampler1D";

		case GL_UNSIGNED_INT_SAMPLER_2D:
			return "usampler2D";

		case GL_UNSIGNED_INT_SAMPLER_3D:
			return "usampler3D";

		case GL_UNSIGNED_INT_SAMPLER_CUBE:
			return "usamplerCube";

		case GL_UNSIGNED_INT_SAMPLER_1D_ARRAY:
			return "usampler2DArray";

		case GL_UNSIGNED_INT_SAMPLER_2D_ARRAY:
			return "usampler2DArray";

		case GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE:
			return "usampler2DMS";

		case GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY:
			return "usampler2DMSArray";

		case GL_UNSIGNED_INT_SAMPLER_BUFFER:
			return "usamplerBuffer";

		case GL_UNSIGNED_INT_SAMPLER_2D_RECT:
			return "usampler2DRect";

		case GL_IMAGE_1D:
			return "image1D";

		case GL_IMAGE_2D:
			return "image2D";

		case GL_IMAGE_3D:
			return "image3D";

		case GL_IMAGE_2D_RECT:
			return "image2DRect";

		case GL_IMAGE_CUBE:
			return "imageCube";

		case GL_IMAGE_BUFFER:
			return "imageBuffer";

		case GL_IMAGE_1D_ARRAY:
			return "image1DArray";

		case GL_IMAGE_2D_ARRAY:
			return "image2DArray";

		case GL_IMAGE_2D_MULTISAMPLE:
			return "image2DMS";

		case GL_IMAGE_2D_MULTISAMPLE_ARRAY:
			return "image2DMSArray";

		case GL_INT_IMAGE_1D:
			return "iimage1D";

		case GL_INT_IMAGE_2D:
			return "iimage2D";

		case GL_INT_IMAGE_3D:
			return "iimage3D";

		case GL_INT_IMAGE_2D_RECT:
			return "iimage2DRect";

		case GL_INT_IMAGE_CUBE:
			return "iimageCube";

		case GL_INT_IMAGE_BUFFER:
			return "iimageBuffer";

		case GL_INT_IMAGE_1D_ARRAY:
			return "iimage1DArray";

		case GL_INT_IMAGE_2D_ARRAY:
			return "iimage2DArray";

		case GL_INT_IMAGE_2D_MULTISAMPLE:
			return "iimage2DMS";

		case GL_INT_IMAGE_2D_MULTISAMPLE_ARRAY:
			return "iimage2DMSArray";

		case GL_UNSIGNED_INT_IMAGE_1D:
			return "uimage1D";

		case GL_UNSIGNED_INT_IMAGE_2D:
			return "uimage2D";

		case GL_UNSIGNED_INT_IMAGE_3D:
			return "uimage3D";

		case GL_UNSIGNED_INT_IMAGE_2D_RECT:
			return "uimage2DRect";

		case GL_UNSIGNED_INT_IMAGE_CUBE:
			return "uimageCube";

		case GL_UNSIGNED_INT_IMAGE_BUFFER:
			return "uimageBuffer";

		case GL_UNSIGNED_INT_IMAGE_1D_ARRAY:
			return "uimage1DArray";

		case GL_UNSIGNED_INT_IMAGE_2D_ARRAY:
			return "uimage2DArray";

		case GL_UNSIGNED_INT_IMAGE_2D_MULTISAMPLE:
			return "uimage2DMS";

		case GL_UNSIGNED_INT_IMAGE_2D_MULTISAMPLE_ARRAY:
			return "uimage2DMSArray";

		case GL_UNSIGNED_INT_ATOMIC_COUNTER:
			return "atomic_uint";
		}
		throw new IllegalArgumentException("Unknown GL type: " + type);
	}

}

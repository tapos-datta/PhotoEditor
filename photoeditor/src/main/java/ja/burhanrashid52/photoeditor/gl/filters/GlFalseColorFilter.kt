package ja.burhanrashid52.photoeditor.gl.filters

import android.opengl.GLES20
import java.nio.FloatBuffer

/**
 * Created by TAPOS DATTA on 28,June,2022
 */

class GlFalseColorFilter() : GLFilter(DEFAULT_VERTEX_SHADER, FALSE_COLOR_FRAGMENT_SHADER) {

    private var secondColor: FloatArray
    private var firstColor: FloatArray

    init {
        firstColor = floatArrayOf(0.1f, 0f, 0f)
        secondColor = floatArrayOf(1f, 0.9f, 0.8f)
    }

    fun setFirstColor(firstColorRGB: FloatArray) {
        firstColor = firstColorRGB
    }

    fun setSecondColor(secondColorRGB: FloatArray) {
        secondColor = secondColorRGB
    }

    override fun onDraw() {
        super.onDraw()
        GLES20.glUniform3fv(getHandle("firstColor"), 1, FloatBuffer.wrap(firstColor))
        GLES20.glUniform3fv(getHandle("secondColor"), 1, FloatBuffer.wrap(secondColor))
    }


    companion object {
        private val FALSE_COLOR_FRAGMENT_SHADER = """
            precision highp float;
            varying highp vec2 vTextureCoord;
            uniform lowp sampler2D sTexture;
            uniform vec3 firstColor;
            uniform vec3 secondColor;
            void main() {
                vec4 baseColor = texture2D(sTexture, vTextureCoord);
                float luma = dot(baseColor.rgb, vec3(0.3,0.59,0.11)); 
                vec3 finalColor = mix(firstColor, secondColor, luma);
                gl_FragColor = vec4(finalColor, baseColor.a);
            }
        """.trimIndent()
    }


}
package ja.burhanrashid52.photoeditor.gl.filters

import android.opengl.GLES20
import ja.burhanrashid52.photoeditor.gl.GLFrameBuffer

/**
 * Created by TAPOS DATTA on 29,June,2022
 */

class GLFilterGroup(private val filterList: List<GLFilter> = List(1) { GLFilter() }) : GLFilter() {

    private var activeIndex = 0
    private var prevTexName = 0
    private var framebufferObjects = MutableList(2) {
        GLFrameBuffer()
    }

    override fun setup() {
        super.setup()
        filterList.forEach { filter ->
            filter.setup()
        }
    }


    override fun draw(texName: Int, fbo: GLFrameBuffer?) {
        fbo?.let {
            activeIndex = 1
            framebufferObjects[activeIndex] = it
            prevTexName = texName

            filterList.forEach { filter ->
                framebufferObjects[activeIndex].enable()
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
                filter.draw(prevTexName, framebufferObjects[activeIndex])
                framebufferObjects[activeIndex].disable()
                prevTexName = framebufferObjects[activeIndex].getTextureId()
                activeIndex = (activeIndex + 1) % 2
            }

            if (framebufferObjects[activeIndex].getTextureId() == it.getTextureId()) {
                it.enable()
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
                super.draw(prevTexName, it)
            }
        } ?: run {
            super.draw(texName, fbo)
        }

    }

    override fun release() {
        filterList.forEach { filter ->
            filter.release()
        }
        framebufferObjects[0].release()
        super.release()
    }

    override fun setFrameSize(width: Int, height: Int) {
        filterList.forEach { filter ->
            filter.setFrameSize(width, height)
        }
        framebufferObjects[0].setup(width, height)
        super.setFrameSize(width, height)
    }

}
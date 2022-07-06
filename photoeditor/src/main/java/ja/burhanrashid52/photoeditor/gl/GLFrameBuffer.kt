package ja.burhanrashid52.photoeditor.gl

import android.opengl.GLES20
import ja.burhanrashid52.photoeditor.GLToolbox

/**
 * Created by TAPOS DATTA on 26,June,2022
 */

class GLFrameBuffer {

    private var width: Int = 0
    private var height: Int = 0
    private var frameBufferName: Int = -1
    private var renderBufferName: Int = -1
    private var textureName: Int = -1

    fun getWidth(): Int {
        return width
    }

    fun getHeight(): Int {
        return height
    }

    fun getTextureId(): Int {
        return textureName
    }

    fun setup(width: Int, height: Int) {
        this.setup(width, height, GLES20.GL_LINEAR, GLES20.GL_LINEAR)
    }

    fun setup(texelWidth: Int, texelHeight: Int, magnifyFilter: Int, minifyFilter: Int) {
        val args = intArrayOf(-1)

        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, args, 0)
        require(!(width > args[0] || height > args[0])) { "GL_MAX_TEXTURE_SIZE " + args[0] }

        GLES20.glGetIntegerv(GLES20.GL_MAX_RENDERBUFFER_SIZE, args, 0)
        require(!(width > args[0] || height > args[0])) { "GL_MAX_RENDERBUFFER_SIZE " + args[0] }

        GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, args, 0)

        val saveFrameBuffer = args[0]
        GLES20.glGetIntegerv(GLES20.GL_RENDERBUFFER_BINDING, args, 0)
        val saveRenderBuffer = args[0]
        GLES20.glGetIntegerv(GLES20.GL_TEXTURE_BINDING_2D, args, 0)
        val saveTextureName = args[0]

        //release the previous ids
        release()

        try {
            width = texelWidth
            height = texelHeight
            GLES20.glGenFramebuffers(args.size, args, 0)

            frameBufferName = args[0]
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferName)
            GLES20.glGenRenderbuffers(args.size, args, 0)

            renderBufferName = args[0]
            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBufferName)
            GLES20.glRenderbufferStorage(
                GLES20.GL_RENDERBUFFER,
                GLES20.GL_DEPTH_COMPONENT16,
                width,
                height
            )
            GLES20.glFramebufferRenderbuffer(
                GLES20.GL_FRAMEBUFFER,
                GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER,
                renderBufferName
            )

            GLES20.glGenTextures(args.size, args, 0)
            textureName = args[0]
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName)
            GLToolbox.setupSampler(GLES20.GL_TEXTURE_2D, magnifyFilter, minifyFilter)
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_RGBA,
                width,
                height,
                0,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                null
            )
            GLES20.glFramebufferTexture2D(
                GLES20.GL_FRAMEBUFFER,
                GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D,
                textureName,
                0
            )
            val status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)
            if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
                throw RuntimeException("Failed to initialize framebuffer object $status")
            }
        } catch (e: RuntimeException) {
            release()
            throw e
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, saveFrameBuffer)
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, saveRenderBuffer)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, saveTextureName)
    }

    fun release() {
        val args = intArrayOf(textureName)
        GLES20.glDeleteTextures(args.size, args, 0)
        textureName = -1

        args[0] = renderBufferName
        GLES20.glDeleteRenderbuffers(args.size, args, 0)
        renderBufferName = 0

        args[0] = frameBufferName
        GLES20.glDeleteFramebuffers(args.size, args, 0)
        frameBufferName = 0
    }

    fun enable() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferName)
    }

    fun disable() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
    }

}
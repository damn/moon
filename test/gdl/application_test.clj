#_(ns clojure.application-test
  ;(:require [moon :as gdx])
  (:import (com.badlogic.gdx Gdx)
           (com.badlogic.gdx ApplicationListener)))

(comment
 (application/newWindow Gdx/app
                        (reify ApplicationListener
                          (create [_]
                            (println"create!"))
                          (dispose [_]
                            (println "dispose!"))
                          (pause [_])
                          (render [_]
                            #_(println "render!"))
                          (resize [_ width height]
                            (println "resize!"))
                          (resume [_]))
                        (Lwjgl3WindowConfiguration.)))

#_(def config {:title "Fooz Baaz"
             :windowed-mode {:width 800
                             :height 600}
             ; Sets the target framerate for the application. The CPU sleeps as needed. Must be positive. Use 0 to never sleep. Default is 0
             :foreground-fps 60

             ; Whether to disable audio or not. If set to true, the returned audio class instances like {@link Audio} or {@link Music} will be mock implementations.
             :disable-audio? false

             :audio {; the maximum number of sources that can be played simultaniously
                     :audioDeviceSimultaneousSources 16
                     ; the audio device buffer count
                     :audioDeviceBufferCount 9
                     ; the audio device buffer size in samples (default 512)
                     :audioDeviceBufferSize 512}

             ; The maximum number of threads to use for network requests. Default is {@link Integer#MAX_VALUE}.
             :max-net-threads Integer/MAX_VALUE

             ; Sets which OpenGL version to use to emulate OpenGL ES.
             ; If the given major/minor version is not supported, the backend falls
             ; back to OpenGL ES 2.0 emulation through OpenGL 2.0.
             ; The default parameters for major and minor should be 3 and 2
             ; respectively to be compatible with Mac OS X.
             ; Specifying major version 4 and minor version 2 will ensure that all OpenGL ES
             ; 3.0 features are supported. Note however that Mac OS X does only support 3.2.
             ;
             ; * @see <a href= "http://legacy.lwjgl.org/javadoc/org/lwjgl/opengl/ContextAttribs.html"> LWJGL OSX ContextAttribs note</a>
             ; *
             ; * @param glVersion which OpenGL ES emulation version to use
             ; * @param gles3MajorVersion OpenGL ES major version, use 3 as default
             ; * @param gles3MinorVersion OpenGL ES minor version, use 2 as default */

             :glEmulation  :GLEmulation/ANGLE_GLES20
             ; :GLEmulation/GL20

             ; ANGLE_GLES20, GL20, GL30, GL31, GL32
             ; [com.badlogicgames.gdx/gdx-lwjgl3-angle "1.13.5"]

             :gles30ContextMajorVersion 3
             :gles30ContextMinorVersion 2

             ; Set transparent window hint. Results may vary on different OS and GPUs. Usage with the ANGLE backend is less consistent.
             :transparentFramebuffer true


             ;:opengl-emulation {:gl-version :angle-gles20
             ;                   :gles-3-major-version 3
             ;                   :gles-3-minor-version 2}
             ; :mac {:glfw-async? true
             ;       :taskbar-icon nil}

             :create (fn [_context]
                       (println"create!"))
             :dispose (fn []
                        (println "dispose!"))
             :render (fn []
                       #_(println "render!"))
             :resize (fn [width height]
                       (println "resize!"))


             })

#_(defn -main []
  (gdx/application config))

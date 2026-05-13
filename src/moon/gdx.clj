(ns moon.gdx
  (:require moon.audio
            moon.graphics
            [moon.input :as input])
  (:import (com.badlogic.gdx ApplicationListener
                             Gdx)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.graphics GL20)))

(defn application!
  [{:keys [create!
           dispose!
           render!
           resize!
           title
           windowed-mode
           foreground-fps]}]
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (reify ApplicationListener
                        (create [_]
                          (create! Gdx/app))

                        (dispose [_]
                          (dispose!))

                        (render [_]
                          (render!))

                        (resize [_ width height]
                          (resize! width height))

                        (pause [_])

                        (resume [_]))
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle title)
                        (.setWindowedMode (:width windowed-mode) (:height windowed-mode))
                        (.setForegroundFPS foreground-fps))))

(extend-type Lwjgl3Application
  moon.audio/Audio
  (new-sound [app path]
    (.newSound (.getAudio app)
               (.internal (.getFiles app) path)))

  moon.graphics/Graphics
  (frames-per-second [app]
    (.getFramesPerSecond (.getGraphics app)))

  (delta-time [app]
    (.getDeltaTime (.getGraphics app)))

  (new-cursor [app pixmap hotspot-x hotspot-y]
    (.newCursor (.getGraphics app) pixmap hotspot-x hotspot-y))

  (set-cursor! [app cursor]
    (.setCursor (.getGraphics app) cursor))

  (clear! [app r g b a]
    (.glClearColor (.getGL20 (.getGraphics app)) r g b a)
    (.glClear      (.getGL20 (.getGraphics app)) GL20/GL_COLOR_BUFFER_BIT))

  moon.input/Input
  (set-processor! [this input-processor]
    (.setInputProcessor (.getInput this) input-processor))

  (key-pressed? [this key]
    (.isKeyPressed (.getInput this) key))

  (key-just-pressed? [this key]
    (.isKeyJustPressed (.getInput this) key))

  (button-just-pressed? [this button]
    (.isButtonJustPressed (.getInput this) button))

  (mouse-position [this]
    [(.getX (.getInput this))
     (.getY (.getInput this))]))

(ns moon.gdx
  (:require [com.badlogic.gdx.graphics.color :as color]
            [moon.app :as app])
  (:import (com.badlogic.gdx ApplicationListener
                             Gdx)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.graphics Colors
                                      GL20)
           (com.badlogic.gdx.scenes.scene2d.ui TooltipManager)))

(defn application!
  [{:keys [create!
           dispose!
           render!
           resize!
           title
           windowed-mode
           foreground-fps
           colors
           ]}]
  (doseq [[name rgba] colors]
    (Colors/put name (color/create rgba)))
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (reify ApplicationListener
                        (create [_]
                          (set! (.initialTime (TooltipManager/getInstance)) 0)
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
  app/App
  (new-sound [app path]
    (.newSound (.getAudio app)
               (.internal (.getFiles app) path)))

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

  (set-input-processor! [this input-processor]
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

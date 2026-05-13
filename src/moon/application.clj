(ns moon.application
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.walk :as walk]
            moon.audio
            moon.graphics
            [moon.input :as input])
  (:import (com.badlogic.gdx ApplicationListener
                             Input
                             Gdx)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.graphics GL20))
  (:gen-class))

(defn edn-resource [path]
  (->> path
       io/resource
       slurp
       (edn/read-string {:readers {'edn/resource edn-resource}})
       (walk/postwalk (fn [form]
                        (if (and (symbol? form) (namespace form))
                          (let [avar (requiring-resolve form)]
                            (assert avar form)
                            avar)
                          form)))))

(def state (atom nil))

(defn -main []
  (let [{:keys [
                create-pipeline
                dispose!
                render-pipeline
                resize!
                title
                windowed-mode
                foreground-fps
                ]}
        (edn-resource "game.edn")]
    (Lwjgl3ApplicationConfiguration/useGlfwAsync)
    (Lwjgl3Application. (reify ApplicationListener
                          (create [_]
                            (reset! state
                                    (reduce (fn [ctx [f & params]]
                                              (apply f ctx params))
                                            {:ctx/app Gdx/app}
                                            create-pipeline)))

                          (dispose [_]
                            (dispose! @state))

                          (render [_]
                            (swap! state
                                   (fn [ctx]
                                     (reduce (fn [ctx [f & params]]
                                               (apply f ctx params))
                                             ctx
                                             render-pipeline))))

                          (resize [_ width height]
                            (resize! @state width height))

                          (pause [_])

                          (resume [_]))
                        (doto (Lwjgl3ApplicationConfiguration.)
                          (.setTitle title)
                          (.setWindowedMode (:width windowed-mode) (:height windowed-mode))
                          (.setForegroundFPS foreground-fps)))))

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

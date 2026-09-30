(ns gdx.lwjgl3-application-configuration
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)))

(defn use-glfw-async! []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync))

(def options
  {:title (fn [cfg title]
            (.setTitle ^Lwjgl3ApplicationConfiguration cfg title))
   :windowed-mode (fn [cfg [width height]]
                    (.setWindowedMode ^Lwjgl3ApplicationConfiguration cfg (int width) (int height)))
   :foreground-fps (fn [cfg fps]
                     (.setForegroundFPS ^Lwjgl3ApplicationConfiguration cfg (int fps)))})

(defn create [opts]
  (let [cfg (Lwjgl3ApplicationConfiguration.)]
    (doseq [[k v] opts :when (options k)]
      ((options k) cfg v))
    cfg))

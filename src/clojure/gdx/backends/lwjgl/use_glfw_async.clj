(ns clojure.gdx.backends.lwjgl.use-glfw-async
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)))

(defn f! []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync))

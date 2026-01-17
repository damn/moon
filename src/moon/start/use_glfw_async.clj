(ns moon.start.use-glfw-async
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)))

(defn step [ctx]
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  ctx)

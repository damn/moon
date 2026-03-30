(ns moon.start.use-glfw-async
  (:require [clj.api.com.badlogic.gdx.backends.lwjgl3.application.config :as config]))

(defn step [ctx]
  (config/use-glfw-async!)
  ctx)

(ns moon.render.clear-screen
  (:require [clj.api.com.badlogic.gdx.utils.screen :as screen]))

(defn do!
  [ctx]
  (screen/clear! 0 0 0 0)
  ctx)

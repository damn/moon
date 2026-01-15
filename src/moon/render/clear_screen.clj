(ns moon.render.clear-screen
  (:import (com.badlogic.gdx.utils ScreenUtils)))

(defn do!
  [ctx]
  (ScreenUtils/clear 0 0 0 0)
  ctx)

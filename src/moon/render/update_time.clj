(ns moon.render.update-time
  (:require [moon.world :as world])
  (:import (com.badlogic.gdx Graphics)))

(defn do!
  [{:keys [ctx/graphics
           ctx/paused?]
    :as ctx}]
  (if paused?
    ctx
    (update ctx :ctx/world world/update-time (Graphics/.getDeltaTime (:graphics/core graphics)))))

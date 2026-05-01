(ns moon.application.resize
  (:require [clojure.gdx.scene2d.stage :as stage]
            [clojure.graphics.viewport :as viewport]))

(defn do!
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (viewport/update! (stage/viewport stage) width height true)
  (viewport/update! world-viewport width height false)
  nil)

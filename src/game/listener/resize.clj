(ns game.listener.resize
  (:require [game.shared :refer [stage world-viewport]])
  (:import (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn resize! [width height]
  (.update (.getViewport ^Stage @stage) width height true)
  (.update ^Viewport @world-viewport width height false))

(ns moon.render.set-camera-on-player
  (:require [moon.graphics.camera :as camera])
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn do!
  [{:keys [ctx/graphics
           ctx/player-eid]
    :as ctx}]
  (camera/set-position! (Viewport/.getCamera (:graphics/world-viewport graphics))
                        (:body/position (:entity/body @player-eid)))
  ctx)

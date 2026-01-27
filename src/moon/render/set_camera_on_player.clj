(ns moon.render.set-camera-on-player
  (:require [moon.camera :as camera])
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn do!
  [{:keys [ctx/player-eid
           ctx/world-viewport]
    :as ctx}]
  (camera/set-position! (Viewport/.getCamera world-viewport)
                        (:body/position (:entity/body @player-eid)))
  ctx)

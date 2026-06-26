(ns orthographic-camera.position
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as camera]
            [gdx.math.vector3.clojurize :as clojurize]))

(defn f [camera]
  (clojurize/f (camera/position camera)))

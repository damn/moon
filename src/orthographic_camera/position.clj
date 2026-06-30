(ns orthographic-camera.position
  (:require [clojure.gdx :as gdx]
            [gdx.math.vector3.clojurize :as clojurize]))

(defn f [camera]
  (clojurize/f (gdx/camera-position camera)))

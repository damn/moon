(ns clojure.gdx.viewport.update
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn f [viewport width height center-camera?]
  (Viewport/.update viewport width height center-camera?))

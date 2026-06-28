(ns graphics.frames-per-second
  (:import (com.badlogic.gdx Graphics)))

(defn f [graphics]
  (.getFramesPerSecond ^Graphics graphics))

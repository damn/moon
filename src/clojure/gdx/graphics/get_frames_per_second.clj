(ns clojure.gdx.graphics.get-frames-per-second
  (:import (com.badlogic.gdx Graphics)))

(defn f [graphics]
  (Graphics/.getFramesPerSecond graphics))

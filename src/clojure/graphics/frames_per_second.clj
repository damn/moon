(ns clojure.graphics.frames-per-second
  (:import (com.badlogic.gdx Graphics)))

(defn f [^Graphics graphics]
  (.getFramesPerSecond graphics))

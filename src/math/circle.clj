(ns math.circle
  (:require [com.badlogic.gdx.math.circle :as circle]))

(defn create [{:keys [position radius]}]
  (circle/create (position 0)
                 (position 1)
                 radius))

(ns math.circle
  (:require [clojure.gdx.circle.new :as new-circle]))

(defn create [{:keys [position radius]}]
  (new-circle/f (position 0)
                (position 1)
                radius))

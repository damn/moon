(ns math.circle
  (:require [clojure.gdx :as gdx]))

(defn create [{:keys [position radius]}]
  (gdx/circle (position 0)
              (position 1)
              radius))

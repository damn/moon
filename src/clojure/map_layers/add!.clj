(ns clojure.map-layers.add!
  (:require [com.badlogic.gdx.maps.map-layers :as map-layers]))

(defn f [& args]
  (apply map-layers/add args))

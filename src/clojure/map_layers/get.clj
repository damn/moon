(ns clojure.map-layers.get
  (:require [com.badlogic.gdx.maps.map-layers :as map-layers]))

(defn f [& args]
  (apply map-layers/get args))

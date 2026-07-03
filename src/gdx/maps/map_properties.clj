(ns gdx.maps.map-properties
  (:require [clojure.gdx.map-properties.get-keys :as get-keys]
            [clojure.gdx.map-properties.get-values :as get-values]))

(defn clojurize [props]
  (zipmap (get-keys/f props)
          (get-values/f props)))

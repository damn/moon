(ns clojure.gdx.map-properties.clojurize
  (:require [clojure.gdx.map-properties.get-keys :as get-keys]
            [clojure.gdx.map-properties.get-values :as get-values]))

(defn f [props]
  (zipmap (get-keys/f props)
          (get-values/f props)))

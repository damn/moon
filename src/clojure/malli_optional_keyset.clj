(ns clojure.malli-optional-keyset
  (:require [clojure.map-schema :as map-schema]
            [clojure.malli-map-keys :as map-keys]))

(defn f [map-schema]
  (set (filter #(map-schema/optional? map-schema %)
               (map-keys/f map-schema))))

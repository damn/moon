(ns clojure.malli-optional-keyset
  (:require [clojure.is-optional :as optional?]
            [clojure.malli-map-keys :as map-keys]))

(defn f [map-schema]
  (set (filter #(optional?/f % map-schema)
               (map-keys/f map-schema))))

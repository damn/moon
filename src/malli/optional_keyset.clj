(ns malli.optional-keyset
  (:require [malli.is-optional :as optional?]
            [malli.map-keys :as map-keys]))

(defn f [map-schema]
  (set (filter #(optional?/f % map-schema)
               (map-keys/f map-schema))))

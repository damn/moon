(ns clojure.schemas-map-keys
  (:require [moon.schema :as schema]
            [moon.map-schema :as map-schema]))

(defn map-keys [schemas schema]
  (map-schema/map-keys (schema/malli-form schema schemas)))

(ns clojure.schemas-map-keys
  (:require [moon.schema :as schema]
            [clojure.malli-map-keys :as map-keys]))

(defn map-keys [schemas schema]
  (map-keys/f (schema/malli-form schema schemas)))

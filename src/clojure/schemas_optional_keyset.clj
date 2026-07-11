(ns clojure.schemas-optional-keyset
  (:require [moon.schema :as schema]
            [moon.map-schema :as map-schema]))

(defn optional-keyset [schemas schema]
  (map-schema/optional-keyset (schema/malli-form schema schemas)))

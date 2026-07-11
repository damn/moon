(ns moon.schemas.optional
  (:require [moon.schema :as schema]
            [moon.map-schema :as map-schema]))

(defn optional? [schemas schema k]
  (map-schema/optional? (schema/malli-form schema schemas) k))

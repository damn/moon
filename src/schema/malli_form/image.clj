(ns schema.malli-form.image
  (:require [moon.schemas.create-map-schema :refer [create-map-schema]]))

(defn f [_ schemas]
  (create-map-schema schemas
                     [:image/file
                      [:image/bounds {:optional true}]]))

(ns moon.schema.image
  (:require [moon.schema :as schema]
            [moon.schemas :as schemas]))

(defmethod schema/malli-form :s/image
  [_ schemas]
  (schemas/create-map-schema schemas
                             [:image/file
                              [:image/bounds {:optional true}]]))

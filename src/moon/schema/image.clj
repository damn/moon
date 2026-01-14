(ns moon.schema.image
  (:require [moon.malli :as malli]
            [moon.schemas :as schemas]))

(defmethod malli/form :s/image [_ schemas]
  (schemas/create-map-schema schemas
                             [:image/file
                              [:image/bounds {:optional true}]]))

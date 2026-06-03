(ns schema.malli-form.image
  (:require [moon.schemas.malli-form :refer [malli-form]]
            [moon.schemas :refer [create-map-schema]]))

(defmethod malli-form :s/image [_ schemas]
  (create-map-schema schemas
                     [:image/file
                      [:image/bounds {:optional true}]]))

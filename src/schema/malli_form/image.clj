(ns schema.malli-form.image
  (:require [moon.schemas :refer [malli-form create-map-schema]]))

(defmethod malli-form :s/image [_ schemas]
  (create-map-schema schemas
                     [:image/file
                      [:image/bounds {:optional true}]]))

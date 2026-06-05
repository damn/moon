(ns schema.malli-form.image
  (:require [clojure.malli-form :refer [malli-form]]
            [moon.schemas.create-map-schema :refer [create-map-schema]]))

(defmethod malli-form :s/image [_ schemas]
  (create-map-schema schemas
                     [:image/file
                      [:image/bounds {:optional true}]]))

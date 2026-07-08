(ns clojure.malli-form-s-image
  (:require [clojure.malli-form :refer [malli-form]]
            [clojure.malli-form-create-map-schema :refer [create-map-schema]]))

(defmethod malli-form :s/image
  [_ schemas]
  (create-map-schema schemas
                     [:image/file
                      [:image/bounds {:optional true}]]))

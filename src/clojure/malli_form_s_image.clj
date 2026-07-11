(ns clojure.malli-form-s-image
  (:require [clojure.malli-form :refer [malli-form]]
            [moon.schemas :as schemas]))

(defmethod malli-form :s/image
  [_ schemas]
  (schemas/create-map-schema schemas
                             [:image/file
                              [:image/bounds {:optional true}]]))

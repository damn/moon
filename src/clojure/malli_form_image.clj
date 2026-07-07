(ns clojure.malli-form-image
  (:require [clojure.malli-form-create-map-schema :as create-map-schema]))

(defn f [_ schemas]
  (create-map-schema/f schemas
                       [:image/file
                        [:image/bounds {:optional true}]]))

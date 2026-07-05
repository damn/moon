(ns moon.schemas.malli-form.image
  (:require [moon.schemas.malli-form.create-map-schema :as create-map-schema]))

(defn f [_ schemas]
  (create-map-schema/f schemas
                       [:image/file
                        [:image/bounds {:optional true}]]))

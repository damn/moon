(ns schema.malli-form.animation
  (:require [moon.schemas.create-map-schema :refer [create-map-schema]]))

(defn f [_ schemas]
  (create-map-schema schemas
                     [:animation/frames
                      :animation/frame-duration
                      :animation/looping?]))

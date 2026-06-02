(ns schema.malli-form.animation
  (:require [moon.schemas :refer [malli-form create-map-schema]]))

(defmethod malli-form :s/animation [_ schemas]
  (create-map-schema schemas
                     [:animation/frames
                      :animation/frame-duration
                      :animation/looping?]))

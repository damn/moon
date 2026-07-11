(ns moon.schema.animation
  (:require [moon.schema :as schema]
            [moon.schemas :as schemas]))

(defmethod schema/malli-form :s/animation
  [_ schemas]
  (schemas/create-map-schema schemas
                             [:animation/frames
                              :animation/frame-duration
                              :animation/looping?]))

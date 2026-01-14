(ns moon.schema.animation
  (:require [moon.malli :as malli]
            [moon.schemas :as schemas]))

(defmethod malli/form :s/animation [_ schemas]
  (schemas/create-map-schema schemas
                             [:animation/frames
                              :animation/frame-duration
                              :animation/looping?]))

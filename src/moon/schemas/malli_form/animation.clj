(ns moon.schemas.malli-form.animation
  (:require [moon.schemas.malli-form.create-map-schema :as create-map-schema]))

(defn f [_ schemas]
  (create-map-schema/f schemas
                       [:animation/frames
                        :animation/frame-duration
                        :animation/looping?]))

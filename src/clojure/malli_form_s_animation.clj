(ns clojure.malli-form-s-animation
  (:require [clojure.malli-form :refer [malli-form]]
            [clojure.malli-form-create-map-schema :refer [create-map-schema]]))

(defmethod malli-form :s/animation
  [_ schemas]
  (create-map-schema schemas
                     [:animation/frames
                      :animation/frame-duration
                      :animation/looping?]))

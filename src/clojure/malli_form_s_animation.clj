(ns clojure.malli-form-s-animation
  (:require [clojure.malli-form :refer [malli-form]]
            [moon.schemas :as schemas]))

(defmethod malli-form :s/animation
  [_ schemas]
  (schemas/create-map-schema schemas
                             [:animation/frames
                              :animation/frame-duration
                              :animation/looping?]))

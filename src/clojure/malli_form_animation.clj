(ns clojure.malli-form-animation
  (:require [clojure.malli-form-create-map-schema :as create-map-schema]))

(defn f [_ schemas]
  (create-map-schema/f schemas
                       [:animation/frames
                        :animation/frame-duration
                        :animation/looping?]))

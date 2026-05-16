(ns moon.resize.stage
  (:require [moon.viewport :as viewport]))

(defn do!
  [{:keys [ctx/stage]} width height]
  (viewport/update! (:stage/viewport stage) width height true))

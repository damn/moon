(ns clojure.levelgen-test.resize
  (:require [clojure.viewport :as viewport]))

(defn resize
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (viewport/update! (:stage/viewport stage) width height true)
  (viewport/update! world-viewport width height false))

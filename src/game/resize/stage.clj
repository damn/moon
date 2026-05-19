(ns game.resize.stage
  (:require [gdl.utils.viewport :as viewport]))

(defn do!
  [{:keys [ctx/stage]} width height]
  (viewport/update! (:stage/viewport stage) width height true))

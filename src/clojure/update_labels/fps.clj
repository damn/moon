(ns clojure.update-labels.fps
  (:require [gdl.graphics :as graphics]))

(def item
  {:label "FPS"
   :update-fn (fn [{:keys [ctx/graphics]}]
                (graphics/get-frames-per-second graphics))
   :icon "images/fps.png"})

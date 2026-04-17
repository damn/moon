(ns moon.dev-menu.update-labels.fps
  (:require [clojure.graphics :as graphics]))

(def item
  {:label "FPS"
   :update-fn (fn [{:keys [ctx/graphics]}]
                (graphics/frames-per-second graphics))
   :icon "images/fps.png"})

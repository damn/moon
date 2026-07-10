(ns clojure.update-labels.fps
  (:require [com.badlogic.gdx.graphics :as graphics]))

(def item
  {:label "FPS"
   :update-fn (fn [{:keys [ctx/graphics]}]
                (graphics/getFramesPerSecond graphics))
   :icon "images/fps.png"})

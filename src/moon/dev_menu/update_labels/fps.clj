(ns moon.dev-menu.update-labels.fps
  (:import (com.badlogic.gdx Graphics)))

(def item
  {:label "FPS"
   :update-fn (fn [{:keys [ctx/graphics]}]
                (Graphics/.getFramesPerSecond graphics))
   :icon "images/fps.png"})

(ns moon.application.render.update-mouse
  (:require [moon.stage :as stage]
            [moon.viewport :as viewport]
            [moon.app :as app]))

(defn step
  [{:keys [ctx/app
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (let [mp (app/mouse-position app)]
    (-> ctx
        (assoc :ctx/world-mouse-position (viewport/unproject world-viewport mp))
        (assoc :ctx/ui-mouse-position (stage/unproject stage mp)))))

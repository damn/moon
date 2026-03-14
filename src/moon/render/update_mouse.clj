(ns moon.render.update-mouse
  (:require [moon.viewport :as viewport]
            [moon.input :as input]))

(defn do!
  [{:keys [ctx/input
           ctx/ui-viewport
           ctx/world-viewport]
    :as ctx}]
  (let [mp (input/mouse-position input)]
    (-> ctx
        (assoc :ctx/world-mouse-position (viewport/unproject world-viewport mp))
        (assoc :ctx/ui-mouse-position (viewport/unproject ui-viewport mp)))))

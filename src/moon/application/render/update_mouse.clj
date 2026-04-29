(ns moon.application.render.update-mouse
  (:require [clojure.graphics.viewport :as viewport]
            [clojure.input :as input]))

(defn step
  [{:keys [ctx/input
           ctx/ui-viewport
           ctx/world-viewport]
    :as ctx}]
  (let [mp (input/mouse-position input)]
    (-> ctx
        (assoc :ctx/world-mouse-position (viewport/unproject world-viewport mp))
        (assoc :ctx/ui-mouse-position (viewport/unproject ui-viewport mp)))))

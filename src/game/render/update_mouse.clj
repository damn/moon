(ns game.render.update-mouse
  (:require [moon.viewport :as viewport]
            [gdl.app :as app]
            [clojure.input :as input]))

(defn step
  [{:keys [ctx/app
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (let [mp (input/mouse-position (app/input app))]
    (-> ctx
        (assoc :ctx/world-mouse-position (viewport/unproject world-viewport mp))
        (assoc :ctx/ui-mouse-position (-> stage :stage/viewport (viewport/unproject mp))))))

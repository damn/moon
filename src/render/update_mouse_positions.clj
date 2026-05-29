(ns render.update-mouse-positions
  (:require [gdx.utils.viewport.viewport :as viewport]
            [game.ctx :as ctx]))

(defn step
  [{:keys [ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (let [mp (ctx/mouse-position ctx)]
    (-> ctx
        (assoc :ctx/world-mouse-position (viewport/unproject world-viewport mp))
        (assoc :ctx/ui-mouse-position (-> stage :stage/viewport (viewport/unproject mp))))))

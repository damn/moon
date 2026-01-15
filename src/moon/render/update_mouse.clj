(ns moon.render.update-mouse
  (:require [clj.api.com.badlogic.gdx.utils.viewport :as viewport]
            [moon.input :as input]))

(defn do!
  [{:keys [ctx/graphics
           ctx/input
           ctx/ui-viewport]
    :as ctx}]
  (let [mp (input/mouse-position input)]
    (-> ctx
        (assoc-in [:ctx/graphics :graphics/world-mouse-position] (viewport/unproject (:graphics/world-viewport graphics) mp))
        (assoc-in [:ctx/graphics :graphics/ui-mouse-position] (viewport/unproject ui-viewport mp)))))

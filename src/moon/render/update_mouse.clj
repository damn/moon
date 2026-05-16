(ns moon.render.update-mouse
  (:require [moon.stage :as stage]
            [moon.viewport :as viewport]
            [clojure.gdx.app :as app]
            [clojure.input :as input]))

(defn step
  [{:keys [ctx/app
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (let [mp (input/mouse-position (app/input app))]
    (-> ctx
        (assoc :ctx/world-mouse-position (viewport/unproject world-viewport mp))
        (assoc :ctx/ui-mouse-position (stage/unproject stage mp)))))

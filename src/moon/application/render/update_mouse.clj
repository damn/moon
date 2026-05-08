(ns moon.application.render.update-mouse
  (:require [moon.stage :as stage]
            [clojure.gdx.utils.viewport :as viewport]
            [com.badlogic.gdx.input :as input]))

(defn step
  [{:keys [ctx/input
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (let [mp (input/mouse-position input)]
    (-> ctx
        (assoc :ctx/world-mouse-position (viewport/unproject world-viewport mp))
        (assoc :ctx/ui-mouse-position (viewport/unproject (stage/viewport stage) mp)))))

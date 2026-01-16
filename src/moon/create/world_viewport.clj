(ns moon.create.world-viewport
  (:import (com.badlogic.gdx.graphics OrthographicCamera)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn step
  [{:keys [ctx/world-unit-scale]
    :as ctx}
   {:keys [width height]}]
  (assoc ctx :ctx/world-viewport
         (let [world-width  (* width world-unit-scale)
               world-height (* height world-unit-scale)]
           (FitViewport. world-width
                         world-height
                         (doto (OrthographicCamera.)
                           (.setToOrtho false world-width world-height))))))

(ns game.listener.render.draw-world.draw-cell-debug
  (:require [game.colors :refer [colors]]
            [game.draw :refer [draw-fn-filled-rectangle]]
            [moon.world :as world])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)
           (com.badlogic.gdx.math Vector3)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn draw-cell-debug
  [ctx shape-drawer ^Viewport world-viewport world
   show-cell-entities? show-cell-occupied? show-potential-field-colors?
   factions-iterations]
  (let [world @world
        ^OrthographicCamera camera (.getCamera world-viewport)
        plane-points (mapv (fn [^Vector3 v3]
                             [(.x v3) (.y v3) (.z v3)])
                           (.planePoints (.frustum camera)))
        frustum-points (take 4 plane-points)
        left-x   (apply min (map first  frustum-points))
        right-x  (apply max (map first  frustum-points))
        bottom-y (apply min (map second frustum-points))
        top-y    (apply max (map second frustum-points))
        tile-positions (for [x (range (int left-x) (int right-x))
                             y (range (int bottom-y) (+ 2 (int top-y)))]
                         [x y])]
    (doseq [[[x y] cell*] (world/cells-at world tile-positions)]
      (when (and @show-cell-entities? (seq (:entities cell*)))
        (draw-fn-filled-rectangle shape-drawer x y 1 1 (:colors/debug-cell-entities colors)))
      (when (and @show-cell-occupied? (seq (:occupied cell*)))
        (draw-fn-filled-rectangle shape-drawer x y 1 1 (:colors/debug-cell-occupied colors)))
      (when-let [faction @show-potential-field-colors?]
        (let [{:keys [distance]} (faction cell*)]
          (when distance
            (let [ratio (/ distance (factions-iterations faction))]
              (draw-fn-filled-rectangle shape-drawer x y 1 1 ((:colors/debug-potential-field colors) ratio)))))))))
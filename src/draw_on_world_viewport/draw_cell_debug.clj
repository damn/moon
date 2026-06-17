(ns draw-on-world-viewport.draw-cell-debug
  (:require [gdx.orthographic-camera.visible-tiles :refer [visible-tiles]]
            [game.constants :refer [show-potential-field-colors?
                                    show-cell-entities?
                                    show-cell-occupied?]]))

(defn f
  [{:keys [ctx/colors
           ctx/grid
           ctx/factions-iterations
           ctx/world-viewport]}]
  (apply concat
         (for [[x y] (visible-tiles (:viewport/camera world-viewport))
               :let [cell (grid [x y])]
               :when cell
               :let [cell* @cell]]
           [(when (and show-cell-entities? (seq (:entities cell*)))
              [:draw/filled-rectangle x y 1 1 (:colors/debug-cell-entities colors)])
            (when (and show-cell-occupied? (seq (:occupied cell*)))
              [:draw/filled-rectangle x y 1 1 (:colors/debug-cell-occupied colors)])
            (when-let [faction show-potential-field-colors?]
              (let [{:keys [distance]} (faction cell*)]
                (when distance
                  (let [ratio (/ distance (factions-iterations faction))]
                    [:draw/filled-rectangle x y 1 1 ((:colors/debug-potential-field colors) ratio)]))))])))

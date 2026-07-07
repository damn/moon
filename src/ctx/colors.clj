(ns ctx.colors
  (:require [clojure.color :as color]))

(defn step [_ctx]
  (let [outline-alpha 0.4]
    {
     :colors/mouseover-tile-air  (color/float-bits [1 1 0 0.5])
     :colors/mouseover-tile-none (color/float-bits [1 0 0 0.5])
     :colors/debug-body-outline-collides (color/float-bits [1 1 1 1])
     :colors/debug-body-outline (color/float-bits [0.5 0.5 0.5 1])
     :colors/debug-body-outline-render-error (color/float-bits [1 0 0 1])
     :colors/debug-cell-entities (color/float-bits [1 0 0 0.6])
     :colors/debug-cell-occupied (color/float-bits [0 0 1 0.6])
     :colors/debug-potential-field (fn [ratio]
                                     (color/float-bits [ratio (- 1 ratio) ratio 0.6]))
     :colors/target-all-line (color/float-bits [1 0 0 0.75])
     :colors/target-all-render (color/float-bits [1 0 0 0.5])
     :colors/target-entity-line (color/float-bits [1 0 0 0.75])
     :colors/target-entity-in-range (color/float-bits [1 0 0 0.5])
     :colors/target-entity-not-in-range (color/float-bits [1 1 0 0.5])
     :colors/enemy-color (color/float-bits [1 0 0 outline-alpha])
     :colors/friendly-color (color/float-bits [0 1 0 outline-alpha])
     :colors/neutral-color  (color/float-bits [1 1 1 outline-alpha])
     :colors/hp-bar (fn [ratio]
                      (let [ratio (float ratio)
                            color (cond
                                   (> ratio 0.75) :green
                                   (> ratio 0.5)  :darkgreen
                                   (> ratio 0.25) :yellow
                                   :else          :red)]
                        (color {:green     (color/float-bits [0 0.8 0 1])
                                :darkgreen (color/float-bits [0 0.5 0 1])
                                :yellow    (color/float-bits [0.5 0.5 0 1])
                                :red       (color/float-bits [0.5 0 0 1])})))
     :colors/hp-bar-rect (color/float-bits [0 0 0 1])
     :colors/temp-modifier (color/float-bits [0.5 0.5 0.5 0.4])
     :colors/active-skill-circle (color/float-bits [1 1 1 0.125])
     :colors/active-skill-sector (color/float-bits [1 1 1 0.5])
     :colors/stunned (color/float-bits [1 1 1 0.6])
     :colors/explored-tile (color/float-bits [0.5 0.5 0.5 1])
     :colors/visible-tile (color/float-bits [1 1 1 1])
     :colors/invisible-tile (color/float-bits [0 0 0 1])
     :colors/droppable-item (color/float-bits [0 0.6 0 0.8 1])
     :colors/not-allowed-drop-item (color/float-bits [0.6 0 0 0.8 1])
     :colors/item-rect (color/float-bits [0.5 0.5 0.5 1])
     }))

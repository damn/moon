(ns ctx.colors
  (:require [clojure.gdx.color.float-bits :as float-bits]))

(defn step [_ctx]
  (let [outline-alpha 0.4]
    {
     :colors/mouseover-tile-air  (float-bits/f [1 1 0 0.5])
     :colors/mouseover-tile-none (float-bits/f [1 0 0 0.5])
     :colors/debug-body-outline-collides (float-bits/f [1 1 1 1])
     :colors/debug-body-outline (float-bits/f [0.5 0.5 0.5 1])
     :colors/debug-body-outline-render-error (float-bits/f [1 0 0 1])
     :colors/debug-cell-entities (float-bits/f [1 0 0 0.6])
     :colors/debug-cell-occupied (float-bits/f [0 0 1 0.6])
     :colors/debug-potential-field (fn [ratio]
                                     (float-bits/f [ratio (- 1 ratio) ratio 0.6]))
     :colors/target-all-line (float-bits/f [1 0 0 0.75])
     :colors/target-all-render (float-bits/f [1 0 0 0.5])
     :colors/target-entity-line (float-bits/f [1 0 0 0.75])
     :colors/target-entity-in-range (float-bits/f [1 0 0 0.5])
     :colors/target-entity-not-in-range (float-bits/f [1 1 0 0.5])
     :colors/enemy-color (float-bits/f [1 0 0 outline-alpha])
     :colors/friendly-color (float-bits/f [0 1 0 outline-alpha])
     :colors/neutral-color  (float-bits/f [1 1 1 outline-alpha])
     :colors/hp-bar (fn [ratio]
                      (let [ratio (float ratio)
                            color (cond
                                   (> ratio 0.75) :green
                                   (> ratio 0.5)  :darkgreen
                                   (> ratio 0.25) :yellow
                                   :else          :red)]
                        (color {:green     (float-bits/f [0 0.8 0 1])
                                :darkgreen (float-bits/f [0 0.5 0 1])
                                :yellow    (float-bits/f [0.5 0.5 0 1])
                                :red       (float-bits/f [0.5 0 0 1])})))
     :colors/hp-bar-rect (float-bits/f [0 0 0 1])
     :colors/temp-modifier (float-bits/f [0.5 0.5 0.5 0.4])
     :colors/active-skill-circle (float-bits/f [1 1 1 0.125])
     :colors/active-skill-sector (float-bits/f [1 1 1 0.5])
     :colors/stunned (float-bits/f [1 1 1 0.6])
     :colors/explored-tile (float-bits/f [0.5 0.5 0.5 1])
     :colors/visible-tile (float-bits/f [1 1 1 1])
     :colors/invisible-tile (float-bits/f [0 0 0 1])
     :colors/droppable-item (float-bits/f [0 0.6 0 0.8 1])
     :colors/not-allowed-drop-item (float-bits/f [0.6 0 0 0.8 1])
     :colors/item-rect (float-bits/f [0.5 0.5 0.5 1])
     }))

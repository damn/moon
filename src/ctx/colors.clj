(ns ctx.colors
  (:require [clojure.color :refer [black white gray red]]
            [clojure.gdx :as gdx]))

(defn step [_ctx]
  (let [outline-alpha 0.4]
    {
     :colors/mouseover-tile-air  (gdx/float-bits [1 1 0 0.5])
     :colors/mouseover-tile-none (gdx/float-bits [1 0 0 0.5])
     :colors/debug-body-outline-collides (gdx/float-bits white)
     :colors/debug-body-outline (gdx/float-bits gray)
     :colors/debug-body-outline-render-error (gdx/float-bits red)
     :colors/debug-cell-entities (gdx/float-bits [1 0 0 0.6])
     :colors/debug-cell-occupied (gdx/float-bits [0 0 1 0.6])
     :colors/debug-potential-field (fn [ratio]
                                     (gdx/float-bits [ratio (- 1 ratio) ratio 0.6]))
     :colors/target-all-line (gdx/float-bits [1 0 0 0.75])
     :colors/target-all-render (gdx/float-bits [1 0 0 0.5])
     :colors/target-entity-line (gdx/float-bits [1 0 0 0.75])
     :colors/target-entity-in-range (gdx/float-bits [1 0 0 0.5])
     :colors/target-entity-not-in-range (gdx/float-bits [1 1 0 0.5])
     :colors/enemy-color (gdx/float-bits [1 0 0 outline-alpha])
     :colors/friendly-color (gdx/float-bits [0 1 0 outline-alpha])
     :colors/neutral-color  (gdx/float-bits [1 1 1 outline-alpha])
     :colors/hp-bar (fn [ratio]
                      (let [ratio (float ratio)
                            color (cond
                                   (> ratio 0.75) :green
                                   (> ratio 0.5)  :darkgreen
                                   (> ratio 0.25) :yellow
                                   :else          :red)]
                        (color {:green     (gdx/float-bits [0 0.8 0 1])
                                :darkgreen (gdx/float-bits [0 0.5 0 1])
                                :yellow    (gdx/float-bits [0.5 0.5 0 1])
                                :red       (gdx/float-bits [0.5 0 0 1])})))
     :colors/hp-bar-rect (gdx/float-bits black)
     :colors/temp-modifier (gdx/float-bits [0.5 0.5 0.5 0.4])
     :colors/active-skill-circle (gdx/float-bits [1 1 1 0.125])
     :colors/active-skill-sector (gdx/float-bits [1 1 1 0.5])
     :colors/stunned (gdx/float-bits [1 1 1 0.6])
     :colors/explored-tile (gdx/float-bits [0.5 0.5 0.5 1])
     :colors/visible-tile (gdx/float-bits [1 1 1 1])
     :colors/invisible-tile (gdx/float-bits [0 0 0 1])
     :colors/droppable-item (gdx/float-bits [0 0.6 0 0.8 1])
     :colors/not-allowed-drop-item (gdx/float-bits [0.6 0 0 0.8 1])
     :colors/item-rect (gdx/float-bits [0.5 0.5 0.5 1])
     }))

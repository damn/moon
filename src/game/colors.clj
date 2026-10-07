(ns game.colors
  (:import (com.badlogic.gdx.graphics Color)))

(defn- float-bits [[r g b a]]
  (Color/toFloatBits (float r) (float g) (float b) (float a)))

(def colors
  (let [outline-alpha 0.4]
    {:colors/mouseover-tile-air (float-bits [1 1 0 0.5])
     :colors/mouseover-tile-none (float-bits [1 0 0 0.5])
     :colors/debug-body-outline-collides (float-bits [1 1 1 1])
     :colors/debug-body-outline (float-bits [0.5 0.5 0.5 1])
     :colors/debug-body-outline-render-error (float-bits [1 0 0 1])
     :colors/debug-cell-entities (float-bits [1 0 0 0.6])
     :colors/debug-cell-occupied (float-bits [0 0 1 0.6])
     :colors/debug-potential-field (fn [ratio]
                                     (float-bits [ratio (- 1 ratio) ratio 0.6]))
     :colors/target-all-line (float-bits [1 0 0 0.75])
     :colors/target-all-render (float-bits [1 0 0 0.5])
     :colors/target-entity-line (float-bits [1 0 0 0.75])
     :colors/target-entity-in-range (float-bits [1 0 0 0.5])
     :colors/target-entity-not-in-range (float-bits [1 1 0 0.5])
     :colors/enemy-color (float-bits [1 0 0 outline-alpha])
     :colors/friendly-color (float-bits [0 1 0 outline-alpha])
     :colors/neutral-color (float-bits [1 1 1 outline-alpha])
     :colors/hp-bar (fn [ratio]
                      (cond
                        (> ratio 0.75) (float-bits [0 0.8 0 1])
                        (> ratio 0.5) (float-bits [0 0.5 0 1])
                        (> ratio 0.25) (float-bits [0.5 0.5 0 1])
                        :else (float-bits [0.5 0 0 1])))
     :colors/hp-bar-rect (float-bits [0 0 0 1])
     :colors/temp-modifier (float-bits [0.5 0.5 0.5 0.4])
     :colors/active-skill-circle (float-bits [1 1 1 0.125])
     :colors/active-skill-sector (float-bits [1 1 1 0.5])
     :colors/stunned (float-bits [1 1 1 0.6])
     :colors/explored-tile (float-bits [0.5 0.5 0.5 1])
     :colors/visible-tile (float-bits [1 1 1 1])
     :colors/invisible-tile (float-bits [0 0 0 1])
     :colors/droppable-item (float-bits [0 0.6 0 0.8])
     :colors/not-allowed-drop-item (float-bits [0.6 0 0 0.8])
     :colors/item-rect (float-bits [0.5 0.5 0.5 1])}))

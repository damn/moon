(ns clojure.moon.create-colors
  (:require [com.badlogic.gdx.graphics.color :as color]))

(def colors
  (let [outline-alpha 0.4]
    {:colors/mouseover-tile-air (color/toFloatBits [1 1 0 0.5])
     :colors/mouseover-tile-none (color/toFloatBits [1 0 0 0.5])
     :colors/debug-body-outline-collides (color/toFloatBits [1 1 1 1])
     :colors/debug-body-outline (color/toFloatBits [0.5 0.5 0.5 1])
     :colors/debug-body-outline-render-error (color/toFloatBits [1 0 0 1])
     :colors/debug-cell-entities (color/toFloatBits [1 0 0 0.6])
     :colors/debug-cell-occupied (color/toFloatBits [0 0 1 0.6])
     :colors/debug-potential-field (fn [ratio]
                                     (color/toFloatBits [ratio (- 1 ratio) ratio 0.6]))
     :colors/target-all-line (color/toFloatBits [1 0 0 0.75])
     :colors/target-all-render (color/toFloatBits [1 0 0 0.5])
     :colors/target-entity-line (color/toFloatBits [1 0 0 0.75])
     :colors/target-entity-in-range (color/toFloatBits [1 0 0 0.5])
     :colors/target-entity-not-in-range (color/toFloatBits [1 1 0 0.5])
     :colors/enemy-color (color/toFloatBits [1 0 0 outline-alpha])
     :colors/friendly-color (color/toFloatBits [0 1 0 outline-alpha])
     :colors/neutral-color (color/toFloatBits [1 1 1 outline-alpha])
     :colors/hp-bar (fn [ratio]
                      (let [ratio (float ratio)
                            color (cond
                                    (> ratio 0.75) :green
                                    (> ratio 0.5) :darkgreen
                                    (> ratio 0.25) :yellow
                                    :else :red)]
                        (color {:green (color/toFloatBits [0 0.8 0 1])
                                :darkgreen (color/toFloatBits [0 0.5 0 1])
                                :yellow (color/toFloatBits [0.5 0.5 0 1])
                                :red (color/toFloatBits [0.5 0 0 1])})))
     :colors/hp-bar-rect (color/toFloatBits [0 0 0 1])
     :colors/temp-modifier (color/toFloatBits [0.5 0.5 0.5 0.4])
     :colors/active-skill-circle (color/toFloatBits [1 1 1 0.125])
     :colors/active-skill-sector (color/toFloatBits [1 1 1 0.5])
     :colors/stunned (color/toFloatBits [1 1 1 0.6])
     :colors/explored-tile (color/toFloatBits [0.5 0.5 0.5 1])
     :colors/visible-tile (color/toFloatBits [1 1 1 1])
     :colors/invisible-tile (color/toFloatBits [0 0 0 1])
     :colors/droppable-item (color/toFloatBits [0 0.6 0 0.8 1])
     :colors/not-allowed-drop-item (color/toFloatBits [0.6 0 0 0.8 1])
     :colors/item-rect (color/toFloatBits [0.5 0.5 0.5 1])}))

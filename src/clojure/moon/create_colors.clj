(ns clojure.moon.create-colors
  (:require [clojure.float-bits]))

(def colors
  (let [outline-alpha 0.4]
    {:colors/mouseover-tile-air (clojure.float-bits/f [1 1 0 0.5])
     :colors/mouseover-tile-none (clojure.float-bits/f [1 0 0 0.5])
     :colors/debug-body-outline-collides (clojure.float-bits/f [1 1 1 1])
     :colors/debug-body-outline (clojure.float-bits/f [0.5 0.5 0.5 1])
     :colors/debug-body-outline-render-error (clojure.float-bits/f [1 0 0 1])
     :colors/debug-cell-entities (clojure.float-bits/f [1 0 0 0.6])
     :colors/debug-cell-occupied (clojure.float-bits/f [0 0 1 0.6])
     :colors/debug-potential-field (fn [ratio]
                                     (clojure.float-bits/f [ratio (- 1 ratio) ratio 0.6]))
     :colors/target-all-line (clojure.float-bits/f [1 0 0 0.75])
     :colors/target-all-render (clojure.float-bits/f [1 0 0 0.5])
     :colors/target-entity-line (clojure.float-bits/f [1 0 0 0.75])
     :colors/target-entity-in-range (clojure.float-bits/f [1 0 0 0.5])
     :colors/target-entity-not-in-range (clojure.float-bits/f [1 1 0 0.5])
     :colors/enemy-color (clojure.float-bits/f [1 0 0 outline-alpha])
     :colors/friendly-color (clojure.float-bits/f [0 1 0 outline-alpha])
     :colors/neutral-color (clojure.float-bits/f [1 1 1 outline-alpha])
     :colors/hp-bar (fn [ratio]
                      (let [ratio (float ratio)
                            color (cond
                                    (> ratio 0.75) :green
                                    (> ratio 0.5) :darkgreen
                                    (> ratio 0.25) :yellow
                                    :else :red)]
                        (color {:green (clojure.float-bits/f [0 0.8 0 1])
                                :darkgreen (clojure.float-bits/f [0 0.5 0 1])
                                :yellow (clojure.float-bits/f [0.5 0.5 0 1])
                                :red (clojure.float-bits/f [0.5 0 0 1])})))
     :colors/hp-bar-rect (clojure.float-bits/f [0 0 0 1])
     :colors/temp-modifier (clojure.float-bits/f [0.5 0.5 0.5 0.4])
     :colors/active-skill-circle (clojure.float-bits/f [1 1 1 0.125])
     :colors/active-skill-sector (clojure.float-bits/f [1 1 1 0.5])
     :colors/stunned (clojure.float-bits/f [1 1 1 0.6])
     :colors/explored-tile (clojure.float-bits/f [0.5 0.5 0.5 1])
     :colors/visible-tile (clojure.float-bits/f [1 1 1 1])
     :colors/invisible-tile (clojure.float-bits/f [0 0 0 1])
     :colors/droppable-item (clojure.float-bits/f [0 0.6 0 0.8 1])
     :colors/not-allowed-drop-item (clojure.float-bits/f [0.6 0 0 0.8 1])
     :colors/item-rect (clojure.float-bits/f [0.5 0.5 0.5 1])}))

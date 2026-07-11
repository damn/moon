(ns clojure.levels.modules.create-scaled-grid
  (:require [moon.g2d :as g2d]))

(defn f [w]
  (assoc w :scaled-grid (g2d/scale-by (:grid w) (:scale w))))

(ns moon.modules.create-scaled-grid
  (:require [clojure.grid2d :as g2d]))

(defn step [w]
  (assoc w :scaled-grid (g2d/scale-grid (:grid w) (:scale w))))

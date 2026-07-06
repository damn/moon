(ns world-fns.modules.create-scaled-grid
  (:require [clojure.grid2d.scale-grid :as scale-grid]))

(defn f [w]
  (assoc w :scaled-grid (scale-grid/f (:grid w) (:scale w))))

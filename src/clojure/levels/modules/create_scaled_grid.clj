(ns clojure.levels.modules.create-scaled-grid
  (:require [clojure.scale-grid :as scale-grid]))

(defn f [w]
  (assoc w :scaled-grid (scale-grid/f (:grid w) (:scale w))))

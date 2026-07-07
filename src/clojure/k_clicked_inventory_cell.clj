(ns clojure.k-clicked-inventory-cell
  (:require [clojure.clicked-inventory-cell-player-idle :as clicked-inventory-cell-player-idle]
            [clojure.clicked-inventory-cell-player-item-on-cursor :as clicked-inventory-cell-player-item-on-cursor]))

(def k->clicked-inventory-cell
  {:player-item-on-cursor clicked-inventory-cell-player-item-on-cursor/f
   :player-idle clicked-inventory-cell-player-idle/f})

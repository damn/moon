(ns clojure.k-clicked-inventory-cell
  (:require [clojure.k-clicked-inventory-cell.player-idle :as player-idle]
            [clojure.k-clicked-inventory-cell.player-item-on-cursor :as player-item-on-cursor]))

(def k->clicked-inventory-cell
  {:player-item-on-cursor player-item-on-cursor/f
   :player-idle player-idle/f})

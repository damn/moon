(ns clojure.moon.clicked-inventory-cell
  (:require [clojure.moon.clicked-inventory-cell.player-idle :as player-idle]
            [clojure.moon.clicked-inventory-cell.player-item-on-cursor :as player-item-on-cursor]))

(def k->clicked-inventory-cell
  {:player-item-on-cursor player-item-on-cursor/f
   :player-idle player-idle/f})

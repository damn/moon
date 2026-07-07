(ns clojure.k-cursor
  (:require [clojure.cursor-player-idle :as cursor-player-idle]))

(def k->cursor
  {:player-item-on-cursor :cursors/hand-grab
   :player-dead :cursors/black-x
   :active-skill :cursors/sandclock
   :stunned :cursors/denied
   :player-moving :cursors/walking
   :player-idle cursor-player-idle/f})

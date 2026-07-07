(ns clojure.k-state-enter
  (:require [clojure.enter-active-skill :as enter-active-skill]
            [clojure.enter-npc-moving :as enter-npc-moving]
            [clojure.enter-player-item-on-cursor :as enter-player-item-on-cursor]
            [clojure.enter-player-moving :as enter-player-moving]
            [clojure.npc-dead :as npc-dead]
            [clojure.player-dead :as player-dead]))

(def k->state-enter
  {:player-item-on-cursor enter-player-item-on-cursor/f
   :active-skill enter-active-skill/f
   :npc-dead npc-dead/f
   :player-moving enter-player-moving/f
   :player-dead player-dead/f
   :npc-moving enter-npc-moving/f})

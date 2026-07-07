(ns clojure.k-state-enter
  (:require [clojure.k-state-enter.active-skill :as active-skill]
            [clojure.k-state-enter.npc-dead :as npc-dead]
            [clojure.k-state-enter.npc-moving :as npc-moving]
            [clojure.k-state-enter.player-dead :as player-dead]
            [clojure.k-state-enter.player-item-on-cursor :as player-item-on-cursor]
            [clojure.k-state-enter.player-moving :as player-moving]))

(def k->state-enter
  {:player-item-on-cursor player-item-on-cursor/f
   :active-skill active-skill/f
   :npc-dead npc-dead/f
   :player-moving player-moving/f
   :player-dead player-dead/f
   :npc-moving npc-moving/f})

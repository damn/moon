(ns clojure.k-state-exit
  (:require [clojure.exit-npc-moving :as exit-npc-moving]
            [clojure.exit-npc-sleeping :as exit-npc-sleeping]
            [clojure.exit-player-item-on-cursor :as exit-player-item-on-cursor]
            [clojure.exit-player-moving :as exit-player-moving]))

(def k->state-exit
  {:player-item-on-cursor exit-player-item-on-cursor/f
   :player-moving exit-player-moving/f
   :npc-sleeping exit-npc-sleeping/f
   :npc-moving exit-npc-moving/f})

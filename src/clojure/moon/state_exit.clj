(ns clojure.moon.state-exit
  (:require [clojure.moon.state-exit.npc-moving :as npc-moving]
            [clojure.moon.state-exit.npc-sleeping :as npc-sleeping]
            [clojure.moon.state-exit.player-item-on-cursor :as player-item-on-cursor]
            [clojure.moon.state-exit.player-moving :as player-moving]))

(def k->state-exit
  {:player-item-on-cursor player-item-on-cursor/f
   :player-moving player-moving/f
   :npc-sleeping npc-sleeping/f
   :npc-moving npc-moving/f})

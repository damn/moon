(ns clojure.moon.state-enter
  (:require [clojure.moon.state-enter.active-skill :as active-skill]
            [clojure.moon.state-enter.npc-dead :as npc-dead]
            [clojure.moon.state-enter.npc-moving :as npc-moving]
            [clojure.moon.state-enter.player-dead :as player-dead]
            [clojure.moon.state-enter.player-item-on-cursor :as player-item-on-cursor]
            [clojure.moon.state-enter.player-moving :as player-moving]))

(def k->state-enter
  {:player-item-on-cursor player-item-on-cursor/f
   :active-skill active-skill/f
   :npc-dead npc-dead/f
   :player-moving player-moving/f
   :player-dead player-dead/f
   :npc-moving npc-moving/f})

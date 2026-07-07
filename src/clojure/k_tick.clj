(ns clojure.k-tick
  (:require [clojure.alert-friendlies-after-duration :as alert-friendlies-after-duration]
            [clojure.movement :as movement]
            [clojure.npc-idle :as npc-idle]
            [clojure.tick-active-skill :as tick-active-skill]
            [clojure.tick-animation :as tick-animation]
            [clojure.tick-delete-after-duration :as tick-delete-after-duration]
            [clojure.tick-npc-moving :as tick-npc-moving]
            [clojure.tick-npc-sleeping :as tick-npc-sleeping]
            [clojure.tick-projectile-collision :as tick-projectile-collision]
            [clojure.tick-skills :as tick-skills]
            [clojure.tick-string-effect :as tick-string-effect]
            [clojure.tick-stunned :as tick-stunned]
            [clojure.tick-temp-modifier :as tick-temp-modifier]))

(def k->tick
  {:entity/animation tick-animation/f
   :entity/alert-friendlies-after-duration alert-friendlies-after-duration/f
   :entity/string-effect tick-string-effect/f
   :entity/skills tick-skills/f
   :entity/temp-modifier tick-temp-modifier/f
   :entity/projectile-collision tick-projectile-collision/f
   :active-skill tick-active-skill/f
   :entity/delete-after-duration tick-delete-after-duration/f
   :stunned tick-stunned/f
   :npc-moving tick-npc-moving/f
   :npc-sleeping tick-npc-sleeping/f
   :npc-idle npc-idle/f
   :entity/movement movement/f})

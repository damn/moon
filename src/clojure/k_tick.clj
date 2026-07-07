(ns clojure.k-tick
  (:require [clojure.k-tick.alert-friendlies-after-duration :as alert-friendlies-after-duration]
            [clojure.k-tick.animation :as animation]
            [clojure.k-tick.active-skill :as active-skill]
            [clojure.k-tick.delete-after-duration :as delete-after-duration]
            [clojure.k-tick.movement :as movement]
            [clojure.k-tick.npc-idle :as npc-idle]
            [clojure.k-tick.npc-moving :as npc-moving]
            [clojure.k-tick.npc-sleeping :as npc-sleeping]
            [clojure.k-tick.projectile-collision :as projectile-collision]
            [clojure.k-tick.skills :as skills]
            [clojure.k-tick.string-effect :as string-effect]
            [clojure.k-tick.stunned :as stunned]
            [clojure.k-tick.temp-modifier :as temp-modifier]))

(def k->tick
  {:entity/animation animation/f
   :entity/alert-friendlies-after-duration alert-friendlies-after-duration/f
   :entity/string-effect string-effect/f
   :entity/skills skills/f
   :entity/temp-modifier temp-modifier/f
   :entity/projectile-collision projectile-collision/f
   :active-skill active-skill/f
   :entity/delete-after-duration delete-after-duration/f
   :stunned stunned/f
   :npc-moving npc-moving/f
   :npc-sleeping npc-sleeping/f
   :npc-idle npc-idle/f
   :entity/movement movement/f})

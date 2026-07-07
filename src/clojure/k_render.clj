(ns clojure.k-render
  (:require [clojure.k-render.active-skill :as active-skill]
            [clojure.k-render.animation :as animation]
            [clojure.k-render.clickable :as clickable]
            [clojure.k-render.image :as image]
            [clojure.k-render.line-render :as line-render]
            [clojure.k-render.mouseover :as mouseover]
            [clojure.k-render.npc-sleeping :as npc-sleeping]
            [clojure.k-render.player-item-on-cursor :as player-item-on-cursor]
            [clojure.k-render.stats :as stats]
            [clojure.k-render.string-effect :as string-effect]
            [clojure.k-render.stunned :as stunned]
            [clojure.k-render.temp-modifier :as temp-modifier]))

(def k->render
  {:entity/clickable clickable/f
   :player-item-on-cursor player-item-on-cursor/f
   :entity/animation animation/f
   :entity/image image/f
   :entity/line-render line-render/f
   :entity/mouseover? mouseover/f
   :entity/stats stats/f
   :entity/string-effect string-effect/f
   :entity/temp-modifier temp-modifier/f
   :active-skill active-skill/f
   :npc-sleeping npc-sleeping/f
   :stunned stunned/f})

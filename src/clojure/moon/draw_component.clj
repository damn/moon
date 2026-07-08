(ns clojure.moon.draw-component
  (:require [clojure.moon.render.active-skill :as active-skill]
            [clojure.moon.render.animation :as animation]
            [clojure.moon.render.clickable :as clickable]
            [clojure.moon.render.image :as image]
            [clojure.moon.render.line-render :as line-render]
            [clojure.moon.render.mouseover :as mouseover]
            [clojure.moon.render.npc-sleeping :as npc-sleeping]
            [clojure.moon.render.player-item-on-cursor :as player-item-on-cursor]
            [clojure.moon.render.stats :as stats]
            [clojure.moon.render.string-effect :as string-effect]
            [clojure.moon.render.stunned :as stunned]
            [clojure.moon.render.temp-modifier :as temp-modifier]))

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

(defn draw-component
  [ctx entity k v]
  ((k->render k) v entity ctx))

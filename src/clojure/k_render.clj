(ns clojure.k-render
  (:require [clojure.clickable :as clickable]
            [clojure.line-render :as line-render]
            [clojure.mouseover :as mouseover]
            [clojure.render-active-skill :as render-active-skill]
            [clojure.render-animation :as render-animation]
            [clojure.render-image :as render-image]
            [clojure.render-npc-sleeping :as render-npc-sleeping]
            [clojure.render-player-item-on-cursor :as render-player-item-on-cursor]
            [clojure.render-stats :as render-stats]
            [clojure.render-string-effect :as render-string-effect]
            [clojure.render-stunned :as render-stunned]
            [clojure.render-temp-modifier :as render-temp-modifier]))

(def k->render
  {:entity/clickable clickable/f
   :player-item-on-cursor render-player-item-on-cursor/f
   :entity/animation render-animation/f
   :entity/image render-image/f
   :entity/line-render line-render/f
   :entity/mouseover? mouseover/f
   :entity/stats render-stats/f
   :entity/string-effect render-string-effect/f
   :entity/temp-modifier render-temp-modifier/f
   :active-skill render-active-skill/f
   :npc-sleeping render-npc-sleeping/f
   :stunned render-stunned/f})

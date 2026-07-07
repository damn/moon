(ns clojure.handle-melee-damage
  (:require [clojure.handle :as handle]
            [clojure.creature-melee-damage :as melee-damage]))

(defn f
  [_ {:keys [effect/source] :as effect-ctx} ctx]
  ; TODO AT EFFECT CREATION MAKE
  ; same @ applicable
  (handle/f [:effects.target/damage (melee-damage/f @source)] effect-ctx ctx))

(ns effects-target.applicable.convert
  (:require [moon.faction :as faction]))

(defn f
  [_ {:keys [effect/source effect/target]}]
  (and target
       (= (:entity/faction @target)
          (faction/enemy (:entity/faction @source)))))

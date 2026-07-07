(ns clojure.update-effect-ctx
  (:require [clojure.line-of-sight :as line-of-sight?]))

(defn f
  [raycaster {:keys [effect/source effect/target] :as effect-ctx}]
  (if (and target
           (not (:entity/destroyed? @target))
           (line-of-sight?/f raycaster @source @target))
    effect-ctx
    (dissoc effect-ctx :effect/target)))

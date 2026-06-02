(ns entity.tick.active-skill
  (:require [game.effect :as effect]
            [moon.raycaster :as raycaster]
            [clojure.timer.stopped :refer [stopped?]]))

(defn- update-effect-ctx
  [raycaster {:keys [effect/source effect/target] :as effect-ctx}]
  (if (and target
           (not (:entity/destroyed? @target))
           (raycaster/line-of-sight? raycaster @source @target))
    effect-ctx
    (dissoc effect-ctx :effect/target)))

(defn f
  [{:keys [skill effect-ctx counter]}
   eid
   {:keys [ctx/elapsed-time
           ctx/raycaster]}]
  (let [effect-ctx (update-effect-ctx raycaster effect-ctx)]
    (cond
     (not (seq (filter #(effect/applicable? % effect-ctx)
                       (:skill/effects skill))))
     [[:tx/event eid :action-done]]

     (stopped? elapsed-time counter)
     [[:tx/effect effect-ctx (:skill/effects skill)]
      [:tx/event eid :action-done]])))

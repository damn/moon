(ns entity.tick.active-skill
  (:require [moon.effect.is-applicable :as applicable?]
            [moon.raycaster.line-of-sight :as line-of-sight?]
            [timer.stopped :refer [stopped?]]))

(defn- update-effect-ctx
  [raycaster {:keys [effect/source effect/target] :as effect-ctx}]
  (if (and target
           (not (:entity/destroyed? @target))
           (line-of-sight?/f raycaster @source @target))
    effect-ctx
    (dissoc effect-ctx :effect/target)))

(defn f
  [{:keys [skill effect-ctx counter]}
   eid
   {:keys [ctx/elapsed-time
           ctx/raycaster]}]
  (let [effect-ctx (update-effect-ctx raycaster effect-ctx)]
    (cond
     (not (seq (filter #(applicable?/f % effect-ctx)
                       (:skill/effects skill))))
     [[:tx/event eid :action-done]]

     (stopped? elapsed-time counter)
     [[:tx/effect effect-ctx (:skill/effects skill)]
      [:tx/event eid :action-done]])))

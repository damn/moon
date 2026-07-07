(ns ctx.entity.tick.active-skill
  (:require [moon.effect.is-applicable :as applicable?]
            [moon.update-effect-ctx :as update-effect-ctx]
            [clojure.stopped :refer [stopped?]]))

(defn f
  [{:keys [skill effect-ctx counter]}
   eid
   {:keys [ctx/elapsed-time
           ctx/raycaster]}]
  (let [effect-ctx (update-effect-ctx/f raycaster effect-ctx)]
    (cond
     (not (seq (filter #(applicable?/f % effect-ctx)
                       (:skill/effects skill))))
     [[:tx/event eid :action-done]]

     (stopped? elapsed-time counter)
     [[:tx/effect effect-ctx (:skill/effects skill)]
      [:tx/event eid :action-done]])))

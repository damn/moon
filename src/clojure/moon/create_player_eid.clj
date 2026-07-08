(ns clojure.moon.create-player-eid)

(defn f [ctx]
  (let [eid (get @(:ctx/entity-ids ctx) 1)]
    (assert (:entity/player? @eid))
    (assoc ctx :ctx/player-eid eid)))

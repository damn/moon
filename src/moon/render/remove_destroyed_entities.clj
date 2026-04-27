(ns moon.render.remove-destroyed-entities
  (:require [moon.entity :as entity]
            [moon.txs :as txs]))

(defn step
  [ctx]
  (txs/handle! ctx (mapcat
                    (fn [eid]
                      (cons
                       [:tx/unregister-eid eid]
                       (mapcat (fn [[k v]]
                                 (entity/destroy [k v] eid))
                               @eid)))
                    (filter (comp :entity/destroyed? deref)
                            (vals @(:ctx/entity-ids ctx)))))
  ctx)

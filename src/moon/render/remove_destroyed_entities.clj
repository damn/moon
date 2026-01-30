(ns moon.render.remove-destroyed-entities
  (:require [moon.ctx :as ctx]
            [moon.entity :as entity]))

(defn do!
  [ctx]
  (ctx/handle! ctx (mapcat
                    (fn [eid]
                      (cons
                       [:tx/unregister-eid eid]
                       (mapcat (fn [[k v]]
                                 (entity/destroy [k v] eid))
                               @eid)))
                    (filter (comp :entity/destroyed? deref)
                            (vals @(:ctx/entity-ids ctx)))))
  ctx)

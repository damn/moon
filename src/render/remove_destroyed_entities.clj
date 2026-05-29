(ns render.remove-destroyed-entities
  (:require [game.ctx :as ctx]
            [game.entity :as entity]))

(defn step
  [ctx]
  (ctx/do! ctx (mapcat
                (fn [eid]
                  (cons
                   [:tx/unregister-eid eid]
                   (mapcat (fn [[k v]]
                             (entity/destroy [k v] eid))
                           @eid)))
                (filter (comp :entity/destroyed? deref)
                        (vals @(:ctx/entity-ids ctx)))))
  ctx)

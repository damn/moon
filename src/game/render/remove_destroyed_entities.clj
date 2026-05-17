(ns game.render.remove-destroyed-entities
  (:require [moon.entity :as entity]
            [moon.txs :as txs]))

(defmethod entity/destroy :entity/destroy-audiovisual
  [[_ audiovisuals-id] eid]
  [[:tx/audiovisual
    (:body/position (:entity/body @eid))
    audiovisuals-id]])

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

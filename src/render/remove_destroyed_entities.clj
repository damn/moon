(ns render.remove-destroyed-entities
  (:require render.remove-destroyed-entities.destroy-audiovisual
            [game.ctx.do :refer [do!]]))

(def k->destroy
  {
   :entity/destroy-audiovisual render.remove-destroyed-entities.destroy-audiovisual/f
   }
  )

(defn step
  [ctx]
  (do! ctx
       (mapcat
        (fn [eid]
          (cons
           [:tx/unregister-eid eid]
           (mapcat (fn [[k v]]
                     (if-let [f (k->destroy k)]
                       (f v eid)
                       nil))
                   @eid)))
        (filter (comp :entity/destroyed? deref)
                (vals @(:ctx/entity-ids ctx)))))
  ctx)

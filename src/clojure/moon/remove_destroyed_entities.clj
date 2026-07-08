(ns clojure.moon.remove-destroyed-entities
  (:require [clojure.moon.ctx-do :refer [do!]]))

(def k->destroy
  {
   :entity/destroy-audiovisual
   (fn
     [audiovisuals-id eid]
     [[:tx/audiovisual (:body/position (:entity/body @eid)) audiovisuals-id]])
   })

(defn f [ctx]
  (do! ctx
       (mapcat
        (fn [eid]
          (cons
           [:tx/unregister-eid eid]
           (mapcat (fn [[k v]]
                     (if-let [destroy-fn (k->destroy k)]
                       (destroy-fn v eid)
                       nil))
                   @eid)))
        (filter (comp :entity/destroyed? deref)
                (vals @(:ctx/entity-ids ctx)))))
  ctx)

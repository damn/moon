(ns clojure.remove-destroyed-entities
  (:require [clojure.ctx-do :refer [do!]]
            [clojure.k-destroy :refer [k->destroy]]))

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

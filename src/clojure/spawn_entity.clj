(ns clojure.spawn-entity
  (:require [clojure.records-entity :as entity]
            [clojure.create-component :refer [create-component]]
            [clojure.after-create-component :refer [after-create-component]]
            [clojure.register-eid :as register-eid]))

(defn do!
  [ctx entity]
  (let [entity (reduce (fn [m [k v]]
                         (assoc m k (create-component ctx k v)))
                       {}
                       entity)
        entity (merge (entity/map->R {}) entity)
        eid (atom entity)]
    (register-eid/do! ctx eid)
    (mapcat (fn [component]
              (after-create-component ctx eid component))
            @eid)))

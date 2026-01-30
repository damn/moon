(ns moon.tx.spawn-entity
  (:require [moon.content-grid :as content-grid]
            [moon.entity :as entity]
            [moon.grid :as grid]
            [qrecord.core :as q]))

(q/defrecord Entity [entity/body])

(defn do! [ctx entity]
  (let [

        ; TODO this can do before ? @ property/entity/db/denormalize?
        ; or is okay here and can re-use at 'tx/assoc' ? tx/assoc-in ??
        entity (reduce (fn [m [k v]]
                         (assoc m k (entity/create [k v] ctx))) ; TODO state doesnt impl this? 2 creates?
                       {}
                       entity)

        ; The record can be given as of the type?
        ; e.g. from database item 'moon.info/text' on an item which is resolved at db get
        ; into a record Item with behaviour
        entity (merge (map->Entity {}) entity)
        eid (atom entity)]


    ; (ctx/add-entity! ctx entity)  ; <- returns eid

    (assert (and (not (contains? @eid :entity/id))))
    (let [id (swap! (:ctx/id-counter ctx) inc)]
      (assert (number? id))
      (swap! eid assoc :entity/id id)
      (swap! (:ctx/entity-ids ctx) assoc id eid))

    (assert (:entity/body @eid)) ; -< inside content grid
    (content-grid/add-entity! (:ctx/content-grid ctx) eid)

    (assert (:entity/body @eid)) ; <- inside the grid add fn ?
    (when (:body/collides? (:entity/body @eid))
      (assert (grid/valid-position? (:ctx/grid ctx) (:entity/body @eid) (:entity/id @eid))))
    (grid/set-touched-cells! (:ctx/grid ctx) eid)
    (when (:body/collides? (:entity/body @eid)) ; entity/collides? separate fooziboosh, no 'when' just a callback?
      (grid/set-occupied-cells! (:ctx/grid ctx) eid))


    (mapcat #(entity/after-create % eid ctx) @eid)))

; TODO testability / protocols ? / ctx/register-new-entity?
; TODO after-create code smell ? can do before? just player callbacks? simple fns?

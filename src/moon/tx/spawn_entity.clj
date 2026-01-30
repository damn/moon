(ns moon.tx.spawn-entity
  (:require [malli.core :as m]
            [malli.utils :as mu]
            [moon.content-grid :as content-grid]
            [moon.entity :as entity]
            [moon.grid :as grid]
            [qrecord.core :as q]))

; TODO this really does nothing, but could remove default behavior of create/after-create
; ...
(def ^:private components-schema
  (m/schema [:map {:closed true}
             [:entity/body :some]
             [:entity/image {:optional true} :some]
             [:entity/animation {:optional true} :some]
             [:entity/alert-friendlies-after-duration {:optional true} :some]
             [:entity/line-render {:optional true} :some]
             [:entity/delete-after-duration {:optional true} :some]
             [:entity/destroy-audiovisual {:optional true} :some]
             [:entity/fsm {:optional true} :some]
             [:entity/player? {:optional true} :some]
             [:entity/free-skill-points {:optional true} :some]
             [:entity/click-distance-tiles {:optional true} :some]
             [:entity/clickable {:optional true} :some]
             [:property/id {:optional true} :some]
             [:property/pretty-name {:optional true} :some]
             [:creature/level {:optional true} :some]
             [:entity/faction {:optional true} :some]
             [:entity/species {:optional true} :some]
             [:entity/movement {:optional true} :some]
             [:entity/skills {:optional true} :some]
             [:entity/stats {:optional true} :some]
             [:entity/inventory    {:optional true} :some]
             [:entity/item {:optional true} :some]
             [:entity/projectile-collision {:optional true} :some]]))

(q/defrecord Entity [entity/body])

(defn do! [ctx entity]
  (mu/validate-humanize components-schema entity)
  (let [

        ; TODO this can do before ? @ property/entity/db/denormalize?
        ; or is okay here and can re-use at 'tx/assoc' ? tx/assoc-in ??
        entity (reduce (fn [m [k v]]
                         (assoc m k (entity/create [k v] ctx)))
                       {}
                       entity)

        ; this part of add to entity/ids ?
        _ (assert (and (not (contains? entity :entity/id))))
        entity (assoc entity :entity/id (swap! (:ctx/id-counter ctx) inc))

        ; The record can be given as of the type?
        ; e.g. from database item 'moon.info/text' on an item which is resolved at db get
        ; into a record Item with behaviour
        entity (merge (map->Entity {}) entity)
        eid (atom entity)]


    (let [id (:entity/id @eid)]
      (assert (number? id))
      (swap! (:ctx/entity-ids ctx) assoc id eid))

    (content-grid/add-entity! (:ctx/content-grid ctx) eid)

    (when (:body/collides? (:entity/body @eid))
      (assert (grid/valid-position? (:ctx/grid ctx) (:entity/body @eid) (:entity/id @eid))))
    (grid/set-touched-cells! (:ctx/grid ctx) eid)
    (when (:body/collides? (:entity/body @eid)) ; entity/collides? separate fooziboosh, no 'when' just a callback?
      (grid/set-occupied-cells! (:ctx/grid ctx) eid))


    (mapcat #(entity/after-create % eid ctx) @eid)))

; TODO testability / protocols ? / ctx/register-new-entity?
; TODO after-create code smell ? can do before? just player callbacks? simple fns?

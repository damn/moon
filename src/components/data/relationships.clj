(ns components.data.relationships
  (:require [core.component :refer [defcomponent]]
            [core.info :as info]
            [core.context :as ctx]
            [core.data :as data]
            [core.property :as property]
            [gdx.scene2d.actor :as actor]
            [gdx.scene2d.group :as group]
            [gdx.scene2d.ui :as ui]))

; TODO schemas not checking if that property exists in db...
; https://github.com/damn/core/issues/59

(defcomponent :one-to-many
  (data/->value [[_ property-type]]
    {:schema [:set [:qualified-keyword {:namespace (property/property-type->id-namespace property-type)}]]
     :linked-property-type property-type}))

(defmethod data/edn->value :one-to-many [_ property-ids ctx]
  (map (partial ctx/property ctx) property-ids))

(defcomponent :one-to-one
  (data/->value [[_ property-type]]
    {:schema [:qualified-keyword {:namespace (property/property-type->id-namespace property-type)}]
     :linked-property-type property-type}))

(defmethod data/edn->value :one-to-one [_ property-id ctx]
  (ctx/property ctx property-id))

(defn- add-one-to-many-rows [ctx table property-type property-ids]
  (let [redo-rows (fn [ctx property-ids]
                    (group/clear-children! table)
                    (add-one-to-many-rows ctx table property-type property-ids)
                    (actor/pack-ancestor-window! table))]
    (ui/add-rows!
     table
     [[(ui/->text-button ctx "+"
                         (fn [ctx]
                           (let [window (ui/->window {:title "Choose"
                                                      :modal? true
                                                      :close-button? true
                                                      :center? true
                                                      :close-on-escape? true})
                                 clicked-id-fn (fn [ctx id]
                                                 (actor/remove! window)
                                                 (redo-rows ctx (conj property-ids id))
                                                 ctx)]
                             (ui/add! window (ctx/->overview-table ctx property-type clicked-id-fn))
                             (.pack window)
                             (ctx/add-to-stage! ctx window))))]
      (for [property-id property-ids]
        (let [property (ctx/property ctx property-id)
              image-widget (ui/->image-widget (property/->image property)
                                              {:id property-id})]
          (actor/add-tooltip! image-widget #(info/->text property %))
          image-widget))
      (for [id property-ids]
        (ui/->text-button ctx "-" #(do (redo-rows % (disj property-ids id)) %)))])))

(defmethod data/->widget :one-to-many [[_ data] property-ids context]
  (let [table (ui/->table {:cell-defaults {:pad 5}})]
    (add-one-to-many-rows context
                          table
                          (:linked-property-type data)
                          property-ids)
    table))

(defmethod data/widget->value :one-to-many [_ widget]
  (->> (group/children widget)
       (keep actor/id)
       set))

(defn- add-one-to-one-rows [ctx table property-type property-id]
  (let [redo-rows (fn [ctx id]
                    (group/clear-children! table)
                    (add-one-to-one-rows ctx table property-type id)
                    (actor/pack-ancestor-window! table))]
    (ui/add-rows!
     table
     [[(when-not property-id
         (ui/->text-button ctx "+"
                           (fn [ctx]
                             (let [window (ui/->window {:title "Choose"
                                                        :modal? true
                                                        :close-button? true
                                                        :center? true
                                                        :close-on-escape? true})
                                   clicked-id-fn (fn [ctx id]
                                                   (actor/remove! window)
                                                   (redo-rows ctx id)
                                                   ctx)]
                               (ui/add! window (ctx/->overview-table ctx property-type clicked-id-fn))
                               (.pack window)
                               (ctx/add-to-stage! ctx window)))))]
      [(when property-id
         (let [property (ctx/property ctx property-id)
               image-widget (ui/->image-widget (property/->image property)
                                               {:id property-id})]
           (actor/add-tooltip! image-widget #(info/->text property %))
           image-widget))]
      [(when property-id
         (ui/->text-button ctx "-" #(do (redo-rows % nil) %)))]])))

(defmethod data/->widget :one-to-one [[_ data] property-id ctx]
  (let [table (ui/->table {:cell-defaults {:pad 5}})]
    (add-one-to-one-rows ctx
                         table
                         (:linked-property-type data)
                         property-id)
    table))

(defmethod data/widget->value :one-to-one [_ widget]
  (->> (group/children widget) (keep actor/id) first))

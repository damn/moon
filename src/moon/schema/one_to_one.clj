(ns moon.schema.one-to-one
  (:require [gdl.scene2d.event :as event]
            [gdl.scene2d.group :as group]
            [gdl.scene2d.ui.widget-group :as widget-group]
            [moon.actor :as actor]
            [moon.db :as db]
            [moon.property :as property]
            [moon.stage :as stage]
            [moon.table :as table]
            [moon.textures :as textures]
            [moon.ui :as ui]))

(defn malli-form [[_ property-type] _schemas]
  [:qualified-keyword {:namespace (property/type->id-namespace property-type)}])

(defn create-value [_ property-id db]
  (db/build db property-id))

(defn- add-one-to-one-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-id]
  (let [redo-rows (fn [ctx id]
                    (group/clear-children! table)
                    (add-one-to-one-rows ctx table property-type id)
                    (widget-group/pack! (actor/find-ancestor table :ui/window)))]
    (table/add-rows!
     table
     [[(when-not property-id
         {:actor (ui/create
                  {:type :ui/text-button
                   :text "+"
                   :skin skin
                   :actor/listeners {:listener/change (fn [event _actor]
                                                        (let [{:keys [ctx/db
                                                                      ctx/skin
                                                                      ctx/stage
                                                                      ctx/textures]
                                                               :as ctx} (stage/ctx (event/stage event))]
                                                          (stage/add-actor!
                                                           stage
                                                           ((get (:ctx/actor-fns ctx) :ui/property-overview-window)
                                                            {:db db
                                                             :textures textures
                                                             :skin skin
                                                             :property-type property-type
                                                             :clicked-id-fn (fn [actor id ctx]
                                                                              (actor/remove! (actor/find-ancestor actor :ui/window))
                                                                              (redo-rows ctx id))}))))}})})]
      [(when property-id
         (let [property (db/get-raw db property-id)]
           {:actor (ui/create
                    {:type :ui/image
                     :texture-region (textures/texture-region textures (property/image property))
                     :actor/user-object property-id
                     :actor/listeners {:listener/text-tooltip [(property/tooltip property) skin]}})}))]
      [(when property-id
         {:actor (ui/create
                  {:type :ui/text-button
                   :text "-"
                   :skin skin
                   :actor/listeners {:listener/change (fn [event _actor]
                                                        (redo-rows (stage/ctx (event/stage event))
                                                                   nil))}})})]])))

(defn create [[_ property-type] property-id ctx]
  (let [table (ui/create
               {:type :ui/table
                :table/cell-defaults {:pad 5}})]
    (add-one-to-one-rows ctx table property-type property-id)
    table))

(defn value [_  widget _schemas]
  (->> (group/children widget)
       (keep actor/user-object)
       first))

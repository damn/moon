(ns game.schema-widget.one-to-one
  (:require [gdl.scene2d.actor :as actor]
            [gdl.scene2d.event :as event]
            [gdl.scene2d.ui :as ui]
            [gdl.scene2d.group :as group]
            [gdl.scene2d.stage :as stage]
            [gdl.scene2d.ui.table :as table]
            [gdl.scene2d.ui.widget-group :as widget-group]
            [moon.db :as db]
            [moon.property :as property]
            [moon.schema :as schema]
            [moon.textures :as textures]))

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
                    (widget-group/pack! (actor/find-ancestor table ui/window?)))]
    (table/add-rows!
     table
     [[(when-not property-id
         {:actor (actor/create
                  {:type :ui/text-button
                   :text "+"
                   :skin skin
                   :actor/listeners {:listener/change (fn [event _actor]
                                                        (let [{:keys [ctx/db
                                                                      ctx/skin
                                                                      ctx/stage
                                                                      ctx/textures]
                                                               :as ctx} (:stage/ctx (event/stage event))]
                                                          (stage/add-actor!
                                                           stage
                                                           {:type :ui/property-overview-window
                                                            :db db
                                                            :textures textures
                                                            :skin skin
                                                            :property-type property-type
                                                            :clicked-id-fn (fn [actor id ctx]
                                                                             (actor/remove! (actor/find-ancestor actor ui/window?))
                                                                             (redo-rows ctx id))})))}})})]
      [(when property-id
         (let [property (db/get-raw db property-id)]
           {:actor (actor/create
                    {:type :ui/image
                     :content (textures/texture-region textures (property/image property))
                     :actor/user-object property-id
                     :actor/listeners {:listener/text-tooltip [(property/tooltip property) skin]}})}))]
      [(when property-id
         {:actor (actor/create
                  {:type :ui/text-button
                   :text "-"
                   :skin skin
                   :actor/listeners {:listener/change (fn [event _actor]
                                                        (redo-rows (:stage/ctx (event/stage event))
                                                                   nil))}})})]])))

(defmethod schema/create :s/one-to-one [[_ property-type] property-id ctx]
  (let [table (actor/create
               {:type :ui/table
                :table/cell-defaults {:pad 5}})]
    (add-one-to-one-rows ctx table property-type property-id)
    table))

(defmethod schema/value :s/one-to-one [_  widget _schemas]
  (->> (group/children widget)
       (keep actor/user-object)
       first))

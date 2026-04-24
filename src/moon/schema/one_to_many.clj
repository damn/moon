(ns moon.schema.one-to-many
  (:require [clojure.scene2d.actor :as actor]
            [clojure.scene2d.event :as event]
            [clojure.scene2d.group :as group]
            [clojure.scene2d.ui.widget-group :as widget-group]
            [moon.db :as db]
            [moon.property :as property]
            [clojure.scene2d.stage :as stage]
            [clojure.scene2d.ui.table :as table]
            [moon.textures :as textures]))

(defn create-value [_ property-ids db]
  (set (map (partial db/build db) property-ids)))

(defn- add-one-to-many-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-ids]
  (let [redo-rows (fn [ctx property-ids]
                    (group/clear-children! table)
                    (add-one-to-many-rows ctx table property-type property-ids)
                    (widget-group/pack! (actor/find-ancestor table :ui/window)))]
    (table/add-rows!
     table
     [[{:actor (actor/create
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
                                                         (actor/create
                                                          {:type :ui/property-overview-window
                                                           :db db
                                                           :textures textures
                                                           :skin skin
                                                           :property-type property-type
                                                           :clicked-id-fn (fn [actor id ctx]
                                                                            (actor/remove! (actor/find-ancestor actor :ui/window))
                                                                            (redo-rows ctx (conj property-ids id)))}))))}})}]
      (for [property-id property-ids]
        (let [property (db/get-raw db property-id)]
          {:actor (actor/create
                   {:type :ui/image
                    :content (textures/texture-region textures (property/image property))
                    :actor/user-object property-id
                    :actor/listeners {:listener/text-tooltip [(property/tooltip property) skin]}})}))
      (for [id property-ids]
        {:actor (actor/create
                 {:type :ui/text-button
                  :text "-"
                  :skin skin
                  :actor/listeners {:listener/change (fn [event _actor]
                                                       (redo-rows (stage/ctx (event/stage event))
                                                                  (disj property-ids id)))}})})])))

(defn create [[_ property-type] property-ids ctx]
  (let [table (actor/create
               {:type :ui/table
                :table/cell-defaults {:pad 5}})]
    (add-one-to-many-rows ctx table property-type property-ids)
    table))

(defn value [_  widget _schemas]
  (->> (group/children widget)
       (keep actor/user-object)
       set))

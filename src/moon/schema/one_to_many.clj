(ns moon.schema.one-to-many
  (:require [gdl.scene2d.event :as event]
            [gdl.scene2d.group :as group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as gdx-table]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [moon.actor :as actor]
            [moon.db :as db]
            [moon.property :as property]
            [moon.stage :as stage]
            [moon.table :as table]
            [moon.textures :as textures]))

(defn malli-form [[_ property-type] _schemas]
  [:set [:qualified-keyword {:namespace (property/type->id-namespace property-type)}]])

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
     [[{:actor (doto (text-button/create "+" skin)
                 (actor/add-listener!
                  (change-listener/create
                    (fn [event _actor]
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
                                            (redo-rows ctx (conj property-ids id)))})))))))}]
      (for [property-id property-ids]
        (let [property (db/get-raw db property-id)
              texture-region (textures/texture-region textures (property/image property))
              image-widget (doto (image/create texture-region)
                             (actor/set-user-object! property-id)
                             (actor/add-listener! (text-tooltip/create (property/tooltip property) skin)))]
          {:actor image-widget}))
      (for [id property-ids]
        {:actor (doto (text-button/create "-" skin)
                  (actor/add-listener!
                   (change-listener/create
                     (fn [event _actor]
                       (redo-rows (stage/ctx (event/stage event))
                                  (disj property-ids id))))))})])))

(defn create [[_ property-type] property-ids ctx]
  (let [table (doto (gdx-table/create)
                (table/set-cell-defaults! {:pad 5}))]
    (add-one-to-many-rows ctx table property-type property-ids)
    table))

(defn value [_  widget _schemas]
  (->> (group/children widget)
       (keep actor/user-object)
       set))

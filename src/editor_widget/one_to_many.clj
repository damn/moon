(ns editor-widget.one-to-many
  (:require [com.badlogic.gdx.scenes.scene2d.event :as event]
            [game.ctx :as ctx]
            [game.schema :as schema]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.group :as group]
            [gdx.scenes.scene2d.ui :as ui]
            [gdx.scenes.scene2d.ui.image :as image]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [gdx.stage :as stage]
            [moon.db :as db]
            [moon.property :as property]
            [moon.textures :as textures]
            [moon.ui.error-window]
            [moon.ui.property-overview-window]))

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
                    (widget-group/pack! (actor/find-ancestor table ui/window?)))]
    (table/add-rows!
     table
     [[{:actor (text-button/create
                {:text "+"
                 :skin skin
                 :actor/listeners {:listener/change (fn [event _actor]
                                                      (let [{:keys [ctx/db
                                                                    ctx/skin
                                                                    ctx/stage
                                                                    ctx/textures]
                                                             :as ctx} (:stage/ctx (event/stage event))]
                                                        (stage/add-actor!
                                                         stage
                                                         (moon.ui.property-overview-window/create
                                                          {:db db
                                                           :textures textures
                                                           :skin skin
                                                           :property-type property-type
                                                           :clicked-id-fn (fn [actor id ctx]
                                                                            (actor/remove! (actor/find-ancestor actor ui/window?))
                                                                            (redo-rows ctx (conj property-ids id)))}))))}})}]
      (for [property-id property-ids]
        (let [property (db/get-raw db property-id)]
          {:actor (image/create
                   {:content (textures/texture-region textures (property/image property))
                    :actor/user-object property-id
                    :actor/listeners {:listener/text-tooltip [(property/tooltip property) skin]}})}))
      (for [id property-ids]
        {:actor (text-button/create
                 {:text "-"
                  :skin skin
                  :actor/listeners {:listener/change (fn [event _actor]
                                                       (redo-rows (:stage/ctx (event/stage event))
                                                                  (disj property-ids id)))}})})])))

(defmethod schema/create :s/one-to-many [[_ property-type] property-ids ctx]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-one-to-many-rows ctx table property-type property-ids)
    table))

(defmethod schema/value :s/one-to-many [_  widget _schemas]
  (->> (group/children widget)
       (keep actor/user-object)
       set))

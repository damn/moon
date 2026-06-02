(ns editor.widget.one-to-many
  (:require [clojure.gdx.scene2d.event :as event]
            [clojure.gdx.scene2d.actor.remove :refer [remove!]]
            [clojure.gdx.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [clojure.gdx.scene2d.actor.user-object :refer [actor-user-object]]
            [editor.property-overview-window]
            [editor.widget :as widget]
            [clojure.gdx.scene2d.group.children :refer [children]]
            [clojure.gdx.scene2d.group.clear-children :refer [clear-children!]]
            [gdx.scenes.scene2d.ui :as ui]
            [gdx.scenes.scene2d.ui.image :as image]
            [gdx.scenes.scene2d.ui.table :as table]
            [clojure.gdx.scene2d.ui.table.add-rows :refer [add-rows!]]
            [clojure.gdx.scene2d.ui.text-button :as text-button]
            [clojure.gdx.scene2d.ui.widget-group.pack :refer [pack!]]
            [gdx.stage :as stage]
            [moon.db :as db]
            [moon.property :as property]
            [moon.textures :as textures]
            [moon.ui.error-window]))

(defn- add-one-to-many-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-ids]
  (let [redo-rows (fn [ctx property-ids]
                    (clear-children! table)
                    (add-one-to-many-rows ctx table property-type property-ids)
                    (pack! (find-ancestor table ui/window?)))]
    (add-rows!
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
                                                         (editor.property-overview-window/create
                                                          {:db db
                                                           :textures textures
                                                           :skin skin
                                                           :property-type property-type
                                                           :clicked-id-fn (fn [actor id ctx]
                                                                            (remove! (find-ancestor actor ui/window?))
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

(defmethod widget/create :s/one-to-many [[_ property-type] property-ids ctx]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-one-to-many-rows ctx table property-type property-ids)
    table))

(defmethod widget/value :s/one-to-many [_  widget _schemas]
  (->> (children widget)
       (keep actor-user-object)
       set))

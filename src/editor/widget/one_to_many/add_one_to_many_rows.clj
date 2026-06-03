(ns editor.widget.one-to-many.add-one-to-many-rows
  (:require [clojure.gdx.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [clojure.gdx.scene2d.event :as event]
            [clojure.gdx.scene2d.actor :refer [remove!
                                               set-user-object!]]
            [clojure.gdx.scene2d.group.clear-children :refer [clear-children!]]
            [clojure.gdx.scene2d.ui.table.add-rows :refer [add-rows!]]
            [clojure.gdx.scene2d.ui.text-button :as text-button]
            [clojure.gdx.scene2d.ui.text-tooltip :as text-tooltip]
            [clojure.gdx.scene2d.ui.widget-group.pack :refer [pack!]]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [editor.property-overview-window :as property-overview-window]
            [gdx.scenes.scene2d.ui :as ui]
            [gdx.scenes.scene2d.ui.image :as image]
            [gdx.stage :as stage]
            [moon.db :as db]
            [moon.property :as property]
            [moon.textures :as textures]))

(defn add-one-to-many-rows
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
                 :actor/listeners [(change-listener/create
                                    (fn [event _actor]
                                      (let [{:keys [ctx/db
                                                    ctx/skin
                                                    ctx/stage
                                                    ctx/textures]
                                             :as ctx} (:stage/ctx (event/stage event))]
                                        (stage/add-actor!
                                         stage
                                         (property-overview-window/create
                                          {:db db
                                           :textures textures
                                           :skin skin
                                           :property-type property-type
                                           :clicked-id-fn (fn [actor id ctx]
                                                            (remove! (find-ancestor actor ui/window?))
                                                            (redo-rows ctx (conj property-ids id)))})))))]})}]
      (for [property-id property-ids]
        (let [property (db/get-raw db property-id)]
          {:actor (doto (image/create
                         {:content (textures/texture-region textures (property/image property))
                          :actor/listeners [(text-tooltip/create (property/tooltip property) skin)]})
                    (set-user-object! property-id))}))
      (for [id property-ids]
        {:actor (text-button/create
                 {:text "-"
                  :skin skin
                  :actor/listeners [(change-listener/create
                                     (fn [event _actor]
                                       (redo-rows (:stage/ctx (event/stage event))
                                                  (disj property-ids id))))]})})])))

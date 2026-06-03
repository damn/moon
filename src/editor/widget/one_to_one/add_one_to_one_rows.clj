(ns editor.widget.one-to-one.add-one-to-one-rows
  (:require [clojure.gdx.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [clojure.gdx.scene2d.actor :refer [remove!
                                               set-user-object!]]
            [clojure.gdx.scene2d.event :as event]
            [clojure.gdx.scene2d.group.clear-children :refer [clear-children!]]
            [clojure.gdx.scene2d.ui.table.add-rows :refer [add-rows!]]
            [clojure.gdx.scene2d.ui.text-button :as text-button]
            [clojure.gdx.scene2d.ui.text-tooltip :as text-tooltip]
            [clojure.gdx.scene2d.ui.widget-group.pack :refer [pack!]]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [editor.property-overview-window]
            [gdx.scenes.scene2d.ui :as ui]
            [gdx.scenes.scene2d.ui.image :as image]
            [gdx.stage :as stage]
            [moon.db :as db]
            [moon.property :as property]
            [moon.textures :as textures]))

(defn add-one-to-one-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-id]
  (let [redo-rows (fn [ctx id]
                    (clear-children! table)
                    (add-one-to-one-rows ctx table property-type id)
                    (pack! (find-ancestor table ui/window?)))]
    (add-rows!
     table
     [[(when-not property-id
         {:actor (text-button/create
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
                                           (editor.property-overview-window/create
                                            {:db db
                                             :textures textures
                                             :skin skin
                                             :property-type property-type
                                             :clicked-id-fn (fn [actor id ctx]
                                                              (remove! (find-ancestor actor ui/window?))
                                                              (redo-rows ctx id))})))))]})})]
      [(when property-id
         (let [property (db/get-raw db property-id)]
           {:actor (doto (image/create
                          {:content (textures/texture-region textures (property/image property))
                           :actor/listeners [(text-tooltip/create (property/tooltip property) skin)]})
                     (set-user-object! property-id))}))]
      [(when property-id
         {:actor (text-button/create
                  {:text "-"
                   :skin skin
                   :actor/listeners [(change-listener/create
                                      (fn [event _actor]
                                        (redo-rows (:stage/ctx (event/stage event))
                                                   nil)))]})})]])))

(ns editor.widget.one-to-many.add-one-to-many-rows
  (:require [com.badlogic.gdx.scenes.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [com.badlogic.gdx.scenes.scene2d.event.get-stage :refer [get-stage]]
            [com.badlogic.gdx.scenes.scene2d.actor.set-user-object :refer [set-user-object!]]
            [com.badlogic.gdx.scenes.scene2d.actor.remove :refer [remove!]]
            [com.badlogic.gdx.scenes.scene2d.actor.add-listener :refer [add-listener!]]
            [com.badlogic.gdx.scenes.scene2d.group.clear-children :refer [clear-children!]]
            [com.badlogic.gdx.scenes.scene2d.ui.table.add-rows :refer [add-rows!]]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [com.badlogic.gdx.scenes.scene2d.utils.layout.pack :refer [pack!]]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [gdx.scenes.scene2d.ui :as ui]
            [com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [com.badlogic.gdx.scenes.scene2d.stage.add-actor :refer [add-actor!]]
            [moon.db.get-raw :refer [get-raw]]
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
     [[{:actor (doto (text-button/create
                      {:text "+"
                       :skin skin})
                 (add-listener! (change-listener/create
                                 (fn [event _actor]
                                   (let [{:keys [ctx/db
                                                 ctx/skin
                                                 ctx/stage
                                                 ctx/textures
                                                 ctx/property-overview-window]
                                          :as ctx} (:stage/ctx (get-stage event))]
                                     (add-actor!
                                      stage
                                      (property-overview-window
                                       {:db db
                                        :textures textures
                                        :skin skin
                                        :property-type property-type
                                        :clicked-id-fn (fn [actor id ctx]
                                                         (remove! (find-ancestor actor ui/window?))
                                                         (redo-rows ctx (conj property-ids id)))})))))))}]
      (for [property-id property-ids]
        (let [property (get-raw db property-id)]
          {:actor (doto (image/create (textures/texture-region textures (property/image property)))
                    (add-listener! (text-tooltip/create (property/tooltip property) skin))
                    (set-user-object! property-id))}))
      (for [id property-ids]
        {:actor (doto (text-button/create
                       {:text "-"
                        :skin skin})
                  (add-listener! (change-listener/create
                                  (fn [event _actor]
                                    (redo-rows (:stage/ctx (get-stage event))
                                               (disj property-ids id))))))})])))

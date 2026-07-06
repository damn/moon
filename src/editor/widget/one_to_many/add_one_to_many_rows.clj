(ns editor.widget.one-to-many.add-one-to-many-rows
  (:require
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]
            [clojure.gdx.stage.add-actor :as add-actor]
            [scene2d.actor.find-ancestor :refer [find-ancestor]]
            [scene2d.ui.table.add-rows :refer [add-rows!]]
            [scene2d.ui.text-button :as text-button]
            [scene2d.ui.text-tooltip :as text-tooltip]
            [scene2d.utils.change-listener :as change-listener]
            [moon.db.get-raw :refer [get-raw]]
            [moon.property.tooltip :as tooltip]
            [moon.property.image :as property-image]
            [moon.textures :as textures]))

(defn add-one-to-many-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-ids]
  (let [redo-rows (fn [ctx property-ids]
                    (group/clear-children! table)
                    (add-one-to-many-rows ctx table property-type property-ids)
                    (layout/pack (find-ancestor table window?/f)))]
    (add-rows!
     table
     [[{:actor (doto (text-button/create
                      {:text "+"
                       :skin skin})
                 (actor/add-listener! (change-listener/create
                                  (fn [event _actor]
                                    (let [{:keys [ctx/db
                                                  ctx/skin
                                                  ctx/stage
                                                  ctx/textures
                                                  ctx/property-overview-window]
                                           :as ctx} (:stage/ctx (event/get-stage event))]
                                      (add-actor/f
                                       stage
                                       (property-overview-window
                                        {:db db
                                         :textures textures
                                         :skin skin
                                         :property-type property-type
                                         :clicked-id-fn (fn [actor id ctx]
                                                          (actor/remove! (find-ancestor actor window?/f))
                                                          (redo-rows ctx (conj property-ids id)))})))))))}]
      (for [property-id property-ids]
        (let [property (get-raw db property-id)]
          {:actor (doto (image/new (textures/texture-region textures (property-image/f property)))
                    (actor/add-listener! (text-tooltip/create (tooltip/f property) skin))
                    (actor/set-user-object! property-id))}))
      (for [id property-ids]
        {:actor (doto (text-button/create
                       {:text "-"
                        :skin skin})
                  (actor/add-listener! (change-listener/create
                                   (fn [event _actor]
                                     (redo-rows (:stage/ctx (event/get-stage event))
                                                (disj property-ids id))))))})])))

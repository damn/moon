(ns editor.widget.one-to-one.add-one-to-one-rows
  (:require [scene2d.actor.find-ancestor :refer [find-ancestor]]
            [scene2d.actor.set-user-object :refer [set-user-object!]]
            [scene2d.actor.remove :refer [remove!]]
            [scene2d.actor.add-listener :refer [add-listener!]]
            [scene2d.event.get-stage :as get-stage]
            [scene2d.group.clear-children :refer [clear-children!]]
            [scene2d.ui.table.add-rows :refer [add-rows!]]
            [scene2d.ui.text-button :as text-button]
            [scene2d.ui.text-tooltip :as text-tooltip]
            [scene2d.layout.pack :refer [pack!]]
            [scene2d.change-listener :as change-listener]
            [scene2d.actor.is-window :as window?]
            [scene2d.ui.image :as image]
            [scene2d.stage.add-actor :refer [add-actor!]]
            [moon.db.get-raw :refer [get-raw]]
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
                    (pack! (find-ancestor table window?/f)))]
    (add-rows!
     table
     [[(when-not property-id
         {:actor (doto (text-button/create {:text "+" :skin skin})
                   (add-listener! (change-listener/create
                                   (fn [event _actor]
                                     (let [{:keys [ctx/db
                                                   ctx/skin
                                                   ctx/stage
                                                   ctx/textures
                                                   ctx/property-overview-window]
                                            :as ctx} (:stage/ctx (get-stage/f event))]
                                       (add-actor!
                                        stage
                                        (property-overview-window
                                         {:db db
                                          :textures textures
                                          :skin skin
                                          :property-type property-type
                                          :clicked-id-fn (fn [actor id ctx]
                                                           (remove! (find-ancestor actor window?/f))
                                                           (redo-rows ctx id))})))))))})]
      [(when property-id
         (let [property (get-raw db property-id)]
           {:actor (doto (image/create (textures/texture-region textures (property/image property)))
                     (add-listener! (text-tooltip/create (property/tooltip property) skin))
                     (set-user-object! property-id))}))]
      [(when property-id
         {:actor (doto (text-button/create
                        {:text "-"
                         :skin skin})
                   (add-listener! (change-listener/create
                                   (fn [event _actor]
                                     (redo-rows (:stage/ctx (get-stage/f event))
                                                nil)))))})]])))

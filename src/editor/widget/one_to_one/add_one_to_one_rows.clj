(ns editor.widget.one-to-one.add-one-to-one-rows
  (:require [gdl.find-ancestor :refer [find-ancestor]]
            [gdl.set-user-object :refer [set-user-object!]]
            [gdl.remove :refer [remove!]]
            [gdl.add-listener :refer [add-listener!]]
            [gdl.get-stage :refer [get-stage]]
            [gdl.clear-children :refer [clear-children!]]
            [ui.table.add-rows :refer [add-rows!]]
            [ui.text-button :as text-button]
            [ui.text-tooltip :as text-tooltip]
            [gdl.pack :refer [pack!]]
            [gdl.change-listener :as change-listener]
            [gdl.is-window :as window?]
            [ui.image :as image]
            [gdl.stage.add-actor :refer [add-actor!]]
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
                                            :as ctx} (:stage/ctx (get-stage event))]
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
                                     (redo-rows (:stage/ctx (get-stage event))
                                                nil)))))})]])))

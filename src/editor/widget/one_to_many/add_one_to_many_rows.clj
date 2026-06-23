(ns editor.widget.one-to-many.add-one-to-many-rows
  (:require [scene2d.actor.find-ancestor :refer [find-ancestor]]
            [gdl.get-stage :refer [get-stage]]
            [gdl.set-user-object :refer [set-user-object!]]
            [scene2d.actor.remove :refer [remove!]]
            [scene2d.actor.add-listener :refer [add-listener!]]
            [scene2d.group.clear-children :refer [clear-children!]]
            [ui.table.add-rows :refer [add-rows!]]
            [ui.text-button :as text-button]
            [ui.text-tooltip :as text-tooltip]
            [gdl.pack :refer [pack!]]
            [gdl.change-listener :as change-listener]
            [gdl.is-window :as window?]
            [ui.image :as image]
            [scene2d.stage.add-actor :refer [add-actor!]]
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
                    (pack! (find-ancestor table window?/f)))]
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
                                                         (remove! (find-ancestor actor window?/f))
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

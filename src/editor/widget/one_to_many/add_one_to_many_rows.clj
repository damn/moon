(ns editor.widget.one-to-many.add-one-to-many-rows
  (:require [clojure.gdx :as gdx]
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
                    (gdx/clear-children! table)
                    (add-one-to-many-rows ctx table property-type property-ids)
                    (gdx/pack! (find-ancestor table gdx/window?)))]
    (add-rows!
     table
     [[{:actor (doto (text-button/create
                      {:text "+"
                       :skin skin})
                 (gdx/add-listener! (change-listener/create
                                     (fn [event _actor]
                                       (let [{:keys [ctx/db
                                                     ctx/skin
                                                     ctx/stage
                                                     ctx/textures
                                                     ctx/property-overview-window]
                                              :as ctx} (:stage/ctx (gdx/event-get-stage event))]
                                         (gdx/add-actor!
                                          stage
                                          (property-overview-window
                                           {:db db
                                            :textures textures
                                            :skin skin
                                            :property-type property-type
                                            :clicked-id-fn (fn [actor id ctx]
                                                             (gdx/remove! (find-ancestor actor gdx/window?))
                                                             (redo-rows ctx (conj property-ids id)))})))))))}]
      (for [property-id property-ids]
        (let [property (get-raw db property-id)]
          {:actor (doto (gdx/image (textures/texture-region textures (property-image/f property)))
                    (gdx/add-listener! (text-tooltip/create (tooltip/f property) skin))
                    (gdx/set-user-object! property-id))}))
      (for [id property-ids]
        {:actor (doto (text-button/create
                       {:text "-"
                        :skin skin})
                  (gdx/add-listener! (change-listener/create
                                      (fn [event _actor]
                                        (redo-rows (:stage/ctx (gdx/event-get-stage event))
                                                   (disj property-ids id))))))})])))

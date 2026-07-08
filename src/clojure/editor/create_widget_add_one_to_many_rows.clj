(ns clojure.editor.create-widget-add-one-to-many-rows
  (:require [clojure.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [clojure.scene2d.actor.remove-actor]
            [clojure.scene2d.actor.set-user-object]
            [clojure.scene2d.actor.add-listener]
            [clojure.ui.table.add-rows :refer [add-rows!]]
            [clojure.editor.property-overview-window :refer [property-overview-window]]
            [clojure.event :as event]
            [clojure.db.get-raw :refer [get-raw]]
            [clojure.scene2d.group :as group]
            [clojure.image :as image]
            [clojure.moon-textures :as textures]
            [clojure.pack! :as pack!]
            [clojure.property-image :as property-image]
            [clojure.stage :as stage]
            [clojure.tooltip :as tooltip]
            [clojure.ui-text-button :as text-button]
            [clojure.ui-text-tooltip :as text-tooltip]
            [clojure.scene2d.utils.change-listener :as change-listener]
            [clojure.window :as gdx-window]))

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
                    (pack!/f (find-ancestor table (partial instance? gdx-window/class))))]
    (add-rows!
     table
     [[{:actor (doto (text-button/create
                      {:text "+"
                       :skin skin})
                 (clojure.scene2d.actor.add-listener/f (change-listener/create
                                          (fn [event _actor]
                                            (let [{:keys [ctx/db
                                                          ctx/skin
                                                          ctx/stage
                                                          ctx/textures]
                                                   :as ctx} (:stage/ctx (event/get-stage event))]
                                              (stage/add-actor!
                                               stage
                                               (property-overview-window
                                                {:db db
                                                 :textures textures
                                                 :skin skin
                                                 :property-type property-type
                                                 :clicked-id-fn (fn [actor id ctx]
                                                                  (clojure.scene2d.actor.remove-actor/f (find-ancestor actor (partial instance? gdx-window/class)))
                                                                  (redo-rows ctx (conj property-ids id)))})))))))}]
      (for [property-id property-ids]
        (let [property (get-raw db property-id)]
          {:actor (doto (image/new (textures/texture-region textures (property-image/f property)))
                    (clojure.scene2d.actor.add-listener/f (text-tooltip/create (tooltip/f property) skin))
                    (clojure.scene2d.actor.set-user-object/f property-id))}))
      (for [id property-ids]
        {:actor (doto (text-button/create
                       {:text "-"
                        :skin skin})
                  (clojure.scene2d.actor.add-listener/f (change-listener/create
                                           (fn [event _actor]
                                             (redo-rows (:stage/ctx (event/get-stage event))
                                                        (disj property-ids id))))))})])))

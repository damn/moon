(ns clojure.editor.create-widget-add-one-to-one-rows
  (:require [clojure.actor.find-ancestor :refer [find-ancestor]]
            [clojure.actor.remove-actor]
            [clojure.actor.set-user-object]
            [clojure.actor.add-listener]
            [clojure.table.add-rows :refer [add-rows!]]
            [clojure.editor.property-overview-window :refer [property-overview-window]]
            [clojure.event :as event]
            [clojure.get-raw :refer [get-raw]]
            [clojure.group :as group]
            [clojure.image :as image]
            [clojure.moon-textures :as textures]
            [clojure.pack! :as pack!]
            [clojure.property-image :as property-image]
            [clojure.stage :as stage]
            [clojure.tooltip :as tooltip]
            [clojure.ui-text-button :as text-button]
            [clojure.ui-text-tooltip :as text-tooltip]
            [clojure.utils-change-listener :as change-listener]
            [clojure.window :as gdx-window]))

(defn add-one-to-one-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-id]
  (let [redo-rows (fn [ctx id]
                    (group/clear-children! table)
                    (add-one-to-one-rows ctx table property-type id)
                    (pack!/f (find-ancestor table (partial instance? gdx-window/class))))]
    (add-rows!
     table
     [[(when-not property-id
         {:actor (doto (text-button/create {:text "+" :skin skin})
                   (clojure.actor.add-listener/f (change-listener/create
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
                                                                    (clojure.actor.remove-actor/f (find-ancestor actor (partial instance? gdx-window/class)))
                                                                    (redo-rows ctx id))})))))))})]
      [(when property-id
         (let [property (get-raw db property-id)]
           {:actor (doto (image/new (textures/texture-region textures (property-image/f property)))
                     (clojure.actor.add-listener/f (text-tooltip/create (tooltip/f property) skin))
                     (clojure.actor.set-user-object/f property-id))}))]
      [(when property-id
         {:actor (doto (text-button/create
                        {:text "-"
                         :skin skin})
                   (clojure.actor.add-listener/f (change-listener/create
                                            (fn [event _actor]
                                              (redo-rows (:stage/ctx (event/get-stage event))
                                                         nil)))))})]])))

(ns clojure.add-one-to-many-rows
  (:require
            [clojure.add-listener]
            [clojure.remove-actor]
            [clojure.set-user-object] [clojure.stage :as stage]
            [clojure.window :as window]
            [clojure.image :as image]
            [clojure.group :as group]
            [clojure.event :as event]
            [clojure.layout :as layout]
            [clojure.find-ancestor :refer [find-ancestor]]
            [clojure.add-rows :refer [add-rows!]]
            [clojure.ui-text-button :as text-button]
            [clojure.ui-text-tooltip :as text-tooltip]
            [clojure.utils-change-listener :as change-listener]
            [clojure.get-raw :refer [get-raw]]
            [clojure.tooltip :as tooltip]
            [clojure.property-image :as property-image]
            [clojure.moon-textures :as textures]))

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
                    (layout/pack! (find-ancestor table (partial instance? window/class))))]
    (add-rows!
     table
     [[{:actor (doto (text-button/create
                      {:text "+"
                       :skin skin})
                 (clojure.add-listener/f (change-listener/create
                                  (fn [event _actor]
                                    (let [{:keys [ctx/db
                                                  ctx/skin
                                                  ctx/stage
                                                  ctx/textures
                                                  ctx/property-overview-window]
                                           :as ctx} (:stage/ctx (event/get-stage event))]
                                      (stage/add-actor!
                                       stage
                                       (property-overview-window
                                        {:db db
                                         :textures textures
                                         :skin skin
                                         :property-type property-type
                                         :clicked-id-fn (fn [actor id ctx]
                                                          (clojure.remove-actor/f (find-ancestor actor (partial instance? window/class)))
                                                          (redo-rows ctx (conj property-ids id)))})))))))}]
      (for [property-id property-ids]
        (let [property (get-raw db property-id)]
          {:actor (doto (image/new (textures/texture-region textures (property-image/f property)))
                    (clojure.add-listener/f (text-tooltip/create (tooltip/f property) skin))
                    (clojure.set-user-object/f property-id))}))
      (for [id property-ids]
        {:actor (doto (text-button/create
                       {:text "-"
                        :skin skin})
                  (clojure.add-listener/f (change-listener/create
                                   (fn [event _actor]
                                     (redo-rows (:stage/ctx (event/get-stage event))
                                                (disj property-ids id))))))})])))

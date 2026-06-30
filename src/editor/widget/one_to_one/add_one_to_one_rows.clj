(ns editor.widget.one-to-one.add-one-to-one-rows
  (:require [clojure.gdx :as gdx]
            [scene2d.actor.find-ancestor :refer [find-ancestor]]
            [scene2d.ui.table.add-rows :refer [add-rows!]]
            [scene2d.ui.text-button :as text-button]
            [scene2d.ui.text-tooltip :as text-tooltip]
            [scene2d.utils.change-listener :as change-listener]
            [moon.db.get-raw :refer [get-raw]]
            [moon.property.tooltip :as tooltip]
            [moon.property.image :as property-image]
            [moon.textures :as textures])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Event)
           (com.badlogic.gdx.scenes.scene2d.ui Image
                                               Window)))

(defn add-one-to-one-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-id]
  (let [redo-rows (fn [ctx id]
                    (gdx/clear-children! table)
                    (add-one-to-one-rows ctx table property-type id)
                    (.pack (find-ancestor table #(instance? Window %))))]
    (add-rows!
     table
     [[(when-not property-id
         {:actor (doto (text-button/create {:text "+" :skin skin})
                   (Actor/.addListener (change-listener/create
                                        (fn [event _actor]
                                          (let [{:keys [ctx/db
                                                        ctx/skin
                                                        ctx/stage
                                                        ctx/textures
                                                        ctx/property-overview-window]
                                                 :as ctx} (:stage/ctx (Event/.getStage event))]
                                            (gdx/add-actor!
                                             stage
                                             (property-overview-window
                                              {:db db
                                               :textures textures
                                               :skin skin
                                               :property-type property-type
                                               :clicked-id-fn (fn [actor id ctx]
                                                                (Actor/.remove (find-ancestor actor #(instance? Window %)))
                                                                (redo-rows ctx id))})))))))})]
      [(when property-id
         (let [property (get-raw db property-id)]
           {:actor (doto (Image. (textures/texture-region textures (property-image/f property)))
                     (.addListener (text-tooltip/create (tooltip/f property) skin))
                     (.setUserObject property-id))}))]
      [(when property-id
         {:actor (doto (text-button/create
                        {:text "-"
                         :skin skin})
                   (Actor/.addListener (change-listener/create
                                        (fn [event _actor]
                                          (redo-rows (:stage/ctx (Event/.getStage event))
                                                     nil)))))})]])))

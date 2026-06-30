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
            [moon.textures :as textures])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Event Group)
           (com.badlogic.gdx.scenes.scene2d.ui Image
                                               Window)))

(defn add-one-to-many-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-ids]
  (let [redo-rows (fn [ctx property-ids]
                    (Group/.clearChildren table)
                    (add-one-to-many-rows ctx table property-type property-ids)
                    (.pack (find-ancestor table #(instance? Window %))))]
    (add-rows!
     table
     [[{:actor (doto (text-button/create
                      {:text "+"
                       :skin skin})
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
                                                              (redo-rows ctx (conj property-ids id)))})))))))}]
      (for [property-id property-ids]
        (let [property (get-raw db property-id)]
          {:actor (doto (Image. (textures/texture-region textures (property-image/f property)))
                    (.addListener (text-tooltip/create (tooltip/f property) skin))
                    (.setUserObject property-id))}))
      (for [id property-ids]
        {:actor (doto (text-button/create
                       {:text "-"
                        :skin skin})
                  (Actor/.addListener (change-listener/create
                                       (fn [event _actor]
                                         (redo-rows (:stage/ctx (Event/.getStage event))
                                                    (disj property-ids id))))))})])))

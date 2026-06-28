(ns editor.widget.one-to-one.add-one-to-one-rows
  (:require [scene2d.ui.table.add-rows :refer [add-rows!]]
            [scene2d.ui.text-button :as text-button]
            [scene2d.ui.text-tooltip :as text-tooltip]
            [scene2d.utils.layout.pack :refer [pack!]]
            [scene2d.utils.change-listener :as change-listener]
            [scene2d.ui.image :as image]
            [moon.db.get-raw :refer [get-raw]]
            [moon.property.tooltip :as tooltip]
            [moon.property.image :as property-image]
            [moon.textures :as textures])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Event Group)
           (com.badlogic.gdx.scenes.scene2d.ui Window)
           (scene2d Stage)))

(defn- find-window-ancestor [actor]
  (loop [a actor]
    (if-let [p (Actor/.getParent a)]
      (if (instance? Window p)
        p
        (recur p))
      (throw (Error. (str "Actor has no parent window " actor))))))

(defn add-one-to-one-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-id]
  (let [redo-rows (fn [ctx id]
                    (Group/.clearChildren table)
                    (add-one-to-one-rows ctx table property-type id)
                    (pack! (find-window-ancestor table)))]
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
                                            (Stage/.addActor
                                             stage
                                             (property-overview-window
                                              {:db db
                                               :textures textures
                                               :skin skin
                                               :property-type property-type
                                               :clicked-id-fn (fn [actor id ctx]
                                                                (Actor/.remove (find-window-ancestor actor))
                                                                (redo-rows ctx id))})))))))})]
      [(when property-id
         (let [property (get-raw db property-id)]
           {:actor (doto (image/create (textures/texture-region textures (property-image/f property)))
                     (Actor/.addListener (text-tooltip/create (tooltip/f property) skin))
                     (Actor/.setUserObject property-id))}))]
      [(when property-id
         {:actor (doto (text-button/create
                        {:text "-"
                         :skin skin})
                   (Actor/.addListener (change-listener/create
                                        (fn [event _actor]
                                          (redo-rows (:stage/ctx (Event/.getStage event))
                                                     nil)))))})]])))

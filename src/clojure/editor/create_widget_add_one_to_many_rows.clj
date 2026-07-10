(ns clojure.editor.create-widget-add-one-to-many-rows
  (:require [clojure.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.ui.table.add-rows :refer [add-rows!]]
            [clojure.editor.property-overview-window :refer [property-overview-window]]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [clojure.db :as db]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [clojure.moon-textures :as textures]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]
            [clojure.property-image :as property-image]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [clojure.tooltip :as tooltip]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as gdx-window]))

(defn add-one-to-many-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-ids]
  (let [redo-rows (fn [ctx property-ids]
                    (group/clearChildren table)
                    (add-one-to-many-rows ctx table property-type property-ids)
                    (layout/pack (find-ancestor table (partial instance? gdx-window/class))))]
    (add-rows!
     table
     [[{:actor (doto (text-button/new "+" skin)
                 (actor/addListener (change-listener/create
                                          (fn [event _actor]
                                            (let [{:keys [ctx/db
                                                          ctx/skin
                                                          ctx/stage
                                                          ctx/textures]
                                                   :as ctx} (:stage/ctx (event/getStage event))]
                                              (stage/addActor
                                               stage
                                               (property-overview-window
                                                {:db db
                                                 :textures textures
                                                 :skin skin
                                                 :property-type property-type
                                                 :clicked-id-fn (fn [actor id ctx]
                                                                  (actor/remove (find-ancestor actor (partial instance? gdx-window/class)))
                                                                  (redo-rows ctx (conj property-ids id)))})))))))}]
      (for [property-id property-ids]
        (let [property (db/get-raw db property-id)]
          {:actor (doto (image/new (textures/texture-region textures (property-image/f property)))
                    (actor/addListener (text-tooltip/new (tooltip/f property) skin))
                    (actor/setUserObject property-id))}))
      (for [id property-ids]
        {:actor (doto (text-button/new "-" skin)
                  (actor/addListener (change-listener/create
                                           (fn [event _actor]
                                             (redo-rows (:stage/ctx (event/getStage event))
                                                        (disj property-ids id))))))})])))

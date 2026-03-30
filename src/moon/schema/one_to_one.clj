(ns moon.schema.one-to-one
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.group :as group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [moon.actor :as actor]
            [moon.db :as db]
            [moon.property :as property]
            [moon.property-overview-window :as property-overview-window]
            [moon.stage :as stage]
            [moon.table :as table]
            [moon.textures :as textures])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            Event)
           (com.badlogic.gdx.scenes.scene2d.ui Image
                                               Skin
                                               Table
                                               TextButton
                                               Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)
           (moon Stage)))

(defn malli-form [[_ property-type] _schemas]
  [:qualified-keyword {:namespace (property/type->id-namespace property-type)}])

(defn create-value [_ property-id db]
  (db/build db property-id))

(defn- add-one-to-one-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   ^Table table
   property-type
   property-id]
  (let [redo-rows (fn [ctx id]
                    (.clearChildren table)
                    (add-one-to-one-rows ctx table property-type id)
                    (Window/.pack (actor/find-ancestor table Window)))]
    (table/add-rows!
     table
     [[(when-not property-id
         {:actor (doto (TextButton. "+" ^Skin skin)
                   (.addListener
                    (proxy [ChangeListener] []
                      (changed [^Event event _actor]
                        (let [{:keys [ctx/db
                                      ctx/skin
                                      ctx/stage
                                      ctx/textures]} (.ctx ^Stage (.getStage event))]
                          (stage/add-actor!
                           stage
                           (property-overview-window/create
                            {:db db
                             :textures textures
                             :skin skin
                             :property-type property-type
                             :clicked-id-fn (fn [actor id ctx]
                                              (Actor/.remove (actor/find-ancestor actor Window))
                                              (redo-rows ctx id))})))))))})]
      [(when property-id
         (let [property (db/get-raw db property-id)
               texture-region (textures/texture-region textures (property/image property))
               image-widget (doto (Image. ^TextureRegion texture-region)
                              (.setUserObject property-id)
                              (.addListener (text-tooltip/create (property/tooltip property) skin)))]
           {:actor image-widget}
           image-widget))]
      [(when property-id
         {:actor (doto (TextButton. "-" ^Skin skin)
                   (.addListener
                    (proxy [ChangeListener] []
                      (changed [^Event event _actor]
                        (redo-rows (.ctx ^Stage (.getStage event))
                                   nil)))))})]])))

(defn create [[_ property-type] property-id ctx]
  (let [table (doto (Table.)
                (table/set-cell-defaults! {:pad 5}))]
    (add-one-to-one-rows ctx table property-type property-id)
    table))

(defn value [_  widget _schemas]
  (->> (group/children widget)
       (keep Actor/.getUserObject)
       first))

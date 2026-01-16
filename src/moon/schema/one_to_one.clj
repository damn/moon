(ns moon.schema.one-to-one
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [moon.db :as db]
            [moon.schema :as schema]
            [moon.textures :as textures]
            [moon.property :as property]
            [moon.ui.editor.property :as editor.property]
            [moon.ui.property-overview-window :as property-overview-window]
            [moon.ui.text-button :as text-button]
            [moon.ui.window :as window])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Image
                                               Skin
                                               TextTooltip)))

(defmethod schema/malli-form :s/one-to-one [[_ property-type] _schemas]
  [:qualified-keyword {:namespace (property/type->id-namespace property-type)}])

(defmethod schema/create-value :s/one-to-one [_ property-id db]
  (db/build db property-id))

(defn- add-one-to-one-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-id]
  (let [redo-rows (fn [ctx id]
                    (.clearChildren table)
                    (add-one-to-one-rows ctx table property-type id)
                    (.pack (window/find-ancestor table)))]
    (table/add-rows!
     table
     [[(when-not property-id
         {:actor (text-button/create
                  {:text "+"
                   :on-clicked (fn [_actor {:keys [ctx/db
                                                   ctx/skin
                                                   ctx/stage
                                                   ctx/textures]}]
                                 (Stage/.addActor
                                  stage
                                  (property-overview-window/create
                                   {:db db
                                    :textures textures
                                    :skin skin
                                    :property-type property-type
                                    :clicked-id-fn (fn [actor id ctx]
                                                     (.remove (window/find-ancestor actor))
                                                     (redo-rows ctx id))})))
                   :skin skin})})]
      [(when property-id
         (let [property (db/get-raw db property-id)
               texture-region (textures/texture-region textures (editor.property/image property))
               image-widget (doto (Image. ^TextureRegion texture-region)
                              (.setUserObject property-id)
                              (.addListener (TextTooltip. (editor.property/tooltip property) ^Skin skin)))]
           {:actor image-widget}
           image-widget))]
      [(when property-id
         {:actor (text-button/create
                  {:text "-"
                   :on-clicked (fn [_actor ctx]
                                 (redo-rows ctx nil))
                   :skin skin})})]])))

(defmethod schema/create :s/one-to-one [[_ property-type] property-id ctx]
  (let [table (table/create
               {:cell-defaults {:pad 5}})]
    (add-one-to-one-rows ctx table property-type property-id)
    table))

(defmethod schema/value :s/one-to-one [_  widget _schemas]
  (->> (.getChildren widget)
       (keep Actor/.getUserObject)
       first))

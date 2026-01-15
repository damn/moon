(ns moon.schema.one-to-many
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [moon.db :as db]
            [moon.schema :as schema]
            [moon.textures :as textures]
            [moon.property :as property]
            [moon.ui.editor.property :as editor.property]
            [moon.ui.editor.overview-window :as overview-window]
            [moon.ui.text-button :as text-button]
            [moon.ui.window :as window])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Image
                                               Skin
                                               TextTooltip)))

(defmethod schema/malli-form :s/one-to-many [[_ property-type] _schemas]
  [:set [:qualified-keyword {:namespace (property/type->id-namespace property-type)}]])

(defmethod schema/create-value :s/one-to-many [_ property-ids db]
  (set (map (partial db/build db) property-ids)))

(defn- add-one-to-many-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-ids]
  (let [redo-rows (fn [ctx property-ids]
                    (.clearChildren table)
                    (add-one-to-many-rows ctx table property-type property-ids)
                    (.pack (window/find-ancestor table)))]
    (table/add-rows!
     table
     [[{:actor (text-button/create
                {:text "+"
                 :on-clicked (fn [_actor {:keys [ctx/db
                                                 ctx/skin
                                                 ctx/stage
                                                 ctx/textures]}]
                               (Stage/.addActor
                                stage
                                (overview-window/create
                                 {:db db
                                  :textures textures
                                  :skin skin
                                  :property-type property-type
                                  :clicked-id-fn (fn [actor id ctx]
                                                   (.remove (window/find-ancestor actor))
                                                   (redo-rows ctx (conj property-ids id)))})))
                 :skin skin})}]
      (for [property-id property-ids]
        (let [property (db/get-raw db property-id)
              texture-region (textures/texture-region textures (editor.property/image property))
              image-widget (doto (Image. ^TextureRegion texture-region)
                             (.setUserObject property-id)
                             (.addListener (TextTooltip. (editor.property/tooltip property) ^Skin skin)))]
          {:actor image-widget}))
      (for [id property-ids]
        {:actor (text-button/create
                 {:text "-"
                  :on-clicked (fn [_actor ctx]
                                (redo-rows ctx (disj property-ids id)))
                  :skin skin})})])))

(defmethod schema/create :s/one-to-many [[_ property-type] property-ids ctx]
  (let [table (table/create
               {:cell-defaults {:pad 5}})]
    (add-one-to-many-rows ctx table property-type property-ids)
    table))

(defmethod schema/value :s/one-to-many [_  widget _schemas]
  (->> (.getChildren widget)
       (keep Actor/.getUserObject)
       set))

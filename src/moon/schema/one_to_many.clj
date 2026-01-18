(ns moon.schema.one-to-many
  (:require [moon.db :as db]
            [moon.textures :as textures]
            [moon.property :as property]
            [moon.ui :as ui]
            [moon.ui.actor :as actor]
            [moon.ui.table :as table])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Image
                                               Skin
                                               TextTooltip
                                               Window)))

(defn malli-form [[_ property-type] _schemas]
  [:set [:qualified-keyword {:namespace (property/type->id-namespace property-type)}]])

(defn create-value [_ property-ids db]
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
                    (.pack (actor/find-ancestor table Window)))]
    (table/add-rows!
     table
     [[{:actor (ui/actor
                {:type :ui/text-button
                 :text "+"
                 :on-clicked (fn [_actor {:keys [ctx/db
                                                 ctx/skin
                                                 ctx/stage
                                                 ctx/textures]}]
                               (Stage/.addActor
                                stage
                                (ui/actor
                                 {:type :ui/property-overview-window
                                  :db db
                                  :textures textures
                                  :skin skin
                                  :property-type property-type
                                  :clicked-id-fn (fn [actor id ctx]
                                                   (.remove (actor/find-ancestor actor Window))
                                                   (redo-rows ctx (conj property-ids id)))})))
                 :skin skin})}]
      (for [property-id property-ids]
        (let [property (db/get-raw db property-id)
              texture-region (textures/texture-region textures (property/image property))
              image-widget (doto (Image. ^TextureRegion texture-region)
                             (.setUserObject property-id)
                             (.addListener (TextTooltip. (property/tooltip property) ^Skin skin)))]
          {:actor image-widget}))
      (for [id property-ids]
        {:actor (ui/actor
                 {:type :ui/text-button
                  :text "-"
                  :on-clicked (fn [_actor ctx]
                                (redo-rows ctx (disj property-ids id)))
                  :skin skin})})])))

(defn create [[_ property-type] property-ids ctx]
  (let [table (ui/actor
               {:type :ui/table
                :cell-defaults {:pad 5}})]
    (add-one-to-many-rows ctx table property-type property-ids)
    table))

(defn value [_  widget _schemas]
  (->> (.getChildren widget)
       (keep Actor/.getUserObject)
       set))

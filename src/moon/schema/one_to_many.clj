(ns moon.schema.one-to-many
  (:require [moon.db :as db]
            [moon.textures :as textures]
            [moon.property :as property]
            [moon.ui.actor :as actor]
            [moon.ui.property-overview-window :as property-overview-window]
            [moon.ui.table :as table]
            [moon.ui.text-button :as text-button])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            Group
                                            Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Image
                                               Skin
                                               Table
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
   ^Table table
   property-type
   property-ids]
  (let [redo-rows (fn [ctx property-ids]
                    (.clearChildren table)
                    (add-one-to-many-rows ctx table property-type property-ids)
                    (Window/.pack (actor/find-ancestor table Window)))]
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
                                (property-overview-window/create
                                 {:db db
                                  :textures textures
                                  :skin skin
                                  :property-type property-type
                                  :clicked-id-fn (fn [actor id ctx]
                                                   (Actor/.remove (actor/find-ancestor actor Window))
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
        {:actor (text-button/create
                 {:text "-"
                  :on-clicked (fn [_actor ctx]
                                (redo-rows ctx (disj property-ids id)))
                  :skin skin})})])))

(defn create [[_ property-type] property-ids ctx]
  (let [table (table/create {:cell-defaults {:pad 5}})]
    (add-one-to-many-rows ctx table property-type property-ids)
    table))

(defn value [_  widget _schemas]
  (->> (Group/.getChildren widget)
       (keep Actor/.getUserObject)
       set))

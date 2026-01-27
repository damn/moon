(ns moon.ui-impl.property-overview-window
  (:require [moon.db :as db]
            [moon.property :as property]
            [moon.textures :as textures]
            [moon.ui :as ui]
            [moon.ui.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Touchable)))

(def ^:private property-type->overview-table-props
  {:properties/audiovisuals {:columns 10
                             :image-scale 2
                             :sort-by-fn (comp name :property/id)
                             :extra-info-text (constantly "")}
   :properties/creatures    {:columns 15
                             :image-scale 1.5
                             :sort-by-fn #(vector (:creature/level %)
                                                  (name (:entity/species %))
                                                  (name (:property/id %)))
                             :extra-info-text #(str (:creature/level %))}
   :properties/items        {:columns 20
                             :image-scale 1.1
                             :sort-by-fn #(vector (name (:item/slot %))
                                                  (name (:property/id %)))
                             :extra-info-text (constantly "")}
   :properties/projectiles  {:columns 16
                             :image-scale 2
                             :sort-by-fn (comp name :property/id)
                             :extra-info-text (constantly "")}
   :properties/skills       {:columns 16
                             :image-scale 2
                             :sort-by-fn (comp name :property/id)
                             :extra-info-text (constantly "")}})

(defn- overview-table-rows* [skin image-scale rows]
  (for [row rows]
    (for [{:keys [texture-region
                  on-clicked
                  tooltip
                  extra-info-text]} row]
      {:actor (doto (ui/actor {:type :ui/stack})
                (group/add-actors! [(ui/actor
                                     {:type :ui/image-button
                                      :drawable/texture-region texture-region
                                      :on-clicked on-clicked
                                      :drawable/scale image-scale
                                      :tooltip tooltip
                                      :skin skin})
                                    (doto (ui/actor
                                           {:type :ui/label
                                            :label/text extra-info-text
                                            :label/skin skin})
                                      (Actor/.setTouchable Touchable/disabled))]))})))

(defn- overview-table-rows
  [db
   skin
   textures
   property-type
   clicked-id-fn]
  (let [{:keys [sort-by-fn
                extra-info-text
                columns
                image-scale]} (get property-type->overview-table-props property-type)]
    (->> (db/all-raw db property-type)
         (sort-by sort-by-fn)
         (map (fn [property]
                {:texture-region (textures/texture-region textures (property/image property))
                 :on-clicked (fn [actor ctx]
                               (clicked-id-fn actor (:property/id property) ctx))
                 :tooltip (property/tooltip property)
                 :extra-info-text (extra-info-text property)}))
         (partition-all columns)
         (overview-table-rows* skin image-scale))))

(defn create
  [{:keys [db
           textures
           skin
           property-type
           clicked-id-fn]}]
  (ui/actor
   {:type :ui/window
    :skin skin
    :title "Edit"
    :modal? true
    :close-button? true
    :center? true
    :close-on-escape? true
    :pack? true
    :rows (overview-table-rows db
                               skin
                               textures
                               property-type
                               clicked-id-fn)}))

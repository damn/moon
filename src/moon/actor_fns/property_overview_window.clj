(ns moon.actor-fns.property-overview-window
  (:require [clj.api.com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [gdl.scene2d.event :as event]
            [clj.api.com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.stack :as stack]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.window :as gdx-window]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.drawable :as drawable]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.actor :as actor]
            [moon.db :as db]
            [moon.group :as group]
            [moon.property :as property]
            [moon.stage :as stage]
            [moon.table :as table]
            [moon.textures :as textures]
            [moon.window :as window]))

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
      {:actor (doto (stack/create)
                (group/add-actors! [(doto (image-button/create (doto (texture-region-drawable/create texture-region)
                                                                 (drawable/set-min-size! (* image-scale (texture-region/width texture-region))
                                                                                         (* image-scale (texture-region/height texture-region)))))
                                      (actor/add-listener! (change-listener/create
                                                            (fn [event actor]
                                                              (on-clicked actor (stage/ctx (event/stage event))))))
                                      (actor/add-listener! (text-tooltip/create tooltip skin)))
                                    (doto (label/create extra-info-text skin)
                                      (actor/set-touchable! touchable/disabled))]))})))

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
  (doto (gdx-window/create "Edit" skin)
    (window/add-close-button! skin)
    (table/add-rows! (overview-table-rows db skin textures property-type clicked-id-fn))
    (gdx-window/set-modal! true)
    (widget-group/pack!)))

(ns moon.ui.editor.overview-window
  (:require [moon.db :as db]
            [moon.graphics :as graphics]
            [gdl.ui.actor :as actor]
            [gdl.ui.touchable :as touchable]
            [moon.ui.editor.property :as property]
            [moon.ui.image-button :as image-button]
            [moon.ui.label :as label]
            [moon.ui.stack :as stack]
            [moon.ui.stage :as stage]
            [moon.ui.window :as window]))

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

(defn- overview-table-rows* [image-scale rows]
  (for [row rows]
    (for [{:keys [texture-region
                  on-clicked
                  tooltip
                  extra-info-text]} row]
      {:actor (stack/create
               {:group/actors [(image-button/create
                                {:drawable/texture-region texture-region
                                 :on-clicked on-clicked
                                 :drawable/scale image-scale
                                 :tooltip tooltip})
                               (doto (label/create extra-info-text)
                                 (actor/set-touchable! touchable/disabled))]})})))

(defn- overview-table-rows
  [db
   graphics
   property-type
   clicked-id-fn]
  (let [{:keys [sort-by-fn
                extra-info-text
                columns
                image-scale]} (get property-type->overview-table-props property-type)]
    (->> (db/all-raw db property-type)
         (sort-by sort-by-fn)
         (map (fn [property]
                {:texture-region (graphics/texture-region graphics (property/image property))
                 :on-clicked (fn [actor ctx]
                               (clicked-id-fn actor (:property/id property) ctx))
                 :tooltip (property/tooltip property)
                 :extra-info-text (extra-info-text property)}))
         (partition-all columns)
         (overview-table-rows* image-scale))))

(defn create
  [{:keys [db
           graphics
           property-type
           clicked-id-fn]}]
  (window/create
   {:title "Edit"
    :modal? true
    :close-button? true
    :center? true
    :close-on-escape? true
    :pack? true
    :rows (overview-table-rows db
                               graphics
                               property-type
                               clicked-id-fn)}))

(defmethod stage/build :actor/editor-overview-window [opts]
  (create opts))

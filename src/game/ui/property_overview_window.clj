(ns game.ui.property-overview-window
  (:require [clojure.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.scenes.scene2d.event :as event]
            [clojure.scene2d.stage :as stage]
            [moon.db :as db]
            [moon.property :as property]
            [moon.textures :as textures]))

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
      {:actor (actor/create
               {:type :ui/stack
                :group/actors [(actor/create
                                {:type :ui/image-button
                                 :drawable {:drawable/texture-region texture-region
                                            :drawable/scale image-scale}
                                 :actor/listeners {:listener/change (fn [event actor]
                                                                      (on-clicked actor (:stage/ctx (event/stage event))))
                                                   :listener/text-tooltip [tooltip skin]}})
                               (actor/create
                                {:type :ui/label
                                 :text extra-info-text
                                 :skin skin
                                 :actor/touchable :touchable/disabled})]})})))

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

(defmethod actor/create :ui/property-overview-window
  [{:keys [db
           textures
           skin
           property-type
           clicked-id-fn]}]
  (actor/create
   {:type :ui/window
    :title "Edit"
    :skin skin
    :window/close-button? skin
    :window/modal? true
    :table/rows (overview-table-rows db skin textures property-type clicked-id-fn)}))

(ns editor.ui.overview
  (:require [clojure.pprint :refer [pprint]]
            [moon.db :as db]
            [moon.textures :as textures])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Group Touchable)
           (com.badlogic.gdx.scenes.scene2d.ui ImageButton Label Stack TextButton TextTooltip Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener TextureRegionDrawable)))

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

(defn- overview-table-rows* [state image-scale rows]
  (let [{:keys [skin]} @state]
    (for [row rows]
      (for [{:keys [texture-region
                    on-clicked
                    tooltip
                    extra-info-text]} row]
        {:actor (let [stack (Stack.)]
                  (run! #(.addActor ^Group stack %)
                        [(doto (ImageButton.
                                (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                                  (.setMinSize (* image-scale (.getRegionWidth ^TextureRegion texture-region))
                                               (* image-scale (.getRegionHeight ^TextureRegion texture-region)))))
                           (.addListener (proxy [ChangeListener] []
                                           (changed [event actor]
                                             (on-clicked actor))))
                           (.addListener (TextTooltip. ^String tooltip skin)))
                         (doto (Label. ^String extra-info-text skin)
                           (.setTouchable Touchable/disabled))])
                  stack)}))))

(defn- property-overview-rows [state property-type clicked-id-fn]
  (let [{:keys [db textures]} @state
        {:keys [sort-by-fn
                extra-info-text
                columns
                image-scale]} (get property-type->overview-table-props property-type)]
    (->> (db/all-raw db property-type)
         (sort-by sort-by-fn)
         (map (fn [property]
                {:texture-region (textures/texture-region textures (or (:entity/image property)
                                                                       (first (:animation/frames (:entity/animation property)))))
                 :on-clicked (fn [actor]
                               (clicked-id-fn actor (:property/id property)))
                 :tooltip (binding [*print-level* 2]
                            (with-out-str
                              (pprint property)))
                 :extra-info-text (extra-info-text property)}))
         (partition-all columns)
         (overview-table-rows* state image-scale))))

(defn property-overview-window [state property-type clicked-id-fn]
  (let [{:keys [skin]} @state
        window (Window. "Edit" skin)]
    (doseq [row (property-overview-rows state property-type clicked-id-fn)]
      (doseq [cell row]
        (.add window ^com.badlogic.gdx.scenes.scene2d.Actor (:actor cell)))
      (.row window))
    (.pack window)
    (.add (.getTitleTable window)
          (doto (TextButton. "X" skin)
            (.addListener (proxy [ChangeListener] []
                            (changed [_event _actor]
                              (.remove window))))))
    (.setModal window true)
    window))

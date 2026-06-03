(ns editor.property-overview-window
  (:require [clojure.gdx.scene2d.actor :refer [set-touchable!]]
            [clojure.gdx.scene2d.event :as event]
            [editor.constants :refer [property-type->overview-table-props]]
            [gdx.stage :as stage]
            [gdx.scenes.scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.stack :as stack]
            [gdx.scenes.scene2d.ui.image-button :as image-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [clojure.gdx.scene2d.ui.text-tooltip :as text-tooltip]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [clojure.gdx.scene2d.touchable :as touchable]
            [moon.db :as db]
            [moon.property :as property]
            [moon.textures :as textures]))

(defn- overview-table-rows* [skin image-scale rows]
  (for [row rows]
    (for [{:keys [texture-region
                  on-clicked
                  tooltip
                  extra-info-text]} row]
      {:actor (stack/create
               {:group/actors [(image-button/create
                                {:drawable (texture-region-drawable/create*
                                            {:drawable/texture-region texture-region
                                             :drawable/scale image-scale})
                                 :actor/listeners [(change-listener/create
                                                    (fn [event actor]
                                                      (on-clicked actor (:stage/ctx (event/stage event)))))
                                                   (text-tooltip/create tooltip skin)]})
                               (doto (label/create
                                      {:text extra-info-text
                                       :skin skin})
                                 (set-touchable! touchable/disabled))]})})))

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
  (window/create
   {:title "Edit"
    :skin skin
    :window/close-button? skin
    :window/modal? true
    :table/rows (overview-table-rows db skin textures property-type clicked-id-fn)}))

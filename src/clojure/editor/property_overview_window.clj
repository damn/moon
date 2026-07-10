(ns clojure.editor.property-overview-window
  (:require
            [clojure.table-set-opts :as table-set-opts]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.ui.window.add-close-button :as add-close-button]
            [clojure.db :as db]
            [clojure.editor.constants :refer [property-type->overview-table-props]]
            [clojure.moon-textures :as textures]
            [clojure.property-image :as property-image]
            [clojure.table-rows :refer [overview-table-rows*]]
            [clojure.tooltip :as tooltip]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as gdx-window]))

(defn property-overview-window
  [{:keys [db
           textures
           skin
           property-type
           clicked-id-fn]}]
  (doto (doto (window/new "Edit" skin)
    (table-set-opts/set-opts! {:title "Edit"
          :skin skin
          :table/rows (let [{:keys [sort-by-fn
                                    extra-info-text
                                    columns
                                    image-scale]} (get property-type->overview-table-props property-type)]
                        (->> (db/all-raw db property-type)
                             (sort-by sort-by-fn)
                             (map (fn [property]
                                    {:texture-region (textures/texture-region textures (property-image/f property))
                                     :on-clicked (fn [actor ctx]
                                                   (clicked-id-fn actor (:property/id property) ctx))
                                     :tooltip (tooltip/f property)
                                     :extra-info-text (extra-info-text property)}))
                             (partition-all columns)
                             (overview-table-rows* skin image-scale)))}))
    (add-close-button/f! skin)
    (gdx-window/setModal true)))

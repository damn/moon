(ns editor.property-overview-window
  (:require [clojure.gdx :as gdx]
            [editor.constants :refer [property-type->overview-table-props]]
            [editor.property-overview-window.table-rows :refer [overview-table-rows*]]
            [gdx.scenes.scene2d.ui.window :as window]
            [moon.db.all-raw :refer [all-raw]]
            [scene2d.ui.window.add-close-button :as add-close-button]
            [moon.property.tooltip :as tooltip]
            [moon.property.image :as image]
            [moon.textures :as textures]))

(defn create
  [{:keys [db
           textures
           skin
           property-type
           clicked-id-fn]}]
  (doto (window/create
         {:title "Edit"
          :skin skin
          :table/rows (let [{:keys [sort-by-fn
                                    extra-info-text
                                    columns
                                    image-scale]} (get property-type->overview-table-props property-type)]
                        (->> (all-raw db property-type)
                             (sort-by sort-by-fn)
                             (map (fn [property]
                                    {:texture-region (textures/texture-region textures (image/f property))
                                     :on-clicked (fn [actor ctx]
                                                   (clicked-id-fn actor (:property/id property) ctx))
                                     :tooltip (tooltip/f property)
                                     :extra-info-text (extra-info-text property)}))
                             (partition-all columns)
                             (overview-table-rows* skin image-scale)))})
    (add-close-button/f! skin)
    (gdx/window-set-modal! true)))

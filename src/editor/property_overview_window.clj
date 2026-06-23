(ns editor.property-overview-window
  (:require [editor.constants :refer [property-type->overview-table-props]]
            [editor.property-overview-window.table-rows :refer [overview-table-rows*]]
            [gdx.scenes.scene2d.ui.window :as window]
            [moon.db.all-raw :refer [all-raw]]
            [ui.window.add-close-button :as add-close-button]
            [ui.window.set-modal :as set-modal]
            [moon.property :as property]
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
                                    {:texture-region (textures/texture-region textures (property/image property))
                                     :on-clicked (fn [actor ctx]
                                                   (clicked-id-fn actor (:property/id property) ctx))
                                     :tooltip (property/tooltip property)
                                     :extra-info-text (extra-info-text property)}))
                             (partition-all columns)
                             (overview-table-rows* skin image-scale)))})
    (add-close-button/f! skin)
    (set-modal/f! true)))

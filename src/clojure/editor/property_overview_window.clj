(ns clojure.editor.property-overview-window
  (:require [clojure.scene2d.actor.get-height]
            [clojure.scene2d.actor.get-stage]
            [clojure.scene2d.actor.get-user-object]
            [clojure.scene2d.actor.get-width]
            [clojure.scene2d.actor.remove-actor]
            [clojure.scene2d.actor.set-user-object]
            [clojure.ui.window.add-close-button :as add-close-button]
            [clojure.scene2d.actor.add-listener]
            [clojure.db.all-raw :refer [all-raw]]
            [clojure.constants :refer [property-type->overview-table-props]]
            [clojure.moon-textures :as textures]
            [clojure.property-image :as property-image]
            [clojure.table-rows :refer [overview-table-rows*]]
            [clojure.tooltip :as tooltip]
            [clojure.ui-window :as window]
            [clojure.window :as gdx-window]))

(defn property-overview-window
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
                                    {:texture-region (textures/texture-region textures (property-image/f property))
                                     :on-clicked (fn [actor ctx]
                                                   (clicked-id-fn actor (:property/id property) ctx))
                                     :tooltip (tooltip/f property)
                                     :extra-info-text (extra-info-text property)}))
                             (partition-all columns)
                             (overview-table-rows* skin image-scale)))})
    (add-close-button/f! skin)
    (gdx-window/set-modal! true)))

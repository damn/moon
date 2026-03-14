(ns moon.ui-actors.dev-menu
  (:require [moon.dev-menu :as dev-menu]))

(defn create
  [{:keys [ctx/skin
           ctx/textures]
    :as ctx}
   {:keys [update-labels
           menus]}]
  (dev-menu/create
   {:menus (for [create-fn menus]
             (create-fn ctx))
    :update-labels (for [item (map deref update-labels)]
                     (if (:icon item)
                       (update item :icon #(get textures %))
                       item))
    :skin skin}))

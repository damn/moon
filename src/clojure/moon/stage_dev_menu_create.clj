(ns clojure.moon.stage-dev-menu-create
  (:require [clojure.menus.v :as menus]
            [clojure.ui.dev-menu :as dev-menu]
            [clojure.update-labels :as update-labels]))

(defn stage-dev-menu-create
  [{:keys [ctx/skin
           ctx/textures]}]
  (dev-menu/create
   {:menus menus/v
    :update-labels (for [item update-labels/v]
                     (if (:icon item)
                       (update item :icon #(get textures %))
                       item))
    :skin skin}))

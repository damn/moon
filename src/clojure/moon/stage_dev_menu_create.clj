(ns clojure.moon.stage-dev-menu-create
  (:require [clojure.ctx-data :as ctx-data]
            [clojure.debug-flags :as debug-flags]
            [clojure.levels.modules :as modules]
            [clojure.moon.create-controls-info :refer [controls-info]]
            [clojure.tmx :as tmx]
            [clojure.uf-caves :as uf-caves]
            [clojure.ui.dev-menu :as dev-menu]
            [clojure.update-labels :as update-labels]))

(defn stage-dev-menu-create
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}]
  (dev-menu/create
   {:menus [ctx-data/item
            debug-flags/item
            {:label "Help"
             :items [{:label controls-info}]}
            {:label "Select World"
             :items (for [[label world-fn] [["Vampire" tmx/vampire]
                                            ["UF Caves" uf-caves/create]
                                            ["Modules" modules/create]]]
                      {:label (str "Start " label)
                       :on-click (fn [ctx]
                                   ctx)})}]
    :update-labels (for [item update-labels/v]
                     (if (:icon item)
                       (update item :icon #(get textures %))
                       item))
    :skin skin}))

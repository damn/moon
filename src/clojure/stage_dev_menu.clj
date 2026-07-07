(ns clojure.stage-dev-menu
  (:require [clojure.ui-dev-menu :as dev-menu]
            [clojure.ctx-data :as ctx-data]
            [clojure.debug-flags :as debug-flags]
            [clojure.update-labels :as update-labels]
            [clojure.tmx :as tmx]
            [clojure.uf-caves :as uf-caves]
            [clojure.modules :as modules]))

(defn create
  [{:keys [ctx/controls-info
           ctx/db
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
                                   #_(let [rebuild-actors! nil
                                           #_(fn rebuild-actors! [stage ctx]
                                               (.clear stage)
                                               ((requiring-resolve 'game.create.add-actors/step) ctx))
                                           create-world nil
                                           #_(requiring-resolve 'game.create.world/step)
                                           ui stage
                                           stage (get-stage actor)]  ; get before clear, otherwise the actor does not have a stage anymore
                                       (rebuild-actors! ui ctx)
                                       #_(disposable/dispose! (:ctx/tiled-map ctx))
                                       (set! (.ctx ^Stage stage) (create-world ctx world-fn)))
                                   ctx)})}

            ]
    :update-labels (for [item update-labels/v]
                     (if (:icon item)
                       (update item :icon #(get textures %))
                       item))
    :skin skin}))

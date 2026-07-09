(ns clojure.moon.stage-dev-menu-create
  (:require [clojure.ctx-data :as ctx-data]
            [clojure.debug-flags :as debug-flags]
            [clojure.levels.modules :as modules]
            [clojure.moon.create-controls-info :refer [controls-info]]
            [clojure.levels.tmx :as tmx]
            [clojure.levels.uf-caves :as uf-caves]
            [clojure.ui.dev-menu :as dev-menu]
            [clojure.update-labels :as update-labels]))

(defn stage-dev-menu-create
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}]
  (dev-menu/create
   {:menus

    ; This is a thing - 'menus'
    ; give a folder and define somewhere
    [ctx-data/item
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
                            ctx)})}]

    :update-labels (for [item update-labels/v]
                     (if (:icon item)
                       (update item :icon #(get textures %))
                       item))
    :skin skin}))

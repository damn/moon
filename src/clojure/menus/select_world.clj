(ns clojure.menus.select-world
  (:require [clojure.levels.modules :as modules]
            [clojure.levels.tmx :as tmx]
            [clojure.levels.uf-caves :as uf-caves]))

(def item
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
                         ctx)})})

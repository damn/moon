(ns stage.dev-menu
  (:require [gdx.scenes.scene2d.ui.dev-menu :as dev-menu]
            [stage.dev-menu.ctx-data :as ctx-data]
            [stage.dev-menu.update-labels :as update-labels]))

(defn create
  [{:keys [ctx/controls-info
           ctx/db
           ctx/skin
           ctx/textures]}]
  (dev-menu/create
   {:menus [ctx-data/item
            {:label "Help"
             :items [{:label controls-info}]}
            {:label "Select World"
             :items (for [world-fn ["config/world_fns/vampire.edn"
                                    "config/world_fns/uf_caves.edn"
                                    "config/world_fns/modules.edn"]]
                      {:label (str "Start " world-fn)
                       :on-click (fn [actor {:keys [ctx/stage] :as ctx}]
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
                                       (set! (.ctx ^Stage stage) (create-world ctx world-fn))))})}

            ]
    :update-labels (for [item update-labels/v]
                     (if (:icon item)
                       (update item :icon #(get textures %))
                       item))
    :skin skin}))

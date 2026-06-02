(ns stage.dev-menu
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.graphics :as graphics]
            [gdx.graphics.orthographic-camera :as camera]
            [gdx.scenes.scene2d.ui.dev-menu :as dev-menu]
            [moon.number :as number]
            [stage.dev-menu.ctx-data :as ctx-data]))

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
                                           stage (Actor/.getStage actor)]  ; get before clear, otherwise the actor does not have a stage anymore
                                       (rebuild-actors! ui ctx)
                                       #_(disposable/dispose! (:ctx/tiled-map ctx))
                                       (set! (.ctx ^Stage stage) (create-world ctx world-fn))))})}

            ]
    :update-labels (for [item [
                               {:label "elapsed-time"
                                :update-fn (fn [{:keys [ctx/elapsed-time]}]
                                             (str (number/readable elapsed-time) " seconds"))
                                :icon "images/clock.png"}
                               {:label "FPS"
                                :update-fn (fn [{:keys [ctx/app]}]
                                             (graphics/frames-per-second (app/graphics app)))
                                :icon "images/fps.png"}
                               {:label "Mouseover-entity id"
                                :update-fn (fn [{:keys [ctx/mouseover-eid]}]
                                             (when-let [entity (and mouseover-eid @mouseover-eid)]
                                               (:entity/id entity)))
                                :icon "images/mouseover.png"}
                               {:label "paused?"
                                :update-fn :ctx/paused?}
                               {:label "GUI"
                                :update-fn (fn [{:keys [ctx/ui-mouse-position]}]
                                             (mapv int ui-mouse-position))}
                               {:label "World"
                                :update-fn (fn [{:keys [ctx/world-mouse-position]}]
                                             (mapv int world-mouse-position))}
                               {:label "Zoom"
                                :update-fn (fn [{:keys [ctx/world-viewport]}]
                                             (camera/zoom (:viewport/camera world-viewport)))
                                :icon "images/zoom.png"}
                               ]]
                     (if (:icon item)
                       (update item :icon #(get textures %))
                       item))
    :skin skin}))

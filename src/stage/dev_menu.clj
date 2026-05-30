(ns stage.dev-menu
  (:require [clojure.string :as str]
            [game.app :as app]
            [game.ctx :as ctx]
            [game.schema]
            [gdx.graphics.orthographic-camera :as camera]
            [gdx.scenes.scene2d.stage :as stage]
            [gdx.scenes.scene2d.ui.data-viewer-window :as data-viewer-window]
            [gdx.scenes.scene2d.ui.dev-menu :as dev-menu]
            [moon.db :as db]
            [moon.number :as number]
            [moon.ui.property-overview-window]))

(defn frames-per-second [{:keys [ctx/app]}]
  (app/frames-per-second app))

(defn camera-zoom [{:keys [ctx/world-viewport]}]
  (camera/zoom (:viewport/camera world-viewport)))

(defn create
  [{:keys [ctx/controls-info
           ctx/db
           ctx/skin
           ctx/textures]}]
  (dev-menu/create
   {:menus [
            {:label "Ctx Data"
             :items [{:label "Show data"
                      :on-click (fn [_actor {:keys [ctx/skin
                                                    ctx/stage] :as ctx}]
                                  (stage/add-actor! stage
                                                    (data-viewer-window/create
                                                     {:title "Data View"
                                                      :data ctx
                                                      :width 1000
                                                      :height 1000
                                                      :skin skin})))}]}
            {:label "Editor"
             :items (for [property-type (sort (db/property-types db))]
                      {:label (str/capitalize (name property-type))
                       :on-click (fn [_actor {:keys [ctx/db
                                                     ctx/skin
                                                     ctx/stage
                                                     ctx/textures]
                                              :as ctx}]
                                   (stage/add-actor! stage
                                                     (moon.ui.property-overview-window/create
                                                      {:db db
                                                       :textures textures
                                                       :skin skin
                                                       :property-type property-type
                                                       :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                                                        (stage/add-actor! stage
                                                                                          (game.schema/property-editor-window
                                                                                           {:ctx ctx
                                                                                            :property (db/get-raw db id)})))})))})}
            {:label "Help"
             :items [{:label controls-info}]}
            {:label "Select World"
             :items (for [world-fn ["world_fns/vampire.edn"
                                    "world_fns/uf_caves.edn"
                                    "world_fns/modules.edn"]]
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
                                :update-fn frames-per-second
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
                                :update-fn camera-zoom
                                :icon "images/zoom.png"}
                               ]]
                     (if (:icon item)
                       (update item :icon #(get textures %))
                       item))
    :skin skin}))

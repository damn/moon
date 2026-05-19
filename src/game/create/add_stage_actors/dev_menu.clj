(ns game.create.add-stage-actors.dev-menu
  (:require [moon.camera :as camera]
            [moon.stage :as stage]
            [gdl.graphics :as graphics]
            [clojure.string :as str]
            [moon.db :as db]
            [gdl.app :as app]
            [moon.number :as number]))

(defn create
  [{:keys [ctx/controls-info
           ctx/db
           ctx/skin
           ctx/textures]}]
  {:type :ui/menu
   :menus [
           {:label "Ctx Data"
            :items [{:label "Show data"
                     :on-click (fn [_actor {:keys [ctx/skin
                                                   ctx/stage] :as ctx}]
                                 (stage/add-actor! stage
                                                   {:type :ui/data-viewer-window
                                                    :title "Data View"
                                                    :data ctx
                                                    :width 1000
                                                    :height 1000
                                                    :skin skin}))}]}
           {:label "Editor"
            :items (for [property-type (sort (db/property-types db))]
                     {:label (str/capitalize (name property-type))
                      :on-click (fn [_actor {:keys [ctx/db
                                                    ctx/skin
                                                    ctx/stage
                                                    ctx/textures]
                                             :as ctx}]
                                  (stage/add-actor! stage
                                                    {:type :ui/property-overview-window
                                                     :db db
                                                     :textures textures
                                                     :skin skin
                                                     :property-type property-type
                                                     :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                                                      (stage/add-actor! stage
                                                                                        {:type :ui/property-editor-window
                                                                                         :ctx ctx
                                                                                         :property (db/get-raw db id)}))}))})}
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
                                      #_(Disposable/.dispose (:ctx/tiled-map ctx))
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
   :skin skin})

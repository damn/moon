(ns moon.application.create.add-stage-actors
  (:require [clojure.gdx.scene2d.actor :as actor]
            [clojure.gdx.scene2d.stage :as stage]
            [clojure.graphics :as graphics]
            [clojure.graphics.orthographic-camera :as camera]
            [clojure.graphics.viewport :as viewport]
            [clojure.string :as str]
            [moon.db :as db]
            [moon.info :as info]
            [moon.number :as number]
            [moon.state :as state]
            [moon.txs :as txs]
            [moon.ui-actors.action-bar]
            [moon.ui-actors.dev-menu]
            [moon.ui-actors.hp-mana-bar]
            [moon.ui-actors.player-message]
            [moon.ui-actors.player-state-draw]
            [moon.ui-actors.windows.info]
            [moon.ui-actors.windows.inventory]))

(defn- dev-menu-config
  [{:keys [ctx/controls-info
           ctx/db
           ctx/skin
           ctx/textures]}]
  {:menus [
           {:label "Ctx Data"
            :items [{:label "Show data"
                     :on-click (fn [_actor {:keys [ctx/skin
                                                   ctx/stage] :as ctx}]
                                 (stage/add-actor! stage
                                                   (actor/create
                                                    {:type :ui/data-viewer-window
                                                     :title "Data View"
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
                                                    (actor/create
                                                     {:type :ui/property-overview-window
                                                      :db db
                                                      :textures textures
                                                      :skin skin
                                                      :property-type property-type
                                                      :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                                                       (stage/add-actor! stage
                                                                                         (actor/create
                                                                                          {:type :ui/property-editor-window
                                                                                           :ctx ctx
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
                                              ((requiring-resolve 'moon.application.create.add-actors/step) ctx))
                                          create-world nil
                                          #_(requiring-resolve 'moon.application.create.world/step)
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
                               :update-fn (fn [{:keys [ctx/graphics]}]
                                            (graphics/frames-per-second graphics))
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
                                            (camera/zoom (viewport/camera world-viewport)))
                               :icon "images/zoom.png"}
                              ]]
                    (if (:icon item)
                      (update item :icon #(get textures %))
                      item))
   :skin skin})

(defn step
  [{:keys [ctx/stage]
    :as ctx}]
  (doseq [actor [(moon.ui-actors.dev-menu/create (dev-menu-config ctx))
                 (moon.ui-actors.action-bar/create)
                 (moon.ui-actors.hp-mana-bar/create ctx)
                 (actor/create
                  {:type :ui/group
                   :group/actors [(moon.ui-actors.windows.info/create
                                   {:title "Entity Info"
                                    :actor-name "moon.ui.windows.entity-info"
                                    :visible? false
                                    :position [(viewport/world-width (stage/viewport stage)) 0]
                                    :set-label-text! (fn [{:keys [ctx/mouseover-eid]
                                                           :as ctx}]
                                                       (if-let [eid mouseover-eid]
                                                         (info/text (apply dissoc @eid [:entity/skills
                                                                                        :entity/faction
                                                                                        :active-skill])
                                                                    ctx)
                                                         ""))
                                    :skin (:ctx/skin ctx)})
                                  (moon.ui-actors.windows.inventory/create ctx
                                                                           (fn clicked-inventory-cell [cell {:keys [ctx/player-eid] :as ctx}]
                                                                             (let [entity @player-eid
                                                                                   state-k (:state (:entity/fsm entity))]
                                                                               (txs/handle! ctx
                                                                                            (state/clicked-inventory-cell [state-k (state-k entity)]
                                                                                                                          player-eid
                                                                                                                          cell)))))]
                   :actor/name "moon.ui.windows"})
                 (moon.ui-actors.player-state-draw/create)
                 (moon.ui-actors.player-message/create)]]
    (stage/add-actor! (:ctx/stage ctx) actor))
  ctx)

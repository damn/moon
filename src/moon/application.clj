(ns moon.application
  (:require moon.application.render.draw-on-world-viewport.draw-tile-grid
            moon.application.render.draw-on-world-viewport.draw-cell-debug
            moon.application.render.draw-on-world-viewport.draw-entities
            moon.application.render.draw-on-world-viewport.highlight-mouseover-tile
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.math.vector2 :as v]
            [clojure.walk :as walk]
            [gdl.files :as files]
            [gdl.graphics.bitmap-font :as bitmap-font]
            [gdl.graphics.bitmap-font.data :as bitmap-font-data]
            [gdl.input.buttons :as input.buttons]
            [gdl.ui.actor :as actor]
            [gdl.ui.stage :as stage]
            [gdl.ui.skin :as skin]
            [gdl.utils.disposable :as disposable]
            [malli.core :as m]
            [malli.utils :as mu]
            moon.application.create.ui.dev-menu-config
            [moon.application.create.ui.hp-mana-bar-config :as hp-mana-bar-config]
            [moon.application.create.ui.inventory-window :as inventory-window]
            [moon.audio :as audio]
            [moon.color :as color]
            [moon.db :as db]
            [moon.db.impl]
            [moon.entity.body :as body]
            [moon.entity.inventory :as inventory]
            [moon.entity.skills.skill :as skill]
            [moon.entity.state.player-item-on-cursor :as player-item-on-cursor]
            [moon.entity.stats :as stats]
            [moon.graphics :as graphics]
            [moon.graphics.impl]
            [moon.input :as input]
            [moon.throwable :as throwable]
            [moon.ui :as ui]
            [moon.ui.info-window :as info-window]
            [moon.ui.message :as message]
            [moon.world :as world]
            [moon.world.impl]
            [moon.world.info :as info]
            [moon.world-fns.creature-tiles]
            [moon.world.tiled-map :as tiled-map]
            [moon.world.raycaster :as raycaster]
            moon.ui.impl
            moon.ui.build.editor-window
            moon.ui.dev-menu
            moon.ui.editor.overview-window
            moon.ui.editor.window
            moon.ui.editor.widgets-impl
            moon.entity.state-impl
            [moon.tx-handler :as tx-handler]
            [moon.txs :as txs]
            [qrecord.core :as q])
  (:import (com.badlogic.gdx ApplicationListener
                             Gdx)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)
           (gdl.ui Stage))
  (:gen-class))

(def ^:private schema
  (m/schema
   [:map {:closed true}
    [:ctx/audio :some]
    [:ctx/graphics :some]
    [:ctx/input :some]
    [:ctx/stage :some]
    [:ctx/skin :some]
    [:ctx/db :some]
    [:ctx/world :some]]))

(defn- validate [ctx]
  (mu/validate-humanize schema ctx)
  ctx)

(defn- spawn-player!
  [{:keys [ctx/db
           ctx/world]
    :as ctx}]
  (txs/handle! ctx
               [[:tx/spawn-creature (let [{:keys [creature-id
                                                  components]} (:world/player-components world)]
                                      {:position (mapv (partial + 0.5) (:world/start-position world))
                                       :creature-property (db/build db creature-id)
                                       :components components})]])
  (let [eid (get @(:world/entity-ids world) 1)]
    (assert (:entity/player? @eid))
    (assoc-in ctx [:ctx/world :world/player-eid] eid)))

(defn- spawn-enemies!
  [{:keys [ctx/db
           ctx/world]
    :as ctx}]
  (txs/handle!
   ctx
   (for [[position creature-id] (tiled-map/spawn-positions (:world/tiled-map world))]
     [:tx/spawn-creature {:position (mapv (partial + 0.5) position)
                          :creature-property (db/build db (keyword creature-id))
                          :components (:world/enemy-components world)}]))
  ctx)

(defn- call-world-fn
  [world-fn creature-properties graphics]
  (let [[f params] (-> world-fn io/resource slurp edn/read-string)]
    ((requiring-resolve f)
     (assoc params
            :level/creature-properties (moon.world-fns.creature-tiles/prepare creature-properties
                                                                             #(graphics/texture-region graphics %))
            :textures (:graphics/textures graphics)))))

(def ^:private world-params
  {:content-grid-cell-size 16
   :world/factions-iterations {:good 15 :evil 5}
   :world/max-delta 0.04
   :world/minimum-size 0.39
   :world/z-orders [:z-order/on-ground
                    :z-order/ground
                    :z-order/flying
                    :z-order/effect]
   :world/enemy-components {:entity/fsm {:fsm :fsms/npc
                                         :initial-state :npc-sleeping}
                            :entity/faction :evil}
   :world/player-components {:creature-id :creatures/vampire
                             :components {:entity/fsm {:fsm :fsms/player
                                                       :initial-state :player-idle}
                                          :entity/faction :good
                                          :entity/player? true
                                          :entity/free-skill-points 3
                                          :entity/clickable {:type :clickable/player}
                                          :entity/click-distance-tiles 1.5}}
   :world/effect-body-props {:width 0.5
                             :height 0.5
                             :z-order :z-order/effect}
   :world/create-fns (update-vals '{:entity/animation             moon.entity.animation/create
                                    :entity/body                  moon.entity.body/create
                                    :entity/delete-after-duration moon.entity.delete-after-duration/create
                                    :entity/projectile-collision  moon.entity.projectile-collision/create
                                    :entity/stats                 moon.entity.stats/create}
                                  requiring-resolve)
   :world/after-create-fns (update-vals '{:entity/fsm                             moon.entity.fsm/create!
                                          :entity/inventory                       moon.entity.inventory/create!
                                          :entity/skills                          moon.entity.skills/create!}
                                        requiring-resolve)
   })



(def reaction-txs-fn-map
  (update-vals '{
                 :tx/sound                    moon.tx.sound/do!
                 :tx/toggle-inventory-visible moon.tx.toggle-inventory-visible/do!
                 :tx/show-message             moon.tx.show-message/do!
                 :tx/show-modal               moon.tx.show-modal/do!
                 :tx/set-item                 moon.tx.set-item/do!
                 :tx/remove-item              moon.tx.remove-item/do!
                 :tx/add-skill                moon.tx.add-skill/do!
                 }
               requiring-resolve))

(def txs-fn-map
  (update-vals '{
                 ;; FIXME only this passes ctx, otherwise 'world' only
                 :tx/state-exit               moon.world.tx.state-exit/do!
                 :tx/audiovisual              moon.world.tx.audiovisual/do!
                 ;;

                 :tx/assoc                    moon.world.tx.assoc/do!
                 :tx/assoc-in                 moon.world.tx.assoc-in/do!
                 :tx/dissoc                   moon.world.tx.dissoc/do!
                 :tx/update                   moon.world.tx.update/do!
                 :tx/mark-destroyed           moon.world.tx.mark-destroyed/do!
                 :tx/set-cooldown             moon.world.tx.set-cooldown/do!
                 :tx/add-text-effect          moon.world.tx.add-text-effect/do!
                 :tx/add-skill                moon.world.tx.add-skill/do!
                 :tx/set-item                 moon.world.tx.set-item/do!
                 :tx/remove-item              moon.world.tx.remove-item/do!
                 :tx/pickup-item              moon.world.tx.pickup-item/do!
                 :tx/event                    moon.world.tx.event/do!
                 :tx/state-enter              moon.world.tx.state-enter/do!
                 :tx/effect                   moon.world.tx.effect/do!
                 :tx/spawn-alert              moon.world.tx.spawn-alert/do!
                 :tx/spawn-line               moon.world.tx.spawn-line/do!
                 :tx/move-entity              moon.world.tx.move-entity/do!
                 :tx/spawn-projectile         moon.world.tx.spawn-projectile/do!
                 :tx/spawn-effect             moon.world.tx.spawn-effect/do!
                 :tx/spawn-item               moon.world.tx.spawn-item/do!
                 :tx/spawn-creature           moon.world.tx.spawn-creature/do!
                 :tx/spawn-entity             moon.world.tx.spawn-entity/do!
                 :tx/sound                    moon.world.tx.nothing/do!
                 :tx/toggle-inventory-visible moon.world.tx.nothing/do!
                 :tx/show-message             moon.world.tx.nothing/do!
                 :tx/show-modal               moon.world.tx.nothing/do!
                 }
               requiring-resolve))

(defn reduce-actions!
  [txs-fn-map ctx txs]
  (loop [ctx ctx
         txs txs]
    (if (empty? txs)
      ctx
      (let [[k & params :as tx] (first txs)]
        (if tx
          (let [_ (assert (vector? tx))
                f (get txs-fn-map k)
                new-ctx (try
                         (if (nil? f)
                           ctx
                           (apply f ctx params))
                         (catch Throwable t
                           (throw (ex-info "Error handling tx"
                                           {:tx tx}
                                           t))))]
            (recur new-ctx
                   (rest txs)))
          (recur ctx
                 (rest txs)))))))


(q/defrecord Context []
  txs/TransactionHandler
  (handle! [ctx txs]
    (let [handled-txs (try (tx-handler/actions! txs-fn-map ctx txs)
                           (catch Throwable t
                             (throw (ex-info "Error handling txs"
                                             {:txs txs} t))))]
      (reduce-actions! reaction-txs-fn-map
                       ctx
                       handled-txs))))

(defn- create-world
  [{:keys [ctx/db
           ctx/graphics
           ctx/world]
    :as ctx}
   world-fn]
  (let [world-fn-result (call-world-fn world-fn
                                       (db/all-raw db :properties/creatures)
                                       graphics)]
    (-> ctx
        (assoc :ctx/world (moon.world.impl/create world-params world-fn-result))
        spawn-player!
        spawn-enemies!)))

(defn- create-dev-menu
  [ctx]
  (moon.application.create.ui.dev-menu-config/create
   ctx
   nil #_(fn rebuild-actors! [stage ctx]
           (stage/clear! stage)
           ((requiring-resolve 'moon.application.create.add-actors/step) ctx)) ; remove
   nil #_(requiring-resolve 'moon.application.create.world/step); remove
   nil #_(requiring-resolve 'moon.application.open-editor/do!))) ; editor separte ... - javafx ? -

(defn- create-action-bar [_ctx]
  {:type :actor/action-bar})

(defn- create-hp-mana-bar* [create-draws]
  {:type :actor/actor
   :act (fn [_this _delta])
   :draw (fn [actor _batch _parent-alpha]
           (when-let [stage (actor/stage actor)]
             (graphics/draw! (:ctx/graphics (stage/ctx stage))
                             (create-draws (stage/ctx stage)))))})

(defn- create-hp-mana-bar [ctx]
  (create-hp-mana-bar* (hp-mana-bar-config/create ctx)))

(defn- create-info-window
  [{:keys [ctx/skin
           ctx/stage]
    :as ctx}]
  (info-window/create skin
                      {:title "Entity Info"
                       :actor-name "moon.ui.windows.entity-info"
                       :visible? false
                       :position [(ui/viewport-width stage) 0]
                       :set-label-text! (fn [{:keys [ctx/world]}]
                                          (if-let [eid (:world/mouseover-eid world)]
                                            (info/text (apply dissoc @eid [:entity/skills
                                                                           :entity/faction
                                                                           :active-skill])
                                                       world)
                                            ""))}))

(defn- create-ui-windows
  [{:keys [ctx/skin]
    :as ctx}]
  {:type :actor/group
   :actor/name "moon.ui.windows"
   :group/actors (for [f [create-info-window
                          inventory-window/create]]
                   (f ctx))})

(def state->draw-ui-view
  {:player-item-on-cursor (fn
                            [eid
                             {:keys [ctx/graphics
                                     ctx/input
                                     ctx/stage]}]
                            ; TODO see player-item-on-cursor at render layers
                            ; always draw it here at right position, then render layers does not need input/stage
                            ; can pass world to graphics, not handle here at application
                            (when (not (player-item-on-cursor/world-item? (ui/mouseover-actor stage (input/mouse-position input))))
                              [[:draw/texture-region
                                (graphics/texture-region graphics (:entity/image (:entity/item-on-cursor @eid)))
                                (:graphics/ui-mouse-position graphics)
                                {:center? true}]]))})

(defn- player-state-handle-draws
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (let [player-eid (:world/player-eid world)
        entity @player-eid
        state-k (:state (:entity/fsm entity))]
    (when-let [f (state->draw-ui-view state-k)]
      (graphics/draw! graphics (f player-eid ctx)))))

(defn- create-player-state-draw-actor [_ctx]
  {:type :actor/actor
   :draw (fn [this _batch _parent-alpha]
           (player-state-handle-draws (stage/ctx (actor/stage this))))
   :act (fn [_ _delta])})

(def message-duration-seconds 0.5)

(defn- create-player-message-actor [_ctx]
  (message/create message-duration-seconds))

(defn- create! [config]
  (let [graphics (moon.graphics.impl/create! Gdx/graphics Gdx/files (:graphics config))
        stage (moon.ui.impl/create! graphics)
        skin (skin/create (files/internal Gdx/files "uiskin.json"))
        ctx (merge (map->Context {})
                   {:ctx/db (moon.db.impl/create)
                    :ctx/audio (moon.audio/create Gdx/audio Gdx/files (:audio config))
                    :ctx/graphics graphics
                    :ctx/input Gdx/input
                    :ctx/stage stage
                    :ctx/skin skin})]
    (.setInputProcessor Gdx/input stage)
    (-> skin
        (skin/font "default-font")
        bitmap-font/data
        (bitmap-font-data/set-enable-markup! true))
    (doseq [actor-create-fn [create-dev-menu
                             create-action-bar
                             create-hp-mana-bar
                             create-ui-windows
                             create-player-state-draw-actor
                             create-player-message-actor]]
      (stage/add-actor! stage (stage/build (actor-create-fn ctx))))
    (create-world ctx (:world config))))

(defn- dispose!
  [{:keys [ctx/audio
           ctx/graphics
           ctx/skin
           ctx/stage
           ctx/world]
    :as ctx}]
  (audio/dispose! audio)
  (graphics/dispose! graphics)
  (ui/dispose! stage)
  (disposable/dispose! skin)
  (world/dispose! world)
  ctx)

(defn- resize!
  [{:keys [ctx/graphics] :as ctx} width height]
  (graphics/update-ui-viewport! graphics width height)
  (graphics/update-world-vp! graphics width height)
  ctx)

(defn- get-stage-ctx
  [{:keys [ctx/stage]
    :as ctx}]
  (or (ui/get-ctx stage)
      ctx)) ; first render stage does not have ctx set.

(defn- update-mouse
  [{:keys [ctx/graphics
           ctx/input]
    :as ctx}]
  (let [mp (input/mouse-position input)]
    (-> ctx
        (assoc-in [:ctx/graphics :graphics/world-mouse-position] (graphics/unproject-world graphics mp))
        (assoc-in [:ctx/graphics :graphics/ui-mouse-position] (graphics/unproject-ui graphics mp)))))

(defn- update-mouseover-eid
  [{:keys [ctx/graphics
           ctx/input
           ctx/stage
           ctx/world]
    :as ctx}]
  (let [mouseover-actor (ui/mouseover-actor stage (input/mouse-position input))
        new-eid (if mouseover-actor
                  nil
                  (world/mouseover-entity world (:graphics/world-mouse-position graphics)))]
    (when-let [mouseover-eid (:world/mouseover-eid world)]
      (swap! mouseover-eid dissoc :entity/mouseover?))
    (when new-eid
      (swap! new-eid assoc :entity/mouseover? true))
    (assoc-in ctx [:ctx/world :world/mouseover-eid] new-eid)))

(defn- check-open-debug
  [{:keys [ctx/graphics
           ctx/input
           ctx/skin
           ctx/stage
           ctx/world]
    :as ctx}]
  (when (input/button-just-pressed? input (:open-debug-button input/controls))
    (let [data (or (and (:world/mouseover-eid world) @(:world/mouseover-eid world))
                   @((:world/grid world) (mapv int (:graphics/world-mouse-position graphics))))]
      (ui/show-data-viewer! stage data skin)))
  ctx)

(defn- assoc-active-entities
  [{:keys [ctx/world]
    :as ctx}]
  (update ctx :ctx/world world/cache-active-entities))

(defn- set-camera-on-player
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (graphics/set-position! graphics (world/player-position world))
  ctx)

(defn- clear-screen!
  [{:keys [ctx/graphics] :as ctx}]
  (graphics/clear-screen! graphics color/black)
  ctx)

(defn- tile-color-setter
  [{:keys [ray-blocked?
           explored-tile-corners
           light-position
           see-all-tiles?
           explored-tile-color
           visible-tile-color
           invisible-tile-color]}]
  #_(reset! do-once false)
  (let [light-cache (atom {})]
    (fn tile-color-setter [_color x y]
      (let [position [(int x) (int y)]
            explored? (get @explored-tile-corners position) ; TODO needs int call ?
            base-color (if explored?
                         explored-tile-color
                         invisible-tile-color)
            cache-entry (get @light-cache position :not-found)
            blocked? (if (= cache-entry :not-found)
                       (let [blocked? (ray-blocked? light-position position)]
                         (swap! light-cache assoc position blocked?)
                         blocked?)
                       cache-entry)]
        #_(when @do-once
            (swap! ray-positions conj position))
        (if blocked?
          (if see-all-tiles?
            visible-tile-color
            base-color)
          (do (when-not explored?
                (swap! explored-tile-corners assoc (mapv int position) true))
              visible-tile-color))))))

(comment
 (def ^:private count-rays? false)

 (def ray-positions (atom []))
 (def do-once (atom true))

 (count @ray-positions)
 2256
 (count (distinct @ray-positions))
 608
 (* 608 4)
 2432
 )

(defn- draw-world-map!
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (graphics/draw-tiled-map! graphics
                            (:world/tiled-map world)
                            (tile-color-setter
                             {:ray-blocked? (partial raycaster/blocked? world)
                              :explored-tile-corners (:world/explored-tile-corners world)
                              :light-position (graphics/position graphics)
                              :see-all-tiles? false
                              :explored-tile-color  [0.5 0.5 0.5 1]
                              :visible-tile-color   [1 1 1 1]
                              :invisible-tile-color [0 0 0 1]}))
  ctx)

(defn- draw-on-world-viewport!
  [{:keys [ctx/graphics]
    :as ctx}]
  (graphics/draw-on-world-vp! graphics
                              (fn []
                                (doseq [f [moon.application.render.draw-on-world-viewport.draw-tile-grid/do!
                                           moon.application.render.draw-on-world-viewport.draw-cell-debug/do!
                                           moon.application.render.draw-on-world-viewport.draw-entities/do!
                                           #_moon.application.render.draw-on-world-viewport.geom-test/do!
                                           moon.application.render.draw-on-world-viewport.highlight-mouseover-tile/do!]]
                                  (graphics/draw! graphics (f ctx)))))
  ctx)

(defn- player-effect-ctx [mouseover-eid world-mouse-position player-eid]
  (let [target-position (or (and mouseover-eid
                                 (:body/position (:entity/body @mouseover-eid)))
                            world-mouse-position)]
    {:effect/source player-eid
     :effect/target mouseover-eid
     :effect/target-position target-position
     :effect/target-direction (v/direction (:body/position (:entity/body @player-eid))
                                           target-position)}))

(defn- interaction-state
  [stage
   world-mouse-position
   mouseover-eid
   player-eid
   mouseover-actor]
  (cond
   mouseover-actor
   [:interaction-state/mouseover-actor (ui/actor-information stage mouseover-actor)]

   (and mouseover-eid
        (:entity/clickable @mouseover-eid))
   [:interaction-state/clickable-mouseover-eid
    {:clicked-eid mouseover-eid
     :in-click-range? (< (body/distance (:entity/body @player-eid)
                                        (:entity/body @mouseover-eid))
                         (:entity/click-distance-tiles @player-eid))}]

   :else
   (if-let [skill-id (ui/action-bar-selected-skill stage)]
     (let [entity @player-eid
           skill (skill-id (:entity/skills entity))
           effect-ctx (player-effect-ctx mouseover-eid world-mouse-position player-eid)
           state (skill/usable-state skill entity effect-ctx)]
       (if (= state :usable)
         [:interaction-state.skill/usable [skill effect-ctx]]
         [:interaction-state.skill/not-usable state]))
     [:interaction-state/no-skill-selected])))

(defn- assoc-interaction-state
  [{:keys [ctx/graphics
           ctx/input
           ctx/stage
           ctx/world]
    :as ctx}]
  (assoc ctx :ctx/interaction-state (interaction-state stage
                                                       (:graphics/world-mouse-position graphics)
                                                       (:world/mouseover-eid world)
                                                       (:world/player-eid    world)
                                                       (ui/mouseover-actor stage (input/mouse-position input)))))

(defn- player-idle->cursor [player-eid {:keys [ctx/interaction-state]}]
  (let [[k params] interaction-state]
    (case k
      :interaction-state/mouseover-actor
      (let [[actor-type params] params
            inventory-cell-with-item? (and (= actor-type :mouseover-actor/inventory-cell)
                                           (let [inventory-slot params]
                                             (get-in (:entity/inventory @player-eid) inventory-slot)))]
        (cond
          inventory-cell-with-item?
          :cursors/hand-before-grab

          (= actor-type :mouseover-actor/window-title-bar)
          :cursors/move-window

          (= actor-type :mouseover-actor/button)
          :cursors/over-button

          (= actor-type :mouseover-actor/unspecified)
          :cursors/default

          :else
          :cursors/default))

      :interaction-state/clickable-mouseover-eid
      (let [{:keys [clicked-eid
                    in-click-range?]} params]
        (case (:type (:entity/clickable @clicked-eid))
          :clickable/item (if in-click-range?
                            :cursors/hand-before-grab
                            :cursors/hand-before-grab-gray)
          :clickable/player :cursors/bag))

      :interaction-state.skill/usable
      :cursors/use-skill

      :interaction-state.skill/not-usable
      :cursors/skill-not-usable

      :interaction-state/no-skill-selected
      :cursors/no-skill-selected)))

(let [fn-map {:active-skill :cursors/sandclock
              :player-dead :cursors/black-x
              :player-idle player-idle->cursor
              :player-item-on-cursor :cursors/hand-grab
              :player-moving :cursors/walking
              :stunned :cursors/denied}]
  (defn state->cursor [[k v] eid ctx]
    (let [->cursor (k fn-map)]
      (if (keyword? ->cursor)
        ->cursor
        (->cursor eid ctx)))))

(defn- set-cursor
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (let [eid (:world/player-eid world)
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-key (state->cursor [state-k (state-k entity)] eid ctx)]
    (graphics/set-cursor! graphics cursor-key))
  ctx)

(defn- interaction-state->txs [[k params] stage player-eid]
  (case k
    :interaction-state/mouseover-actor nil

    :interaction-state/clickable-mouseover-eid
    (let [{:keys [clicked-eid
                  in-click-range?]} params]
      (if in-click-range?
        (case (:type (:entity/clickable @clicked-eid))
          :clickable/player
          [[:tx/toggle-inventory-visible]]

          :clickable/item
          (let [item (:entity/item @clicked-eid)]
            (cond
             (ui/inventory-window-visible? stage)
             [[:tx/sound "bfxr_takeit"]
              [:tx/mark-destroyed clicked-eid]
              [:tx/event player-eid :pickup-item item]]

             (inventory/can-pickup-item? (:entity/inventory @player-eid) item)
             [[:tx/sound "bfxr_pickup"]
              [:tx/mark-destroyed clicked-eid]
              [:tx/pickup-item player-eid item]]

             :else
             [[:tx/sound "bfxr_denied"]
              [:tx/show-message "Your Inventory is full"]])))
        [[:tx/sound "bfxr_denied"]
         [:tx/show-message "Too far away"]]))

    :interaction-state.skill/usable
    (let [[skill effect-ctx] params]
      [[:tx/event player-eid :start-action [skill effect-ctx]]])

    :interaction-state.skill/not-usable
    (let [state params]
      [[:tx/sound "bfxr_denied"]
       [:tx/show-message (case state
                           :cooldown "Skill is still on cooldown"
                           :not-enough-mana "Not enough mana"
                           :invalid-params "Cannot use this here")]])

    :interaction-state/no-skill-selected
    [[:tx/sound "bfxr_denied"]
     [:tx/show-message "No selected skill"]]))

(let [fn-map {:player-idle           (fn
                                       [player-eid
                                        {:keys [ctx/input
                                                ctx/interaction-state
                                                ctx/stage] :as ctx}]
                                       (if-let [movement-vector (input/player-movement-vector input)]
                                         [[:tx/event player-eid :movement-input movement-vector]]
                                         (when (input/button-just-pressed? input input.buttons/left)
                                           (interaction-state->txs interaction-state
                                                                   stage
                                                                   player-eid))))

              :player-item-on-cursor (fn
                                       [eid {:keys [ctx/input
                                                    ctx/stage]}]
                                       (let [mouseover-actor (ui/mouseover-actor stage (input/mouse-position input))]
                                         (when (and (input/button-just-pressed? input input.buttons/left)
                                                    (player-item-on-cursor/world-item? mouseover-actor))
                                           [[:tx/event eid :drop-item]])))

              :player-moving         (let [speed (fn [{:keys [entity/stats]}]
                                                   (or (stats/get-stat-value stats :stats/movement-speed)
                                                       0))]
                                       (fn [eid {:keys [ctx/input]}]
                                         (if-let [movement-vector (input/player-movement-vector input)]
                                           [[:tx/assoc eid :entity/movement {:direction movement-vector
                                                                             :speed (speed @eid)}]]
                                           [[:tx/event eid :no-movement-input]])))}]
  (defn- state->handle-input [[k v] eid ctx]
    (if-let [f (k fn-map)]
      (f eid ctx)
      nil)))

(defn- player-state-handle-input
  [{:keys [ctx/world]
    :as ctx}]
  (let [eid (:world/player-eid world)
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (state->handle-input [state-k (state-k entity)] eid ctx)]
    (txs/handle! ctx txs))
  ctx)

(defn- dissoc-interaction-state [ctx]
  (dissoc ctx :ctx/interaction-state))

(def pausing? true)

(def state->pause-game? {:stunned false
                         :player-moving false
                         :player-item-on-cursor true
                         :player-idle true
                         :player-dead true
                         :active-skill false})

(defn- assoc-paused
  [{:keys [ctx/input
           ctx/world]
    :as ctx}]
  (assoc-in ctx [:ctx/world :world/paused?]
            (or #_error
                (and pausing?
                     (state->pause-game? (:state (:entity/fsm @(:world/player-eid world))))
                     (not (or (input/key-just-pressed? input (:unpause-once input/controls))
                              (input/key-pressed? input (:unpause-continously input/controls))))))))

(defn- update-world-time
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (if (:world/paused? (:ctx/world ctx))
    ctx
    (update ctx :ctx/world world/update-time (graphics/delta-time graphics))))

(defn- update-potential-fields
  [{:keys [ctx/world]
    :as ctx}]
  (if (:world/paused? world)
    ctx
    (do
     (world/update-potential-fields! world)
     ctx)))

(defn- tick-entities
  [{:keys [ctx/skin
           ctx/stage
           ctx/world]
    :as ctx}]
  (if (:world/paused? world)
    ctx
    (do (try
         (txs/handle! ctx (world/tick-entities! world))
         (catch Throwable t
           (throwable/pretty-pst t)
           (ui/show-error-window! stage skin t)))
        ctx)))

(defn- remove-destroyed-entities
  [{:keys [ctx/world]
    :as ctx}]
  (txs/handle! ctx (world/remove-destroyed-entities! world))
  ctx)

(def zoom-speed 0.025)

(defn- window-camera-controls
  [{:keys [ctx/input
           ctx/graphics
           ctx/stage]
    :as ctx}]
  (when (input/key-pressed? input (:zoom-in input/controls))
    (graphics/change-zoom! graphics zoom-speed))

  (when (input/key-pressed? input (:zoom-out input/controls))
    (graphics/change-zoom! graphics (- zoom-speed)))

  (when (input/key-just-pressed? input (:close-windows-key input/controls))
    (ui/close-all-windows! stage))

  (when (input/key-just-pressed? input (:toggle-inventory input/controls))
    (ui/toggle-inventory-visible! stage))

  (when (input/key-just-pressed? input (:toggle-entity-info input/controls))
    (ui/toggle-entity-info-window! stage))
  ctx)

(defn- render-stage
  [{:keys [^Stage ctx/stage]
    :as ctx}]
  (set! (.ctx stage) ctx)
  (.act  stage)
  (.draw stage)
  (.ctx  stage))

(defn render! [ctx]
  (-> ctx
      get-stage-ctx
      validate
      update-mouse
      update-mouseover-eid
      check-open-debug
      assoc-active-entities
      set-camera-on-player
      clear-screen!
      draw-world-map!
      draw-on-world-viewport!
      assoc-interaction-state
      set-cursor
      player-state-handle-input
      dissoc-interaction-state
      assoc-paused
      update-world-time
      update-potential-fields
      tick-entities
      remove-destroyed-entities
      window-camera-controls
      render-stage
      validate))

(defn- edn-resource [path]
  (->> path
       io/resource
       slurp
       (edn/read-string {:readers {'edn/resource edn-resource}})
       (walk/postwalk (fn [form]
                        (if (and (symbol? form) (namespace form))
                          (let [avar (requiring-resolve form)]
                            (assert avar form)
                            avar)
                          form)))))

(def state (atom nil))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (let [config (edn-resource "config.edn")
        listener (reify ApplicationListener
                   (create [_]
                     (reset! state (create! config)))

                   (dispose [_]
                     (dispose! @state))

                   (render [_]
                     (swap! state render!))

                   (resize [_ width height]
                     (resize! @state width height))

                   (pause [_])

                   (resume [_]))]
    (Lwjgl3Application. listener
                        (doto (Lwjgl3ApplicationConfiguration.)
                          (.setTitle (:title config))
                          (.setWindowedMode (:width (:window config))
                                            (:height (:window config)))
                          (.setForegroundFPS (:fps config))))))

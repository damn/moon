(ns moon.application
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.walk :as walk]
            [gdl.files :as files]
            [gdl.graphics.bitmap-font :as bitmap-font]
            [gdl.graphics.bitmap-font.data :as bitmap-font-data]
            [gdl.input :as input]
            [gdl.ui.actor :as actor]
            [gdl.ui.stage :as stage]
            [gdl.ui.skin :as skin]
            [gdl.utils.disposable :as disposable]
            moon.application.create.ui.dev-menu-config
            [moon.application.create.ui.hp-mana-bar-config :as hp-mana-bar-config]
            [moon.application.create.ui.inventory-window :as inventory-window]
            [moon.audio :as audio]
            [moon.db :as db]
            [moon.db.impl]
            [moon.entity.state.player-item-on-cursor :as player-item-on-cursor]
            [moon.graphics :as graphics]
            [moon.graphics.impl]
            [moon.ui :as ui]
            [moon.ui.info-window :as info-window]
            [moon.ui.message :as message]
            [moon.world :as world]
            [moon.world.info :as info]
            [moon.world-fns.creature-tiles]
            [moon.world.tiled-map :as tiled-map]
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
                                             Lwjgl3ApplicationConfiguration))
  (:gen-class))

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
  [{:keys [ctx/config
           ctx/db
           ctx/graphics
           ctx/world]
    :as ctx}
   world-fn]
  (let [world-fn-result (call-world-fn world-fn
                                       (db/all-raw db :properties/creatures)
                                       graphics)]
    (-> ctx
        (assoc :ctx/world ((:world-impl config) world-params world-fn-result))
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
                    :ctx/config (:config config)
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
        render!  (:render!  config)
        listener (reify ApplicationListener
                   (create [_]
                     (reset! state (create! config)))

                   (dispose [_]
                     (dispose! @state))

                   (render [_]
                     (swap! state (fn [ctx]
                                    (reduce (fn [ctx [f & params]]
                                              (apply f ctx params))
                                            ctx
                                            render!))))

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

(ns cdq.ctx
  (:require [cdq.audio :as audio]
            [cdq.db.impl]
            ;;
            [cdq.game.create.add-actors]
            [cdq.game.create.dev-menu-config]
            [cdq.game.create.world]
            ;;
            [cdq.graphics :as graphics]
            [cdq.graphics.impl]
            [cdq.ui :as ui]
            [cdq.ui.build.editor-window]
            [cdq.ui.dev-menu]
            [cdq.ui.editor.overview-window]
            [cdq.ui.editor.window]
            [cdq.ui.impl]
            [cdq.world :as world]
            [cdq.world.impl]
            [clojure.tx-handler :as tx-handler]
            [clojure.txs :as txs]
            [qrecord.core :as q]))

(def reaction-txs-fn-map
  (update-vals '{
                 :tx/sound                    cdq.tx.sound/do!
                 :tx/toggle-inventory-visible cdq.tx.toggle-inventory-visible/do!
                 :tx/show-message             cdq.tx.show-message/do!
                 :tx/show-modal               cdq.tx.show-modal/do!
                 :tx/set-item                 cdq.tx.set-item/do!
                 :tx/remove-item              cdq.tx.remove-item/do!
                 :tx/add-skill                cdq.tx.add-skill/do!
                 :tx/get-stage-ctx cdq.game.render.get-stage-ctx/step
                 :tx/validate cdq.game.render.validate/step
                 :tx/update-mouse cdq.game.render.update-mouse/step
                 :tx/update-mouseover-eid cdq.game.render.update-mouseover-eid/step
                 :tx/check-open-debug cdq.game.render.check-open-debug/step
                 :tx/assoc-active-entities cdq.game.render.assoc-active-entities/step
                 :tx/set-camera-on-player cdq.game.render.set-camera-on-player/step
                 :tx/clear-screen cdq.game.render.clear-screen/step
                 :tx/draw-world-map cdq.game.render.draw-world-map/step
                 :tx/draw-on-world-viewport cdq.game.render.draw-on-world-viewport/step
                 :tx/assoc-interaction-state cdq.game.render.assoc-interaction-state/step
                 :tx/set-cursor cdq.game.render.set-cursor/step
                 :tx/player-state-handle-input cdq.game.render.player-state-handle-input/step
                 :tx/dissoc-interaction-state cdq.game.render.dissoc-interaction-state/step
                 :tx/assoc-paused cdq.game.render.assoc-paused/step
                 :tx/update-world-time cdq.game.render.update-world-time/step
                 :tx/update-potential-fields cdq.game.render.update-potential-fields/step
                 :tx/tick-entities cdq.game.render.tick-entities/step
                 :tx/remove-destroyed-entities cdq.game.render.remove-destroyed-entities/step
                 :tx/window-camera-controls cdq.game.render.window-camera-controls/step
                 :tx/render-stage cdq.game.render.render-stage/step
                 :tx/update-viewports cdq.tx.update-viewports/do!
                 :tx/dispose cdq.tx.dispose/do!
                 }
               requiring-resolve))

(def txs-fn-map
  (update-vals '{
                 ;; FIXME only this passes ctx, otherwise 'world' only
                 :tx/state-exit               cdq.world.tx.state-exit/do!
                 :tx/audiovisual              cdq.world.tx.audiovisual/do!
                 ;;

                 :tx/assoc                    cdq.world.tx.assoc/do!
                 :tx/assoc-in                 cdq.world.tx.assoc-in/do!
                 :tx/dissoc                   cdq.world.tx.dissoc/do!
                 :tx/update                   cdq.world.tx.update/do!
                 :tx/mark-destroyed           cdq.world.tx.mark-destroyed/do!
                 :tx/set-cooldown             cdq.world.tx.set-cooldown/do!
                 :tx/add-text-effect          cdq.world.tx.add-text-effect/do!
                 :tx/add-skill                cdq.world.tx.add-skill/do!
                 :tx/set-item                 cdq.world.tx.set-item/do!
                 :tx/remove-item              cdq.world.tx.remove-item/do!
                 :tx/pickup-item              cdq.world.tx.pickup-item/do!
                 :tx/event                    cdq.world.tx.event/do!
                 :tx/state-enter              cdq.world.tx.state-enter/do!
                 :tx/effect                   cdq.world.tx.effect/do!
                 :tx/spawn-alert              cdq.world.tx.spawn-alert/do!
                 :tx/spawn-line               cdq.world.tx.spawn-line/do!
                 :tx/move-entity              cdq.world.tx.move-entity/do!
                 :tx/spawn-projectile         cdq.world.tx.spawn-projectile/do!
                 :tx/spawn-effect             cdq.world.tx.spawn-effect/do!
                 :tx/spawn-item               cdq.world.tx.spawn-item/do!
                 :tx/spawn-creature           cdq.world.tx.spawn-creature/do!
                 :tx/spawn-entity             cdq.world.tx.spawn-entity/do!
                 :tx/sound                    cdq.world.tx.nothing/do!
                 :tx/toggle-inventory-visible cdq.world.tx.nothing/do!
                 :tx/show-message             cdq.world.tx.nothing/do!
                 :tx/show-modal               cdq.world.tx.nothing/do!
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

(defn create!
  [{:keys [audio
           files
           graphics
           input]}
   config]
  (let [graphics (cdq.graphics.impl/create! graphics files (:graphics config))
        stage (cdq.ui.impl/create! graphics {:dev-menu cdq.game.create.dev-menu-config/create})
        skin (com.badlogic.gdx.scenes.scene2d.ui.Skin. (.internal files "uiskin.json"))
        ; (-> (vis-ui/skin) (skin/font "default-font") bitmap-font/data (bmfont-data/set-enable-markup! true)
        ctx (-> (map->Context {})
                (assoc :ctx/graphics graphics)
                (assoc :ctx/stage stage)
                (assoc :ctx/audio (cdq.audio/create audio files (:audio config)))
                (assoc :ctx/db (cdq.db.impl/create))
                (assoc :ctx/input input)
                ; TODO dispose
                (assoc :ctx/skin skin)
                (assoc :ctx/config {:world-impl cdq.world.impl/create}))]
    (.bindRoot #'cdq.ui/skin skin)
    (.setInputProcessor input stage)
    (cdq.game.create.add-actors/step stage ctx)
    (cdq.game.create.world/step ctx (:world config))))

(defn dispose!
  [ctx]
  (reduce-actions! reaction-txs-fn-map
                   ctx
                   [[:tx/dispose]]))

(defn render! [ctx]
  (reduce-actions! reaction-txs-fn-map
                   ctx
                   [
                    [:tx/get-stage-ctx]
                    [:tx/validate]
                    [:tx/update-mouse]
                    [:tx/update-mouseover-eid]
                    [:tx/check-open-debug]
                    [:tx/assoc-active-entities]
                    [:tx/set-camera-on-player]
                    [:tx/clear-screen]
                    [:tx/draw-world-map]
                    [:tx/draw-on-world-viewport]
                    [:tx/assoc-interaction-state]
                    [:tx/set-cursor]
                    [:tx/player-state-handle-input]
                    [:tx/dissoc-interaction-state]
                    [:tx/assoc-paused]
                    [:tx/update-world-time]
                    [:tx/update-potential-fields]
                    [:tx/tick-entities]
                    [:tx/remove-destroyed-entities]
                    [:tx/window-camera-controls]
                    [:tx/render-stage]
                    [:tx/validate]
                    ]
                   ))

(defn resize! [ctx width height]
  (reduce-actions! reaction-txs-fn-map
                   ctx
                   [[:tx/update-viewports width height]]))

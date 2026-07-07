(ns clojure.moon-start
  (:require [clojure.action-bar :as action-bar]
            [clojure.application :as application]
            [clojure.assoc-interaction-state :as assoc-interaction-state]
            [clojure.assoc-paused :as assoc-paused]
            [clojure.check-debug-viewer :as check-debug-viewer]
            [clojure.clear-screen :as clear-screen]
            [clojure.content-grid :as content-grid]
            [clojure.controls :as controls]
            [clojure.ctx-audio :as ctx-audio]
            [clojure.ctx-batch :as ctx-batch]
            [clojure.ctx-colors :as ctx-colors]
            [clojure.ctx-db :as ctx-db]
            [clojure.ctx-grid :as ctx-grid]
            [clojure.ctx-shape-drawer :as ctx-shape-drawer]
            [clojure.ctx-skin :as ctx-skin]
            [clojure.ctx-stage :as ctx-stage]
            [clojure.ctx-textures :as ctx-textures]
            [clojure.ctx-tiled-map :as ctx-tiled-map]
            [clojure.cursors :as cursors]
            [clojure.default-font :as default-font]
            [clojure.disposable :as disposable]
            [clojure.draw-cell-debug :as draw-cell-debug]
            [clojure.draw-entities :as draw-entities]
            [clojure.draw-on-world-viewport :as draw-on-world-viewport]
            [clojure.explored-tile-corners :as explored-tile-corners]
            [clojure.gdx.audio :as audio]
            [clojure.gdx.files :as files]
            [clojure.gdx.graphics :as graphics]
            [clojure.gdx.input :as input]
            [clojure.handle-player-input :as handle-player-input]
            [clojure.highlight-mouseover-tile :as highlight-mouseover-tile]
            [clojure.hp-mana-bar :as hp-mana-bar]
            [clojure.if-not-paused :as if-not-paused]
            [clojure.if-not-paused-update-potential-fields :as if-not-paused-update-potential-fields]
            [clojure.inventory-window :as inventory-window]
            [clojure.listener :as listener]
            [clojure.lwjgl3-application :as lwjgl3-application]
            [clojure.max-speed :as max-speed]
            [clojure.player-message-actor :as player-message-actor]
            [clojure.player-state-draw :as player-state-draw]
            [clojure.put-colors :as put-colors]
            [clojure.raycaster :as raycaster]
            [clojure.record :as record]
            [clojure.remove-destroyed-entities :as remove-destroyed-entities]
            [clojure.render-draw-tiled-map :as render-draw-tiled-map]
            [clojure.render-validate :as render-validate]
            [clojure.render-z-order :as render-z-order]
            [clojure.set-active-entities :as set-active-entities]
            [clojure.set-camera-position :as set-camera-position]
            [clojure.set-cursor :as set-cursor]
            [clojure.shape-drawer-texture :as shape-drawer-texture]
            [clojure.spawn-enemies :as spawn-enemies]
            [clojure.spawn-player :as spawn-player]
            [clojure.stage :as stage]
            [clojure.stage-dev-menu :as stage-dev-menu]
            [clojure.stage-info-window :as stage-info-window]
            [clojure.tick-entities :as tick-entities]
            [clojure.tmx :as tmx]
            [clojure.tooltip-manager-opts :as tooltip-manager-opts]
            [clojure.update-draw-stage :as update-draw-stage]
            [clojure.update-mouse-positions :as update-mouse-positions]
            [clojure.update-mouseover-eid :as update-mouseover-eid]
            [clojure.update-time :as update-time]
            [clojure.viewport :as viewport]
            [clojure.window-camera-controls :as window-camera-controls]
            [clojure.windows :as windows]
            [clojure.world-viewport :as world-viewport])
  (:gen-class))

(defn- create [_ctx]
  (let [ctx {:ctx/audio (audio/f)
             :ctx/files (files/f)
             :ctx/graphics (graphics/f)
             :ctx/input (input/f)
             :ctx/unit-scale (atom 1)
             :ctx/active-entities nil
             :ctx/delta-time nil
             :ctx/ui-mouse-position nil
             :ctx/world-mouse-position nil
             :ctx/mouseover-eid nil
             :ctx/paused? false
             :ctx/elapsed-time 0
             :ctx/potential-field-cache (atom nil)
             :ctx/id-counter (atom 0)
             :ctx/entity-ids (atom {})
             :ctx/show-potential-field-colors? nil
             :ctx/show-cell-entities? false
             :ctx/show-cell-occupied? false
             :ctx/show-body-bounds? false}
        ctx (assoc ctx :ctx/batch (ctx-batch/step ctx))
        ctx (assoc ctx :ctx/audio (ctx-audio/step ctx))
        ctx (assoc ctx :ctx/shape-drawer-texture (shape-drawer-texture/step ctx))
        ctx (assoc ctx :ctx/shape-drawer (ctx-shape-drawer/step ctx))
        ctx (assoc ctx :ctx/skin (ctx-skin/step ctx))
        ctx (assoc ctx :ctx/stage (ctx-stage/step ctx))
        _ (tooltip-manager-opts/step ctx)
        _ (put-colors/step ctx)
        ctx (assoc ctx :ctx/cursors (cursors/step ctx))
        ctx (assoc ctx :ctx/textures (ctx-textures/step ctx {:folder "resources/"
                                                             :extensions #{"png" "bmp"}}))
        ctx (assoc ctx :ctx/world-viewport (world-viewport/step ctx))
        ctx (assoc ctx :ctx/default-font (default-font/step ctx))
        ctx (record/step ctx)
        ctx (controls/step ctx)
        ctx (assoc ctx :ctx/colors (ctx-colors/step ctx))
        ctx (assoc ctx :ctx/render-z-order (render-z-order/step ctx))
        ctx (assoc ctx :ctx/max-speed (max-speed/step ctx))
        ctx (assoc ctx :ctx/db (ctx-db/step ctx))
        _ (doseq [[f & params] [[stage-dev-menu/create]
                                [action-bar/create]
                                [hp-mana-bar/create]
                                [windows/create [stage-info-window/create
                                                 inventory-window/create]]
                                [player-state-draw/create]
                                [player-message-actor/create]]]
             (stage/add-actor! (:ctx/stage ctx) (apply f ctx params)))
        ctx (ctx-tiled-map/step ctx tmx/vampire)
        ctx (assoc ctx :ctx/grid (ctx-grid/step ctx))
        ctx (assoc ctx :ctx/content-grid (content-grid/step ctx))
        ctx (assoc ctx :ctx/explored-tile-corners (explored-tile-corners/step ctx))
        ctx (assoc ctx :ctx/raycaster (raycaster/step ctx))
        ctx (spawn-player/step ctx)
        _ (spawn-enemies/step ctx)]
    (dissoc ctx :ctx/files)))

(defn- dispose-app!
  [{:keys [ctx/audio
           ctx/batch
           ctx/cursors
           ctx/default-font
           ctx/shape-drawer-texture
           ctx/skin
           ctx/textures
           ctx/tiled-map]}]
  (run! disposable/dispose! (vals audio))
  (disposable/dispose! batch)
  (run! disposable/dispose! (vals cursors))
  (disposable/dispose! default-font)
  (disposable/dispose! shape-drawer-texture)
  (disposable/dispose! skin)
  (run! disposable/dispose! (vals textures))
  (disposable/dispose! tiled-map))

(defn- sync-stage-ctx
  [{:keys [ctx/stage]
    :as ctx}]
  (or (:stage/ctx stage)
      ctx))

(defn- resize-app!
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (viewport/update! (:stage/viewport stage) width height true)
  (viewport/update! world-viewport width height false))

(defn -main []
  (lwjgl3-application/f!
   {:title "Moon"
    :windowed-mode {:width 1440
                    :height 900}
    :foreground-fps 60}
   (listener/f
    {:state-var #'application/state
     :create-pipeline [[create]]
     :dispose! dispose-app!
     :render-pipeline [[sync-stage-ctx]
                       [render-validate/step]
                       [update-mouse-positions/step]
                       [update-mouseover-eid/step]
                       [check-debug-viewer/step]
                       [set-active-entities/step]
                       [set-camera-position/step]
                       [clear-screen/step]
                       [render-draw-tiled-map/step]
                       [draw-on-world-viewport/step
                        [[draw-cell-debug/f]
                         [draw-entities/do! [#{:entity/mouseover?
                                               :stunned
                                               :player-item-on-cursor}
                                             #{:entity/clickable
                                               :entity/animation
                                               :entity/image
                                               :entity/line-render}
                                             #{:npc-sleeping
                                               :entity/temp-modifier
                                               :entity/string-effect}
                                             #{:entity/stats
                                               :active-skill}]]
                         [highlight-mouseover-tile/f]]]
                       [(fn [ctx] (assoc ctx :ctx/interaction-state (assoc-interaction-state/create ctx)))]
                       [set-cursor/step]
                       [handle-player-input/step]
                       [(fn [ctx] (dissoc ctx :ctx/interaction-state))]
                       [assoc-paused/step]
                       [if-not-paused/step [update-time/f
                                            if-not-paused-update-potential-fields/f
                                            tick-entities/f]]
                       [remove-destroyed-entities/step]
                       [window-camera-controls/step]
                       [update-draw-stage/step]
                       [render-validate/step]]
     :resize! resize-app!})))

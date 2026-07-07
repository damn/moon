(ns clojure.moon-start
  (:require [clojure.action-bar :as action-bar]
            [clojure.add-stage-actors :as add-stage-actors]
            [clojure.application :as application]
            [clojure.assoc-create :as assoc-create]
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
            [clojure.ctx-dispose :as ctx-dispose]
            [clojure.ctx-grid :as ctx-grid]
            [clojure.ctx-resize :as ctx-resize]
            [clojure.ctx-shape-drawer :as ctx-shape-drawer]
            [clojure.ctx-skin :as ctx-skin]
            [clojure.ctx-stage :as ctx-stage]
            [clojure.ctx-textures :as ctx-textures]
            [clojure.ctx-tiled-map :as ctx-tiled-map]
            [clojure.cursors :as cursors]
            [clojure.default-font :as default-font]
            [clojure.do :as do]
            [clojure.draw-cell-debug :as draw-cell-debug]
            [clojure.draw-entities :as draw-entities]
            [clojure.draw-on-world-viewport :as draw-on-world-viewport]
            [clojure.explored-tile-corners :as explored-tile-corners]
            [clojure.gdx-context :as gdx-context]
            [clojure.get-stage-ctx :as get-stage-ctx]
            [clojure.handle-player-input :as handle-player-input]
            [clojure.highlight-mouseover-tile :as highlight-mouseover-tile]
            [clojure.hp-mana-bar :as hp-mana-bar]
            [clojure.if-not-paused :as if-not-paused]
            [clojure.if-not-paused-update-potential-fields :as if-not-paused-update-potential-fields]
            [clojure.inventory-window :as inventory-window]
            [clojure.listener :as listener]
            [clojure.lwjgl3-application :as lwjgl3-application]
            [clojure.map-create :as map-create]
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
            [clojure.stage-dev-menu :as stage-dev-menu]
            [clojure.stage-info-window :as stage-info-window]
            [clojure.tick-entities :as tick-entities]
            [clojure.tmx :as tmx]
            [clojure.tooltip-manager-opts :as tooltip-manager-opts]
            [clojure.unorganised :as unorganised]
            [clojure.update-draw-stage :as update-draw-stage]
            [clojure.update-mouse-positions :as update-mouse-positions]
            [clojure.update-mouseover-eid :as update-mouseover-eid]
            [clojure.update-time :as update-time]
            [clojure.window-camera-controls :as window-camera-controls]
            [clojure.windows :as windows]
            [clojure.world-viewport :as world-viewport])
  (:gen-class))

(defn -main []
  (lwjgl3-application/f!
   {:title "Moon"
    :windowed-mode {:width 1440
                    :height 900}
    :foreground-fps 60}
   (listener/f
    {:state-var #'application/state
     :create-pipeline [[gdx-context/f]
                       [unorganised/step]
                       [assoc-create/f :ctx/batch ctx-batch/step]
                       [assoc-create/f :ctx/audio ctx-audio/step]
                       [assoc-create/f :ctx/shape-drawer-texture shape-drawer-texture/step]
                       [assoc-create/f :ctx/shape-drawer ctx-shape-drawer/step]
                       [assoc-create/f :ctx/skin ctx-skin/step]
                       [assoc-create/f :ctx/stage ctx-stage/step]
                       [do/step [tooltip-manager-opts/step]]
                       [do/step [put-colors/step]]
                       [assoc-create/f :ctx/cursors cursors/step]
                       [map-create/f :ctx/textures [ctx-textures/step
                                                    {:folder "resources/"
                                                     :extensions #{"png" "bmp"}}]]
                       [assoc-create/f :ctx/world-viewport world-viewport/step]
                       [assoc-create/f :ctx/default-font default-font/step]
                       [record/step]
                       [controls/step]
                       [assoc-create/f :ctx/colors ctx-colors/step]
                       [assoc-create/f :ctx/render-z-order render-z-order/step]
                       [assoc-create/f :ctx/max-speed max-speed/step]
                       [assoc-create/f :ctx/db ctx-db/step]
                       [do/step [add-stage-actors/f!
                                 [[stage-dev-menu/create]
                                  [action-bar/create]
                                  [hp-mana-bar/create]
                                  [windows/create [stage-info-window/create
                                                   inventory-window/create]]
                                  [player-state-draw/create]
                                  [player-message-actor/create]]]]
                       [ctx-tiled-map/step tmx/vampire]
                       ; [ctx-tiled-map/step modules/create]
                       ; [ctx-tiled-map/step uf-caves/create]
                       [assoc-create/f :ctx/grid ctx-grid/step]
                       [assoc-create/f :ctx/content-grid content-grid/step]
                       [assoc-create/f :ctx/explored-tile-corners explored-tile-corners/step]
                       [assoc-create/f :ctx/raycaster raycaster/step]
                       [spawn-player/step]
                       [do/step [spawn-enemies/step]]
                       [clojure.core/dissoc :ctx/files]]
     :dispose! ctx-dispose/do!
     :render-pipeline [[get-stage-ctx/step]
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
                       [assoc-create/f :ctx/interaction-state assoc-interaction-state/create]
                       [set-cursor/step]
                       [handle-player-input/step]
                       [clojure.core/dissoc :ctx/interaction-state]
                       [assoc-paused/step]
                       [if-not-paused/step [update-time/f
                                            if-not-paused-update-potential-fields/f
                                            tick-entities/f]]
                       [remove-destroyed-entities/step]
                       [window-camera-controls/step]
                       [update-draw-stage/step]
                       [render-validate/step]]
     :resize! ctx-resize/do!})))

(ns clojure.moon
  (:require [gdl.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
            [clojure.malli-form-register-methods]
            [clojure.moon.create-audio :as create-audio]
            [clojure.moon.create-batch :as create-batch]
            [clojure.moon.create-bootstrap :as create-bootstrap]
            [clojure.moon.create-content-grid :as create-content-grid]
            [clojure.moon.create-context :as create-context]
            [clojure.moon.create-cursors :as create-cursors]
            [clojure.moon.create-db :as create-db]
            [clojure.moon.create-default-font :as create-default-font]
            [clojure.moon.create-dissoc-files :as create-dissoc-files]
            [clojure.moon.create-explored-tile-corners :as create-explored-tile-corners]
            [clojure.moon.create-game-config :as create-game-config]
            [clojure.moon.create-grid :as create-grid]
            [clojure.moon.create-init-tooltip :as create-init-tooltip]
            [clojure.moon.create-player-eid :as create-player-eid]
            [clojure.moon.create-raycaster :as create-raycaster]
            [clojure.moon.create-shape-drawer :as create-shape-drawer]
            [clojure.moon.create-shape-drawer-texture :as create-shape-drawer-texture]
            [clojure.moon.create-skin :as create-skin]
            [clojure.moon.create-spawn-creatures :as create-spawn-creatures]
            [clojure.moon.create-spawn-player :as create-spawn-player]
            [clojure.moon.create-stage :as create-stage]
            [clojure.moon.create-stage-actors :as create-stage-actors]
            [clojure.moon.create-textures :as create-textures]
            [clojure.moon.create-tiled-map :as create-tiled-map]
            [clojure.moon.create-world-viewport :as create-world-viewport]
            [clojure.moon.stage-ctx :as stage-ctx]
            [clojure.moon.render-validate :as render-validate]
            [clojure.moon.update-mouse-positions :as update-mouse-positions]
            [clojure.moon.update-mouseover-eid :as update-mouseover-eid]
            [clojure.moon.check-debug-viewer :as check-debug-viewer]
            [clojure.moon.set-active-entities :as set-active-entities]
            [clojure.moon.set-camera-position :as set-camera-position]
            [clojure.moon.clear-screen :as clear-screen]
            [clojure.moon.render-draw-tiled-map :as render-draw-tiled-map]
            [clojure.moon.draw-on-world-viewport :as draw-on-world-viewport]
            [clojure.moon.assoc-interaction-state :as assoc-interaction-state]
            [clojure.moon.set-cursor :as set-cursor]
            [clojure.moon.handle-player-input :as handle-player-input]
            [clojure.moon.dissoc-interaction-state :as dissoc-interaction-state]
            [clojure.moon.assoc-paused :as assoc-paused]
            [clojure.moon.when-not-paused :as when-not-paused]
            [clojure.moon.remove-destroyed-entities :as remove-destroyed-entities]
            [clojure.moon.window-camera-controls :as window-camera-controls]
            [clojure.moon.update-draw-stage :as update-draw-stage]
            [com.badlogic.gdx.utils.disposable :as disposable]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport])
  (:gen-class))

(def state (atom nil))

(defn create [application]
  (-> application
      create-bootstrap/f
      create-batch/f
      create-audio/f
      create-shape-drawer-texture/f
      create-shape-drawer/f
      create-skin/f
      create-stage/f
      create-init-tooltip/f
      create-cursors/f
      create-textures/f
      create-world-viewport/f
      create-default-font/f
      create-context/f
      create-game-config/f
      create-db/f
      create-stage-actors/f
      create-tiled-map/f
      create-grid/f
      create-content-grid/f
      create-explored-tile-corners/f
      create-raycaster/f
      create-spawn-player/f
      create-player-eid/f
      create-spawn-creatures/f
      create-dissoc-files/f))

(defn dispose
  [{:keys [ctx/audio
           ctx/batch
           ctx/cursors
           ctx/default-font
           ctx/shape-drawer-texture
           ctx/skin
           ctx/textures
           ctx/tiled-map]}]
  (run! disposable/dispose (vals audio))
  (disposable/dispose batch)
  (run! disposable/dispose (vals cursors))
  (disposable/dispose default-font)
  (disposable/dispose shape-drawer-texture)
  (disposable/dispose skin)
  (run! disposable/dispose (vals textures))
  (disposable/dispose tiled-map))

(defn render [ctx]
  (-> ctx
      stage-ctx/f
      render-validate/f
      update-mouse-positions/f
      update-mouseover-eid/f
      check-debug-viewer/f
      set-active-entities/f
      set-camera-position/f
      clear-screen/f
      render-draw-tiled-map/f
      draw-on-world-viewport/f
      assoc-interaction-state/f
      set-cursor/f
      handle-player-input/f
      dissoc-interaction-state/f
      assoc-paused/f
      when-not-paused/f
      remove-destroyed-entities/f
      window-camera-controls/f
      update-draw-stage/f
      render-validate/f))

(defn resize
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (viewport/update (:stage/viewport stage) width height true)
  (viewport/update world-viewport width height false))

(defn -main []
  (lwjgl3-application/create
   {:create! (fn [app]
               (reset! state (create app)))
    :dispose! (fn []
                (dispose @state))
    :render! (fn []
               (swap! state render))
    :resize! (fn [width height]
               (resize @state width height))
    :pause! (fn [])
    :resume! (fn [])}
   {:config/set-title "Moon"
    :config/set-windowed-mode {:width 1440
                               :height 900}
    :config/set-foreground-fps 60}))

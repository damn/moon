(ns clojure.moon.create
  (:require [clojure.malli-form-register-methods]
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
            [clojure.moon.create-world-viewport :as create-world-viewport]))

(defn create [^com.badlogic.gdx.Application app]
  (-> app
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

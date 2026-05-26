(ns game.ctx
  (:require [clojure.app :as app]
            [clojure.audio.sound :as sound]
            [clojure.graphics :as graphics]
            [clojure.graphics.gl20 :as gl20]
            [clojure.input :as input]
            [clojure.utils.disposable :refer [dispose!]]
            [malli.core :as m]
            [malli.utils :as mu]))

(def schema
  (m/schema
   [:map {:closed true}
    ; GDX app
    [:ctx/app :some] ; <- input, (audio), (files), graphics

    [:ctx/audio :some] ; 'sounds'

    ; Graphics:
    [:ctx/batch :some]
    [:ctx/cursors :some]
    [:ctx/default-font :some]
    [:ctx/unit-scale :some]
    [:ctx/world-unit-scale :some]
    [:ctx/world-viewport :some]
    [:ctx/shape-drawer :some]
    [:ctx/shape-drawer-texture :some]
    [:ctx/textures :some]

    ; UI
    [:ctx/skin :some]
    [:ctx/stage :some]


    ; Frame
    [:ctx/active-entities :any]
    [:ctx/delta-time :any]
    [:ctx/mouseover-eid :any]
    [:ctx/ui-mouse-position :any]
    [:ctx/world-mouse-position :any]

    ; Constants
    [:ctx/colors :some] ; cant as not bind-root'ed...
    [:ctx/controls :some]
    [:ctx/controls-info :some]
    [:ctx/max-delta :some]
    [:ctx/max-speed :some]
    [:ctx/minimum-size :some]
    [:ctx/render-z-order :some]
    [:ctx/z-orders :some]

    ; Game (level, time, db ?)
    ; The 'game' could be a separate library with no ligdx dependencies (which it is already)

    ; LEVEL
    [:ctx/content-grid :some]
    [:ctx/entity-ids :some]
    [:ctx/explored-tile-corners :some]
    [:ctx/factions-iterations :some]
    [:ctx/grid :some]
    [:ctx/id-counter :some]
    [:ctx/potential-field-cache :some]
    [:ctx/raycaster :some]
    [:ctx/start-position :some]
    [:ctx/tiled-map :some]

    ; ETC?
    [:ctx/db :some]
    [:ctx/elapsed-time :some]
    [:ctx/paused? :some]
    [:ctx/player-eid :some]
    ]))

(defn validate [ctx]
  (mu/validate-humanize schema ctx))

(defn delta-time
  [{:keys [ctx/app]}]
  (graphics/delta-time (app/graphics app)))

(defn frames-per-second
  [{:keys [ctx/app]}]
  (graphics/frames-per-second (app/graphics app)))

(defn clear-screen!
  [{:keys [ctx/app]}]
  (let [gl (graphics/gl20 (app/graphics app))]
    (gl20/clear-color! gl 0 0 0 0)
    (gl20/clear! gl gl20/color-buffer-bit)))

(defn set-cursor!
  [{:keys [ctx/app]} cursor]
  (graphics/set-cursor! (app/graphics app) cursor))

(defn key-pressed?
  [{:keys [ctx/app]} input-key]
  (input/key-pressed? (app/input app) input-key))

(defn key-just-pressed?
  [{:keys [ctx/app]} input-key]
  (input/key-just-pressed? (app/input app) input-key))

(defn mouse-position
  [{:keys [ctx/app]}]
  (input/mouse-position (app/input app)))

(defn button-just-pressed?
  [{:keys [ctx/app]} input-button]
  (input/button-just-pressed? (app/input app) input-button))

(defn dispose!
  [{:keys [ctx/audio
           ctx/batch
           ctx/cursors
           ctx/default-font
           ctx/shape-drawer-texture
           ctx/skin
           ctx/textures
           ctx/tiled-map]}]
  (run! dispose! (vals audio))
  (dispose! batch)
  (run! dispose! (vals cursors))
  (dispose! default-font)
  (dispose! shape-drawer-texture)
  (dispose! skin)
  (run! dispose! (vals textures))
  (dispose! tiled-map))

(defn play-sound!
  [{:keys [ctx/audio]} sound-name]
  (let [sounds audio]
    (assert (contains? sounds sound-name) (str sound-name))
    (sound/play! (get sounds sound-name))))

(defn sound-names
  [{:keys [ctx/audio]}]
  (map first audio))

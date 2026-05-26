(ns game.ctx
  (:require [clojure.app :as app]
            [clojure.audio.sound :as sound]
            [clojure.graphics :as graphics]
            [clojure.graphics.batch :as batch]
            [clojure.graphics.gl20 :as gl20]
            [clojure.graphics.orthographic-camera :as camera]
            [clojure.graphics.shape-drawer :as shape-drawer]
            [clojure.graphics.g2d.bitmap-font :as font]
            [clojure.graphics.g2d.bitmap-font.data :as data]
            [clojure.graphics.g2d.texture-region :as texture-region]
            [clojure.input :as input]
            [clojure.string :as str]
            [clojure.utils.disposable :refer [dispose!]]
            [malli.core :as m]
            [malli.utils :as mu]
            [moon.draws :as draws]
            [moon.raycaster :as raycaster]
            [moon.state :as state]))

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

(defmethod state/cursor :player-dead
  [_ _eid _ctx]
  :cursors/black-x)

(defmethod state/cursor :active-skill
  [_ _eid _ctx]
  :cursors/sandclock)

(defmethod state/cursor :stunned
  [_ _eid _ctx]
  :cursors/denied)

(defmethod state/cursor :player-moving
  [_ _eid _ctx]
  :cursors/walking)

(defmethod state/cursor :player-idle
  [_ eid {:keys [ctx/interaction-state]}]
  (let [[k params] interaction-state]
    (case k
      :interaction-state/mouseover-actor
      (let [[actor-type params] params
            inventory-cell-with-item? (and (= actor-type :mouseover-actor/inventory-cell)
                                           (let [inventory-slot params]
                                             (get-in (:entity/inventory @eid) inventory-slot)))]
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

(defn set-cursor!
  [{:keys [ctx/app
           ctx/cursors
           ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-key (state/cursor [state-k (state-k entity)] eid ctx)]
    (assert (contains? cursors cursor-key))
    (graphics/set-cursor! (app/graphics app) (get cursors cursor-key))))

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

(defn draw-text!
  [{:keys [ctx/batch
           ctx/unit-scale
           ctx/default-font]}
   {:keys [font scale x y text h-align up?]}]
  (let [font (or font default-font)
        old-scale (data/scale-x (font/data font))
        target-width 0
        wrap? false
        scale (* (float @unit-scale)
                 (float (or scale 1)))]
    (data/set-scale! (font/data font) (* old-scale scale))
    (font/draw! font
                batch
                text
                x
                (+ y (if up?
                       (-> text
                           (str/split #"\n")
                           count
                           (* (font/line-height font)))
                       0))
                target-width
                :align/center
                wrap?)
    (data/set-scale! (font/data font) old-scale)))

(defn draw-texture-region!
  [{:keys [ctx/batch
           ctx/unit-scale
           ctx/world-unit-scale]}
   texture-region
   [x y]
   & {:keys [center? rotation]}]
  (let [[w h] (let [dimensions [(texture-region/width  texture-region)
                                (texture-region/height texture-region)]]
                (if (= @unit-scale 1)
                  dimensions
                  (mapv (comp float (partial * world-unit-scale))
                        dimensions)))]
    (if center?
      (batch/draw! batch
                   texture-region
                   (- (float x) (/ (float w) 2))
                   (- (float y) (/ (float h) 2))
                   (/ (float w) 2)
                   (/ (float h) 2)
                   w
                   h
                   1
                   1
                   (or rotation 0))
      (batch/draw! batch texture-region x y w h))))

(defn draw-on-world-viewport!
  [{:keys [ctx/batch
           ctx/shape-drawer
           ctx/unit-scale
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}
   draw-fns]
  ; fix scene2d.ui.tooltip flickering
  ; _everything_ flickers with TextToolTip!
  ; it changes batch color somehow and does not change it back ! FIXME
  (batch/set-color! batch 1 1 1 1)
  ;
  (batch/set-projection-matrix! batch (camera/combined (:viewport/camera world-viewport)))
  (batch/begin! batch)
  (let [old-line-width (shape-drawer/default-line-width shape-drawer)]
    (shape-drawer/set-default-line-width! shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [f draw-fns]
      (draws/handle ctx (f ctx)))
    (reset! unit-scale 1)
    (shape-drawer/set-default-line-width! shape-drawer old-line-width))
  (batch/end! batch))

(defn- tile-color-setter*
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

(defn- tile-color-setter
  [{:keys [ctx/colors
           ctx/explored-tile-corners
           ctx/raycaster
           ctx/world-viewport]}]
  (tile-color-setter*
   {:ray-blocked? (partial raycaster/blocked? raycaster)
    :explored-tile-corners explored-tile-corners
    :light-position (camera/position (:viewport/camera world-viewport))
    :see-all-tiles? false
    :explored-tile-color  (:colors/explored-tile colors)
    :visible-tile-color   (:colors/visible-tile colors)
    :invisible-tile-color (:colors/invisible-tile colors)}))

(defn draw-tiled-map!
  [{:keys [ctx/batch
           ctx/tiled-map
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}]
  (batch/draw-tiled-map! batch
                         world-unit-scale
                         (:viewport/camera world-viewport)
                         tiled-map
                         (tile-color-setter ctx)))

(ns game.ctx
  (:require [clojure.app :as app]
            [clojure.audio :as audio]
            [clojure.audio.sound :as sound]
            [clojure.edn :as edn]
            [clojure.files :as files]
            [clojure.files.file-handle :as file-handle]
            [clojure.gdx.graphics.colors :as colors]
            [clojure.gdx.graphics.pixmap]
            [clojure.gdx.graphics.orthographic-camera :as orthographic-camera]
            [clojure.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [clojure.gdx.scenes.scene2d.ctx-stage :as ctx-stage]
            [clojure.gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]
            [clojure.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [clojure.graphics :as graphics]
            [clojure.graphics.batch :as batch]
            [clojure.graphics.texture :as texture]
            [clojure.graphics.texture.filter :as texture.filter]
            [clojure.graphics.pixmap :as pixmap]
            [clojure.graphics.gl20 :as gl20]
            [clojure.graphics.orthographic-camera :as camera]
            [clojure.graphics.shape-drawer :as shape-drawer]
            [clojure.graphics.g2d.bitmap-font :as font]
            [clojure.graphics.g2d.bitmap-font.data :as font.data]
            [clojure.graphics.g2d.freetype.font-generator :as font-generator]
            [clojure.graphics.g2d.texture-region :as texture-region]
            [clojure.java.io :as io]
            [clojure.scene2d.stage :as stage]
            [clojure.scene2d.actor :as actor]
            [clojure.scene2d.group :as group]
            [clojure.scene2d.ui.skin :as skin]
            [clojure.input :as input]
            [clojure.string :as str]
            [clojure.utils.disposable :refer [dispose!]]
            [clojure.utils.viewport :as viewport]
            [game.impl.textures]
            [malli.core :as m]
            [malli.utils :as mu]
            [moon.draws :as draws]
            [moon.raycaster :as raycaster]
            [moon.state :as state]
            [qrecord.core :as q]))

(q/defrecord Context [])

(defn create-record [ctx]
  (merge (map->Context {}) ctx))

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
        old-scale (font.data/scale-x (font/data font))
        target-width 0
        wrap? false
        scale (* (float @unit-scale)
                 (float (or scale 1)))]
    (font.data/set-scale! (font/data font) (* old-scale scale))
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
    (font.data/set-scale! (font/data font) old-scale)))

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

(def world-unit-scale :ctx/world-unit-scale)

(defn camera-zoom [{:keys [ctx/world-viewport]}]
  (camera/zoom (:viewport/camera world-viewport)))

(defn visible-tiles [{:keys [ctx/world-viewport]}]
  (camera/visible-tiles (:viewport/camera world-viewport)))

(defn camera-frustum [{:keys [ctx/world-viewport]}]
  (camera/frustum (:viewport/camera world-viewport)))

(defn set-camera-position!
  [{:keys [ctx/player-eid
           ctx/world-viewport]
    :as ctx}]
  (camera/set-position! (:viewport/camera world-viewport)
                        (:body/position (:entity/body @player-eid))))

(defn update-mouse-positions
  [{:keys [ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (let [mp (mouse-position ctx)]
    (-> ctx
        (assoc :ctx/world-mouse-position (viewport/unproject world-viewport mp))
        (assoc :ctx/ui-mouse-position (-> stage :stage/viewport (viewport/unproject mp))))))

(def zoom-speed 0.025)

(defn window-camera-controls
  [{:keys [ctx/controls
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (when (key-pressed? ctx (:zoom-in controls))
    (camera/inc-zoom! (:viewport/camera world-viewport) zoom-speed))

  (when (key-pressed? ctx (:zoom-out controls))
    (camera/inc-zoom! (:viewport/camera world-viewport) (- zoom-speed)))

  (when (key-just-pressed? ctx (:close-windows-key controls))
    (->> (stage/find-actor stage "moon.ui.windows")
         group/children
         (run! #(actor/set-visible! % false))))

  (when (key-just-pressed? ctx (:toggle-inventory controls))
    (-> stage
        (stage/find-actor "moon.ui.windows.inventory")
        actor/toggle-visible!))

  (when (key-just-pressed? ctx (:toggle-entity-info controls))
    (-> stage
        (stage/find-actor "moon.ui.windows.entity-info")
        actor/toggle-visible!))
  ctx)

(defn resize!
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (viewport/update! (:stage/viewport stage) width height true)
  (viewport/update! world-viewport width height false))

(defn world-viewport-width
  [{:keys [ctx/world-viewport]}]
  (:viewport/world-width world-viewport))

(defn world-viewport-height
  [{:keys [ctx/world-viewport]}]
  (:viewport/world-height world-viewport))

(defn create-app [app]
  (colors/put! {"PRETTY_NAME" [0.84 0.8 0.52 1]})
  (tooltip-manager/set-initial-time! 0)
  (let [batch (sprite-batch/create)
        white-pixel-texture (let [pixmap (doto (clojure.gdx.graphics.pixmap/create 1 1)
                                           (pixmap/set-color! 1 1 1 1)
                                           (pixmap/draw-pixel! 0 0))
                                  texture (pixmap/texture pixmap)]
                              (pixmap/dispose! pixmap)
                              texture)
        world-unit-scale (float (/ 48))]
    {:ctx/app app
     :ctx/audio (into {}
                      (for [sound-name (-> "sounds.edn" io/resource slurp edn/read-string)]
                        [sound-name
                         (audio/new-sound (app/audio app)
                                          (files/internal (app/files app) (format "sounds/%s.wav" sound-name)))]))
     :ctx/batch batch
     :ctx/shape-drawer-texture white-pixel-texture
     :ctx/shape-drawer (batch/shape-drawer batch (texture/region white-pixel-texture 1 0 1 1))
     :ctx/default-font (let [path "exocet/films.EXL_____.ttf"
                             size 16
                             quality-scaling 2
                             generator (file-handle/freetype-font-generator (files/internal (app/files app) path))
                             font (font-generator/generate-font generator
                                                                {:size (* size quality-scaling)
                                                                 ; texture.filter/linear because scaling to world-units
                                                                 :min-filter texture.filter/linear
                                                                 :mag-filter texture.filter/linear})]
                         (font-generator/dispose! generator)
                         (font.data/set-scale! (font/data font) (/ quality-scaling))
                         (font.data/set-markup-enabled! (font/data font) true)
                         (font/set-use-integer-positions! font false)
                         font)
     :ctx/world-unit-scale world-unit-scale
     :ctx/world-viewport (let [world-width  (* 1440 world-unit-scale)
                               world-height (* 900  world-unit-scale)]
                           (fit-viewport/create world-width
                                                world-height
                                                (orthographic-camera/create
                                                 {:y-down? false
                                                  :world-width world-width
                                                  :world-height world-height})))
     :ctx/cursors (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
                    (update-vals data
                                 (fn [[path [hotspot-x hotspot-y]]]
                                   (let [pixmap (file-handle/pixmap (files/internal (app/files app) (format path-format path)))
                                         cursor (graphics/new-cursor (app/graphics app) pixmap hotspot-x hotspot-y)]
                                     (pixmap/dispose! pixmap)
                                     cursor))))
     :ctx/stage (let [stage (ctx-stage/create (fit-viewport/create 1440 900) batch)]
                  (input/set-processor! (app/input app) stage)
                  stage)
     :ctx/skin (let [skin (file-handle/skin (files/internal (app/files app) "uiskin.json"))]
                 (-> skin
                     (skin/font "default-font")
                     font/data
                     (font.data/set-markup-enabled! true))
                 skin)
     :ctx/unit-scale (atom 1)}))

(defn create-textures
  [ctx]
  (assoc ctx :ctx/textures (game.impl.textures/create ctx)))

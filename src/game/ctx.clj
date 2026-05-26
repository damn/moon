(ns game.ctx
  ; TODO all the abbreviations
  ; 'gdl.*'
  ; gdl is a proposed game development language with just API = protocols
  ; each protocol is released separately
  ; we try to create a language for wiritng video games through the context
  ; you are writing a langauge exactly for your game .....
  ; it can become one ?
  (:require [clojure.app :as app]
            [clojure.audio :as audio]
            [clojure.audio.sound :as sound]
            [clojure.config :as config]
            [clojure.edn :as edn]
            [clojure.files :as files]
            [clojure.files.file-handle :as file-handle]
            [clojure.graphics.color :as color]
            [clojure.gdx :as gdx]
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
            [clojure.input.buttons :as input.buttons]
            [clojure.input.keys :as input.keys]
            [clojure.math.vector2 :as v]
            [clojure.string :as str]
            [clojure.utils.disposable :refer [dispose!]]
            [clojure.utils.viewport :as viewport]
            [game.impl.textures]
            [game.impl.db]
            [malli.core :as m]
            [malli.utils :as mu]
            [moon.controls :as controls]
            [moon.db :as db]
            [moon.creature-tiles]
            [moon.draws :as draws]
            [moon.raycaster :as raycaster]
            [moon.state :as state]
            [moon.textures :as textures]
            [moon.txs :as txs]
            [moon.order :as order]
            [qrecord.core :as q]))

(def world-fn-file
   "world_fns/modules.edn"
  ; "world_fns/vampire.edn"
  ; "world_fns/uf_caves.edn"
  )

(defn- actions!
  [txs-fn-map ctx txs]
  (loop [ctx ctx
         txs txs
         handled-txs []]
    (if (empty? txs)
      handled-txs
      (let [[k & params :as tx] (first txs)]
        (if tx
          (let [_ (assert (vector? tx))
                f (get txs-fn-map k)
                _ (assert f (str "Cannot find function for tx: " k))
                new-txs (try
                         (apply f ctx params)
                         (catch Throwable t
                           (throw (ex-info "Error handling tx"
                                           {:tx tx}
                                           t))))]

            ; TODO VALID RETURNS -> check!
            ; either nil? or vector of transactions (vectors = vector with keyword & params?)
            (recur ctx
                   (concat (or new-txs []) (rest txs))
                   (conj handled-txs tx)))
          (recur ctx
                 (rest txs)
                 handled-txs))))))

(defn- reduce-actions!
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

(q/defrecord Context [])

(defn impl-txs! [ctx {:keys [txs-fn-map reaction-txs-fn-map]}]
  (extend-type Context
    txs/Txs
    (handle! [ctx txs]
      (let [handled-txs (try (actions! txs-fn-map ctx txs)
                             (catch Throwable t
                               (throw (ex-info "Error handling txs"
                                               {:txs txs} t))))]
        (reduce-actions! reaction-txs-fn-map
                         ctx
                         handled-txs))))
  ctx)

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
  (gdx/put-colors! {"PRETTY_NAME" [0.84 0.8 0.52 1]})
  (gdx/set-tooltip-initial-time! 0)
  (let [batch (gdx/sprite-batch)
        white-pixel-texture (let [pixmap (doto (gdx/pixmap 1 1)
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
                           (gdx/fit-viewport world-width
                                             world-height
                                             (gdx/orthographic-camera
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
     :ctx/stage (let [stage (gdx/stage (gdx/fit-viewport 1440 900) batch)]
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

(def draw-fns
  {
   :draw/circle           (fn
                            [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/circle! shape-drawer x y radius))
   :draw/ellipse          (fn
                            [{:keys [ctx/shape-drawer]} [x y] radius-x radius-y color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/ellipse! shape-drawer x y radius-x radius-y))
   :draw/filled-circle    (fn
                            [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/filled-circle! shape-drawer x y radius))
   :draw/filled-rectangle (fn
                            [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/filled-rectangle! shape-drawer x y w h))
   :draw/grid             (fn
                            [ctx leftx bottomy gridw gridh cellw cellh color-float-bits]
                            (let [w (* (float gridw) (float cellw))
                                  h (* (float gridh) (float cellh))
                                  topy (+ (float bottomy) (float h))
                                  rightx (+ (float leftx) (float w))]
                              (doseq [idx (range (inc (float gridw)))
                                      :let [linex (+ (float leftx) (* (float idx) (float cellw)))]]
                                (draws/handle ctx
                                              [[:draw/line [linex topy] [linex bottomy] color-float-bits]]))
                              (doseq [idx (range (inc (float gridh)))
                                      :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
                                (draws/handle ctx
                                              [[:draw/line [leftx liney] [rightx liney] color-float-bits]]))))
   :draw/line             (fn
                            [{:keys [ctx/shape-drawer]} [sx sy] [ex ey] color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/line! shape-drawer sx sy ex ey))
   :draw/rectangle        (fn
                            [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/rectangle! shape-drawer x y w h))
   :draw/sector           (fn
                            [{:keys [ctx/shape-drawer]} [center-x center-y] radius start-radians radians color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/sector! shape-drawer center-x center-y radius start-radians radians))
   :draw/text             draw-text!
   :draw/texture-region   draw-texture-region!
   :draw/with-line-width  (fn
                            [{:keys [ctx/shape-drawer]
                              :as ctx}
                             width
                             draws]
                            (let [old-line-width (shape-drawer/default-line-width shape-drawer)]
                              (shape-drawer/set-default-line-width! shape-drawer (* width old-line-width))
                              (draws/handle ctx draws)
                              (shape-drawer/set-default-line-width! shape-drawer old-line-width)))
   })

(defn impl-draws! [ctx]
  (extend-type Context
    draws/Draws
    (handle [ctx draws]
      (doseq [{k 0 :as component} draws
              :when component]
        (apply (get draw-fns k) ctx (rest component)))))
  ctx)

(defn unorganised [ctx]
  (assoc ctx

         ;frame
         :ctx/active-entities nil
         :ctx/delta-time nil
         :ctx/ui-mouse-position nil
         :ctx/world-mouse-position nil
         :ctx/mouseover-eid nil
         :ctx/paused? false

         ; stuff
         :ctx/elapsed-time 0
         :ctx/potential-field-cache (atom nil)
         :ctx/id-counter (atom 0)
         :ctx/entity-ids (atom {})

         ; constants (config & not state?)
         :ctx/factions-iterations {:good 15 :evil 5}
         :ctx/max-delta 0.04
         :ctx/minimum-size 0.39
         :ctx/z-orders [:z-order/on-ground
                        :z-order/ground
                        :z-order/flying
                        :z-order/effect]
         ))

(defn create-controls [ctx]
  (extend-type Context
    controls/Controls
    (player-movement-vector [ctx]
      (let [r (when (key-pressed? ctx input.keys/d) [1  0])
            l (when (key-pressed? ctx input.keys/a) [-1 0])
            u (when (key-pressed? ctx input.keys/w) [0  1])
            d (when (key-pressed? ctx input.keys/s) [0 -1])]
        (when (or r l u d)
          (let [v (v/normalise (reduce v/add [0 0] (remove nil? [r l u d])))]
            (when (pos? (v/length v))
              v)))))
    )
  (assoc ctx
         :ctx/controls {
                        :zoom-in input.keys/minus
                        :zoom-out input.keys/equals
                        :unpause-once input.keys/p
                        :unpause-continously input.keys/space
                        :close-windows-key input.keys/escape
                        :toggle-inventory  input.keys/i
                        :toggle-entity-info input.keys/e
                        :open-debug-button input.buttons/right
                        }
         :ctx/controls-info (str/join "\n"
                                      ["[W][A][S][D] - Move"
                                       "[ESCAPE] - Close windows"
                                       "[I] - Inventory window"
                                       "[E] - Entity Info window"
                                       "[-]/[=] - Zoom"
                                       "[P]/[SPACE] - Unpause"
                                       "rightclick on tile or entity - open debug data window"
                                       "Leftmouse click - use skill/drop item on cursor"])))

(def black [0 0 0 1])
(def white [1 1 1 1])
(def gray  [0.5 0.5 0.5 1])
(def red   [1 0 0 1])

(def outline-alpha 0.4)

(defn create-colors [ctx]
  (assoc ctx :ctx/colors
         {
          :colors/mouseover-tile-air  (color/float-bits [1 1 0 0.5])
          :colors/mouseover-tile-none (color/float-bits [1 0 0 0.5])
          :colors/debug-body-outline-collides (color/float-bits white)
          :colors/debug-body-outline (color/float-bits gray)
          :colors/debug-body-outline-render-error (color/float-bits red)
          :colors/debug-cell-entities (color/float-bits [1 0 0 0.6])
          :colors/debug-cell-occupied (color/float-bits [0 0 1 0.6])
          :colors/debug-potential-field (fn [ratio]
                                          (color/float-bits [ratio (- 1 ratio) ratio 0.6]))
          :colors/target-all-line (color/float-bits [1 0 0 0.75])
          :colors/target-all-render (color/float-bits [1 0 0 0.5])
          :colors/target-entity-line (color/float-bits [1 0 0 0.75])
          :colors/target-entity-in-range (color/float-bits [1 0 0 0.5])
          :colors/target-entity-not-in-range (color/float-bits [1 1 0 0.5])
          :colors/enemy-color (color/float-bits [1 0 0 outline-alpha])
          :colors/friendly-color (color/float-bits [0 1 0 outline-alpha])
          :colors/neutral-color  (color/float-bits [1 1 1 outline-alpha])
          :colors/hp-bar (fn [ratio]
                           (let [ratio (float ratio)
                                 color (cond
                                        (> ratio 0.75) :green
                                        (> ratio 0.5)  :darkgreen
                                        (> ratio 0.25) :yellow
                                        :else          :red)]
                             (color {:green     (color/float-bits [0 0.8 0 1])
                                     :darkgreen (color/float-bits [0 0.5 0 1])
                                     :yellow    (color/float-bits [0.5 0.5 0 1])
                                     :red       (color/float-bits [0.5 0 0 1])})))
          :colors/hp-bar-rect (color/float-bits black)
          :colors/temp-modifier (color/float-bits [0.5 0.5 0.5 0.4])
          :colors/active-skill-circle (color/float-bits [1 1 1 0.125])
          :colors/active-skill-sector (color/float-bits [1 1 1 0.5])
          :colors/stunned (color/float-bits [1 1 1 0.6])
          :colors/explored-tile (color/float-bits [0.5 0.5 0.5 1])
          :colors/visible-tile (color/float-bits [1 1 1 1])
          :colors/invisible-tile (color/float-bits [0 0 0 1])
          :colors/droppable-item (color/float-bits [0 0.6 0 0.8 1])
          :colors/not-allowed-drop-item (color/float-bits [0.6 0 0 0.8 1])
          :colors/item-rect (color/float-bits [0.5 0.5 0.5 1])
          }))

(defn create-render-z-order
  [{:keys [ctx/z-orders]
    :as ctx}]
  (assoc ctx :ctx/render-z-order (order/define-order z-orders)))

(defn create-max-speed
  [{:keys [ctx/minimum-size
           ctx/max-delta]
    :as ctx}]
  (assoc ctx :ctx/max-speed (/ minimum-size max-delta)))

(defn create-db [ctx]
  (assoc ctx :ctx/db (game.impl.db/create)))

(defn add-stage-actors
  [{:keys [ctx/stage]
    :as ctx}
   actor-fns]
  (doseq [[actor-fn & params] actor-fns]
    (stage/add-actor! stage (apply actor-fn ctx params)))
  ctx)

(defn create-tiled-map
  [{:keys [ctx/db
           ctx/textures]
    :as ctx}]
  (let [[f params] (config/edn-resource world-fn-file)
        {:keys [tiled-map
                start-position]} (f
                                  (assoc params
                                         :level/creature-properties (moon.creature-tiles/prepare
                                                                     (db/all-raw db :properties/creatures)
                                                                     #(textures/texture-region textures %))
                                         :textures textures))]
    (assoc ctx
           :ctx/tiled-map tiled-map
           :ctx/start-position start-position)))

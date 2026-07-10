(ns clojure.moon
  (:require [clojure.body.distance :as distance]
            [clojure.body.draw-rectangle :as draw-rectangle]
            [clojure.content-grid.active-entities :as active-entities]
            [clojure.ctx.clear-screen :as ctx-clear-screen]
            [clojure.db.all-raw :refer [all-raw]]
            [clojure.db.build :refer [build]]
            [clojure.edn :as edn]
            [clojure.files.create-textures :as create-textures]
            [clojure.g2d.cells :refer [->cells]]
            [clojure.g2d.height :refer [->height]]
            [clojure.g2d.width :refer [->width]]
            [clojure.grid.cell.blocks-vision :as blocks-vision?]
            [clojure.grid-update-potential-fields :as update-potential-fields]
            [clojure.grid-cell :as grid-cell]
            [clojure.grid2d :as g2d]
            [clojure.inc-zoom :refer [inc-zoom!]]
            [clojure.java.io :as io]
            [clojure.levels.tmx :as tmx]
            [clojure.line-of-sight :as line-of-sight?]
            [clojure.malli-form-register-methods]
            [clojure.malli.schema :as malli-schema]
            [clojure.max-delta :refer [max-delta]]
            [clojure.minimum-size :refer [minimum-size]]
            [clojure.mouse-position :refer [mouse-position]]
            [clojure.mouseover-actor :refer [mouseover-actor]]
            [clojure.movement-property :as movement-property]
            [clojure.moon.action-bar-create :refer [action-bar-create]]
            [clojure.moon.color-setter :refer [tile-color-setter*]]
            [clojure.moon.context :as context]
            [clojure.moon.ctx-do :refer [do!]]
            [clojure.moon.draw :refer [draw!]]
            [clojure.moon.draw-component :refer [draw-component]]
            [clojure.moon.factions-iterations :refer [factions-iterations]]
            [clojure.moon.hp-mana-bar-create :refer [hp-mana-bar-create]]
            [clojure.moon.inventory-window-create :refer [inventory-window-create]]
            [clojure.moon.k-handle-input :refer [k->handle-input]]
            [clojure.moon.player-message-actor-create :refer [player-message-actor-create]]
            [clojure.moon.player-state-draw-create :refer [player-state-draw-create]]
            [clojure.moon.schema :refer [schema]]
            [clojure.moon.stage-dev-menu-create :refer [stage-dev-menu-create]]
            [clojure.moon.stage-info-window-create :refer [stage-info-window-create]]
            [clojure.moon.windows-create :refer [windows-create]]
            [clojure.moon.world-unit-scale :refer [world-unit-scale]]
            [clojure.moon.z-orders :refer [z-orders]]
            [clojure.moon-db :as db]
            [clojure.moon-textures :as textures]
            [clojure.orthographic-camera-position :as get-position]
            [clojure.orthographic-camera-set-position :as camera-set-position]
            [clojure.orthographic-camera.visible-tiles :refer [visible-tiles]]
            [clojure.pausing :refer [pausing?]]
            [clojure.player-effect-ctx :as player-effect-ctx]
            [clojure.point-to-entities :refer [point->entities]]
            [clojure.raycaster :as raycaster]
            [clojure.scene2d-stage :as scene2d-stage]
            [clojure.scene2d.actor.mouseover-info :refer [mouseover-actor-info]]
            [clojure.set-ctx :as set-ctx]
            [clojure.sort-by-order :as sort-by-order]
            [clojure.spawn-positions :as spawn-positions]
            [clojure.state-pause-game :refer [state->pause-game?]]
            [clojure.moon.controls-info :refer [controls-info]]
            [clojure.tick-component :refer [tick-component]]
            [clojure.tiled-map.creature-tiles :as creature-tiles]
            [clojure.throwable :as throwable]
            [clojure.ui.action-bar.selected-skill :as selected-skill]
            [clojure.ui.data-viewer-window :as data-viewer-window]
            [clojure.ui.error-window :as error-window]
            [clojure.unproject :as unproject]
            [clojure.usable-state :as usable-state]
            [com.badlogic.gdx.application :as application]
            [com.badlogic.gdx.audio :as audio]
            [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.graphics :as graphics]
            [com.badlogic.gdx.graphics.color :as color]
            [com.badlogic.gdx.graphics.colors :as colors]
            [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]
            [com.badlogic.gdx.graphics.g2d.bitmap-font$bitmap-font-data :as bitmap-font-data]
            [com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.graphics.glutils.pixmap-texture-data :as pixmap-texture-data]
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [com.badlogic.gdx.graphics.pixmap :as pixmap]
            [com.badlogic.gdx.graphics.pixmap$format :as pixmap-format]
            [com.badlogic.gdx.graphics.texture :as texture]
            [com.badlogic.gdx.graphics.texture$texture-filter :as texture-filter]
            [com.badlogic.gdx.input :as input]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]
            [com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]
            [com.badlogic.gdx.utils.disposable :as disposable]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]
            [gdl.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
            [gdl.graphics.g2d.freetype.font-generator :as free-type-font-generator]
            [gdl.input.buttons :as input-buttons]
            [gdl.input.keys :as input-keys]
            [gdl.maps.map-properties :as map-properties]
            [gdx.graphics.g2d.batch.draw-tiled-map :as draw-tiled-map]
            [space.earlygrey.shapedrawer.shape-drawer :as shape-drawer])
  (:gen-class))

(def colors
  (let [outline-alpha 0.4]
    {:colors/mouseover-tile-air (color/toFloatBits [1 1 0 0.5])
     :colors/mouseover-tile-none (color/toFloatBits [1 0 0 0.5])
     :colors/debug-body-outline-collides (color/toFloatBits [1 1 1 1])
     :colors/debug-body-outline (color/toFloatBits [0.5 0.5 0.5 1])
     :colors/debug-body-outline-render-error (color/toFloatBits [1 0 0 1])
     :colors/debug-cell-entities (color/toFloatBits [1 0 0 0.6])
     :colors/debug-cell-occupied (color/toFloatBits [0 0 1 0.6])
     :colors/debug-potential-field (fn [ratio]
                                     (color/toFloatBits [ratio (- 1 ratio) ratio 0.6]))
     :colors/target-all-line (color/toFloatBits [1 0 0 0.75])
     :colors/target-all-render (color/toFloatBits [1 0 0 0.5])
     :colors/target-entity-line (color/toFloatBits [1 0 0 0.75])
     :colors/target-entity-in-range (color/toFloatBits [1 0 0 0.5])
     :colors/target-entity-not-in-range (color/toFloatBits [1 1 0 0.5])
     :colors/enemy-color (color/toFloatBits [1 0 0 outline-alpha])
     :colors/friendly-color (color/toFloatBits [0 1 0 outline-alpha])
     :colors/neutral-color (color/toFloatBits [1 1 1 outline-alpha])
     :colors/hp-bar (fn [ratio]
                      (let [ratio (float ratio)
                            color (cond
                                    (> ratio 0.75) :green
                                    (> ratio 0.5) :darkgreen
                                    (> ratio 0.25) :yellow
                                    :else :red)]
                        (color {:green (color/toFloatBits [0 0.8 0 1])
                                :darkgreen (color/toFloatBits [0 0.5 0 1])
                                :yellow (color/toFloatBits [0.5 0.5 0 1])
                                :red (color/toFloatBits [0.5 0 0 1])})))
     :colors/hp-bar-rect (color/toFloatBits [0 0 0 1])
     :colors/temp-modifier (color/toFloatBits [0.5 0.5 0.5 0.4])
     :colors/active-skill-circle (color/toFloatBits [1 1 1 0.125])
     :colors/active-skill-sector (color/toFloatBits [1 1 1 0.5])
     :colors/stunned (color/toFloatBits [1 1 1 0.6])
     :colors/explored-tile (color/toFloatBits [0.5 0.5 0.5 1])
     :colors/visible-tile (color/toFloatBits [1 1 1 1])
     :colors/invisible-tile (color/toFloatBits [0 0 0 1])
     :colors/droppable-item (color/toFloatBits [0 0.6 0 0.8 1])
     :colors/not-allowed-drop-item (color/toFloatBits [0.6 0 0 0.8 1])
     :colors/item-rect (color/toFloatBits [0.5 0.5 0.5 1])}))

(def controls
  {:zoom-in :input.keys/minus
   :zoom-out :input.keys/equals
   :unpause-once :input.keys/p
   :unpause-continously :input.keys/space
   :close-windows-key :input.keys/escape
   :toggle-inventory :input.keys/i
   :toggle-entity-info :input.keys/e
   :open-debug-button :input.buttons/right})

(def max-speed
  (/ minimum-size max-delta))

(def render-z-order
  (apply hash-map (interleave z-orders (range))))

(defn create-bootstrap [application]
  {:ctx/audio    (application/getAudio    application)
   :ctx/files    (application/getFiles    application)
   :ctx/graphics (application/getGraphics application)
   :ctx/input    (application/getInput    application)
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
   :ctx/show-body-bounds? false})

(defn create-batch [ctx]
  (assoc ctx :ctx/batch (sprite-batch/new)))

(defn create-audio [ctx]
  (assoc ctx
         :ctx/audio (into {}
                          (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
                                :let [path (format "sounds/%s.wav" sound-name)]]
                            [sound-name
                             (audio/newSound (:ctx/audio ctx)
                                              (files/internal (:ctx/files ctx) path))]))))

(defn create-shape-drawer-texture [ctx]
  (let [pixmap (doto (pixmap/new 1 1 pixmap-format/RGBA8888)
                 (pixmap/setColor 1 1 1 1)
                 (pixmap/drawPixel 0 0))
        texture (texture/new (pixmap-texture-data/new pixmap
                                                      (pixmap/getFormat pixmap)
                                                      false
                                                      false))]
    (disposable/dispose pixmap)
    (assoc ctx :ctx/shape-drawer-texture texture)))

(defn create-shape-drawer [ctx]
  (assoc ctx
         :ctx/shape-drawer (shape-drawer/new (:ctx/batch ctx)
                                             (texture-region/new (:ctx/shape-drawer-texture ctx) 1 0 1 1))))

(defn create-skin [{:keys [ctx/files] :as ctx}]
  (let [skin (skin/new (files/internal files "skin/uiskin.json"))]
    (-> skin
        (skin/getFont "default-font")
        bitmap-font/getData
        (bitmap-font-data/set-markupEnabled true))
    (assoc ctx :ctx/skin skin)))

(defn create-stage [{:keys [ctx/input
                            ctx/batch] :as ctx}]
  (let [stage* (scene2d-stage/create (fit-viewport/new 1440 900) batch)]
    (input/setInputProcessor input stage*)
    (assoc ctx :ctx/stage stage*)))

(defn create-init-tooltip [ctx]
  (tooltip-manager/setInitialTime (tooltip-manager/getInstance) 0)
  (colors/put "PRETTY_NAME" (color/new [0.84 0.8 0.52 1]))
  ctx)

(defn create-cursors [ctx]
  (assoc ctx
         :ctx/cursors (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
                         (update-vals data
                                      (fn [[path-segment [hotspot-x hotspot-y]]]
                                        (let [path (format path-format path-segment)
                                              pixmap* (pixmap/new (files/internal (:ctx/files ctx) path))
                                              cursor (graphics/newCursor (:ctx/graphics ctx) pixmap* hotspot-x hotspot-y)]
                                          (disposable/dispose pixmap*)
                                          cursor))))))

(defn create-textures [{:keys [ctx/files] :as ctx}]
  (assoc ctx :ctx/textures (create-textures/f files {:folder "resources/"
                                                      :extensions #{"png" "bmp"}})))

(defn create-world-viewport [ctx]
  (assoc ctx
         :ctx/world-viewport (let [world-width (* 1440 world-unit-scale)
                                   world-height (* 900 world-unit-scale)]
                               (fit-viewport/new world-width
                                                    world-height
                                                    (doto (orthographic-camera/new)
                                                      (orthographic-camera/setToOrtho false world-width world-height))))))

(defn create-default-font [ctx]
  (assoc ctx
         :ctx/default-font (let [{:keys [path
                                         size
                                         quality-scaling
                                         use-integer-positions?]} {:path "fonts/films.EXL_____.ttf"
                                                                   :size 16
                                                                   :quality-scaling 2
                                                                   :use-integer-positions? false}
                                 generator (free-type-font-generator/new (files/internal (:ctx/files ctx) path))
                                 parameter {
                                            :set-size (* size quality-scaling)
                                            :set-min-filter texture-filter/Linear
                                            :set-mag-filter texture-filter/Linear
                                            }
                                 font (free-type-font-generator/generate-font generator parameter)
                                 font-data (bitmap-font/getData font)]
                             (disposable/dispose generator)
                             (bitmap-font-data/setScale font-data (/ quality-scaling))
                             (bitmap-font-data/set-markupEnabled font-data true)
                             (bitmap-font/setUseIntegerPositions font use-integer-positions?)
                             font)))

(defn create-context [ctx]
  (merge (context/map->R {}) ctx))

(defn create-game-config [ctx]
  (-> ctx
      (assoc :ctx/controls controls)
      (assoc :ctx/controls-info controls-info)
      (assoc :ctx/colors colors)
      (assoc :ctx/render-z-order render-z-order)
      (assoc :ctx/max-speed max-speed)))

(defn create-db [ctx]
  (assoc ctx :ctx/db (db/create)))

(defn create-stage-actors [ctx]
  (doseq [[actor-fn & params] [[action-bar-create]
                                [stage-dev-menu-create]
                                [hp-mana-bar-create]
                                [windows-create [stage-info-window-create
                                                 inventory-window-create]]
                                [player-state-draw-create]
                                [player-message-actor-create]]]
    (stage/addActor (:ctx/stage ctx) (apply actor-fn ctx params)))
  ctx)

(defn create-tiled-map [ctx]
  (let [{:keys [tiled-map
                start-position]} (tmx/vampire
                                   {:level/creature-properties (creature-tiles/prepare
                                                                 (all-raw (:ctx/db ctx) :properties/creatures)
                                                                 #(textures/texture-region (:ctx/textures ctx) %))
                                    :textures (:ctx/textures ctx)})]
    (assoc ctx
           :ctx/tiled-map tiled-map
           :ctx/start-position start-position)))

(defn create-grid [ctx]
  (assoc ctx
         :ctx/grid (g2d/create-grid (map-properties/get (tiled-map/getProperties (:ctx/tiled-map ctx)) "width")
                                    (map-properties/get (tiled-map/getProperties (:ctx/tiled-map ctx)) "height")
                                    (fn [position]
                                      (atom
                                       (grid-cell/map->R
                                        {:position position
                                         :middle (mapv (partial + 0.5) position)
                                         :movement (case (movement-property/f (:ctx/tiled-map ctx) position)
                                                      "none" :none
                                                      "air" :air
                                                      "all" :all)
                                         :entities #{}
                                         :occupied #{}}))))))

(defn create-content-grid [ctx]
  (let [width (map-properties/get (tiled-map/getProperties (:ctx/tiled-map ctx)) "width")
        height (map-properties/get (tiled-map/getProperties (:ctx/tiled-map ctx)) "height")
        cell-size 16]
    (assoc ctx
           :ctx/content-grid {:grid (g2d/create-grid
                                     (inc (int (/ width cell-size)))
                                     (inc (int (/ height cell-size)))
                                     (fn [idx]
                                       (atom {:idx idx
                                              :entities #{}})))
                              :cell-w cell-size
                              :cell-h cell-size})))

(defn create-explored-tile-corners [ctx]
  (assoc ctx
         :ctx/explored-tile-corners (atom (g2d/create-grid (map-properties/get (tiled-map/getProperties (:ctx/tiled-map ctx)) "width")
                                                            (map-properties/get (tiled-map/getProperties (:ctx/tiled-map ctx)) "height")
                                                            (constantly false)))))

(defn create-raycaster [ctx]
  (let [grid (:ctx/grid ctx)
        width (->width grid)
        height (->height grid)
        cells (for [cell (map deref (->cells grid))]
                [(:position cell)
                 (boolean (blocks-vision?/f cell))])
        arr (make-array Boolean/TYPE width height)]
    (doseq [[[x y] blocked?] cells]
      (aset arr x y (boolean blocked?)))
    (assoc ctx :ctx/raycaster [arr width height])))

(defn create-spawn-player [ctx]
  (do! ctx
       [[:tx/spawn-creature {:position (mapv (partial + 0.5) (:ctx/start-position ctx))
                             :creature-property (build (:ctx/db ctx) :creatures/vampire)
                             :components {:entity/fsm {:fsm :fsms/player
                                                       :initial-state :player-idle}
                                          :entity/faction :good
                                          :entity/player? true
                                          :entity/free-skill-points 3
                                          :entity/clickable {:type :clickable/player}
                                          :entity/click-distance-tiles 1.5}}]])
  ctx)

(defn create-player-eid [ctx]
  (let [eid (get @(:ctx/entity-ids ctx) 1)]
    (assert (:entity/player? @eid))
    (assoc ctx :ctx/player-eid eid)))

(defn create-spawn-creatures [ctx]
  (do! ctx
       (for [[position creature-id] (spawn-positions/f (:ctx/tiled-map ctx))]
         [:tx/spawn-creature {:position (mapv (partial + 0.5) position)
                              :creature-property (build (:ctx/db ctx) (keyword creature-id))
                              :components {:entity/fsm {:fsm :fsms/npc
                                                        :initial-state :npc-sleeping}
                                           :entity/faction :evil}}]))
  ctx)

(defn create-dissoc-files [ctx]
  (dissoc ctx :ctx/files))

(defn stage-ctx
  [{:keys [ctx/stage] :as ctx}]
  (or (:stage/ctx stage) ctx))

(defn render-validate [ctx]
  (malli-schema/validate-humanize schema ctx)
  ctx)

(defn update-mouse-positions
  [{:keys [ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (let [mp (mouse-position ctx)]
    (-> ctx
        (assoc :ctx/world-mouse-position (unproject/f world-viewport mp))
        (assoc :ctx/ui-mouse-position (-> stage :stage/viewport (unproject/f mp))))))

(defn update-mouseover-eid
  [{:keys [ctx/mouseover-eid
           ctx/player-eid
           ctx/grid
           ctx/raycaster
           ctx/render-z-order
           ctx/world-mouse-position]
    :as ctx}]
  (let [position world-mouse-position
        new-eid (if (mouseover-actor ctx)
                  nil
                  (let [player @player-eid
                        hits (remove #(= (:body/z-order (:entity/body @%)) :z-order/effect)
                                     (point->entities grid position))]
                    (->> render-z-order
                         (sort-by-order/f hits #(:body/z-order (:entity/body @%)))
                         reverse
                         (filter #(line-of-sight?/f raycaster player @%))
                         first)))]
    (when mouseover-eid
      (swap! mouseover-eid dissoc :entity/mouseover?))
    (when new-eid
      (swap! new-eid assoc :entity/mouseover? true))
    (assoc ctx :ctx/mouseover-eid new-eid)))

(defn check-debug-viewer
  [{:keys [ctx/input
           ctx/controls
           ctx/mouseover-eid
           ctx/skin
           ctx/stage
           ctx/grid
           ctx/world-mouse-position]
    :as ctx}]
  (when (input/isButtonJustPressed input (input-buttons/key-to-value (:open-debug-button controls)))
    (let [data (or (and mouseover-eid @mouseover-eid)
                   @(grid (mapv int world-mouse-position)))]
      (stage/addActor stage
                        (data-viewer-window/create
                         {:title "Data View"
                          :data data
                          :width 500
                          :height 500
                          :skin skin}))))
  ctx)

(defn set-active-entities
  [{:keys [ctx/player-eid
           ctx/content-grid]
    :as ctx}]
  (assoc ctx :ctx/active-entities
         (active-entities/f content-grid @player-eid)))

(defn set-camera-position
  [{:keys [ctx/player-eid
           ctx/world-viewport]
    :as ctx}]
  (camera-set-position/set-position! (viewport/getCamera world-viewport)
                                     (:body/position (:entity/body @player-eid)))
  ctx)

(defn clear-screen [ctx]
  (ctx-clear-screen/step ctx))

(defn render-draw-tiled-map
  [{:keys [ctx/batch
           ctx/colors
           ctx/explored-tile-corners
           ctx/raycaster
           ctx/tiled-map
           ctx/world-viewport]
    :as ctx}]
  (draw-tiled-map/draw! batch
                     world-unit-scale
                     (viewport/getCamera world-viewport)
                     tiled-map
                     (tile-color-setter*
                      {:ray-blocked? (partial raycaster/blocked? raycaster)
                       :explored-tile-corners explored-tile-corners
                       :light-position (get-position/f (viewport/getCamera world-viewport))
                       :see-all-tiles? false
                       :explored-tile-color (:colors/explored-tile colors)
                       :visible-tile-color (:colors/visible-tile colors)
                       :invisible-tile-color (:colors/invisible-tile colors)}))
  ctx)

(def ^:private render-layers
  [#{:entity/mouseover?
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
     :active-skill}])

(defn- draw-cell-debug
  [{:keys [ctx/colors
           ctx/grid
           ctx/world-viewport
           ctx/show-potential-field-colors?
           ctx/show-cell-entities?
           ctx/show-cell-occupied?]}]
  (apply concat
         (for [[x y] (visible-tiles (viewport/getCamera world-viewport))
               :let [cell (grid [x y])]
               :when cell
               :let [cell* @cell]]
           [(when (and show-cell-entities? (seq (:entities cell*)))
              [:draw/filled-rectangle x y 1 1 (:colors/debug-cell-entities colors)])
            (when (and show-cell-occupied? (seq (:occupied cell*)))
              [:draw/filled-rectangle x y 1 1 (:colors/debug-cell-occupied colors)])
            (when-let [faction show-potential-field-colors?]
              (let [{:keys [distance]} (faction cell*)]
                (when distance
                  (let [ratio (/ distance (factions-iterations faction))]
                    [:draw/filled-rectangle x y 1 1 ((:colors/debug-potential-field colors) ratio)]))))])))

(defn- draw-entities!
  [{:keys [ctx/active-entities
           ctx/colors
           ctx/player-eid
           ctx/raycaster
           ctx/render-z-order
           ctx/show-body-bounds?]
    :as ctx}]
  (let [entities (map deref active-entities)
        player @player-eid
        should-draw? (fn [entity z-order]
                       (or (= z-order :z-order/effect)
                           (line-of-sight?/f raycaster player entity)))]
    (doseq [[z-order entities] (sort-by-order/f (group-by (comp :body/z-order :entity/body) entities)
                                                first
                                                render-z-order)
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (try
        (do
          (when show-body-bounds?
            (draw! ctx (draw-rectangle/f (:entity/body entity)
                                         (if (:body/collides? (:entity/body entity))
                                           (:colors/debug-body-outline-collides colors)
                                           (:colors/debug-body-outline colors)))))
          (doseq [[k v] entity
                  :when (get render-layer k)]
            (draw! ctx (draw-component ctx entity k v))))
        (catch Throwable t
          (draw! ctx (draw-rectangle/f (:entity/body entity) (:colors/debug-body-outline-render-error colors)))
          (throwable/pretty-pst t))))))

(defn- highlight-mouseover-tile
  [{:keys [ctx/colors
           ctx/grid
           ctx/world-mouse-position]}]
  (let [[x y] (mapv int world-mouse-position)
        cell (grid [x y])]
    (when (and cell (#{:air :none} (:movement @cell)))
      [[:draw/rectangle x y 1 1
        (case (:movement @cell)
          :air (:colors/mouseover-tile-air colors)
          :none (:colors/mouseover-tile-none colors))]])))

(defn draw-on-world-viewport
  [{:keys [ctx/batch
           ctx/shape-drawer
           ctx/unit-scale
           ctx/world-viewport]
    :as ctx}]
  (batch/setColor batch 1 1 1 1)
  (batch/setProjectionMatrix batch (orthographic-camera/combined (viewport/getCamera world-viewport)))
  (batch/begin batch)
  (let [old-line-width (shape-drawer/getDefaultLineWidth shape-drawer)]
    (shape-drawer/setDefaultLineWidth shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [draw-fn [draw-cell-debug
                     draw-entities!
                     highlight-mouseover-tile]]
      (draw! ctx (draw-fn ctx)))
    (reset! unit-scale 1)
    (shape-drawer/setDefaultLineWidth shape-drawer old-line-width))
  (batch/end batch)
  ctx)

(defn- make-interaction-state
  [{:keys [ctx/mouseover-eid
           ctx/stage
           ctx/player-eid
           ctx/world-mouse-position]
    :as ctx}]
  (let [mouseover-actor* (mouseover-actor ctx)]
    (cond
      mouseover-actor*
      [:interaction-state/mouseover-actor (mouseover-actor-info mouseover-actor*)]

      (and mouseover-eid
           (:entity/clickable @mouseover-eid))
      [:interaction-state/clickable-mouseover-eid
       {:clicked-eid mouseover-eid
        :in-click-range? (< (distance/f (:entity/body @player-eid)
                                        (:entity/body @mouseover-eid))
                            (:entity/click-distance-tiles @player-eid))}]

      :else
      (if-let [skill-id (-> stage
                            :stage/root
                            (#(group/findActor % "moon.ui.action-bar"))
                            selected-skill/f)]
        (let [entity @player-eid
              skill (skill-id (:entity/skills entity))
              effect-ctx (player-effect-ctx/f mouseover-eid world-mouse-position player-eid)
              state (usable-state/f skill entity effect-ctx)]
          (if (= state :usable)
            [:interaction-state.skill/usable [skill effect-ctx]]
            [:interaction-state.skill/not-usable state]))
        [:interaction-state/no-skill-selected]))))

(defn assoc-interaction-state [ctx]
  (assoc ctx :ctx/interaction-state (make-interaction-state ctx)))

(def k->cursor
  {:player-item-on-cursor :cursors/hand-grab
   :player-dead :cursors/black-x
   :active-skill :cursors/sandclock
   :stunned :cursors/denied
   :player-moving :cursors/walking
   :player-idle (fn
                  [eid {:keys [ctx/interaction-state]}]
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
                      :cursors/no-skill-selected)))})

(defn set-cursor
  [{:keys [ctx/graphics
           ctx/cursors
           ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-fn (k->cursor state-k)
        cursor-key (if (keyword? cursor-fn)
                     cursor-fn
                     (cursor-fn eid ctx))]
    (assert (contains? cursors cursor-key))
    (graphics/setCursor graphics (get cursors cursor-key)))
  ctx)

(defn handle-player-input
  [{:keys [ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (if-let [input-fn (k->handle-input state-k)]
              (input-fn eid ctx)
              nil)]
    (do! ctx txs))
  ctx)

(defn dissoc-interaction-state [ctx]
  (dissoc ctx :ctx/interaction-state))

(defn assoc-paused
  [{:keys [ctx/input
           ctx/controls
           ctx/player-eid]
    :as ctx}]
  (assoc ctx :ctx/paused?
         (or #_error
             (and pausing?
                  (state->pause-game? (:state (:entity/fsm @player-eid)))
                  (not (or (input/isKeyJustPressed input (input-keys/key-to-value (:unpause-once controls)))
                           (input/isKeyPressed input (input-keys/key-to-value (:unpause-continously controls)))))))))

(defn- update-time
  [{:keys [ctx/graphics]
    :as ctx}]
  (let [delta-ms (min (graphics/getDeltaTime graphics) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))

(defn- update-potential-fields
  [{:keys [ctx/active-entities
           ctx/grid
           ctx/potential-field-cache]
    :as ctx}]
  (doseq [[faction max-iterations] factions-iterations]
    (update-potential-fields/tick! grid
                                   potential-field-cache
                                   faction
                                   active-entities
                                   max-iterations))
  ctx)

(defn- tick-entities
  [{:keys [ctx/active-entities
           ctx/skin
           ctx/stage]
    :as ctx}]
  (try
    (do! ctx
         (mapcat (fn [eid]
                   (mapcat (fn [component]
                             (try (tick-component ctx eid component)
                                  (catch Throwable t
                                    (throw (ex-info "Error at `entity/tick`:" {:eid eid} t)))))
                           @eid))
                 active-entities))
    (catch Throwable t
      (throwable/pretty-pst t)
      (stage/addActor stage
                        (error-window/create
                         {:skin skin
                          :throwable t}))))
  ctx)

(defn when-not-paused [ctx]
  (if (:ctx/paused? ctx)
    ctx
    (reduce (fn [ctx step] (step ctx))
            ctx
            [update-time
             update-potential-fields
             tick-entities])))

(def k->destroy
  {
   :entity/destroy-audiovisual
   (fn
     [audiovisuals-id eid]
     [[:tx/audiovisual (:body/position (:entity/body @eid)) audiovisuals-id]])
   })

(defn remove-destroyed-entities [ctx]
  (do! ctx
       (mapcat
        (fn [eid]
          (cons
           [:tx/unregister-eid eid]
           (mapcat (fn [[k v]]
                     (if-let [destroy-fn (k->destroy k)]
                       (destroy-fn v eid)
                       nil))
                   @eid)))
        (filter (comp :entity/destroyed? deref)
                (vals @(:ctx/entity-ids ctx)))))
  ctx)

(def zoom-speed 0.025)

(defn window-camera-controls
  [{:keys [ctx/input
           ctx/controls
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (when (input/isKeyPressed input (input-keys/key-to-value (:zoom-in controls)))
    (inc-zoom! (viewport/getCamera world-viewport) zoom-speed))

  (when (input/isKeyPressed input (input-keys/key-to-value (:zoom-out controls)))
    (inc-zoom! (viewport/getCamera world-viewport) (- zoom-speed)))

  (when (input/isKeyJustPressed input (input-keys/key-to-value (:close-windows-key controls)))
    (->> (group/findActor (:stage/root stage) "moon.ui.windows")
         group/getChildren
         (run! #(actor/setVisible % false))))

  (when (input/isKeyJustPressed input (input-keys/key-to-value (:toggle-inventory controls)))
    (let [inventory (group/findActor (:stage/root stage) "moon.ui.windows.inventory")]
      (actor/setVisible inventory (not (actor/isVisible inventory)))))

  (when (input/isKeyJustPressed input (input-keys/key-to-value (:toggle-entity-info controls)))
    (let [entity-info (group/findActor (:stage/root stage) "moon.ui.windows.entity-info")]
      (actor/setVisible entity-info (not (actor/isVisible entity-info)))))
  ctx)

(defn update-draw-stage
  [{:keys [ctx/stage] :as ctx}]
  (set-ctx/f stage ctx)
  (stage/act stage)
  (stage/draw stage)
  (:stage/ctx stage))

(def state (atom nil))

(defn create [application]
  (-> application
      create-bootstrap
      create-batch
      create-audio
      create-shape-drawer-texture
      create-shape-drawer
      create-skin
      create-stage
      create-init-tooltip
      create-cursors
      create-textures
      create-world-viewport
      create-default-font
      create-context
      create-game-config
      create-db
      create-stage-actors
      create-tiled-map
      create-grid
      create-content-grid
      create-explored-tile-corners
      create-raycaster
      create-spawn-player
      create-player-eid
      create-spawn-creatures
      create-dissoc-files))

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
      stage-ctx
      render-validate
      update-mouse-positions
      update-mouseover-eid
      check-debug-viewer
      set-active-entities
      set-camera-position
      clear-screen
      render-draw-tiled-map
      draw-on-world-viewport
      assoc-interaction-state
      set-cursor
      handle-player-input
      dissoc-interaction-state
      assoc-paused
      when-not-paused
      remove-destroyed-entities
      window-camera-controls
      update-draw-stage
      render-validate))

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

(ns clojure.moon-start
  (:require [clojure.active-entities :as active-entities]
            [clojure.actor-set-position :as actor-set-position]
            [clojure.all-raw :refer [all-raw]]
            [clojure.app-schema :refer [schema]]
            [clojure.application :as application]
            [clojure.audio :as audio]
            [clojure.batch :as batch]
            [clojure.bitmap-font :as bitmap-font]
            [clojure.bitmap-font$bitmap-font-data :as bitmap-font-data]
            [clojure.blocks-vision :as blocks-vision?]
            [clojure.body-distance :as distance]
            [clojure.body-draw-rectangle :as draw-rectangle]
            [clojure.build :refer [build]]
            [clojure.button-group :as button-group]
            [clojure.cells :refer [->cells]]
            [clojure.color-setter :refer [tile-color-setter*]]
            [clojure.colors :as colors]
            [clojure.context :as context]
            [clojure.create-cell :refer [->cell]]
            [clojure.creature-tiles]
            [clojure.ctx-batch :as ctx-batch]
            [clojure.ctx-button-just-pressed :refer [button-just-pressed?]]
            [clojure.ctx-db :as ctx-db]
            [clojure.ctx-do :refer [do!]]
            [clojure.ctx-skin :as ctx-skin]
            [clojure.ctx-stage :as ctx-stage]
            [clojure.ctx-textures :as ctx-textures]
            [clojure.data-viewer-window :as data-viewer-window]
            [clojure.debug-flags :as debug-flags]
            [clojure.define-order :as define-order]
            [clojure.disposable :as disposable]
            [clojure.draw :refer [draw!]]
            [clojure.draw-component :refer [draw-component]]
            [clojure.edn :as edn]
            [clojure.entity-state-draw-ui-view :as entity-state-draw-ui-view]
            [clojure.error-window :as error-window]
            [clojure.factions-iterations :refer [factions-iterations]]
            [clojure.files :as files]
            [clojure.fit-viewport :as fit-viewport]
            [clojure.float-bits]
            [clojure.free-type-font-generator :as free-type-font-generator]
            [clojure.free-type-font-generator$free-type-font-parameter :as font-parameter]
            [clojure.gdx-draw-tiled-map :as draw-tiled-map]
            [clojure.generate-font :as generate-font]
            [clojure.get-hitpoints :as get-hitpoints]
            [clojure.get-mana :as get-mana]
            [clojure.get-stage]
            [clojure.get-user-object]
            [clojure.graphics :as graphics]
            [clojure.graphics-shape-drawer :as shape-drawer]
            [clojure.grid-cell :as grid-cell]
            [clojure.grid-update-potential-fields :as update-potential-fields]
            [clojure.grid2d :as g2d]
            [clojure.group :as group]
            [clojure.height :refer [->height]]
            [clojure.horizontal-group :as horizontal-group]
            [clojure.inc-zoom :refer [inc-zoom!]]
            [clojure.info :refer [info-text]]
            [clojure.is-valid-slot :as valid-slot?]
            [clojure.java.io :as io]
            [clojure.k-cursor :refer [k->cursor]]
            [clojure.k-destroy :refer [k->destroy]]
            [clojure.k-handle-input :refer [k->handle-input]]
            [clojure.key-just-pressed :refer [f] :rename {f key-just-pressed?}]
            [clojure.key-pressed :refer [f] :rename {f key-pressed?}]
            [clojure.line-of-sight :as line-of-sight?]
            [clojure.lwjgl3-application :as lwjgl3-application]
            [clojure.map-properties :as map-properties]
            [clojure.max-delta :refer [max-delta]]
            [clojure.minimum-size :refer [minimum-size]]
            [clojure.modules :as modules]
            [clojure.moon-textures :as textures]
            [clojure.mouse-position :refer [mouse-position]]
            [clojure.mouseover-actor :refer [mouseover-actor]]
            [clojure.mouseover-actor-info :refer [mouseover-actor-info]]
            [clojure.movement-property :as movement-property]
            [clojure.new-color]
            [clojure.orthographic-camera :as orthographic-camera]
            [clojure.orthographic-camera-position :as get-position]
            [clojure.orthographic-camera-set-position :as camera-set-position]
            [clojure.pausing :refer [pausing?]]
            [clojure.pixmap :as pixmap]
            [clojure.pixmap$format :as pixmap-format]
            [clojure.player-effect-ctx :as player-effect-ctx]
            [clojure.point-to-entities :refer [point->entities]]
            [clojure.readable :as readable]
            [clojure.raycaster-is-blocked :as blocked?]
            [clojure.remove-actor]
            [clojure.scene2d-actor :as actor]
            [clojure.selected-skill :as selected-skill]
            [clojure.set-ctx :as set-ctx]
            [clojure.set-fill-parent! :as set-fill-parent!]
            [clojure.set-name :as set-name]
            [clojure.set-user-object :as set-user-object]
            [clojure.set-visible]
            [clojure.sort-by-order :as sort-by-order]
            [clojure.spawn-positions :as spawn-positions]
            [clojure.stage :as stage]
            [clojure.state-pause-game :refer [state->pause-game?]]
            [clojure.string :as str]
            [clojure.texture :as texture]
            [clojure.texture-region :as texture-region]
            [clojure.texture-region-drawable :as texture-region-drawable]
            [clojure.texture$texture-filter :as texture-filter]
            [clojure.throwable :as throwable]
            [clojure.tick-component :refer [tick-component]]
            [clojure.tiled-map :as tiled-map]
            [clojure.tmx :as tmx]
            [clojure.tooltip-manager :as tooltip-manager]
            [clojure.uf-caves :as uf-caves]
            [clojure.ui-dev-menu :as dev-menu]
            [clojure.ui-info-window :as info-window]
            [clojure.ui-table :as table]
            [clojure.ui-window :as window]
            [clojure.unproject :as unproject]
            [clojure.update-labels :as update-labels]
            [clojure.usable-state :as usable-state]
            [clojure.validate-humanize :refer [validate-humanize]]
            [clojure.val-max-ratio :as ratio]
            [clojure.visible]
            [clojure.visible-tiles :refer [visible-tiles]]
            [clojure.viewport :as viewport]
            [clojure.width :refer [->width]]
            [clojure.world-unit-scale :refer [world-unit-scale]]
            [clojure.z-orders :refer [z-orders]]
            [clojure.ctx-data :as ctx-data]
            [clojure.gl20 :as gl20]
            [clojure.zoom-speed :refer [zoom-speed]])
  (:gen-class))

(def ^:private controls
  {:zoom-in :input.keys/minus
   :zoom-out :input.keys/equals
   :unpause-once :input.keys/p
   :unpause-continously :input.keys/space
   :close-windows-key :input.keys/escape
   :toggle-inventory :input.keys/i
   :toggle-entity-info :input.keys/e
   :open-debug-button :input.buttons/right})

(def ^:private controls-info
  (str/join "\n"
            ["[W][A][S][D] - Move"
             "[ESCAPE] - Close windows"
             "[I] - Inventory window"
             "[E] - Entity Info window"
             "[-]/[=] - Zoom"
             "[P]/[SPACE] - Unpause"
             "rightclick on tile or entity - open debug data window"
             "Leftmouse click - use skill/drop item on cursor"]))

(def ^:private colors
  (let [outline-alpha 0.4]
    {:colors/mouseover-tile-air (clojure.float-bits/f [1 1 0 0.5])
     :colors/mouseover-tile-none (clojure.float-bits/f [1 0 0 0.5])
     :colors/debug-body-outline-collides (clojure.float-bits/f [1 1 1 1])
     :colors/debug-body-outline (clojure.float-bits/f [0.5 0.5 0.5 1])
     :colors/debug-body-outline-render-error (clojure.float-bits/f [1 0 0 1])
     :colors/debug-cell-entities (clojure.float-bits/f [1 0 0 0.6])
     :colors/debug-cell-occupied (clojure.float-bits/f [0 0 1 0.6])
     :colors/debug-potential-field (fn [ratio]
                                     (clojure.float-bits/f [ratio (- 1 ratio) ratio 0.6]))
     :colors/target-all-line (clojure.float-bits/f [1 0 0 0.75])
     :colors/target-all-render (clojure.float-bits/f [1 0 0 0.5])
     :colors/target-entity-line (clojure.float-bits/f [1 0 0 0.75])
     :colors/target-entity-in-range (clojure.float-bits/f [1 0 0 0.5])
     :colors/target-entity-not-in-range (clojure.float-bits/f [1 1 0 0.5])
     :colors/enemy-color (clojure.float-bits/f [1 0 0 outline-alpha])
     :colors/friendly-color (clojure.float-bits/f [0 1 0 outline-alpha])
     :colors/neutral-color (clojure.float-bits/f [1 1 1 outline-alpha])
     :colors/hp-bar (fn [ratio]
                      (let [ratio (float ratio)
                            color (cond
                                    (> ratio 0.75) :green
                                    (> ratio 0.5) :darkgreen
                                    (> ratio 0.25) :yellow
                                    :else :red)]
                        (color {:green (clojure.float-bits/f [0 0.8 0 1])
                                :darkgreen (clojure.float-bits/f [0 0.5 0 1])
                                :yellow (clojure.float-bits/f [0.5 0.5 0 1])
                                :red (clojure.float-bits/f [0.5 0 0 1])})))
     :colors/hp-bar-rect (clojure.float-bits/f [0 0 0 1])
     :colors/temp-modifier (clojure.float-bits/f [0.5 0.5 0.5 0.4])
     :colors/active-skill-circle (clojure.float-bits/f [1 1 1 0.125])
     :colors/active-skill-sector (clojure.float-bits/f [1 1 1 0.5])
     :colors/stunned (clojure.float-bits/f [1 1 1 0.6])
     :colors/explored-tile (clojure.float-bits/f [0.5 0.5 0.5 1])
     :colors/visible-tile (clojure.float-bits/f [1 1 1 1])
     :colors/invisible-tile (clojure.float-bits/f [0 0 0 1])
     :colors/droppable-item (clojure.float-bits/f [0 0.6 0 0.8 1])
     :colors/not-allowed-drop-item (clojure.float-bits/f [0.6 0 0 0.8 1])
     :colors/item-rect (clojure.float-bits/f [0.5 0.5 0.5 1])}))

(def ^:private render-z-order
  (define-order/f z-orders))

(def ^:private max-speed
  (/ minimum-size max-delta))

(def ^:private entity-render-layers
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

(defn- action-bar-create [_ctx]
  (doto (table/create
         {:table/cell-defaults {:pad 2}
          :table/rows [[{:actor (doto (horizontal-group/new)
                                  (horizontal-group/space! 2)
                                  (horizontal-group/pad! 2)
                                  (set-name/f "moon.ui.action-bar.horizontal-group")
                                  (set-user-object/f (doto (button-group/new)
                                                       (button-group/set-max-check-count! 1)
                                                       (button-group/set-min-check-count! 0))))
                         :expand? true
                         :bottom? true}]]})
    (set-fill-parent!/f true)
    (set-name/f "moon.ui.action-bar")))

(defn- stage-dev-menu-create
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}]
  (dev-menu/create
   {:menus [ctx-data/item
            debug-flags/item
            {:label "Help"
             :items [{:label controls-info}]}
            {:label "Select World"
             :items (for [[label world-fn] [["Vampire" tmx/vampire]
                                            ["UF Caves" uf-caves/create]
                                            ["Modules" modules/create]]]
                      {:label (str "Start " label)
                       :on-click (fn [ctx]
                                   ctx)})}]
    :update-labels (for [item update-labels/v]
                     (if (:icon item)
                       (update item :icon #(get textures %))
                       item))
    :skin skin}))

(defn- hp-mana-bar-create
  [{:keys [ctx/textures
           ctx/stage]}]
  (let [{:keys [rahmen-file
                rahmenw
                rahmenh
                hpcontent-file
                manacontent-file
                y-mana]} {:rahmen-file "images/rahmen.png"
                          :rahmenw 150
                          :rahmenh 26
                          :hpcontent-file "images/hp.png"
                          :manacontent-file "images/mana.png"
                          :y-mana 80}
        [x y-mana] [(/ (:viewport/world-width (:stage/viewport stage)) 2)
                    y-mana]
        rahmen-tex-reg (textures/texture-region textures {:image/file rahmen-file})
        y-hp (+ y-mana rahmenh)
        render-hpmana-bar (fn [x y content-file minmaxval name]
                            [[:draw/texture-region rahmen-tex-reg [x y]]
                             [:draw/texture-region
                              (textures/texture-region textures
                                                       {:image/file content-file
                                                        :image/bounds [0 0 (* rahmenw (ratio/f minmaxval)) rahmenh]})
                              [x y]]
                             [:draw/text {:text (str (readable/f (minmaxval 0))
                                                     "/"
                                                     (minmaxval 1)
                                                     " "
                                                     name)
                                          :x (+ x 75)
                                          :y (+ y 2)
                                          :up? true}]])
        create-draws (fn [{:keys [ctx/player-eid]}]
                       (let [stats (:entity/stats @player-eid)
                             x (- x (/ rahmenw 2))]
                         (concat
                          (render-hpmana-bar x y-hp hpcontent-file (get-hitpoints/f stats) "HP")
                          (render-hpmana-bar x y-mana manacontent-file (get-mana/f stats) "MP"))))]
    (actor/f
     {:draw! (fn [this _batch _parent-alpha]
               (when-let [stage (clojure.get-stage/f this)]
                 (draw! (:stage/ctx stage)
                        (create-draws (:stage/ctx stage)))))})))

(defn- windows-create [ctx actor-fns]
  (let [group* (group/new)]
    (run! #(group/add-actor! group* %) (for [f actor-fns] (f ctx)))
    (doto group*
      (set-name/f "moon.ui.windows"))))

(defn- stage-info-window-create
  [{:keys [ctx/skin
           ctx/stage]}]
  (info-window/create
   {:title "Entity Info"
    :actor-name "moon.ui.windows.entity-info"
    :visible? false
    :position [(:viewport/world-width (:stage/viewport stage)) 0]
    :set-label-text! (fn [{:keys [ctx/mouseover-eid]
                           :as ctx}]
                       (if-let [eid mouseover-eid]
                         (info-text (apply dissoc @eid [:entity/skills
                                                        :entity/faction
                                                        :active-skill])
                                    ctx)
                         ""))
    :skin skin}))

(defn- inventory-window-build
  [{:keys [do!
           draw!
           item-rect-color
           droppable-item-color
           not-allowed-drop-item-color
           skin
           position
           slot->texture-region
           cell-size]}]
  (let [slot->drawable (fn [slot]
                         (doto (texture-region-drawable/new (slot->texture-region slot))
                           (texture-region-drawable/set-min-size! cell-size cell-size)
                           (texture-region-drawable/tint! (clojure.new-color/f [1 1 1 0.4]))))
        draw-cell-rect (fn [player-entity x y mouseover? cell]
                         [[:draw/rectangle x y cell-size cell-size item-rect-color]
                          (when (and mouseover?
                                     (= :player-item-on-cursor (:state (:entity/fsm player-entity))))
                            (let [item (:entity/item-on-cursor player-entity)
                                  color (if (valid-slot?/f cell item)
                                          droppable-item-color
                                          not-allowed-drop-item-color)]
                              [:draw/filled-rectangle (inc x) (inc y) (- cell-size 2) (- cell-size 2) color]))])
        ->cell (partial ->cell do! draw! slot->drawable draw-cell-rect cell-size)]
    (doto (window/create
           {:title "Inventory"
            :skin skin
            :table/rows [[{:actor (doto (table/create
                                         {:table/rows (concat [[nil nil
                                                                (->cell :inventory.slot/helm)
                                                                (->cell :inventory.slot/necklace)]
                                                               [nil
                                                                (->cell :inventory.slot/weapon)
                                                                (->cell :inventory.slot/chest)
                                                                (->cell :inventory.slot/cloak)
                                                                (->cell :inventory.slot/shield)]
                                                               [nil nil
                                                                (->cell :inventory.slot/leg)]
                                                               [nil
                                                                (->cell :inventory.slot/glove)
                                                                (->cell :inventory.slot/rings :position [0 0])
                                                                (->cell :inventory.slot/rings :position [1 0])
                                                                (->cell :inventory.slot/boot)]]
                                                              (for [y (range 4)]
                                                                (for [x (range 6)]
                                                                  (->cell :inventory.slot/bag :position [x y]))))})
                                    (set-name/f "inventory-cell-table"))
                           :pad 4}]]})
      (set-name/f "moon.ui.windows.inventory")
      (clojure.set-visible/f false)
      (actor-set-position/set-position! position))))

(defn- inventory-window-create
  [{:keys [ctx/colors
           ctx/skin
           ctx/stage
           ctx/textures]}]
  (let [slot->y-sprite-idx #:inventory.slot {:weapon 0
                                             :shield 1
                                             :rings 2
                                             :necklace 3
                                             :helm 4
                                             :cloak 5
                                             :chest 6
                                             :leg 7
                                             :glove 8
                                             :boot 9
                                             :bag 10}
        slot->texture-region (fn [slot]
                               (let [width 48
                                     height 48
                                     sprite-x 21
                                     sprite-y (+ (slot->y-sprite-idx slot) 2)
                                     bounds [(* sprite-x width)
                                             (* sprite-y height)
                                             width
                                             height]]
                                 (textures/texture-region textures
                                                          {:image/file "images/items.png"
                                                           :image/bounds bounds})))]
    (inventory-window-build
     {:do! do!
      :draw! draw!
      :item-rect-color (:colors/item-rect colors)
      :droppable-item-color (:colors/droppable-item colors)
      :not-allowed-drop-item-color (:colors/not-allowed-drop-item colors)
      :skin skin
      :position [(:viewport/world-width (:stage/viewport stage))
                 (:viewport/world-height (:stage/viewport stage))]
      :slot->texture-region slot->texture-region
      :cell-size 48})))

(defn- player-state-draw-create [_ctx]
  (actor/f
   {:draw! (fn [this _batch _parent-alpha]
             (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (clojure.get-stage/f this))
                   entity @player-eid
                   state-k (:state (:entity/fsm entity))]
               (draw! ctx (entity-state-draw-ui-view/f [state-k (state-k entity)] player-eid ctx))))}))

(defn- player-message-actor-create [_ctx]
  (let [message-duration-seconds 0.5]
    (doto (actor/f
           {:draw! (fn [this _batch _parent-alpha]
                     (when-let [stage (clojure.get-stage/f this)]
                       (draw! (:stage/ctx stage)
                              [(let [state (clojure.get-user-object/f this)
                                     vp-width (:viewport/world-width (:stage/viewport stage))
                                     vp-height (:viewport/world-height (:stage/viewport stage))]
                                 (when-let [text (:text @state)]
                                   [:draw/text {:x (/ vp-width 2)
                                                :y (+ (/ vp-height 2) 200)
                                                :text text
                                                :scale 2.5
                                                :up? true}]))])))
            :act! (fn [this delta]
                    (let [state (clojure.get-user-object/f this)]
                      (when (:text @state)
                        (swap! state update :counter + delta)
                        (when (>= (:counter @state) message-duration-seconds)
                          (reset! state nil)))))})
      (set-name/f "player-message")
      (set-user-object/f (atom nil)))))

(defn- create [^com.badlogic.gdx.Application app]
  (let [ctx {:ctx/audio (.getAudio app)
             :ctx/files (.getFiles app)
             :ctx/graphics (.getGraphics app)
             :ctx/input (.getInput app)
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
        ctx (assoc ctx
                   :ctx/audio (into {}
                                    (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
                                          :let [path (format "sounds/%s.wav" sound-name)]]
                                      [sound-name
                                       (audio/new-sound (:ctx/audio ctx)
                                                        (files/internal (:ctx/files ctx) path))])))
        ctx (let [pixmap (doto (pixmap/new 1 1 pixmap-format/rgba8888)
                         (pixmap/set-color! 1 1 1 1)
                         (pixmap/draw-pixel! 0 0))
                  texture* (texture/new-from-pixmap pixmap)]
              (disposable/dispose! pixmap)
              (assoc ctx :ctx/shape-drawer-texture texture*))
        ctx (assoc ctx
                   :ctx/shape-drawer (shape-drawer/new (:ctx/batch ctx)
                                                       (texture-region/new (:ctx/shape-drawer-texture ctx) 1 0 1 1)))
        ctx (assoc ctx :ctx/skin (ctx-skin/step ctx))
        ctx (assoc ctx :ctx/stage (ctx-stage/step ctx))
        _ (tooltip-manager/set-initial-time! (tooltip-manager/get-instance) 0)
        _ (colors/put! "PRETTY_NAME" (clojure.new-color/f [0.84 0.8 0.52 1]))
        ctx (assoc ctx
                   :ctx/cursors (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
                                  (update-vals data
                                               (fn [[path-segment [hotspot-x hotspot-y]]]
                                                 (let [path (format path-format path-segment)
                                                       pixmap* (pixmap/new (files/internal (:ctx/files ctx) path))
                                                       cursor (graphics/new-cursor (:ctx/graphics ctx) pixmap* hotspot-x hotspot-y)]
                                                   (disposable/dispose! pixmap*)
                                                   cursor)))))
        ctx (assoc ctx :ctx/textures (ctx-textures/step ctx {:folder "resources/"
                                                             :extensions #{"png" "bmp"}}))
        ctx (assoc ctx
                   :ctx/world-viewport (let [world-width (* 1440 world-unit-scale)
                                             world-height (* 900 world-unit-scale)]
                                         (fit-viewport/create world-width
                                                              world-height
                                                              (doto (orthographic-camera/new)
                                                                (orthographic-camera/set-to-ortho! false world-width world-height)))))
        ctx (assoc ctx
                   :ctx/default-font (let [{:keys [path
                                                  size
                                                  quality-scaling
                                                  use-integer-positions?]} {:path "fonts/films.EXL_____.ttf"
                                                                            :size 16
                                                                            :quality-scaling 2
                                                                            :use-integer-positions? false}
                                           generator (free-type-font-generator/f (files/internal (:ctx/files ctx) path))
                                           parameter (doto (font-parameter/new)
                                                       (font-parameter/set-size! (* size quality-scaling))
                                                       (font-parameter/set-min-filter! texture-filter/linear)
                                                       (font-parameter/set-mag-filter! texture-filter/linear))
                                           font (generate-font/f generator parameter)
                                           font-data (bitmap-font/get-data font)]
                                       (disposable/dispose! generator)
                                       (bitmap-font-data/set-scale! font-data (/ quality-scaling))
                                       (bitmap-font-data/set-markup-enabled! font-data true)
                                       (bitmap-font/set-use-integer-positions! font use-integer-positions?)
                                       font))
        ctx (merge (context/map->R {}) ctx)
        ctx (assoc ctx :ctx/controls controls :ctx/controls-info controls-info)
        ctx (assoc ctx :ctx/colors colors)
        ctx (assoc ctx :ctx/render-z-order render-z-order)
        ctx (assoc ctx :ctx/max-speed max-speed)
        ctx (assoc ctx :ctx/db (ctx-db/step ctx))
        _ (doseq [[f & params] [[action-bar-create]
                                [stage-dev-menu-create]
                                [hp-mana-bar-create]
                                [windows-create [stage-info-window-create
                                                 inventory-window-create]]
                                [player-state-draw-create]
                                [player-message-actor-create]]]
             (stage/add-actor! (:ctx/stage ctx) (apply f ctx params)))
        ctx (let [{:keys [tiled-map
                          start-position]} (tmx/vampire
                                            {:level/creature-properties (clojure.creature-tiles/prepare
                                                                        (all-raw (:ctx/db ctx) :properties/creatures)
                                                                        #(textures/texture-region (:ctx/textures ctx) %))
                                             :textures (:ctx/textures ctx)})]
              (assoc ctx
                     :ctx/tiled-map tiled-map
                     :ctx/start-position start-position))
        ctx (assoc ctx
                   :ctx/grid (g2d/create-grid (map-properties/get (tiled-map/get-properties (:ctx/tiled-map ctx)) "width")
                                              (map-properties/get (tiled-map/get-properties (:ctx/tiled-map ctx)) "height")
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
                                                   :occupied #{}})))))
        ctx (let [width (map-properties/get (tiled-map/get-properties (:ctx/tiled-map ctx)) "width")
                  height (map-properties/get (tiled-map/get-properties (:ctx/tiled-map ctx)) "height")
                  cell-size 16]
              (assoc ctx
                     :ctx/content-grid {:grid (g2d/create-grid
                                               (inc (int (/ width cell-size)))
                                               (inc (int (/ height cell-size)))
                                               (fn [idx]
                                                 (atom {:idx idx
                                                        :entities #{}})))
                                       :cell-w cell-size
                                       :cell-h cell-size}))
        ctx (assoc ctx
                   :ctx/explored-tile-corners (atom (g2d/create-grid (map-properties/get (tiled-map/get-properties (:ctx/tiled-map ctx)) "width")
                                                                       (map-properties/get (tiled-map/get-properties (:ctx/tiled-map ctx)) "height")
                                                                       (constantly false))))
        ctx (let [grid (:ctx/grid ctx)
                  width (->width grid)
                  height (->height grid)
                  cells (for [cell (map deref (->cells grid))]
                          [(:position cell)
                           (boolean (blocks-vision?/f cell))])
                  arr (make-array Boolean/TYPE width height)]
              (doseq [[[x y] blocked?] cells]
                (aset arr x y (boolean blocked?)))
              (assoc ctx :ctx/raycaster [arr width height]))
        _ (do! ctx
               [[:tx/spawn-creature {:position (mapv (partial + 0.5) (:ctx/start-position ctx))
                                       :creature-property (build (:ctx/db ctx) :creatures/vampire)
                                       :components {:entity/fsm {:fsm :fsms/player
                                                               :initial-state :player-idle}
                                                    :entity/faction :good
                                                    :entity/player? true
                                                    :entity/free-skill-points 3
                                                    :entity/clickable {:type :clickable/player}
                                                    :entity/click-distance-tiles 1.5}}]])
        ctx (let [eid (get @(:ctx/entity-ids ctx) 1)]
              (assert (:entity/player? @eid))
              (assoc ctx :ctx/player-eid eid))
        _ (do! ctx
               (for [[position creature-id] (spawn-positions/f (:ctx/tiled-map ctx))]
                 [:tx/spawn-creature {:position (mapv (partial + 0.5) position)
                                      :creature-property (build (:ctx/db ctx) (keyword creature-id))
                                      :components {:entity/fsm {:fsm :fsms/npc
                                                                :initial-state :npc-sleeping}
                                                   :entity/faction :evil}}]))]
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

(defn- render-validate-step [ctx]
  (validate-humanize schema ctx)
  ctx)

(defn- update-mouse-positions-step
  [{:keys [ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (let [mp (mouse-position ctx)]
    (-> ctx
        (assoc :ctx/world-mouse-position (unproject/f world-viewport mp))
        (assoc :ctx/ui-mouse-position (-> stage :stage/viewport (unproject/f mp))))))

(defn- update-mouseover-eid-step
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

(defn- check-debug-viewer-step
  [{:keys [ctx/controls
           ctx/mouseover-eid
           ctx/skin
           ctx/stage
           ctx/grid
           ctx/world-mouse-position]
    :as ctx}]
  (when (button-just-pressed? ctx (:open-debug-button controls))
    (let [data (or (and mouseover-eid @mouseover-eid)
                   @(grid (mapv int world-mouse-position)))]
      (stage/add-actor! stage
                        (data-viewer-window/create
                         {:title "Data View"
                          :data data
                          :width 500
                          :height 500
                          :skin skin}))))
  ctx)

(defn- set-active-entities-step
  [{:keys [ctx/player-eid
           ctx/content-grid]
    :as ctx}]
  (assoc ctx :ctx/active-entities
         (active-entities/f content-grid @player-eid)))

(defn- set-camera-position-step
  [{:keys [ctx/player-eid
           ctx/world-viewport]
    :as ctx}]
  (camera-set-position/set-position! (:viewport/camera world-viewport)
                                     (:body/position (:entity/body @player-eid)))
  ctx)

(defn- clear-screen-step
  [{:keys [ctx/graphics] :as ctx}]
  (let [gl (graphics/get-gl20 graphics)]
    (gl20/clear-color! gl 0 0 0 0)
    (gl20/clear! gl gl20/color-buffer-bit))
  ctx)

(defn- render-draw-tiled-map-step
  [{:keys [ctx/batch
           ctx/colors
           ctx/explored-tile-corners
           ctx/raycaster
           ctx/tiled-map
           ctx/world-viewport]
    :as ctx}]
  (draw-tiled-map/f! batch
                     world-unit-scale
                     (:viewport/camera world-viewport)
                     tiled-map
                     (tile-color-setter*
                      {:ray-blocked? (partial blocked?/f raycaster)
                       :explored-tile-corners explored-tile-corners
                       :light-position (get-position/f (:viewport/camera world-viewport))
                       :see-all-tiles? false
                       :explored-tile-color (:colors/explored-tile colors)
                       :visible-tile-color (:colors/visible-tile colors)
                       :invisible-tile-color (:colors/invisible-tile colors)}))
  ctx)

(defn- draw-cell-debug
  [{:keys [ctx/colors
           ctx/grid
           ctx/world-viewport
           ctx/show-potential-field-colors?
           ctx/show-cell-entities?
           ctx/show-cell-occupied?]}]
  (apply concat
         (for [[x y] (visible-tiles (:viewport/camera world-viewport))
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
    :as ctx}
   render-layers]
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

(defn- draw-on-world-viewport-step
  [{:keys [ctx/batch
           ctx/shape-drawer
           ctx/unit-scale
           ctx/world-viewport]
    :as ctx}
   draw-fns]
  (batch/set-color! batch 1 1 1 1)
  (batch/set-projection-matrix! batch (orthographic-camera/combined (:viewport/camera world-viewport)))
  (batch/begin! batch)
  (let [old-line-width (shape-drawer/get-default-line-width shape-drawer)]
    (shape-drawer/set-default-line-width! shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [[f & params] draw-fns]
      (draw! ctx (apply f ctx params)))
    (reset! unit-scale 1)
    (shape-drawer/set-default-line-width! shape-drawer old-line-width))
  (batch/end! batch)
  ctx)

(defn- assoc-interaction-state-create
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
                            (#(group/find-actor % "moon.ui.action-bar"))
                            selected-skill/f)]
        (let [entity @player-eid
              skill (skill-id (:entity/skills entity))
              effect-ctx (player-effect-ctx/f mouseover-eid world-mouse-position player-eid)
              state (usable-state/f skill entity effect-ctx)]
          (if (= state :usable)
            [:interaction-state.skill/usable [skill effect-ctx]]
            [:interaction-state.skill/not-usable state]))
        [:interaction-state/no-skill-selected]))))

(defn- set-cursor-step
  [{:keys [ctx/graphics
           ctx/cursors
           ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        f (k->cursor state-k)
        cursor-key (if (keyword? f)
                     f
                     (f eid ctx))]
    (assert (contains? cursors cursor-key))
    (graphics/set-cursor! graphics (get cursors cursor-key)))
  ctx)

(defn- handle-player-input-step
  [{:keys [ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (if-let [f (k->handle-input state-k)]
              (f eid ctx)
              nil)]
    (do! ctx txs))
  ctx)

(defn- assoc-paused-step
  [{:keys [ctx/input
           ctx/controls
           ctx/player-eid]
    :as ctx}]
  (assoc ctx :ctx/paused?
         (or #_error
             (and pausing?
                  (state->pause-game? (:state (:entity/fsm @player-eid)))
                  (not (or (key-just-pressed? input (:unpause-once controls))
                           (key-pressed? input (:unpause-continously controls))))))))

(defn- update-time-step
  [{:keys [ctx/graphics]
    :as ctx}]
  (let [delta-ms (min (graphics/get-delta-time graphics) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))

(defn- if-not-paused-update-potential-fields-step
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

(defn- tick-entities-step
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
      (stage/add-actor! stage
                        (error-window/create
                         {:skin skin
                          :throwable t}))))
  ctx)

(defn- remove-destroyed-entities-step
  [ctx]
  (do! ctx
       (mapcat
        (fn [eid]
          (cons
           [:tx/unregister-eid eid]
           (mapcat (fn [[k v]]
                     (if-let [f (k->destroy k)]
                       (f v eid)
                       nil))
                   @eid)))
        (filter (comp :entity/destroyed? deref)
                (vals @(:ctx/entity-ids ctx)))))
  ctx)

(defn- window-camera-controls-step
  [{:keys [ctx/input
           ctx/controls
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (when (key-pressed? input (:zoom-in controls))
    (inc-zoom! (:viewport/camera world-viewport) zoom-speed))

  (when (key-pressed? input (:zoom-out controls))
    (inc-zoom! (:viewport/camera world-viewport) (- zoom-speed)))

  (when (key-just-pressed? input (:close-windows-key controls))
    (->> (group/find-actor (:stage/root stage) "moon.ui.windows")
         group/get-children
         (run! #(clojure.set-visible/f % false))))

  (when (key-just-pressed? input (:toggle-inventory controls))
    (let [inventory (group/find-actor (:stage/root stage) "moon.ui.windows.inventory")]
      (clojure.set-visible/f inventory (not (clojure.visible/f inventory)))))

  (when (key-just-pressed? input (:toggle-entity-info controls))
    (let [entity-info (group/find-actor (:stage/root stage) "moon.ui.windows.entity-info")]
      (clojure.set-visible/f entity-info (not (clojure.visible/f entity-info)))))
  ctx)

(defn- update-draw-stage-step
  [{:keys [ctx/stage] :as ctx}]
  (set-ctx/f stage ctx)
  (stage/act! stage)
  (stage/draw! stage)
  (:stage/ctx stage))

(defn- render-app!
  [{:keys [ctx/stage]
    :as ctx}]
  (let [ctx (or (:stage/ctx stage) ctx)
        ctx (render-validate-step ctx)
        ctx (update-mouse-positions-step ctx)
        ctx (update-mouseover-eid-step ctx)
        ctx (check-debug-viewer-step ctx)
        ctx (set-active-entities-step ctx)
        ctx (set-camera-position-step ctx)
        ctx (clear-screen-step ctx)
        ctx (render-draw-tiled-map-step ctx)
        ctx (draw-on-world-viewport-step ctx
                                         [[draw-cell-debug]
                                          [draw-entities! entity-render-layers]
                                          [highlight-mouseover-tile]])
        ctx (assoc ctx :ctx/interaction-state (assoc-interaction-state-create ctx))
        ctx (set-cursor-step ctx)
        ctx (handle-player-input-step ctx)
        ctx (dissoc ctx :ctx/interaction-state)
        ctx (assoc-paused-step ctx)
        ctx (if (:ctx/paused? ctx)
              ctx
              (reduce (fn [ctx f] (f ctx))
                      ctx
                      [update-time-step
                       if-not-paused-update-potential-fields-step
                       tick-entities-step]))
        ctx (remove-destroyed-entities-step ctx)
        ctx (window-camera-controls-step ctx)
        ctx (update-draw-stage-step ctx)]
    (render-validate-step ctx)))

(defn- resize-app!
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (viewport/update! (:stage/viewport stage) width height true)
  (viewport/update! world-viewport width height false))

(defn -main []
  (lwjgl3-application/f
   {:create! (fn [app]
               (reset! application/state (create app)))
    :dispose! (fn []
                (dispose-app! @application/state))
    :render! (fn []
               (swap! application/state render-app!))
    :resize! (fn [width height]
               (resize-app! @application/state width height))
    :pause! (fn [])
    :resume! (fn [])}
   {:title "Moon"
    :windowed-mode {:width 1440 :height 900}
    :foreground-fps 60}))

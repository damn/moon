(ns game
  (:require [audio.create :refer [create-audio!]]
            [audio.dispose :as audio.dispose]
            [audio.play :refer [play-sound!]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [effect.applicable :refer [applicable?]]
            [effect.handle :refer [handle-effect]]
            [entity.spawn :refer [spawn-entity!]]
            [files.create :refer [create-sound-file-handles!]]
            [game.audio :refer [audiovisual!]]
            [game.colors :refer [colors]]
            [game.controls :refer [controls]]
            [game.draw :refer [draw-fn-filled-rectangle draw-fn-rectangle]]
            [game.entity :refer [set-item]]
            [game.fsm :refer [handle-fsm-event!]]
            [game.listener.create.ui-actors.action-bar :refer [create-action-bar]]
            [game.listener.create.ui-actors.dev-menu :refer [create-dev-menu dev-menus]]
            [game.listener.create.ui-actors.entity-info :refer [stage-info-window-create]]
            [game.listener.create.ui-actors.hp-mana-bar :refer [hp-mana-bar-create]]
            [game.listener.create.ui-actors.inventory-window :refer [handle-clicked-inventory-cell
                                                                     inventory-window-create]]
            [game.listener.create.ui-actors.player-message :refer [player-message-actor-create]]
            [game.listener.create.ui-actors.player-state-draw :refer [player-state-draw-create]]
            [game.listener.create.ui-actors.windows :refer [windows-create]]
            [game.listener.render.draw-world.draw-cell-debug :refer [draw-cell-debug]]
            [game.listener.render.draw-world.draw-entities :refer [draw-entities!]]
            [game.listener.render.draw-world.draw-tile-grid :refer [draw-tile-grid]]
            [game.listener.render.draw-world.highlight-mouseover-tile :refer [highlight-mouseover-tile]]
            [game.listener.render.tick-world.tick-component :refer [tick-component]]
            [game.mouse :refer [mouseover-actor mouseover-actor-info ui-mouse-position world-mouse-position]]
            [game.spawn :refer [spawn-creature]]
            [game.tile-color :refer [tile-color-setter*]]
            [game.ui :refer [sync-player-ui! toggle-inventory-visible! ui-remove-item! ui-set-item!]]
            [moon.coll :as coll]
            [moon.content-grid :as content-grid]
            [moon.db :as db]
            [moon.grid :as grid]
            [moon.inventory :as inventory]
            [moon.item :as item]
            [moon.level.uf-caves :as uf-caves]
            [moon.number :as number]
            [moon.raycaster :as raycaster]
            [moon.stats :as stats]
            [moon.textures :as textures]
            [moon.tiled-map :as moon-tiled-map]
            [moon.tiled-map.draw :as tiled-map.draw]
            [moon.v2 :as v2]
            [moon.world :as world]
            [potential-field.update :as potential-field.update]
            [skill.usable-state :refer [usable-state]]
            [texture.white-pixel :refer [white-pixel-texture]]
            [ui.action-bar :as action-bar]
            [world.unregister-eid :refer [unregister-eid!]])
  (:import (com.badlogic.gdx ApplicationListener Files Gdx Graphics Input Input$Buttons Input$Keys InputProcessor)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Color Colors Cursor OrthographicCamera Pixmap Texture Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d Batch BitmapFont BitmapFont$BitmapFontData SpriteBatch TextureRegion)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.scenes.scene2d Actor Group Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Skin TooltipManager)
           (com.badlogic.gdx.utils Disposable ScreenUtils)
           (com.badlogic.gdx.utils.viewport FitViewport Viewport)
           (space.earlygrey.shapedrawer ShapeDrawer))
  (:gen-class))

(defn create-shape-drawer! [batch texture]
  (ShapeDrawer. batch
                (TextureRegion. ^Texture texture (int 1) (int 0) (int 1) (int 1))))

(defn create-skin! [^FileHandle file-handle]
  (let [s (Skin. file-handle)]
    (set! (.markupEnabled ^BitmapFont$BitmapFontData
                          (.getData (.getFont ^Skin s "default-font")))
          true)
    s))

(defn create-cursors! [files]
  (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
    (update-vals data
                 (fn [[path-segment [hotspot-x hotspot-y]]]
                   (let [path (format path-format path-segment)
                         pixmap* (Pixmap. ^FileHandle (.internal ^Files files path))
                         cursor (.newCursor ^Graphics Gdx/graphics ^Pixmap pixmap* hotspot-x hotspot-y)]
                     (Disposable/.dispose pixmap*)
                     cursor)))))

(defn create-world-viewport! [world-unit-scale]
  (let [world-width (* 1440 world-unit-scale)
        world-height (* 900 world-unit-scale)]
    (FitViewport. (float world-width)
                  (float world-height)
                  (doto (OrthographicCamera.)
                    (.setToOrtho false world-width world-height)))))

(defn create-default-font! [files {:keys [path
                                          size
                                          quality-scaling
                                          use-integer-positions?]}]
  (let [generator (FreeTypeFontGenerator. ^FileHandle (.internal ^Files files path))
        parameter (let [p (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                    (set! (.size p) (* size quality-scaling))
                    (set! (.minFilter p) Texture$TextureFilter/Linear)
                    (set! (.magFilter p) Texture$TextureFilter/Linear)
                    p)
        font (.generateFont ^FreeTypeFontGenerator generator
                            ^FreeTypeFontGenerator$FreeTypeFontParameter parameter)
        font-data (.getData ^BitmapFont font)]
    (Disposable/.dispose generator)
    (.setScale ^BitmapFont$BitmapFontData font-data (/ quality-scaling))
    (set! (.markupEnabled ^BitmapFont$BitmapFontData font-data) true)
    (.setUseIntegerPositions ^BitmapFont font use-integer-positions?)
    font))

(defn create-level! [level-fn db* textures*]
  (let [{level-tiled-map :tiled-map
         level-start :start-position}
        (level-fn {:level/creature-properties (moon-tiled-map/prepare-creature-tiles
                                               (db/all-raw db* :properties/creatures)
                                               #(textures/texture-region textures* %))
                   :textures textures*})]
    {:tiled-map level-tiled-map
     :start-position level-start}))

(defn spawn-player! [db* schemas world elapsed-time start-position skin stage textures* z-orders minimum-size]
  (let [eid (spawn-entity! world elapsed-time z-orders minimum-size
                           (spawn-creature {:position (mapv (partial + 0.5) @start-position)
                                            :creature-property (db/build db* schemas :creatures/vampire)
                                            :components {:entity/fsm {:fsm :fsms/player
                                                                      :initial-state :player-idle}
                                                         :entity/faction :good
                                                         :entity/player? true
                                                         :entity/free-skill-points 3
                                                         :entity/clickable {:type :clickable/player}
                                                         :entity/click-distance-tiles 1.5}}))]
    (sync-player-ui! skin stage textures* @elapsed-time eid)))

(defn bind-player-eid! [world player-eid]
  (let [eid (get @(:world/entity-ids @world) 1)]
    (assert (:entity/player? @eid))
    (reset! player-eid eid)))

(defn spawn-map-creatures! [db* schemas world elapsed-time start-position tiled-map z-orders minimum-size]
  (let [sp @start-position]
    (doseq [[position creature-id] (moon-tiled-map/spawn-positions @tiled-map)
            :when (not= position sp)]
      (spawn-entity! world elapsed-time z-orders minimum-size
                     (spawn-creature {:position (mapv (partial + 0.5) position)
                                      :creature-property (db/build db* schemas (keyword creature-id))
                                      :components {:entity/fsm {:fsm :fsms/npc
                                                                :initial-state :npc-sleeping}
                                                   :entity/faction :evil}})))))

(defn create-ui-actors
  [world elapsed-time mouseover-eid paused? player-eid
   show-tile-grid? show-cell-entities? show-cell-occupied? show-body-bounds? show-potential-field-colors?
   unit-scale
   z-orders
   minimum-size
   audio default-font shape-drawer skin stage textures* world-viewport]
  (let [cell-size 48]
    [(create-action-bar)
     (create-dev-menu
      {:menus (dev-menus show-tile-grid? show-cell-entities? show-cell-occupied? show-body-bounds? show-potential-field-colors?)
       :update-labels (for [item [{:label "elapsed-time"
                                   :update-fn (fn []
                                                (str (number/readable @elapsed-time) " seconds"))
                                   :icon "images/clock.png"}
                                  {:label "FPS"
                                   :update-fn (fn [] (.getFramesPerSecond ^Graphics Gdx/graphics))
                                   :icon "images/fps.png"}
                                  {:label "Mouseover-entity id"
                                   :update-fn (fn []
                                                (when-let [entity (and @mouseover-eid @@mouseover-eid)]
                                                  (:entity/id entity)))
                                   :icon "images/mouseover.png"}
                                  {:label "paused?"
                                   :update-fn (fn [] @paused?)}
                                  {:label "GUI"
                                   :update-fn (fn []
                                                (mapv int (ui-mouse-position stage)))}
                                  {:label "World"
                                   :update-fn (fn []
                                                (mapv int (world-mouse-position world-viewport)))}
                                  {:label "Zoom"
                                   :update-fn (fn []
                                                (.zoom ^OrthographicCamera (.getCamera ^Viewport world-viewport)))
                                   :icon "images/zoom.png"}]]
                        (if (:icon item)
                          (update item :icon #(get textures* %))
                          item))
       :skin skin})
     (hp-mana-bar-create default-font stage textures* unit-scale player-eid)
     (windows-create [#(stage-info-window-create skin stage mouseover-eid elapsed-time)
                      #(inventory-window-create
                        (fn [_event cell]
                          (handle-clicked-inventory-cell world
                                                         elapsed-time
                                                         @player-eid
                                                         audio
                                                         skin
                                                         stage
                                                         textures*
                                                         z-orders
                                                         minimum-size
                                                         (fn [cell item] (ui-set-item! skin stage textures* cell item))
                                                         (fn [cell] (ui-remove-item! stage cell))
                                                         cell
                                                         (world-mouse-position world-viewport)))
                        (fn [player-entity x y mouseover? cell]
                          (draw-fn-rectangle shape-drawer x y cell-size cell-size (:colors/item-rect colors))
                          (when (and mouseover?
                                     (= :player-item-on-cursor (:state (:entity/fsm player-entity))))
                            (let [item (:entity/item-on-cursor player-entity)
                                  color (if (inventory/valid-slot? cell item)
                                          (:colors/droppable-item colors)
                                          (:colors/not-allowed-drop-item colors))]
                              (draw-fn-filled-rectangle shape-drawer (inc x) (inc y) (- cell-size 2) (- cell-size 2) color))))
                        skin
                        stage
                        textures*
                        player-eid)])
     (player-state-draw-create unit-scale textures* player-eid)
     (player-message-actor-create default-font unit-scale)]))

(defn update-mouseover-eid! [world player-eid mouseover-eid stage world-viewport render-z-order]
  (let [old-mouseover-eid @mouseover-eid
        [x y] (ui-mouse-position stage)
        new-eid (if (mouseover-actor stage x y)
                  nil
                  (let [player @@player-eid
                        world* @world
                        hits (remove #(= (:entity/z-order @%) :z-order/effect)
                                     (grid/point->entities (:world/grid world*) (world-mouse-position world-viewport)))]
                    (->> render-z-order
                         (coll/sort-by-order hits #(:entity/z-order @%))
                         reverse
                         (filter #(raycaster/line-of-sight? (:world/raycaster world*) player @%))
                         first)))]
    (when old-mouseover-eid
      (swap! old-mouseover-eid dissoc :entity/mouseover?))
    (when new-eid
      (swap! new-eid assoc :entity/mouseover? true))
    (reset! mouseover-eid new-eid)))

(defn update-active-entities! [world player-eid active-entities]
  (reset! active-entities (content-grid/active-entities (:world/content-grid @world) @@player-eid)))

(defn set-camera-to-player! [player-eid world-viewport]
  (let [^OrthographicCamera camera (.getCamera ^Viewport world-viewport)
        pos (.position camera)
        [x y] (:entity/position @@player-eid)]
    (set! (.x pos) x)
    (set! (.y pos) y)
    (.update camera)))

(defn draw-tiled-map! [batch world-viewport tiled-map world world-unit-scale]
  (let [world* @world
        tiled-map @tiled-map
        ^OrthographicCamera camera (.getCamera ^Viewport world-viewport)
        pos (.position camera)
        ^Batch batch batch]
    (.setProjectionMatrix batch (.combined camera))
    (.begin batch)
    (tiled-map.draw/draw! tiled-map
                          batch
                          world-unit-scale
                          camera
                          (tile-color-setter*
                           {:ray-blocked? (partial raycaster/blocked? (:world/raycaster world*))
                            :explored-tile-corners (:world/explored-tile-corners world*)
                            :light-position [(.x pos) (.y pos) (.z pos)]
                            :see-all-tiles? false
                            :explored-tile-color (:colors/explored-tile colors)
                            :visible-tile-color (:colors/visible-tile colors)
                            :invisible-tile-color (:colors/invisible-tile colors)}))
    (.end batch)))

(defn current-mouseover-actor [stage]
  (let [[x y] (ui-mouse-position stage)]
    (mouseover-actor stage x y)))

(defn update-cursor! [cursors interaction-state player-eid]
  (let [eid @player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-fn (get {:player-item-on-cursor :cursors/hand-grab
                        :player-dead :cursors/black-x
                        :active-skill :cursors/sandclock
                        :stunned :cursors/denied
                        :player-moving :cursors/walking
                        :player-idle (fn
                                       [eid]
                                       (let [[k params] @interaction-state]
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
                                           :cursors/no-skill-selected)))}
                       state-k)
        cursor-key (if (keyword? cursor-fn)
                     cursor-fn
                     (cursor-fn eid))]
    (assert (contains? cursors cursor-key))
    (.setCursor ^Graphics Gdx/graphics ^Cursor (get cursors cursor-key))))

(defn destroy-entities! [db schemas world elapsed-time audio skin stage textures z-orders minimum-size]
  (doseq [eid (filter (comp :entity/destroyed? deref) (vals @(:world/entity-ids @world)))]
    (unregister-eid! @world eid)
    (doseq [[k v] @eid]
      (case k
        :entity/destroy-audiovisual
        (audiovisual! db schemas world elapsed-time audio skin stage textures z-orders minimum-size (:entity/position @eid) v)
        nil))))

(defn draw-world!
  [batch default-font shape-drawer stage textures world-viewport world-unit-scale unit-scale
   world player-eid elapsed-time show-body-bounds? active-entities
   show-tile-grid? show-cell-entities? show-cell-occupied? show-potential-field-colors?
   factions-iterations render-z-order]
  (let [^OrthographicCamera camera (.getCamera ^Viewport world-viewport)
        [x y] (ui-mouse-position stage)
        mouseover-actor* (mouseover-actor stage x y)
        world-mouse-pos (world-mouse-position world-viewport)]
    (.setColor ^Batch batch (float 1) (float 1) (float 1) (float 1))
    (.setProjectionMatrix ^Batch batch (.combined camera))
    (.begin ^Batch batch)
    (let [old-line-width (.getDefaultLineWidth ^ShapeDrawer shape-drawer)]
      (.setDefaultLineWidth ^ShapeDrawer shape-drawer (* world-unit-scale old-line-width))
      (reset! unit-scale world-unit-scale)
      (doseq [draw-fn [#(draw-tile-grid % shape-drawer world-viewport show-tile-grid?)
                       #(draw-cell-debug % shape-drawer world-viewport world
                                         show-cell-entities? show-cell-occupied? show-potential-field-colors?
                                         factions-iterations)
                       #(draw-entities! % shape-drawer batch default-font textures unit-scale world-unit-scale mouseover-actor* world-mouse-pos
                                        player-eid (:world/raycaster @world) elapsed-time show-body-bounds? active-entities render-z-order)
                       #(highlight-mouseover-tile % shape-drawer world-mouse-pos world)]]
        (draw-fn nil))
      (reset! unit-scale 1)
      (.setDefaultLineWidth ^ShapeDrawer shape-drawer old-line-width))
    (.end ^Batch batch)))

(defn- movement-vector [key-pressed?]
  (let [r (when (key-pressed? Input$Keys/D) [1  0])
        l (when (key-pressed? Input$Keys/A) [-1 0])
        u (when (key-pressed? Input$Keys/W) [0  1])
        d (when (key-pressed? Input$Keys/S) [0 -1])]
    (when (or r l u d)
      (let [v (v2/normalise (reduce #(mapv + %1 %2) [0 0] (remove nil? [r l u d])))]
        (when (pos? (v2/length v))
          v)))))

(defn- show-message! [stage message]
  (-> (.getRoot ^Stage stage)
      (.findActor "player-message")
      (.setUserObject (atom {:text message :counter 0}))))

(defn- interaction-state->txs [[k params] world elapsed-time stage audio skin textures z-orders minimum-size ui-set-item! player-eid world-mouse-position]
  (case k
    :interaction-state/mouseover-actor
    nil

    :interaction-state/clickable-mouseover-eid
    (let [{:keys [clicked-eid in-click-range?]} params]
      (if in-click-range?
        (case (:type (:entity/clickable @clicked-eid))
          :clickable/player
          (do (toggle-inventory-visible! stage)
              nil)

          :clickable/item
          (let [item (:entity/item @clicked-eid)]
            (cond
              (-> (.getRoot ^Stage stage)
                  (.findActor "moon.ui.windows.inventory")
                  .isVisible)
              (do (swap! clicked-eid assoc :entity/destroyed? true)
                  (play-sound! audio "bfxr_takeit.wav")
                  (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size player-eid world-mouse-position :pickup-item item))

              (inventory/can-pickup-item? (:entity/inventory @player-eid) item)
              (do (swap! clicked-eid assoc :entity/destroyed? true)
                  (play-sound! audio "bfxr_pickup.wav")
                  (assert (item/valid? item))
                  (let [[cell cell-item] (inventory/can-pickup-item? (:entity/inventory @player-eid) item)]
                    (assert cell)
                    (assert (nil? cell-item))
                    (swap! player-eid set-item cell item)
                    (ui-set-item! cell item))
                  nil)

              :else
              (do (play-sound! audio "bfxr_denied.wav")
                  (show-message! stage "Your Inventory is full")
                  nil))))
        (do (play-sound! audio "bfxr_denied.wav")
            (show-message! stage "Too far away")
            nil)))

    :interaction-state.skill/usable
    (let [[skill effect-ctx] params]
      (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size player-eid world-mouse-position :start-action [skill effect-ctx]))

    :interaction-state.skill/not-usable
    (let [state params]
      (do (play-sound! audio "bfxr_denied.wav")
          (show-message! stage (case state
                                 :cooldown "Skill is still on cooldown"
                                 :not-enough-mana "Not enough mana"
                                 :invalid-params "Cannot use this here"))
          nil))

    :interaction-state/no-skill-selected
    (do (play-sound! audio "bfxr_denied.wav")
        (show-message! stage "No selected skill")
        nil)))

(defn handle-player-input!
  [world elapsed-time interaction-state player-eid
   audio skin stage textures z-orders minimum-size world-viewport key-pressed? button-just-pressed?]
  (let [eid @player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        left-button-pressed? (button-just-pressed? Input$Buttons/LEFT)
        move-v (movement-vector key-pressed?)
        [x y] (ui-mouse-position stage)
        mouseover-actor* (mouseover-actor stage x y)
        world-mouse-pos (world-mouse-position world-viewport)]
    (case state-k
      :player-idle
      (if move-v
        (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size eid world-mouse-pos :movement-input move-v)
        (when left-button-pressed?
          (interaction-state->txs @interaction-state
                                  world
                                  elapsed-time
                                  stage
                                  audio
                                  skin
                                  textures
                                  z-orders
                                  minimum-size
                                  #(ui-set-item! skin stage textures %1 %2)
                                  eid
                                  world-mouse-pos)))

      :player-moving
      (if move-v
        (do (swap! eid assoc :entity/movement {:direction move-v
                                               :speed (or (stats/get-value (:entity/stats @eid) :stats/movement-speed)
                                                          0)})
            nil)
        (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size eid world-mouse-pos :no-movement-input))

      :player-item-on-cursor
      (when (and left-button-pressed?
                 (not mouseover-actor*))
        (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size eid world-mouse-pos :drop-item))

      nil)))

(defn- tick-entities!
  [db schemas world elapsed-time delta-time active-entities
   audio skin stage textures z-orders minimum-size max-speed world-viewport]
  (let [active-entities* @active-entities
        raycaster (:world/raycaster @world)
        world-mouse-pos (world-mouse-position world-viewport)]
    (letfn [(apply-effects! [effect-ctx effects]
              (doseq [effect (filter #(applicable? % effect-ctx) effects)]
                (handle-effect db schemas world elapsed-time audio skin stage textures z-orders minimum-size
                               effect effect-ctx world-mouse-pos
                               apply-effects!
                               active-entities*
                               colors
                               raycaster)))]
      (doseq [eid active-entities*
              component @eid]
        (tick-component world elapsed-time delta-time audio skin stage textures z-orders
                        minimum-size max-speed
                        nil world-mouse-pos
                        apply-effects!
                        eid component)))))

(defn tick-game!
  [db schemas world elapsed-time delta-time potential-field-cache active-entities paused?
   audio skin stage textures world-viewport factions-iterations z-orders
   minimum-size max-delta max-speed]
  (when-not @paused?
    (let [delta-ms (min (.getDeltaTime ^Graphics Gdx/graphics) max-delta)]
      (reset! delta-time delta-ms)
      (swap! elapsed-time + delta-ms))
    (doseq [[faction max-iterations] factions-iterations]
      (potential-field.update/update! (:world/grid @world)
                                      potential-field-cache
                                      faction
                                      @active-entities
                                      max-iterations))
    (tick-entities! db schemas world elapsed-time delta-time active-entities
                    audio skin stage textures z-orders minimum-size max-speed world-viewport)))

(def listener
  (let [world-unit-scale (float (/ 48))
        minimum-size 0.39
        max-delta 0.04
        max-speed (/ minimum-size max-delta)
        z-orders [:z-order/on-ground
                  :z-order/ground
                  :z-order/flying
                  :z-order/effect]
        render-z-order (apply hash-map (interleave z-orders (range)))
        factions-iterations {:good 15
                             :evil 5}
        level-fn uf-caves/create
        default-font-params {:path "fonts/films.EXL_____.ttf"
                             :size 16
                             :quality-scaling 2
                             :use-integer-positions? false}
        sound-paths (-> "sounds.edn" io/resource slurp edn/read-string)
        audio (atom nil)
        batch (atom nil)
        cursors (atom nil)
        default-font (atom nil)
        world-viewport (atom nil)
        shape-drawer (atom nil)
        shape-drawer-texture (atom nil)
        textures (atom nil)
        skin (atom nil)
        stage (atom nil)
        schemas (-> "schema.edn" io/resource slurp edn/read-string)
        db (db/create schemas)
        world (atom nil)
        tiled-map (atom nil)
        start-position (atom nil)
        player-eid (atom nil)
        potential-field-cache (atom nil)
        active-entities (atom nil)
        delta-time (atom nil)
        mouseover-eid (atom nil)
        elapsed-time (atom 0)
        paused? (atom false)
        show-potential-field-colors? (atom nil)
        show-cell-entities? (atom false)
        show-cell-occupied? (atom false)
        show-body-bounds? (atom false)
        show-tile-grid? (atom false)
        interaction-state (atom nil)
        unit-scale (atom 1)
        pausing? true
        state->pause-game? {:active-skill false
                            :stunned false
                            :player-moving false
                            :player-idle true
                            :player-dead true
                            :player-item-on-cursor true}]
    (reify ApplicationListener
      (create [_]
        (reset! audio (create-audio! Gdx/audio
                                     (create-sound-file-handles! Gdx/files sound-paths)))
        (reset! batch (SpriteBatch.))
        (reset! unit-scale 1)
        (reset! shape-drawer-texture (white-pixel-texture))
        (reset! shape-drawer (create-shape-drawer! @batch @shape-drawer-texture))
        (reset! skin (create-skin! (.internal Gdx/files "skin/uiskin.json")))
        (let [s (Stage. (FitViewport. (float 1440) (float 900)) @batch)]
          (.setInputProcessor ^Input Gdx/input ^InputProcessor s)
          (reset! stage s))
        (set! (.initialTime ^TooltipManager (TooltipManager/getInstance)) 0)
        (Colors/put "PRETTY_NAME" (Color. 0.84 0.8 0.52 1))
        (reset! cursors (create-cursors! Gdx/files))
        (reset! textures (textures/create Gdx/files {:folder "resources/"
                                                     :extensions #{"png" "bmp"}}))
        (reset! world-viewport (create-world-viewport! world-unit-scale))
        (reset! default-font (create-default-font! Gdx/files default-font-params))
        (doseq [^Actor actor (create-ui-actors world
                                               elapsed-time
                                               mouseover-eid
                                               paused?
                                               player-eid
                                               show-tile-grid?
                                               show-cell-entities?
                                               show-cell-occupied?
                                               show-body-bounds?
                                               show-potential-field-colors?
                                               unit-scale
                                               z-orders
                                               minimum-size
                                               @audio
                                               @default-font
                                               @shape-drawer
                                               @skin
                                               @stage
                                               @textures
                                               @world-viewport)]
          (.addActor ^Stage @stage actor))
        (let [{level-tiled-map :tiled-map
               level-start :start-position} (create-level! level-fn db @textures)]
          (reset! tiled-map level-tiled-map)
          (reset! start-position level-start))
        (reset! world (world/create @tiled-map))
        (spawn-player! db schemas world elapsed-time start-position @skin @stage @textures z-orders minimum-size)
        (bind-player-eid! world player-eid)
        (spawn-map-creatures! db schemas world elapsed-time start-position tiled-map z-orders minimum-size))

      (dispose [_]
        (audio.dispose/dispose! @audio)
        (Disposable/.dispose @batch)
        (run! Disposable/.dispose (vals @cursors))
        (Disposable/.dispose @default-font)
        (Disposable/.dispose @shape-drawer-texture)
        (Disposable/.dispose @skin)
        (run! Disposable/.dispose (vals @textures))
        (Disposable/.dispose @tiled-map))

      (render [_]
        (let [input Gdx/input
              key-pressed? #(.isKeyPressed ^Input input (int %))
              key-just-pressed? #(.isKeyJustPressed ^Input input (int %))
              button-just-pressed? #(.isButtonJustPressed ^Input input (int %))
              audio @audio
              batch @batch
              cursors @cursors
              default-font @default-font
              shape-drawer @shape-drawer
              skin @skin
              stage @stage
              textures @textures
              world-viewport @world-viewport]
          (ScreenUtils/clear 0 0 0 0)
          (update-mouseover-eid! world player-eid mouseover-eid stage world-viewport render-z-order)
          (update-active-entities! world player-eid active-entities)
          (set-camera-to-player! player-eid world-viewport)
          (draw-tiled-map! batch world-viewport tiled-map world world-unit-scale)
          (draw-world! batch default-font shape-drawer stage textures world-viewport world-unit-scale unit-scale
                       world player-eid elapsed-time show-body-bounds? active-entities
                       show-tile-grid? show-cell-entities? show-cell-occupied? show-potential-field-colors?
                       factions-iterations render-z-order)
          (reset! interaction-state
                  (let [player-eid @player-eid
                        mouseover-eid @mouseover-eid
                        mouseover-actor (current-mouseover-actor stage)
                        world-mouse-position (world-mouse-position world-viewport)]
                    (cond
                      mouseover-actor
                      [:interaction-state/mouseover-actor (mouseover-actor-info mouseover-actor)]

                      (and mouseover-eid
                           (:entity/clickable @mouseover-eid))
                      [:interaction-state/clickable-mouseover-eid
                       {:clicked-eid mouseover-eid
                        :in-click-range? (< (v2/distance (:entity/position @player-eid)
                                                         (:entity/position @mouseover-eid))
                                            (:entity/click-distance-tiles @player-eid))}]

                      :else
                      (if-let [skill-id (-> (.getRoot ^Stage stage)
                                            (.findActor "moon.ui.action-bar")
                                            action-bar/selected-skill)]
                        (let [entity @player-eid
                              skill (skill-id (:entity/skills entity))
                              target-position (or (and mouseover-eid
                                                       (:entity/position @mouseover-eid))
                                                  world-mouse-position)
                              effect-ctx {:effect/source player-eid
                                          :effect/target mouseover-eid
                                          :effect/target-position target-position
                                          :effect/target-direction (v2/direction (:entity/position @player-eid)
                                                                                target-position)}
                              state (usable-state skill entity effect-ctx)]
                          (if (= state :usable)
                            [:interaction-state.skill/usable [skill effect-ctx]]
                            [:interaction-state.skill/not-usable state]))
                        [:interaction-state/no-skill-selected]))))
          (update-cursor! cursors interaction-state player-eid)
          (handle-player-input! world elapsed-time interaction-state player-eid
                                audio skin stage textures z-orders minimum-size world-viewport
                                key-pressed? button-just-pressed?)
          (reset! interaction-state nil)
          (reset! paused?
                  (or #_error
                      (and pausing?
                           (state->pause-game? (:state (:entity/fsm @@player-eid)))
                           (not (or (key-just-pressed? (:unpause-once controls))
                                    (key-pressed? (:unpause-continously controls)))))))
          (tick-game! db schemas world elapsed-time delta-time potential-field-cache active-entities paused?
                      audio skin stage textures world-viewport factions-iterations z-orders
                      minimum-size max-delta max-speed)
          (destroy-entities! db schemas world elapsed-time audio skin stage textures z-orders minimum-size)
          (doseq [[k f] {(:zoom-in controls) (fn []
                                               (let [^OrthographicCamera camera (.getCamera ^Viewport world-viewport)]
                                                 (set! (.zoom camera) (max 0.1 (+ (.zoom camera) 0.025)))
                                                 (.update camera)))
                         (:zoom-out controls) (fn []
                                                (let [^OrthographicCamera camera (.getCamera ^Viewport world-viewport)]
                                                  (set! (.zoom camera) (max 0.1 (+ (.zoom camera) -0.025)))
                                                  (.update camera)))}]
            (when (key-pressed? k)
              (f)))
          (doseq [[k f] {(:close-windows-key controls) (fn []
                                                         (->> (.getChildren ^Group (.findActor ^Group (.getRoot ^Stage stage) "moon.ui.windows"))
                                                              (run! #(.setVisible ^Actor % false))))
                         (:toggle-inventory controls) #(toggle-inventory-visible! stage)
                         (:toggle-entity-info controls) (fn []
                                                          (let [entity-info (.findActor ^Group (.getRoot ^Stage stage) "moon.ui.windows.entity-info")]
                                                            (.setVisible ^Actor entity-info
                                                                         (not (.isVisible ^Actor entity-info)))))}]
            (when (key-just-pressed? k)
              (f)))
          (let [^Stage stage stage]
            (.act stage)
            (.draw stage))))

      (resize [_ width height]
        (.update (.getViewport ^Stage @stage) width height true)
        (.update ^Viewport @world-viewport width height false))

      (pause [_])

      (resume [_]))))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. listener
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "Moon")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))

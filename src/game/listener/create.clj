(ns game.listener.create
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [game.shared :refer [audio
                                 batch
                                 colors
                                 create-action-bar
                                 create-dev-menu
                                 cursors
                                 db
                                 default-font
                                 dev-menus
                                 draw-fn-filled-rectangle
                                 draw-fn-rectangle
                                 elapsed-time
                                 explored-tile-corners
                                 handle-clicked-inventory-cell
                                 hp-mana-bar-create
                                 inventory-window-create
                                 level-fn
                                 mouseover-eid
                                 paused?
                                 player-eid
                                 player-message-actor-create
                                 player-state-draw-create
                                 raycaster
                                 shape-drawer
                                 shape-drawer-texture
                                 skin
                                 spawn-creature
                                 spawn-entity!
                                 stage
                                 stage-info-window-create
                                 start-position
                                 textures
                                 tiled-map
                                 ui-mouse-position
                                 ui-remove-item!
                                 ui-set-item!
                                 unit-scale
                                 windows-create
                                 world
                                 world-mouse-position
                                 world-unit-scale
                                 world-viewport]]
            [moon.db :as db]
            [moon.g2d :as moon-g2d]
            [moon.inventory :as inventory]
            [moon.number :as number]
            [moon.tiled-map :as moon-tiled-map]
            [moon.textures :as textures]
            [moon.world :as world])
  (:import (com.badlogic.gdx Audio Files Gdx Graphics Input InputProcessor)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Color Colors Cursor OrthographicCamera Pixmap Pixmap$Format Texture Texture$TextureFilter TextureData)
           (com.badlogic.gdx.graphics.glutils PixmapTextureData)
           (com.badlogic.gdx.maps.tiled TiledMap)
           (com.badlogic.gdx.graphics.g2d BitmapFont BitmapFont$BitmapFontData SpriteBatch TextureRegion)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Skin TooltipManager)
           (com.badlogic.gdx.utils Disposable)
           (com.badlogic.gdx.utils.viewport FitViewport Viewport)
           (space.earlygrey.shapedrawer ShapeDrawer)))

(defn create-shape-drawer-texture []
  (let [pixmap (doto ^Pixmap (Pixmap. (int 1) (int 1) Pixmap$Format/RGBA8888)
                 (.setColor 1 1 1 1)
                 (.drawPixel (int 0) (int 0)))
        texture (Texture. ^TextureData (PixmapTextureData. ^Pixmap pixmap
                                                           ^Pixmap$Format (Pixmap/.getFormat ^Pixmap pixmap)
                                                           false
                                                           false))]
    (Disposable/.dispose pixmap)
    texture))

(defn create-ui-actors []
  (let [cell-size 48]
    [(create-action-bar)
     (create-dev-menu
      {:menus dev-menus
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
                                                (mapv int (ui-mouse-position)))}
                                  {:label "World"
                                   :update-fn (fn []
                                                (mapv int (world-mouse-position)))}
                                  {:label "Zoom"
                                   :update-fn (fn []
                                                (.zoom ^OrthographicCamera (.getCamera ^Viewport @world-viewport)))
                                   :icon "images/zoom.png"}]]
                        (if (:icon item)
                          (update item :icon #(get @textures %))
                          item))
       :skin @skin})
     (hp-mana-bar-create)
     (windows-create [stage-info-window-create
                      #(inventory-window-create
                        (fn [_event cell]
                          (handle-clicked-inventory-cell @player-eid
                                                         @audio
                                                         (fn [cell item] (ui-set-item! nil cell item))
                                                         (fn [cell] (ui-remove-item! nil cell))
                                                         cell
                                                         (world-mouse-position)))
                        (fn [player-entity x y mouseover? cell]
                          (draw-fn-rectangle @shape-drawer x y cell-size cell-size (:colors/item-rect colors))
                          (when (and mouseover?
                                     (= :player-item-on-cursor (:state (:entity/fsm player-entity))))
                            (let [item (:entity/item-on-cursor player-entity)
                                  color (if (inventory/valid-slot? cell item)
                                          (:colors/droppable-item colors)
                                          (:colors/not-allowed-drop-item colors))]
                              (draw-fn-filled-rectangle @shape-drawer (inc x) (inc y) (- cell-size 2) (- cell-size 2) color)))))])
     (player-state-draw-create unit-scale)
     (player-message-actor-create @default-font unit-scale)]))

(defn- create-audio! [gdx-audio files]
  (reset! audio
          (into {}
                (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
                      :let [path (format "sounds/%s.wav" sound-name)]]
                  [sound-name
                   (.newSound ^Audio gdx-audio (.internal ^Files files path))]))))

(defn- create-batch! []
  (reset! batch (SpriteBatch.)))

(defn- init-unit-scale! []
  (reset! unit-scale 1))

(defn- init-shape-drawer-texture! []
  (reset! shape-drawer-texture (create-shape-drawer-texture)))

(defn- create-shape-drawer! []
  (reset! shape-drawer
          (ShapeDrawer. @batch
                        (TextureRegion. ^Texture @shape-drawer-texture (int 1) (int 0) (int 1) (int 1)))))

(defn- create-skin! [files]
  (reset! skin
          (let [s (Skin. ^FileHandle (.internal ^Files files "skin/uiskin.json"))]
            (set! (.markupEnabled ^BitmapFont$BitmapFontData
                                  (.getData (.getFont ^Skin s "default-font")))
                  true)
            s)))

(defn- create-stage! [input]
  (let [stage* (Stage. (FitViewport. (float 1440) (float 900)) @batch)]
    (.setInputProcessor ^Input input ^InputProcessor stage*)
    (reset! stage stage*)))

(defn- init-tooltip-manager! []
  (set! (.initialTime ^TooltipManager (TooltipManager/getInstance)) 0))

(defn- put-pretty-name-color! []
  (Colors/put "PRETTY_NAME" (Color. 0.84 0.8 0.52 1)))

(defn- create-cursors! [files]
  (reset! cursors
          (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
            (update-vals data
                         (fn [[path-segment [hotspot-x hotspot-y]]]
                           (let [path (format path-format path-segment)
                                 pixmap* (Pixmap. ^FileHandle (.internal ^Files files path))
                                 cursor (.newCursor ^Graphics Gdx/graphics ^Pixmap pixmap* hotspot-x hotspot-y)]
                             (Disposable/.dispose pixmap*)
                             cursor))))))

(defn- create-textures! [files]
  (reset! textures
          (textures/create files {:folder "resources/"
                                  :extensions #{"png" "bmp"}})))

(defn- create-world-viewport! []
  (reset! world-viewport
          (let [world-width (* 1440 world-unit-scale)
                world-height (* 900 world-unit-scale)]
            (FitViewport. (float world-width)
                          (float world-height)
                          (doto (OrthographicCamera.)
                            (.setToOrtho false world-width world-height))))))

(defn- create-default-font! [files]
  (reset! default-font
          (let [{:keys [path
                        size
                        quality-scaling
                        use-integer-positions?]} {:path "fonts/films.EXL_____.ttf"
                                                  :size 16
                                                  :quality-scaling 2
                                                  :use-integer-positions? false}
                generator (FreeTypeFontGenerator. ^FileHandle (.internal ^Files files path))
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
            font)))

(defn- add-ui-actors! []
  (doseq [actor (create-ui-actors)]
    (.addActor ^Stage @stage actor)))

(defn- create-level! []
  (let [{level-tiled-map :tiled-map
         level-start :start-position}
        (level-fn {:level/creature-properties (moon-tiled-map/prepare-creature-tiles
                                               (db/all-raw db :properties/creatures)
                                               #(textures/texture-region @textures %))
                   :textures @textures})]
    (reset! tiled-map level-tiled-map)
    (reset! start-position level-start)))

(defn- create-world! []
  (reset! world (world/create @tiled-map)))

(defn- create-explored-tile-corners! []
  (reset! explored-tile-corners
          (let [props (.getProperties ^TiledMap @tiled-map)]
            (moon-g2d/create (.get props "width")
                             (.get props "height")
                             (constantly false)))))

(defn- create-raycaster! []
  (let [{:keys [width height cells]} (world/raycaster-data @world)
        arr (make-array Boolean/TYPE width height)]
    (doseq [[[x y] blocked?] cells]
      (aset arr x y (boolean blocked?)))
    (reset! raycaster [arr width height])))

(defn- spawn-player! []
  (spawn-entity! (spawn-creature {:position (mapv (partial + 0.5) @start-position)
                                   :creature-property (db/build db :creatures/vampire)
                                   :components {:entity/fsm {:fsm :fsms/player
                                                             :initial-state :player-idle}
                                                :entity/faction :good
                                                :entity/player? true
                                                :entity/free-skill-points 3
                                                :entity/clickable {:type :clickable/player}
                                                :entity/click-distance-tiles 1.5}})))

(defn- bind-player-eid! []
  (let [eid (world/entity-by-id @world 1)]
    (assert (:entity/player? @eid))
    (reset! player-eid eid)))

(defn- spawn-map-creatures! []
  (let [sp @start-position]
    (doseq [[position creature-id] (moon-tiled-map/spawn-positions @tiled-map)
            :when (not= position sp)]
      (spawn-entity! (spawn-creature {:position (mapv (partial + 0.5) position)
                                       :creature-property (db/build db (keyword creature-id))
                                       :components {:entity/fsm {:fsm :fsms/npc
                                                                 :initial-state :npc-sleeping}
                                                    :entity/faction :evil}})))))

(defn create! [gdx-audio files input]
  (create-audio! gdx-audio files)
  (create-batch!)
  (init-unit-scale!)
  (init-shape-drawer-texture!)
  (create-shape-drawer!)
  (create-skin! files)
  (create-stage! input)
  (init-tooltip-manager!)
  (put-pretty-name-color!)
  (create-cursors! files)
  (create-textures! files)
  (create-world-viewport!)
  (create-default-font! files)
  (add-ui-actors!)
  (create-level!)
  (create-world!)
  (create-explored-tile-corners!)
  (create-raycaster!)
  (spawn-player!)
  (bind-player-eid!)
  (spawn-map-creatures!))

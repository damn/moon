(ns game.listener.create
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [entity.spawn :refer [spawn-entity!]]
            [game.spawn :refer [spawn-creature]]
            [moon.db :as db]
            [moon.g2d :as moon-g2d]
            [moon.level.uf-caves :as uf-caves]
            [moon.tiled-map :as moon-tiled-map]
            [moon.textures :as textures]
            [moon.world :as world])
  (:import (com.badlogic.gdx Audio Files Gdx Graphics)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Color Colors OrthographicCamera Pixmap Texture Texture$TextureFilter)
           (com.badlogic.gdx.maps.tiled TiledMap)
           (com.badlogic.gdx.graphics.g2d BitmapFont BitmapFont$BitmapFontData SpriteBatch TextureRegion)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Skin TooltipManager)
           (com.badlogic.gdx.utils Disposable)
           (com.badlogic.gdx.utils.viewport FitViewport)
           (space.earlygrey.shapedrawer ShapeDrawer)))

(defn create-audio! [gdx-audio files]
  (into {}
        (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
              :let [path (format "sounds/%s.wav" sound-name)]]
          [sound-name
           (.newSound ^Audio gdx-audio (.internal ^Files files path))])))

(defn create-batch! []
  (SpriteBatch.))

(defn create-shape-drawer! [batch texture]
  (ShapeDrawer. batch
                (TextureRegion. ^Texture texture (int 1) (int 0) (int 1) (int 1))))

(defn create-skin! [files]
  (let [s (Skin. ^FileHandle (.internal ^Files files "skin/uiskin.json"))]
    (set! (.markupEnabled ^BitmapFont$BitmapFontData
                          (.getData (.getFont ^Skin s "default-font")))
          true)
    s))

(defn create-stage! [batch]
  (Stage. (FitViewport. (float 1440) (float 900)) batch))

(defn init-tooltip-manager! []
  (set! (.initialTime ^TooltipManager (TooltipManager/getInstance)) 0))

(defn put-pretty-name-color! []
  (Colors/put "PRETTY_NAME" (Color. 0.84 0.8 0.52 1)))

(defn create-cursors! [files]
  (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
    (update-vals data
                 (fn [[path-segment [hotspot-x hotspot-y]]]
                   (let [path (format path-format path-segment)
                         pixmap* (Pixmap. ^FileHandle (.internal ^Files files path))
                         cursor (.newCursor ^Graphics Gdx/graphics ^Pixmap pixmap* hotspot-x hotspot-y)]
                     (Disposable/.dispose pixmap*)
                     cursor)))))

(defn create-textures! [files]
  (textures/create files {:folder "resources/"
                          :extensions #{"png" "bmp"}}))

(defn create-world-viewport! [world-unit-scale]
  (let [world-width (* 1440 world-unit-scale)
        world-height (* 900 world-unit-scale)]
    (FitViewport. (float world-width)
                  (float world-height)
                  (doto (OrthographicCamera.)
                    (.setToOrtho false world-width world-height)))))

(defn create-default-font! [files]
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
    font))

(def level-fn uf-caves/create)

(defn create-level! [db* textures*]
  (let [{level-tiled-map :tiled-map
         level-start :start-position}
        (level-fn {:level/creature-properties (moon-tiled-map/prepare-creature-tiles
                                               (db/all-raw db* :properties/creatures)
                                               #(textures/texture-region textures* %))
                   :textures textures*})]
    {:tiled-map level-tiled-map
     :start-position level-start}))

(defn create-world! [tiled-map]
  (world/create tiled-map))

(defn create-explored-tile-corners! [tiled-map]
  (let [props (.getProperties ^TiledMap tiled-map)]
    (moon-g2d/create (.get props "width")
                     (.get props "height")
                     (constantly false))))

(defn create-raycaster! [world]
  (let [{:keys [width height cells]} (world/raycaster-data world)
        arr (make-array Boolean/TYPE width height)]
    (doseq [[[x y] blocked?] cells]
      (aset arr x y (boolean blocked?)))
    [arr width height]))

(defn spawn-player! [db* world elapsed-time start-position skin stage textures* z-orders minimum-size]
  (spawn-entity! world elapsed-time skin stage textures* z-orders minimum-size
                 (spawn-creature {:position (mapv (partial + 0.5) @start-position)
                                  :creature-property (db/build db* :creatures/vampire)
                                  :components {:entity/fsm {:fsm :fsms/player
                                                            :initial-state :player-idle}
                                               :entity/faction :good
                                               :entity/player? true
                                               :entity/free-skill-points 3
                                               :entity/clickable {:type :clickable/player}
                                               :entity/click-distance-tiles 1.5}})))

(defn bind-player-eid! [world player-eid]
  (let [eid (world/entity-by-id @world 1)]
    (assert (:entity/player? @eid))
    (reset! player-eid eid)))

(defn spawn-map-creatures! [db* world elapsed-time start-position tiled-map skin stage textures* z-orders minimum-size]
  (let [sp @start-position]
    (doseq [[position creature-id] (moon-tiled-map/spawn-positions @tiled-map)
            :when (not= position sp)]
      (spawn-entity! world elapsed-time skin stage textures* z-orders minimum-size
                     (spawn-creature {:position (mapv (partial + 0.5) position)
                                      :creature-property (db/build db* (keyword creature-id))
                                      :components {:entity/fsm {:fsm :fsms/npc
                                                                :initial-state :npc-sleeping}
                                                   :entity/faction :evil}})))))

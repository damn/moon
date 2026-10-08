(ns game.listener.create
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [entity.spawn :refer [spawn-entity!]]
            [game.spawn :refer [spawn-creature]]
            [game.ui :refer [sync-player-ui!]]
            [moon.db :as db]
            [moon.tiled-map :as moon-tiled-map]
            [moon.textures :as textures])
  (:import (com.badlogic.gdx Files Gdx Graphics)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics OrthographicCamera Pixmap Texture Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d BitmapFont BitmapFont$BitmapFontData TextureRegion)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)
           (com.badlogic.gdx.utils Disposable)
           (com.badlogic.gdx.utils.viewport FitViewport)
           (space.earlygrey.shapedrawer ShapeDrawer)))

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

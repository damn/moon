(ns levelgen.listener
  (:require [moon.db :as db]
            [moon.level.modules :as modules]
            [moon.level.tmx :as tmx]
            [moon.level.uf-caves :as uf-caves]
            [moon.textures :as textures]
            [moon.tiled-map :as moon-tiled-map])
  (:import (com.badlogic.gdx.maps.tiled TiledMap TiledMapTileLayer)
           (com.badlogic.gdx ApplicationListener Files Gdx Input Input$Keys InputProcessor)
           (com.badlogic.gdx.graphics Color OrthographicCamera)
           (com.badlogic.gdx.graphics.g2d SpriteBatch)
           (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Skin TextButton Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)
           (com.badlogic.gdx.utils Disposable ScreenUtils)
           (com.badlogic.gdx.utils.viewport FitViewport Viewport)))

(defn- generate-level
  [db textures ^OrthographicCamera camera level-fn]
  (let [level (level-fn {:level/creature-properties
                         (moon-tiled-map/prepare-creature-tiles
                          (db/all-raw db :properties/creatures)
                          #(textures/texture-region textures %))
                         :textures textures})
        ^TiledMap tiled-map (:tiled-map level)
        props (.getProperties tiled-map)
        width (.get props "width")
        height (.get props "height")]
    (assert tiled-map)
    (.setVisible ^TiledMapTileLayer (.get (.getLayers tiled-map) "creatures")
                 true)
    (let [pos (.position camera)
          px (float (/ width 2))
          py (float (/ height 2))
          x-diff (max (- px (float 0)) (- (float width) px))
          y-diff (max (- (float height) py) (- py (float 0)))
          vp-ratio-w (/ (* x-diff 2) (.viewportWidth camera))
          vp-ratio-h (/ (* y-diff 2) (.viewportHeight camera))]
      (set! (.x pos) px)
      (set! (.y pos) py)
      (set! (.zoom camera) (max vp-ratio-w vp-ratio-h))
      (.update camera))
    tiled-map))

(defn- text-button ^TextButton [^Skin skin ^String label on-click!]
  (doto (TextButton. label skin)
    (.addListener (proxy [ChangeListener] []
                    (changed [_event _actor]
                      (on-click!))))))

(defn listener []
  (let [world-unit-scale (float (/ 48))
        world-width (* 1440 world-unit-scale)
        world-height (* 900 world-unit-scale)
        batch (atom nil)
        skin (atom nil)
        ui-stage (atom nil)
        world-viewport (atom nil)
        camera (atom nil)
        db (atom nil)
        textures (atom nil)
        tiled-map (atom nil)
        buttons (for [[label level-fn] [["Vampire" tmx/vampire]
                                        ["UF Caves" uf-caves/create]
                                        ["Modules" modules/create]]]
                  [(str "Generate " label)
                   (fn []
                     (Disposable/.dispose @tiled-map)
                     (reset! tiled-map
                             (generate-level @db @textures @camera level-fn)))])]
    (reify ApplicationListener
      (create [_]
        (reset! batch (SpriteBatch.))
        (reset! skin (Skin. (.internal ^Files Gdx/files "skin/uiskin.json")))
        (let [s (Stage. (FitViewport. 1440 900) @batch)
              ^Skin skin* @skin
              window (Window. "Edit" skin*)]
          (doseq [[label on-click!] buttons]
            (.add window (text-button skin* label on-click!))
            (.row window))
          (.pack window)
          (.addActor s window)
          (reset! ui-stage s))
        (.setInputProcessor ^Input Gdx/input ^InputProcessor @ui-stage)
        (reset! world-viewport (FitViewport. (float world-width)
                                             (float world-height)
                                             (doto (OrthographicCamera.)
                                               (.setToOrtho false
                                                            world-width
                                                            world-height))))
        (reset! camera (.getCamera ^Viewport @world-viewport))
        (reset! db (db/create))
        (reset! textures (textures/create Gdx/files {:folder "resources/"
                                                     :extensions #{"png" "bmp"}}))
        (reset! tiled-map (generate-level @db @textures @camera uf-caves/create)))

      (dispose [_]
        (Disposable/.dispose @batch)
        (Disposable/.dispose @skin)
        (run! Disposable/.dispose (vals @textures))
        (Disposable/.dispose @tiled-map))

      (render [_]
        (let [^OrthographicCamera camera* @camera]
          (ScreenUtils/clear 0 0 0 0)
          (moon-tiled-map/draw! @tiled-map
                                @batch
                                world-unit-scale
                                (.getCamera ^Viewport @world-viewport)
                                (constantly (.toFloatBits Color/WHITE)))
          (doseq [[k f] {Input$Keys/MINUS (fn []
                                            (set! (.zoom camera*) (max 0.1 (+ (.zoom camera*) 0.1)))
                                            (.update camera*))
                         Input$Keys/EQUALS (fn []
                                             (set! (.zoom camera*) (max 0.1 (+ (.zoom camera*) -0.1)))
                                             (.update camera*))
                         Input$Keys/LEFT (fn []
                                           (let [pos (.position camera*)]
                                             (set! (.x pos) (- (.x pos) 1))
                                             (.update camera*)))
                         Input$Keys/RIGHT (fn []
                                            (let [pos (.position camera*)]
                                              (set! (.x pos) (+ (.x pos) 1))
                                              (.update camera*)))
                         Input$Keys/UP (fn []
                                         (let [pos (.position camera*)]
                                           (set! (.y pos) (+ (.y pos) 1))
                                           (.update camera*)))
                         Input$Keys/DOWN (fn []
                                           (let [pos (.position camera*)]
                                             (set! (.y pos) (- (.y pos) 1))
                                             (.update camera*)))}]
            (when (.isKeyPressed ^Input Gdx/input k)
              (f)))
          (.act ^Stage @ui-stage)
          (.draw ^Stage @ui-stage)))

      (resize [_ width height]
        (.update (.getViewport ^Stage @ui-stage) width height true)
        (.update ^Viewport @world-viewport width height false))

      (pause [_])

      (resume [_]))))

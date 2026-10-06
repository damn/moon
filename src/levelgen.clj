(ns levelgen
  (:require [moon.db :as db]
            [moon.camera :as orthographic-camera]
            [moon.level.modules :as modules]
            [moon.level.tmx :as tmx]
            [moon.level.uf-caves :as uf-caves]
            [moon.textures :as textures]
            [moon.tiled-map :as moon-tiled-map]
            [moon.color :as color]
            [moon.scene2d.window :as window]
            [moon.viewport :as viewport])
  (:import (com.badlogic.gdx.maps MapLayers)
           (com.badlogic.gdx.maps.tiled TiledMapTileLayer)
           (com.badlogic.gdx Application ApplicationListener Files Gdx Graphics Input Input$Keys InputProcessor)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics GL20)
           (com.badlogic.gdx.graphics.g2d SpriteBatch)
           (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Skin TextButton)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)
           (com.badlogic.gdx.utils Disposable)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(def ^:private config
  {:initial-level-fn uf-caves/create
   :level-fns [["Vampire" tmx/vampire]
               ["UF Caves" uf-caves/create]
               ["Modules" modules/create]]
   :ui-viewport-width 1440
   :ui-viewport-height 900
   :world-viewport-width 1440
   :world-viewport-height 900
   :tile-size 48
   :ui-skin-path "skin/uiskin.json"
   :textures-config {:folder "resources/"
                     :extensions #{"png" "bmp"}}
   :zoom-speed 0.1
   :camera-movement-speed 1})

(defn- generate-level
  [db textures camera level-fn]
  (let [level (level-fn {:level/creature-properties
                         (moon-tiled-map/prepare-creature-tiles
                          (db/all-raw db :properties/creatures)
                          #(textures/texture-region textures %))
                         :textures textures})
        tiled-map (:tiled-map level)
        width (moon-tiled-map/get-property tiled-map "width")
        height (moon-tiled-map/get-property tiled-map "height")]
    (assert tiled-map)
    (.setVisible ^TiledMapTileLayer (.get ^MapLayers (moon-tiled-map/get-layers tiled-map) "creatures")
                 true)
    (orthographic-camera/set-position! camera [(/ width 2) (/ height 2)])
    (orthographic-camera/zoom-to-rect camera {:left [0 0]
                                              :top [0 height]
                                              :right [width 0]
                                              :bottom [0 0]})
    tiled-map))

(defn create-viewport [world-width world-height]
  (FitViewport. (float world-width)
                (float world-height)
                (doto (orthographic-camera/new)
                  (orthographic-camera/set-to-ortho! false
                                                     world-width
                                                     world-height))))

(defn create-skin [file-handle]
  (Skin. ^FileHandle file-handle))

(defn create-stage [batch viewport actor]
  (doto (Stage. viewport batch)
    (.addActor actor)))

(defn text-button [skin label on-click!]
  (doto (TextButton. label skin)
    (.addListener (proxy [ChangeListener] []
                    (changed [_event _actor]
                      (on-click!))))))

(defn listener
  [{:keys [zoom-speed
           camera-movement-speed
           tile-size
           world-viewport-width
           world-viewport-height
           ui-viewport-width
           ui-viewport-height
           ui-skin-path
           level-fns
           textures-config
           initial-level-fn]}]
  (let [world-unit-scale (float (/ tile-size))
        world-width (* world-viewport-width world-unit-scale)
        world-height (* world-viewport-height world-unit-scale)
        batch (atom nil)
        skin (atom nil)
        ui-stage (atom nil)
        world-viewport (atom nil)
        camera (atom nil)
        db (atom nil)
        textures (atom nil)
        tiled-map (atom nil)
        buttons (for [[label level-fn] level-fns]
                  [(str "Generate " label)
                   (fn []
                     (Disposable/.dispose @tiled-map)
                     (reset! tiled-map
                             (generate-level @db @textures @camera level-fn)))])]
    (reify ApplicationListener
      (create [_]
        (reset! batch (SpriteBatch.))
        (reset! skin (create-skin (.internal ^Files Gdx/files ui-skin-path))) ; same ?
        (reset! ui-stage (create-stage @batch
                                       (FitViewport. ui-viewport-width ui-viewport-height) ; requires gl context ?
                                       (window/create
                                        {:title "Edit"
                                         :skin @skin
                                         :table/rows (for [[label on-click!] buttons]
                                                       [{:actor (text-button @skin label on-click!)}])})))
        (.setInputProcessor ^Input Gdx/input ^InputProcessor @ui-stage)
        (reset! world-viewport (create-viewport world-width world-height)) ; same requires context?
        (reset! camera (viewport/get-camera @world-viewport)) ; ?? sep?
        (reset! db (db/create)) ; needs reloading?
        (reset! textures (textures/create Gdx/files textures-config))
        (reset! tiled-map (generate-level @db @textures @camera initial-level-fn)))

      (dispose [_]
        (Disposable/.dispose @batch)
        (Disposable/.dispose @skin)
        (run! Disposable/.dispose (vals @textures))
        (Disposable/.dispose @tiled-map))

      (render [_]
        (let [gl (.getGL20 ^Graphics Gdx/graphics)
              camera* @camera
              move (fn [idx f]
                     (orthographic-camera/set-position! camera*
                                                        (update (orthographic-camera/position camera*)
                                                                idx
                                                                #(f % camera-movement-speed))))]
          (.glClearColor ^GL20 gl 0 0 0 0)
          (.glClear ^GL20 gl GL20/GL_COLOR_BUFFER_BIT)
          (moon-tiled-map/draw! @tiled-map
                                @batch
                                world-unit-scale
                                (viewport/get-camera @world-viewport)
                                (constantly (color/float-bits [1 1 1 1])))
          (when (.isKeyPressed ^Input Gdx/input Input$Keys/MINUS)
            (orthographic-camera/inc-zoom! camera* zoom-speed))
          (when (.isKeyPressed ^Input Gdx/input Input$Keys/EQUALS)
            (orthographic-camera/inc-zoom! camera* (- zoom-speed)))
          (when (.isKeyPressed ^Input Gdx/input Input$Keys/LEFT)
            (move 0 -))
          (when (.isKeyPressed ^Input Gdx/input Input$Keys/RIGHT)
            (move 0 +))
          (when (.isKeyPressed ^Input Gdx/input Input$Keys/UP)
            (move 1 +))
          (when (.isKeyPressed ^Input Gdx/input Input$Keys/DOWN)
            (move 1 -))
          (.act ^Stage @ui-stage)
          (.draw ^Stage @ui-stage)))

      (resize [_ width height]
        (viewport/update! (.getViewport ^Stage @ui-stage) width height true)
        (viewport/update! @world-viewport width height false))

      (pause [_])

      (resume [_]))))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (listener config)
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "Levelgen Test")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))

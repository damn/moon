(ns moon.levelgen
  (:require [moon.db :as db]
            [moon.camera :as orthographic-camera]
            [moon.level.modules :as modules]
            [moon.level.tmx :as tmx]
            [moon.level.uf-caves :as uf-caves]
            [moon.textures :as textures]
            [gdx.stage :as stage]
            [moon.tiled-map :as moon-tiled-map]
            [gdx.color :as color]
            [gdx.input :as input]
            [gdx.tiled-map-tile-layer :as tiled-map-tile-layer]
            [moon.scene2d.window :as window]
            [moon.viewport :as viewport])
  (:import (com.badlogic.gdx.maps MapLayers)
           (com.badlogic.gdx Application ApplicationListener Files Gdx Graphics)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics GL20)
           (com.badlogic.gdx.graphics.g2d SpriteBatch)
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
    (-> (.get ^MapLayers (moon-tiled-map/get-layers tiled-map) "creatures")
        (tiled-map-tile-layer/set-visible! true))
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

(defn create-skin [path]
  (Skin. ^FileHandle (.internal ^Files Gdx/files path)))

(defn create-stage [batch width height]
  (stage/create (FitViewport. width height)
                batch))

(defn listener []
  (let [zoom-speed (:zoom-speed config)
        camera-movement-speed (:camera-movement-speed config)
        world-unit-scale (float (/ (:tile-size config)))
        world-width (* (:world-viewport-width config) world-unit-scale)
        world-height (* (:world-viewport-height config) world-unit-scale)
        batch (atom nil)
        skin (atom nil)
        ui-stage (atom nil)
        world-viewport (atom nil)
        camera (atom nil)
        db (atom nil)
        textures (atom nil)
        tiled-map (atom nil)]
    (reify ApplicationListener
      (create [_]
        (reset! batch (SpriteBatch.))
        (reset! ui-stage (create-stage @batch (:ui-viewport-width config) (:ui-viewport-height config)))
        (input/set-processor! Gdx/input @ui-stage)
        (reset! skin (create-skin (:ui-skin-path config)))
        (stage/add-actor! @ui-stage
                          (window/create
                           {:title "Edit"
                            :skin @skin
                            :table/rows
                            (for [[label level-fn] (:level-fns config)]
                              [{:actor
                                (doto (TextButton. (str "Generate " label) @skin)
                                  (.addListener (proxy [ChangeListener] []
                                                  (changed [_event _actor]
                                                    (Disposable/.dispose @tiled-map)
                                                    (reset! tiled-map
                                                            (generate-level @db @textures @camera level-fn))))))}])}))
        (reset! world-viewport (create-viewport world-width world-height))
        (reset! camera (viewport/get-camera @world-viewport))
        (reset! db (db/create))
        (reset! textures (textures/create Gdx/files (:textures-config config)))
        (reset! tiled-map (generate-level @db @textures @camera (:initial-level-fn config))))
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
                                (constantly (color/to-float-bits [1 1 1 1])))
          (when (input/key-pressed? Gdx/input :input.keys/minus)
            (orthographic-camera/inc-zoom! camera* zoom-speed))
          (when (input/key-pressed? Gdx/input :input.keys/equals)
            (orthographic-camera/inc-zoom! camera* (- zoom-speed)))
          (when (input/key-pressed? Gdx/input :input.keys/left)
            (move 0 -))
          (when (input/key-pressed? Gdx/input :input.keys/right)
            (move 0 +))
          (when (input/key-pressed? Gdx/input :input.keys/up)
            (move 1 +))
          (when (input/key-pressed? Gdx/input :input.keys/down)
            (move 1 -))
          (stage/act! @ui-stage)
          (stage/draw! @ui-stage)))
      (resize [_ width height]
        (viewport/update! (:stage/viewport @ui-stage) width height true)
        (viewport/update! @world-viewport width height false))
      (pause [_])
      (resume [_]))))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (listener)
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "Levelgen Test")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))

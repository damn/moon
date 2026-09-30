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

(defn- creature-properties [ctx]
  (moon-tiled-map/prepare-creature-tiles
   (db/all-raw (:ctx/db ctx) :properties/creatures)
   #(textures/texture-region (:ctx/textures ctx) %)))

(defn- show-creatures-layer! [tiled-map]
  (let [layers (moon-tiled-map/get-layers tiled-map)]
    (-> (.get ^MapLayers layers "creatures")
        (tiled-map-tile-layer/set-visible! true))))

(defn- fit-camera-to-tiled-map! [ctx tiled-map]
  (let [camera (:ctx/camera ctx)
        width (moon-tiled-map/get-property tiled-map "width")
        height (moon-tiled-map/get-property tiled-map "height")]
    (orthographic-camera/set-position! camera [(/ width 2) (/ height 2)])
    (orthographic-camera/zoom-to-rect camera {:left [0 0]
                                              :top [0 height]
                                              :right [width 0]
                                              :bottom [0 0]}))
  ctx)

(defn- generate-level
  [ctx level-fn]
  (let [level (level-fn {:level/creature-properties (creature-properties ctx)
                         :textures (:ctx/textures ctx)})
        tiled-map (:tiled-map level)
        ctx (assoc ctx :ctx/tiled-map tiled-map)]
    (assert tiled-map)
    (show-creatures-layer! tiled-map)
    (fit-camera-to-tiled-map! ctx tiled-map)))

(defn- regenerate-level! [ctx level-fn]
  (Disposable/.dispose (:ctx/tiled-map ctx))
  (generate-level ctx level-fn))

(defn- zoom-controls! [ctx]
  (let [input (:ctx/input ctx)
        zoom-speed (:ctx/zoom-speed ctx)
        camera (:ctx/camera ctx)]
    (when (input/key-pressed? input :input.keys/minus)
      (orthographic-camera/inc-zoom! camera zoom-speed))
    (when (input/key-pressed? input :input.keys/equals)
      (orthographic-camera/inc-zoom! camera (- zoom-speed)))))

(defn- camera-movement-controls! [ctx]
  (let [input (:ctx/input ctx)
        camera (:ctx/camera ctx)
        speed (:ctx/camera-movement-speed ctx)
        move (fn [idx f]
               (orthographic-camera/set-position! camera
                                                  (update (orthographic-camera/position camera)
                                                          idx
                                                          #(f % speed))))]
    (when (input/key-pressed? input :input.keys/left)
      (move 0 -))
    (when (input/key-pressed? input :input.keys/right)
      (move 0 +))
    (when (input/key-pressed? input :input.keys/up)
      (move 1 +))
    (when (input/key-pressed? input :input.keys/down)
      (move 1 -))))

(defn listener []
  (let [state (atom nil)]
    (reify ApplicationListener
      (create [_]
        (reset! state
                (let [input (.getInput ^Application Gdx/app)
                      files (.getFiles ^Application Gdx/app)
                      batch (SpriteBatch.)
                      skin (Skin. ^FileHandle (.internal ^Files files (:ui-skin-path config)))
                      world-unit-scale (float (/ (:tile-size config)))
                      world-width (* (:world-viewport-width config) world-unit-scale)
                      world-height (* (:world-viewport-height config) world-unit-scale)
                      world-viewport (FitViewport. (float world-width)
                                                   (float world-height)
                                                   (doto (orthographic-camera/new)
                                                     (orthographic-camera/set-to-ortho! false
                                                                                        world-width
                                                                                        world-height)))
                      stage* (stage/create (FitViewport. (float (:ui-viewport-width config))
                                                         (float (:ui-viewport-height config)))
                                           batch)
                      _ (input/set-processor! input stage*)
                      ctx {:ctx/input input
                           :ctx/zoom-speed (:zoom-speed config)
                           :ctx/camera-movement-speed (:camera-movement-speed config)
                           :ctx/world-unit-scale world-unit-scale
                           :ctx/sprite-batch batch
                           :ctx/stage stage*
                           :ctx/skin skin
                           :ctx/world-viewport world-viewport
                           :ctx/camera (viewport/get-camera world-viewport)
                           :ctx/db (db/create)
                           :ctx/textures (textures/create files (:textures-config config))}
                      ctx (generate-level ctx (:initial-level-fn config))]
                  (stage/add-actor!
                   stage*
                   (window/create
                    {:title "Edit"
                     :skin skin
                     :table/rows
                     (for [[label level-fn] (:level-fns config)]
                       [{:actor
                         (doto (TextButton. (str "Generate " label) skin)
                           (.addListener (proxy [ChangeListener] []
                                           (changed [_event _actor]
                                             (swap! state #(regenerate-level! % level-fn))))))}])}))
                  ctx)))
      (dispose [_]
        (let [{:keys [ctx/sprite-batch
                      ctx/skin
                      ctx/textures
                      ctx/tiled-map]} @state]
          (Disposable/.dispose sprite-batch)
          (Disposable/.dispose skin)
          (run! Disposable/.dispose (vals textures))
          (Disposable/.dispose tiled-map)))
      (render [_]
        (swap! state
               (fn [ctx]
                 (let [gl (.getGL20 ^Graphics Gdx/graphics)
                       stage (:ctx/stage ctx)]
                   (.glClearColor ^GL20 gl 0 0 0 0)
                   (.glClear ^GL20 gl GL20/GL_COLOR_BUFFER_BIT)
                   (moon-tiled-map/draw! (:ctx/tiled-map ctx)
                                         (:ctx/sprite-batch ctx)
                                         (:ctx/world-unit-scale ctx)
                                         (viewport/get-camera (:ctx/world-viewport ctx))
                                         (constantly (color/to-float-bits [1 1 1 1])))
                   (zoom-controls! ctx)
                   (camera-movement-controls! ctx)
                   (stage/act! stage)
                   (stage/draw! stage)
                   ctx))))
      (resize [_ width height]
        (let [ctx @state]
          (viewport/update! (:stage/viewport (:ctx/stage ctx)) width height true)
          (viewport/update! (:ctx/world-viewport ctx) width height false)))
      (pause [_])
      (resume [_]))))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (listener)
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "Levelgen Test")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))

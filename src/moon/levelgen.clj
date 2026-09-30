(ns moon.levelgen
  (:require [moon.db :as db]
            [moon.camera :as orthographic-camera]
            [moon.level.modules :as modules]
            [moon.level.tmx :as tmx]
            [moon.level.uf-caves :as uf-caves]
            [moon.textures :as textures]
            [gdx.stage :as stage]
            [moon.scene2d.table :as table]
            [gdx.tiled-map :as moon-tiled-map]
            [gdx.color :as color]
            [gdx.sprite-batch :as sprite-batch]
            [gdx.input :as input]
            [gdx.tiled-map-tile-layer :as tiled-map-tile-layer]
            [gdx.skin :as skin]
            [moon.scene2d.window :as window]
            [gdx.viewport :as viewport])
  (:import (com.badlogic.gdx.maps MapLayers)
           (com.badlogic.gdx Application ApplicationListener Files Gdx Graphics)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.graphics GL20)
           (com.badlogic.gdx.scenes.scene2d.ui Skin TextButton)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)
           (com.badlogic.gdx.utils Disposable)
           (com.badlogic.gdx.utils.viewport FitViewport)))

;; ctx accessors

(defn- get-stage [ctx]
  (:ctx/stage ctx))

(defn- get-input [ctx]
  (:ctx/input ctx))

(defn- get-camera [ctx]
  (:ctx/camera ctx))

(defn- get-db [ctx]
  (:ctx/db ctx))

(defn- get-textures [ctx]
  (:ctx/textures ctx))

(defn- get-zoom-speed [ctx]
  (:ctx/zoom-speed ctx))

(defn- get-camera-movement-speed [ctx]
  (:ctx/camera-movement-speed ctx))

(defn- get-world-unit-scale [ctx]
  (:ctx/world-unit-scale ctx))

(defn- get-sprite-batch [ctx]
  (:ctx/sprite-batch ctx))

(defn- get-skin [ctx]
  (:ctx/skin ctx))

(defn- get-world-viewport [ctx]
  (:ctx/world-viewport ctx))

(defn- get-tiled-map [ctx]
  (:ctx/tiled-map ctx))

(defn- get-files [ctx]
  (:ctx/files ctx))

;; ctx primitives

(defn- gl20 [_ctx]
  (.getGL20 ^Graphics Gdx/graphics))

(defn- key-pressed? [ctx key]
  (input/key-pressed? (get-input ctx) key))

(defn- set-input-processor! [ctx processor]
  (input/set-processor! (get-input ctx) processor))

(defn- texture-region [ctx name]
  (textures/texture-region (get-textures ctx) name))

(defn- creature-properties [ctx]
  (moon-tiled-map/prepare-creature-tiles
   (db/all-raw (get-db ctx) :properties/creatures)
   #(texture-region ctx %)))

(defn- show-creatures-layer! [tiled-map]
  (let [layers (moon-tiled-map/get-layers tiled-map)]
    (-> (.get ^MapLayers layers ^String "creatures")
        (tiled-map-tile-layer/set-visible! true))))

(defn- fit-camera-to-tiled-map! [ctx tiled-map]
  (let [camera (get-camera ctx)
        width (moon-tiled-map/get-property tiled-map "width")
        height (moon-tiled-map/get-property tiled-map "height")]
    (orthographic-camera/set-position! camera [(/ width 2) (/ height 2)])
    (orthographic-camera/zoom-to-rect camera {:left [0 0]
                                                :top [0 height]
                                                :right [width 0]
                                                :bottom [0 0]}))
  ctx)

(defn- clear-screen! [ctx]
  (let [gl (gl20 ctx)]
    (.glClearColor ^GL20 gl 0 0 0 0)
    (.glClear ^GL20 gl GL20/GL_COLOR_BUFFER_BIT)))

(defn- draw-tiled-map! [ctx]
  (moon-tiled-map/draw! (get-tiled-map ctx)
                        (get-sprite-batch ctx)
                        (get-world-unit-scale ctx)
                        (viewport/get-camera (get-world-viewport ctx))
                        (constantly (color/to-float-bits [1 1 1 1]))))

(defn- inc-camera-zoom! [ctx amount]
  (orthographic-camera/inc-zoom! (get-camera ctx) amount))

(defn- zoom-controls! [ctx]
  (when (key-pressed? ctx :input.keys/minus)
    (inc-camera-zoom! ctx (get-zoom-speed ctx)))
  (when (key-pressed? ctx :input.keys/equals)
    (inc-camera-zoom! ctx (- (get-zoom-speed ctx)))))

(defn- move-camera! [ctx idx f]
  (orthographic-camera/set-position! (get-camera ctx)
                                     (update (orthographic-camera/position (get-camera ctx))
                                             idx
                                             #(f % (get-camera-movement-speed ctx)))))

(defn- camera-movement-controls! [ctx]
  (when (key-pressed? ctx :input.keys/left)
    (move-camera! ctx 0 -))
  (when (key-pressed? ctx :input.keys/right)
    (move-camera! ctx 0 +))
  (when (key-pressed? ctx :input.keys/up)
    (move-camera! ctx 1 +))
  (when (key-pressed? ctx :input.keys/down)
    (move-camera! ctx 1 -)))

(defn- update-stage! [ctx]
  (let [stage (get-stage ctx)]
    (stage/act! stage)
    (stage/draw! stage))
  ctx)

(defn- dispose-sprite-batch! [ctx]
  (Disposable/.dispose (get-sprite-batch ctx)))

(defn- dispose-skin! [ctx]
  (Disposable/.dispose (get-skin ctx)))

(defn- dispose-textures! [ctx]
  (run! Disposable/.dispose (vals (get-textures ctx))))

(defn- dispose-tiled-map! [ctx]
  (Disposable/.dispose (get-tiled-map ctx)))

(defn- resize-stage-viewport! [ctx width height]
  (viewport/update! (:stage/viewport (get-stage ctx)) width height true))

(defn- resize-world-viewport! [ctx width height]
  (viewport/update! (get-world-viewport ctx) width height false))

(defn- generate-level
  [ctx level-fn]
  (let [level (level-fn {:level/creature-properties (creature-properties ctx)
                         :textures (get-textures ctx)})
        tiled-map (:tiled-map level)
        ctx (assoc ctx :ctx/tiled-map tiled-map)]
    (assert tiled-map)
    (show-creatures-layer! tiled-map)
    (fit-camera-to-tiled-map! ctx tiled-map)))

(defn- regenerate-level! [ctx level-fn]
  (dispose-tiled-map! ctx)
  (generate-level ctx level-fn))

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

(defn- create-bootstrap [app]
  {:ctx/files (.getFiles ^Application app)
   :ctx/input (.getInput ^Application app)
   :ctx/zoom-speed (:zoom-speed config)
   :ctx/camera-movement-speed (:camera-movement-speed config)
   :ctx/world-unit-scale (float (/ (:tile-size config)))})

(defn- create-sprite-batch [ctx]
  (assoc ctx :ctx/sprite-batch (sprite-batch/create)))

(defn- create-stage [ctx]
  (assoc ctx
         :ctx/stage (stage/create (FitViewport. (float (:ui-viewport-width config))
                                                (float (:ui-viewport-height config)))
                                  (get-sprite-batch ctx))))

(defn- create-skin [ctx]
  (assoc ctx
         :ctx/skin (skin/create (.internal ^Files (get-files ctx)
                                               (:ui-skin-path config)))))

(defn- create-world-viewport [ctx]
  (let [world-unit-scale (get-world-unit-scale ctx)
        world-width (* (:world-viewport-width config) world-unit-scale)
        world-height (* (:world-viewport-height config) world-unit-scale)
        world-viewport (FitViewport. (float world-width)
                                     (float world-height)
                                     (doto (orthographic-camera/new)
                                       (orthographic-camera/set-to-ortho! false
                                                                          world-width
                                                                          world-height)))]
    (-> ctx
        (assoc :ctx/world-viewport world-viewport)
        (assoc :ctx/camera (viewport/get-camera world-viewport)))))

(defn- create-db [ctx]
  (assoc ctx :ctx/db (db/create)))

(defn- create-textures [ctx]
  (assoc ctx
         :ctx/textures (textures/create (get-files ctx)
                                        (:textures-config config))))

(defn- create-initial-level [ctx]
  (generate-level ctx (:initial-level-fn config)))

(defn- set-input-processor-on-stage [ctx]
  (set-input-processor! ctx (get-stage ctx))
  ctx)

(declare state)

(defn- create-edit-window [ctx]
  (stage/add-actor! (get-stage ctx)
                    (window/create
                     {:title "Edit"
                      :skin (get-skin ctx)
                      :table/rows
                      (for [[label level-fn] (:level-fns config)]
                        [{:actor
                          (doto (TextButton. (str "Generate " label) (get-skin ctx))
                            (.addListener (proxy [ChangeListener] []
                              (changed [_event _actor]
                                (swap! state #(regenerate-level! % level-fn))))))}])}))
  ctx)

(defn- create-dissoc-files [ctx]
  (dissoc ctx :ctx/files))

(defn create [app]
  (-> (create-bootstrap app)
      create-sprite-batch
      create-stage
      create-skin
      create-world-viewport
      create-db
      create-textures
      create-initial-level
      set-input-processor-on-stage
      create-edit-window
      create-dissoc-files))

(defn dispose [ctx]
  (dispose-sprite-batch! ctx)
  (dispose-skin! ctx)
  (dispose-textures! ctx)
  (dispose-tiled-map! ctx))

(defn render [ctx]
  (clear-screen! ctx)
  (draw-tiled-map! ctx)
  (zoom-controls! ctx)
  (camera-movement-controls! ctx)
  (update-stage! ctx))

(defn resize [ctx width height]
  (resize-stage-viewport! ctx width height)
  (resize-world-viewport! ctx width height))

(def state (atom nil))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application.
    (reify ApplicationListener
      (create [_]
        (reset! state (create Gdx/app)))
      (dispose [_]
        (dispose @state))
      (render [_]
        (swap! state render))
      (resize [_ width height]
        (resize @state width height))
      (pause [_])
      (resume [_]))
    (doto (Lwjgl3ApplicationConfiguration.)
      (.setTitle "Levelgen Test")
      (.setWindowedMode 1440 900)
      (.setForegroundFPS 60))))

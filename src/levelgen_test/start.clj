(ns levelgen-test.start
  (:require [clojure.edn-resource :refer [edn-resource]]
            [moon.creature-tiles]
            [input.key-pressed :as key-pressed?]
            [orthographic-camera.inc-zoom :refer [inc-zoom!]]
            [orthographic-camera.position :as get-position]
            [orthographic-camera.set-position :refer [set-position!]]
            [orthographic-camera.calculate-zoom :refer [calculate-zoom]]
            [orthographic-camera.set-zoom :refer [set-zoom!]]
            [moon.db.all-raw :refer [all-raw]]
            [clojure.gdx.application-listener.new :as create-listener]
            [clojure.gdx.lwjgl3-application.new :as lwjgl3-application]
            [clojure.gdx.lwjgl3-application-configuration.new :as create-config]
            [clojure.gdx.use-glfw-async :as use-glfw-async!]
            [clojure.gdx.color.float-bits :as float-bits]
            [clojure.gdx.orthographic-camera.new :as new-camera]
            [clojure.gdx.orthographic-camera.set-to-ortho :as set-to-ortho!]
            [moon.db :as db]
            [gdx.scenes.scene2d.ui.window :as window]
            [scene2d.stage :as stage]
            [scene2d.ui.text-button :as text-button]
            [scene2d.utils.change-listener :as change-listener]
            [clojure.gdx.fit-viewport.new :as fit-viewport]
            [moon.application.listener]

            ; CTX:
            [ctx.textures]
            [render.get-stage-ctx]
            [pipeline.do]
            [render.clear-screen]
            [render.update-draw-stage]

            [clojure.gdx.draw-tiled-map :as draw-tiled-map])
  (:import (com.badlogic.gdx Input
                             Files
                             Gdx)
           (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d SpriteBatch
                                          TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            Event)
           (scene2d Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)
           (com.badlogic.gdx.maps MapLayers)
           (com.badlogic.gdx.maps.tiled TiledMap)
           (com.badlogic.gdx.utils Disposable)
           (com.badlogic.gdx.utils.viewport Viewport)))

(def initial-level-fn "config/world_fns/uf_caves.edn")

(def level-fns ["config/world_fns/vampire.edn"
                "config/world_fns/uf_caves.edn"
                "config/world_fns/modules.edn"])

(def ui-viewport-width 1440)
(def ui-viewport-height 900)

(def tile-size 48)

(defn camera-zoom-controls!
  [{:keys [ctx/input
           ctx/camera
           ctx/zoom-speed]}]
  (when (key-pressed?/f input :input.keys/minus)  (inc-zoom! camera zoom-speed))
  (when (key-pressed?/f input :input.keys/equals) (inc-zoom! camera (- zoom-speed))))

(defn camera-movement-controls!
  [{:keys [ctx/input
           ctx/camera
           ctx/camera-movement-speed]}]
  (let [apply-position (fn [idx f]
                         (set-position! camera
                                        (update (get-position/f camera)
                                                idx
                                                #(f % camera-movement-speed))))]
    (if (key-pressed?/f input :input.keys/left)  (apply-position 0 -))
    (if (key-pressed?/f input :input.keys/right) (apply-position 0 +))
    (if (key-pressed?/f input :input.keys/up)    (apply-position 1 +))
    (if (key-pressed?/f input :input.keys/down)  (apply-position 1 -))))

(defn draw-tiled-map!
  [{:keys [ctx/sprite-batch
           ctx/color-setter
           ctx/tiled-map
           ctx/world-unit-scale
           ctx/world-viewport]}]
  (draw-tiled-map/f! sprite-batch
                     world-unit-scale
                     (:viewport/camera world-viewport)
                     tiled-map
                     color-setter))

(defn show-whole-map!
  [{:keys [ctx/camera
           ctx/tiled-map]}]
  (set-position! camera
                 [(/ (.get (TiledMap/.getProperties tiled-map) "width") 2)
                  (/ (.get (TiledMap/.getProperties tiled-map) "height") 2)])
  (set-zoom! camera
             (calculate-zoom camera
                             {:left [0 0]
                              :top [0 (.get (TiledMap/.getProperties tiled-map) "height")]
                              :right [(.get (TiledMap/.getProperties tiled-map) "width") 0]
                              :bottom [0 0]})))

(defn generate-level
  [{:keys [ctx/db
           ctx/textures
           ctx/tiled-map] :as ctx} level-fn]
  (when tiled-map
    (Disposable/.dispose tiled-map))
  (let [level (let [[f params] (edn-resource level-fn)]
                (f
                 (assoc params
                        :level/creature-properties (moon.creature-tiles/prepare
                                                    (all-raw db :properties/creatures)
                                                    (fn [{:keys [image/file image/bounds]}]
                                                      (assert file)
                                                      (assert (contains? textures file))
                                                      (let [^Texture texture (get textures file)]
                                                        (if-let [[x y w h] bounds]
                                                          (TextureRegion. texture (int x) (int y) (int w) (int h))
                                                          (TextureRegion. texture)))))
                        :textures textures)))
        tiled-map (:tiled-map level)
        ctx (assoc ctx :ctx/tiled-map tiled-map)]
    (assert tiled-map)
    (-> tiled-map
        .getLayers
        (MapLayers/.get "creatures")
        (.setVisible true))
    (show-whole-map! ctx)
    ctx))

(defn edit-window [skin level-fns]
  {:title "Edit"
   :skin skin
   :table/rows (for [level-fn level-fns
                     :let [on-clicked (fn [actor ctx]
                                        (let [stage (Actor/.getStage actor)
                                              new-ctx (generate-level ctx level-fn)]
                                          (set! (.ctx stage) new-ctx)))]]
                 [{:actor (doto (text-button/create {:text (str "Generate " level-fn) :skin skin})
                            (Actor/.addListener (change-listener/create
                                                 (fn [event actor]
                                                   (on-clicked actor (:stage/ctx (Event/.getStage event)))))))}])})

(defn create!
  [_ctx]
  (let [files Gdx/files
        input Gdx/input
        graphics Gdx/graphics
        sprite-batch (SpriteBatch.)
        ui-viewport (fit-viewport/create ui-viewport-width ui-viewport-height)
        stage (stage/create ui-viewport sprite-batch)
        skin (Skin. (Files/.internal files "skin/uiskin.json"))
        world-unit-scale (float (/ tile-size))
        world-viewport (let [world-width  (* 1440 world-unit-scale)
                             world-height (* 900  world-unit-scale)]
                         (fit-viewport/create world-width
                                              world-height
                                              (doto (new-camera/f)
                                                (set-to-ortho!/f! false world-width world-height))))
        ctx {:ctx/files files
             :ctx/input input
             :ctx/graphics graphics
             :ctx/stage stage
             :ctx/db (db/create)
             :ctx/textures (ctx.textures/step {:ctx/files files}
                                              {:folder "resources/"
                                               :extensions #{"png" "bmp"}})
             :ctx/sprite-batch sprite-batch
             :ctx/skin skin
             :ctx/world-viewport world-viewport
             :ctx/camera (:viewport/camera world-viewport)
             :ctx/color-setter (constantly (float-bits/f [1 1 1 1]))
             :ctx/zoom-speed 0.1
             :ctx/camera-movement-speed 1
             :ctx/world-unit-scale world-unit-scale}
        ctx (generate-level ctx initial-level-fn)]
    (.setInputProcessor ^Input input stage)
    (Stage/.addActor (:ctx/stage ctx)
                     (window/create (edit-window skin level-fns)))
    ctx))

(defn dispose!
  [{:keys [ctx/skin
           ctx/sprite-batch
           ctx/tiled-map]}]
  ; TODO TEXTURES NOT DISPOSED
  (Disposable/.dispose skin)
  (Disposable/.dispose sprite-batch)
  (Disposable/.dispose tiled-map))

(defn resize!
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (.update ^Viewport (:stage/viewport stage) width height true)
  (.update ^Viewport world-viewport width height false))

(def state (atom nil))

(defn -main []
  (use-glfw-async!/f)
  (lwjgl3-application/f (create-listener/f
                         (moon.application.listener/f
                          {:state-var #'state
                           :create-pipeline [[create!]]
                           :dispose! dispose!
                           :render-pipeline [[render.get-stage-ctx/step]

                                             [pipeline.do/step
                                              [render.clear-screen/step]]

                                             [pipeline.do/step
                                              [draw-tiled-map!]]

                                             [pipeline.do/step
                                              [camera-zoom-controls!]]

                                             [pipeline.do/step
                                              [camera-movement-controls!]]

                                             [render.update-draw-stage/step]]
                           :resize! resize!}))
                        (create-config/f
                         {:title "Levelgen Test"
                          :windowed-mode {:width 1440 :height 900}
                          :foreground-fps 60})))

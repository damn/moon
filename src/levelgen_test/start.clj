(ns levelgen-test.start
  (:require [clojure.edn-resource :refer [edn-resource]]
            [moon.creature-tiles :as creature-tiles]
            [input.key-pressed :as key-pressed?]
            [clojure.gdx.stage.draw :as draw]
            [clojure.gdx.stage.add-actor :as add-actor]
            [orthographic-camera.inc-zoom :refer [inc-zoom!]]
            [orthographic-camera.position :as get-position]
            [orthographic-camera.set-position :refer [set-position!]]
            [orthographic-camera.calculate-zoom :refer [calculate-zoom]]
            [orthographic-camera.set-zoom :refer [set-zoom!]]
            [moon.db.all-raw :refer [all-raw]]
            [clojure.gdx.gdx.files :as files]
            [clojure.gdx.gdx.input :as input]
            [clojure.gdx.gdx.graphics :as graphics]
            [clojure.gdx.skin.new :as skin]
            [clojure.gdx.stage.act :as act]
            [clojure.gdx.input.set-input-processor! :as set-input-processor!]
            [clojure.gdx.sprite-batch.new :as sprite-batch]
            [clojure.gdx.application-listener.new :as create-listener]
            [clojure.gdx.files.internal :as internal]
            [clojure.gdx.texture-region.new :as texture-region]
            [clojure.gdx.lwjgl3-application.new :as lwjgl3-application]
            [clojure.gdx.lwjgl3-application-configuration.new :as create-config]
            [clojure.gdx.use-glfw-async :as use-glfw-async!]
            [clojure.gdx.color.float-bits :as float-bits]
            [clojure.gdx.orthographic-camera.new :as new-camera]
            [clojure.gdx.orthographic-camera.set-to-ortho :as set-to-ortho!]
            [moon.db :as db]
            [clojure.gdx.actor.get-stage :as get-stage]
            [gdx.scenes.scene2d.ui.window :as window]
            [scene2d.stage :as stage]
            [scene2d.ui.text-button :as text-button]
            [scene2d.utils.change-listener :as change-listener]
            [clojure.gdx.fit-viewport.new :as fit-viewport]
            [clojure.gdx.gl20.clear :as clear!]
            [clojure.gdx.gl20.clear-color :as clear-color!]
            [clojure.gdx.gl20.color-buffer-bit :as color-buffer-bit]
            [clojure.gdx.graphics.get-gl20 :as get-gl20]
            [clojure.gdx.draw-tiled-map :as draw-tiled-map]
            [clojure.gdx.actor.add-listener :as add-listener]
            [moon.create-textures :as create-textures])
  (:import (com.badlogic.gdx.maps MapLayers)
           (com.badlogic.gdx.maps.tiled TiledMap)
           (com.badlogic.gdx.utils Disposable)
           (com.badlogic.gdx.utils.viewport Viewport)))

(def lwjgl-app-config
  {:title "Levelgen Test"
   :windowed-mode {:width 1440 :height 900}
   :foreground-fps 60})

(def initial-level-fn "config/world_fns/uf_caves.edn")

(def level-fns ["config/world_fns/vampire.edn"
                "config/world_fns/uf_caves.edn"
                "config/world_fns/modules.edn"])

(def ui-viewport-width 1440)
(def ui-viewport-height 900)

(def world-viewport-width 1440)
(def world-viewport-height 900)

(def tile-size 48)

(def world-unit-scale (float (/ tile-size)))

(def ui-skin-path "skin/uiskin.json")

(def textures-config
  {:folder "resources/"
   :extensions #{"png" "bmp"}})

(def zoom-speed 0.1)

(def camera-movement-speed 1)

(def color-setter (constantly (float-bits/f [1 1 1 1])))

(defn update-draw-stage
  [{:keys [ctx/stage] :as ctx}]
  (set! (.ctx stage) ctx)
  (act/f stage)
  (draw/f stage)
  (:stage/ctx stage))

(defn camera-zoom-controls!
  [{:keys [ctx/input
           ctx/camera]
    :as ctx}]
  (when (key-pressed?/f input :input.keys/minus)  (inc-zoom! camera zoom-speed))
  (when (key-pressed?/f input :input.keys/equals) (inc-zoom! camera (- zoom-speed)))
  ctx)

(defn camera-movement-controls!
  [{:keys [ctx/input
           ctx/camera]
    :as ctx}]
  (let [apply-position (fn [idx f]
                         (set-position! camera
                                        (update (get-position/f camera)
                                                idx
                                                #(f % camera-movement-speed))))]
    (if (key-pressed?/f input :input.keys/left)  (apply-position 0 -))
    (if (key-pressed?/f input :input.keys/right) (apply-position 0 +))
    (if (key-pressed?/f input :input.keys/up)    (apply-position 1 +))
    (if (key-pressed?/f input :input.keys/down)  (apply-position 1 -)))
  ctx)

(defn draw-tiled-map!
  [{:keys [ctx/sprite-batch
           ctx/tiled-map
           ctx/world-viewport]
    :as ctx}]
  (draw-tiled-map/f! sprite-batch
                     world-unit-scale
                     (:viewport/camera world-viewport)
                     tiled-map
                     color-setter)
  ctx)

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
                        :level/creature-properties (creature-tiles/prepare
                                                    (all-raw db :properties/creatures)
                                                    (fn [{:keys [image/file image/bounds]}]
                                                      (assert file)
                                                      (assert (contains? textures file))
                                                      (let [texture (get textures file)]
                                                        (if-let [[x y w h] bounds]
                                                          (texture-region/f texture x y w h)
                                                          (texture-region/f texture)))))
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
   :table/rows (for [level-fn level-fns]
                 [{:actor (doto (text-button/create
                                 {:text (str "Generate " level-fn)
                                  :skin skin})
                            (add-listener/f (change-listener/create
                                             (fn [_event actor]
                                               (let [stage (get-stage/f actor)
                                                     ctx (:stage/ctx stage)
                                                     new-ctx (generate-level ctx level-fn)]
                                                 (set! (.ctx stage) new-ctx))))))}])})

(defn create!
  []
  (let [files (files/f)
        input (input/f)
        graphics (graphics/f)
        sprite-batch (sprite-batch/f)
        ui-viewport (fit-viewport/create ui-viewport-width ui-viewport-height)
        stage (stage/create ui-viewport sprite-batch)
        skin (skin/f (internal/f files ui-skin-path))
        world-viewport (let [world-width  (* world-viewport-width world-unit-scale)
                             world-height (* world-viewport-height  world-unit-scale)]
                         (fit-viewport/create world-width
                                              world-height
                                              (doto (new-camera/f)
                                                (set-to-ortho!/f! false world-width world-height))))
        ctx {:ctx/input input
             :ctx/graphics graphics
             :ctx/stage stage
             :ctx/db (db/create)
             :ctx/textures (create-textures/f files textures-config)
             :ctx/sprite-batch sprite-batch
             :ctx/skin skin
             :ctx/world-viewport world-viewport
             :ctx/camera (:viewport/camera world-viewport)}
        ctx (generate-level ctx initial-level-fn)]
    (set-input-processor!/f input stage)
    (add-actor/f (:ctx/stage ctx)
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

(defn get-stage-ctx
  [{:keys [ctx/stage]
    :as ctx}]
  (or (:stage/ctx stage)
      ctx)) ; first render stage does not have ctx set.

(defn clear-screen!
  [{:keys [ctx/graphics] :as ctx}]
  (let [gl (get-gl20/f graphics)]
    (clear-color!/f gl 0 0 0 0)
    (clear!/f gl color-buffer-bit/v))
  ctx)

(def lwjgl3-application-configuration
  (create-config/f lwjgl-app-config))

(defn application-listener [state]
  {:create! (fn []
              (reset! state (create!)))

   :dispose! (fn []
               (dispose! @state))

   :render! (fn []
              (swap! state (fn [ctx]
                             (-> ctx
                                 get-stage-ctx
                                 clear-screen!
                                 draw-tiled-map!
                                 camera-zoom-controls!
                                 camera-movement-controls!
                                 update-draw-stage))))

   :resize! (fn [width height]
              (resize! @state width height))

   :pause! (fn [])

   :resume! (fn [])})

(def state (atom nil))

(defn -main []
  (use-glfw-async!/f)
  (lwjgl3-application/f (create-listener/f
                         (application-listener state))
                        lwjgl3-application-configuration))

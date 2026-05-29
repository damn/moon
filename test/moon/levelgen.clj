(ns moon.levelgen
  (:require [clojure.core-ext :refer [edn-resource]]
            [clojure.gdx.textures]
            [gdx.graphics.color :as color]
            [clojure.gdx.tiled-map-renderer :as tiled-map-renderer]
            [gdx.utils.viewport.fit-viewport :as fit-viewport]
            [gdx.graphics.orthographic-camera :as camera]
            [gdx.input.keys :as input.keys]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.event :as event]
            [gdx.scenes.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [gdx.scenes.scene2d.stage :as stage]
            [gdx.utils.viewport.viewport :as viewport]
            [moon.creature-tiles]
            [moon.db :as db])
  (:import (com.badlogic.gdx Application
                             ApplicationListener
                             Gdx)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.graphics.g2d SpriteBatch
                                          TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)
           (com.badlogic.gdx.utils Disposable
                                   ScreenUtils)))

(def initial-level-fn "world_fns/uf_caves.edn")

(def level-fns
  ["world_fns/vampire.edn"
   "world_fns/uf_caves.edn"
   "world_fns/modules.edn"])

(defn- show-whole-map! [{:keys [ctx/camera
                                ctx/tiled-map]}]
  (camera/set-position! camera
                        [(/ (.get (.getProperties tiled-map) "width") 2)
                         (/ (.get (.getProperties tiled-map) "height") 2)])
  (camera/set-zoom! camera
                    (camera/calculate-zoom camera
                                           {:left [0 0]
                                            :top [0 (.get (.getProperties tiled-map) "height")]
                                            :right [(.get (.getProperties tiled-map) "width") 0]
                                            :bottom [0 0]})))

(def tile-size 48)

(defn- generate-level
  [{:keys [ctx/db
           ctx/textures
           ctx/tiled-map] :as ctx} level-fn]
  (when tiled-map
    (.dispose tiled-map))
  (let [level (let [[f params] (edn-resource level-fn)]
                (f
                 (assoc params
                        :level/creature-properties (moon.creature-tiles/prepare
                                                    (db/all-raw db :properties/creatures)
                                                    (fn [{:keys [image/file image/bounds]}]
                                                      (assert file)
                                                      (assert (contains? textures file))
                                                      (let [texture (get textures file)]
                                                        (if-let [[x y w h] bounds]
                                                          (TextureRegion. texture x y w h)
                                                          (TextureRegion. texture)))))
                        :textures textures)))
        tiled-map (:tiled-map level)
        ctx (assoc ctx :ctx/tiled-map tiled-map)]
    (assert tiled-map)
    (-> tiled-map
        .getLayers
        (.get "creatures")
        (.setVisible true))
    (show-whole-map! ctx)
    ctx))

(defn- edit-window [skin]
  {:title "Edit"
   :skin skin
   :table/rows (for [level-fn level-fns
                     :let [on-clicked (fn [actor ctx]
                                        (let [stage (actor/stage actor)
                                              new-ctx (generate-level ctx level-fn)]
                                          (stage/set-ctx! stage new-ctx)))]]
                 [{:actor (text-button/create
                           {:text (str "Generate " level-fn)
                            :skin skin
                            :actor/listeners {:listener/change (fn [event actor]
                                                                 (on-clicked actor (:stage/ctx (event/stage event))))}})}])})

(defn create!
  [^Application app]
  (let [files (.getFiles app)
        graphics (.getGraphics app)
        input (.getInput app)
        skin (Skin. (.internal files "uiskin.json"))
        ui-viewport (fit-viewport/create 1440 900)
        sprite-batch (SpriteBatch.)
        stage (stage/create ui-viewport sprite-batch)
        _  (.setInputProcessor input stage)
        tile-size 48
        world-unit-scale (float (/ tile-size))
        ctx {:ctx/stage stage
             :ctx/files files}
        ctx (assoc ctx :ctx/db (db/create))
        ctx (assoc ctx :ctx/textures (clojure.gdx.textures/create files))
        world-viewport (let [world-width  (* 1440 world-unit-scale)
                             world-height (* 900  world-unit-scale)]
                         (fit-viewport/create world-width
                                              world-height
                                              (camera/create
                                               {:y-down? false
                                                :world-width world-width
                                                :world-height world-height})))
        ctx (assoc ctx
                   :ctx/input input
                   :ctx/world-viewport world-viewport
                   :ctx/ui-viewport ui-viewport
                   :ctx/camera (:viewport/camera world-viewport)
                   :ctx/color-setter (constantly (color/float-bits [1 1 1 1]))
                   :ctx/zoom-speed 0.1
                   :ctx/camera-movement-speed 1
                   :ctx/sprite-batch sprite-batch
                   :ctx/world-unit-scale world-unit-scale)
        ctx (generate-level ctx initial-level-fn)]
    (stage/add-actor! (:ctx/stage ctx) (window/create (edit-window skin)))
    ctx))

(defn dispose!
  [{:keys [ctx/skin
           ctx/sprite-batch
           ctx/tiled-map]}]
  (Disposable/.dispose skin)
  (Disposable/.dispose sprite-batch)
  (Disposable/.dispose tiled-map))

(defn- draw-tiled-map! [{:keys [ctx/sprite-batch
                                ctx/color-setter
                                ctx/tiled-map
                                ctx/world-unit-scale
                                ctx/world-viewport]}]
  (tiled-map-renderer/draw! sprite-batch
                            world-unit-scale
                            (:viewport/camera world-viewport)
                            tiled-map
                            color-setter))

(defn- camera-movement-controls! [{:keys [ctx/input
                                          ctx/camera
                                          ctx/camera-movement-speed]}]
  (let [apply-position (fn [idx f]
                         (camera/set-position! camera
                                               (update (camera/position camera)
                                                       idx
                                                       #(f % camera-movement-speed))))]
    (if (.isKeyPressed input input.keys/left)  (apply-position 0 -))
    (if (.isKeyPressed input input.keys/right) (apply-position 0 +))
    (if (.isKeyPressed input input.keys/up)    (apply-position 1 +))
    (if (.isKeyPressed input input.keys/down)  (apply-position 1 -))))

(defn- camera-zoom-controls! [{:keys [ctx/input
                                      ctx/camera
                                      ctx/zoom-speed]}]
  (when (.isKeyPressed input input.keys/minus)  (camera/inc-zoom! camera zoom-speed))
  (when (.isKeyPressed input input.keys/equals) (camera/inc-zoom! camera (- zoom-speed))))

(defn render!
  [{:keys [ctx/stage]
    :as ctx}]
  (let [ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (ScreenUtils/clear 0 0 0 0)
    (draw-tiled-map! ctx)
    (camera-zoom-controls! ctx)
    (camera-movement-controls! ctx)
    (stage/set-ctx! stage ctx)
    (stage/act! stage)
    (stage/draw! stage)
    (:stage/ctx stage)))

(defn resize!
  [{:keys [ctx/ui-viewport
           ctx/world-viewport]}
   width height]
  (viewport/update! ui-viewport    width height true)
  (viewport/update! world-viewport width height false))

(def state (atom nil))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (reify ApplicationListener
                        (create [_]
                          (reset! state (create! Gdx/app)))

                        (dispose [_]
                          (dispose! @state))

                        (render [_]
                          (swap! state render!))

                        (resize [_ width height]
                          (resize! @state width height))

                        (pause [_])

                        (resume [_]))
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "Levelgen Test")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))
